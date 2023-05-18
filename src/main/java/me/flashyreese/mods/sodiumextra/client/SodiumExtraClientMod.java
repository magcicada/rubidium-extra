package me.flashyreese.mods.sodiumextra.client;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraGameOptions;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SodiumExtraClientMod {

    private static SodiumExtraGameOptions CONFIG;
    public static final Logger LOGGER = LogManager.getLogger("Sodium Extra");

    public static SodiumExtraGameOptions options() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
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
