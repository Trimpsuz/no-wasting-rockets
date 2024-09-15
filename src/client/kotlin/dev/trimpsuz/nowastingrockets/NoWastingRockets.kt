package dev.trimpsuz.nowastingrockets

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory

object NoWastingRockets : ClientModInitializer {
	private val logger = LoggerFactory.getLogger("no-wasting-rockets")

	private lateinit var keyBindingToggleEnabled: KeyBinding
	private lateinit var keyBindingOpenConfig: KeyBinding

	override fun onInitializeClient() {
		logger.info("No Wasting Rockets init")

		Config.load()

		keyBindingToggleEnabled = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"Toggle on/off",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"No Wasting Rockets"
			)
		)

		keyBindingOpenConfig = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"Open Config",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"No Wasting Rockets"
			)
		)

		net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register { client ->
			while (keyBindingToggleEnabled.wasPressed()) {
				Config.isEnabled = !Config.isEnabled
				client.player?.sendMessage(Text.literal("Toggled No Wasting Rockets ").append(
					if(Config.isEnabled) Text.literal("ON").setStyle(Style.EMPTY.withColor(Formatting.GREEN)) else Text.literal("OFF").setStyle(Style.EMPTY.withColor(Formatting.RED))
				), true)
				Config.save()
			}

			while (keyBindingOpenConfig.wasPressed()) {
				client.setScreen(ConfigScreen.createConfigScreen(null))
			}
		}

		UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
			onUseBlock(player, hand)
		}
	}

	private fun onUseBlock(player: PlayerEntity, hand: Hand): ActionResult {
		if(!Config.isEnabled) return ActionResult.PASS

		val itemStack = player.getStackInHand(hand)

		if (itemStack.item == Items.FIREWORK_ROCKET) {
			val fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS)

			if(fireworksComponent?.explosions?.isEmpty() == true) {
				return ActionResult.FAIL
			}
		}

		return ActionResult.PASS
	}
}
