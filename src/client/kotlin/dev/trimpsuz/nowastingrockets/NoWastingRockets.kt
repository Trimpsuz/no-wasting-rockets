package dev.trimpsuz.nowastingrockets

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import javax.swing.Action

object NoWastingRockets : ClientModInitializer {
	private val logger = LoggerFactory.getLogger("no-wasting-rockets")

	private lateinit var keyBindingToggleEnabled: KeyBinding
	private lateinit var keyBindingForceBoostElytra: KeyBinding
	private  lateinit var keyBindingBoostWithExplosions: KeyBinding
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

		keyBindingForceBoostElytra = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"Toggle Force Boost Elytra",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				"No Wasting Rockets"
			)
		)

		keyBindingBoostWithExplosions = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"Toggle Boost With Explosions",
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
				client.player?.sendMessage(
					Text.literal("Toggled No Wasting Rockets ").append(
						if (Config.isEnabled) Text.literal("ON")
							.setStyle(Style.EMPTY.withColor(Formatting.GREEN))
						else Text.literal("OFF")
							.setStyle(Style.EMPTY.withColor(Formatting.RED))
					), true
				)
				Config.save()
			}

			while (keyBindingForceBoostElytra.wasPressed()) {
				Config.forceBoostElytra = !Config.forceBoostElytra
				client.player?.sendMessage(
					Text.literal("Toggled Force Boost Elytra ").append(
						if (Config.forceBoostElytra) Text.literal("ON")
							.setStyle(Style.EMPTY.withColor(Formatting.GREEN))
						else Text.literal("OFF")
							.setStyle(Style.EMPTY.withColor(Formatting.RED))
					), true
				)
				Config.save()
			}

			while (keyBindingBoostWithExplosions.wasPressed()) {
				Config.boostWithExplosions = !Config.boostWithExplosions
				client.player?.sendMessage(
					Text.literal("Toggled Boost With Explosions ").append(
						if (Config.boostWithExplosions) Text.literal("ON")
							.setStyle(Style.EMPTY.withColor(Formatting.GREEN))
						else Text.literal("OFF")
							.setStyle(Style.EMPTY.withColor(Formatting.RED))
					), true
				)
				Config.save()
			}

			while (keyBindingOpenConfig.wasPressed()) {
				client.setScreen(ConfigScreen.createConfigScreen(null))
			}
		}

		UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
			onUseBlock(player, hand)
		}

		UseItemCallback.EVENT.register { player, world, hand ->
			onUseItem(player, hand)
		}
	}

	private fun onUseBlock(player: PlayerEntity, hand: Hand): ActionResult {
		if (!Config.isEnabled) return ActionResult.PASS

		val itemStack = player.getStackInHand(hand)

		if (itemStack.item == Items.FIREWORK_ROCKET) {
			val fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS)

			if (fireworksComponent?.explosions?.isEmpty() == true) {
				if (Config.forceBoostElytra && player.isFallFlying) {
					MinecraftClient.getInstance().networkHandler?.sendPacket(
						PlayerInteractItemC2SPacket(
							hand,
							0,
							player.yaw,
							player.pitch
						)
					)
				}

				return ActionResult.FAIL
			}
		}

		return ActionResult.PASS
	}

	private fun onUseItem(player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		val itemStack = player.getStackInHand(hand)

		if (!Config.isEnabled || Config.boostWithExplosions) return TypedActionResult(ActionResult.PASS, itemStack)

		if(itemStack.item == Items.FIREWORK_ROCKET) {
			val fireworksComponent = itemStack.get(DataComponentTypes.FIREWORKS)

			if(fireworksComponent?.explosions?.isNotEmpty() == true && player.isFallFlying) return TypedActionResult.fail(itemStack)
		}

		return TypedActionResult(ActionResult.PASS, itemStack)
	}
}