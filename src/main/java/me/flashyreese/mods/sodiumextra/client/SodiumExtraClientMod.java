package me.flashyreese.mods.sodiumextra.client;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraGameOptions;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SodiumExtraClientMod {
    public static final Logger LOGGER = LogManager.getLogger("Embeddium Extra");
    private static SodiumExtraGameOptions CONFIG;
    private static CaffeineConfig MIXIN_CONFIG;

    public static SodiumExtraGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    public static CaffeineConfig mixinConfig() {
        if (MIXIN_CONFIG == null) {
            MIXIN_CONFIG = CaffeineConfig.builder("Sodium Extra").withSettingsKey("embeddium_extra", "sodium-extra:options")
                    .addMixinOption("adaptive_sync", true)
                    .addMixinOption("animation", true)
                    .addMixinOption("biome_colors", true)
                    .addMixinOption("cloud", true)
                    .addMixinOption("compat", true, false)
                    .addMixinOption("fog", true)
                    .addMixinOption("fog_falloff", true)
                    .addMixinOption("gui", true)
                    .addMixinOption("instant_sneak", true)
                    .addMixinOption("light_updates", true)
                    .addMixinOption("optimizations", true)
                    .addMixinOption("optimizations.beacon_beam_rendering", true)
                    .addMixinOption("optimizations.draw_helpers", false)
                    .addMixinOption("optimizations.fast_weather", false)
                    .addMixinOption("particle", true)
                    .addMixinOption("prevent_shaders", true)
                    .addMixinOption("profiler", true)
                    .addMixinOption("reduce_resolution_on_mac", true)
                    .addMixinOption("render", true)
                    .addMixinOption("render.block", true)
                    .addMixinOption("render.block.entity", true)
                    .addMixinOption("render.entity", true)
                    .addMixinOption("sky", true)
                    .addMixinOption("sky_colors", true)
                    .addMixinOption("sodium", true)
                    .addMixinOption("sodium.accessibility", true)
                    .addMixinOption("sodium.fog", true)
                    .addMixinOption("sodium.cloud", true)
                    .addMixinOption("sodium.resolution", true)
                    .addMixinOption("sodium.scrollable_page", true)
                    .addMixinOption("sodium.vsync", true)
                    .addMixinOption("stars", true)
                    .addMixinOption("steady_debug_hud", true)
                    .addMixinOption("sun_moon", true)
                    .addMixinOption("toasts", true)

                    .withLogger(SodiumExtraClientMod.LOGGER)
                    .withInfoUrl("https://github.com/FlashyReese/sodium-extra-fabric/wiki/Configuration-File")
                    .build(FMLPaths.CONFIGDIR.get().resolve("sodium-extra.properties"));
        }
        return MIXIN_CONFIG;
    }

    private static SodiumExtraGameOptions loadConfig() {
        return SodiumExtraGameOptions.load(FMLPaths.CONFIGDIR.get().resolve("sodium-extra-options.json").toFile());
    }

    public SodiumExtraClientMod() {
        /* if (SodiumExtraClientMod.options().superSecretSettings.fetchSodiumExtraCrowdinTranslations) {
            CrowdinTranslate.downloadTranslations(SodiumExtraClientMod.options().superSecretSettings.sodiumExtraCrowdinProjectIdentifier, "sodium-extra");
        }
        if (SodiumExtraClientMod.options().superSecretSettings.fetchSodiumCrowdinTranslations) {
            CrowdinTranslate.downloadTranslations(SodiumExtraClientMod.options().superSecretSettings.sodiumCrowdinProjectIdentifier, "sodium");
        }*/
    }
}
