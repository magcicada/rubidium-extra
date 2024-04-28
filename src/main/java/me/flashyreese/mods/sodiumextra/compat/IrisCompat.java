package me.flashyreese.mods.sodiumextra.compat;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.render.VertexFormat;
import net.minecraftforge.fml.loading.LoadingModList;

public class IrisCompat {
    private static final boolean irisPresent = LoadingModList.get().getModFileById("oculus") != null;

    public static boolean isRenderingShadowPass() {
        if (irisPresent) {
            try {
                return IrisApi.getInstance().isRenderingShadowPass();
            } catch (Throwable throwable) {
                SodiumExtraClientMod.LOGGER.error(throwable);
            }
        }

        return false;
    }

    public static VertexFormat getTerrainFormat() {
        if (irisPresent) {
            try {
                return IrisVertexFormats.TERRAIN;
            } catch (Throwable throwable) {
                SodiumExtraClientMod.LOGGER.error(throwable);
            }
        }

        return null;
    }

    public static boolean isIrisPresent() {
        return irisPresent;
    }
}