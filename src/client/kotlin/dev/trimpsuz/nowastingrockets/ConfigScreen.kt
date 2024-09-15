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
                    .setSaveConsumer { value -> Config.isEnabled = value}
                    .build()
            )

        builder.setSavingRunnable { Config.save() }

        return builder.build()
    }
}
