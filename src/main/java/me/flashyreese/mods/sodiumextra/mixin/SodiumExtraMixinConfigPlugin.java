package me.flashyreese.mods.sodiumextra.mixin;

import com.llamalad7.mixinextras.utils.MixinInternals;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.caffeinemc.caffeineconfig.AbstractCaffeineConfigMixinPlugin;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import org.embeddedt.embeddium.impl.taint.mixin.MixinTaintDetector;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;

import java.lang.reflect.Field;

public class SodiumExtraMixinConfigPlugin extends AbstractCaffeineConfigMixinPlugin {

    private static final String MIXIN_PACKAGE_ROOT = "me.flashyreese.mods.sodiumextra.mixin.";

    public SodiumExtraMixinConfigPlugin() {
        System.setProperty("embeddium.mixinTaintEnforceLevel", "WARN");
        try {
            Field instanceField = MixinTaintDetector.class.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);
            MixinInternals.unregisterExtension((IExtension) instanceField.get(null));
        } catch (Throwable th) {
            System.out.println("Embeddium is shit");
        }
    }

    @Override
    protected CaffeineConfig createConfig() {
        return SodiumExtraClientMod.mixinConfig();
    }

    @Override
    protected String mixinPackageRoot() {
        return MIXIN_PACKAGE_ROOT;
    }
}
