package dev.trimpsuz.nowastingrockets

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

object ConfigScreen {
    fun createConfigScreen(parent: Screen?): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.of("${Config.MOD_NAME} ${Config.MOD_VERSION}"))

        val entryBuilder = ConfigEntryBuilder.create()

        builder.getOrCreateCategory(Text.of("General"))
            .addEntry(
                entryBuilder.startBooleanToggle(Text.of("Enabled"), Config.isEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Enable or disable the functionality of the mod."))
                    .setSaveConsumer { value -> Config.isEnabled = value}
                    .build()
            )
            .addEntry(
                entryBuilder.startBooleanToggle(Text.of("Force Boost Elytra"), Config.forceBoostElytra)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("If flying with elytra, forces the rocket to boost even while looking at a block."))
                    .setSaveConsumer { value -> Config.forceBoostElytra = value}
                    .build()
            )
            .addEntry(
                entryBuilder.startBooleanToggle(Text.of("Boost With Explosions"), Config.boostWithExplosions)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Turning this off prevents boosting with firework rockets that have explosion effects on them."))
                    .setSaveConsumer { value -> Config.boostWithExplosions = value}
                    .build()
            )

        builder.setSavingRunnable { Config.save() }

        return builder.build()
    }
}
