package dev.trimpsuz.nowastingrockets

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand

object NoWastingRocketsClient : ClientModInitializer {
	private val logger = LoggerFactory.getLogger("no-wasting-rockets")

	override fun onInitializeClient() {
		logger.info("No Wasting Rockets init")

		UseBlockCallback.EVENT.register { player, world, hand, hitResult ->
			onUseBlock(player, hand)
		}
	}

	private fun onUseBlock(player: PlayerEntity, hand: Hand): ActionResult {
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