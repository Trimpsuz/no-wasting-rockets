package dev.trimpsuz.nowastingrockets

import net.minecraft.client.MinecraftClient
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import java.io.File
import java.util.Optional

class Utils {
    companion object {
        fun getModVersion(modId: String): String {
            try {
                val container: Optional<ModContainer> = FabricLoader.getInstance().getModContainer(modId)

                if (container.isPresent) {
                    return container.get().metadata.version.friendlyString
                }
            } catch (_: Exception) { }

            return "?"
        }

        fun getConfigDirectory(): File {
            return File(MinecraftClient.getInstance().runDirectory, "config")
        }
    }
}
