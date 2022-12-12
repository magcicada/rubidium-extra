package me.flashyreese.mods.sodiumextra.mixin.sodium.biome_blend;

import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.render.terrain.color.LinearFlatColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.blender.LinearColorBlender;
import net.minecraft.state.State;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me.jellysquid.mods.sodium.client.model.quad.blender.ConfigurableColorBlender")
public class MixinChunkRenderCache {
    private final ColorBlender linearFlatColorBlender = new LinearFlatColorBlender();

    /**
     * @author FlashyReese
     * @reason Includes linear flat color blender
     */
    @Redirect(
            method = "getColors",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/model/quad/blender/ColorBlender;getColors(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lme/jellysquid/mods/sodium/client/model/quad/ModelQuadView;Lme/jellysquid/mods/sodium/client/model/quad/blender/ColorSampler;Lnet/minecraft/state/State;)[I"
            )
    )
    public <T extends State<O, ?>, O> int[] createBiomeColorBlender(ColorBlender blender, BlockRenderView var1, BlockPos var2, ModelQuadView var3, ColorSampler<T> var4, T var5) {
        if (blender instanceof LinearColorBlender && SodiumExtraClientMod.options().renderSettings.useLinearFlatColorBlender) {
            return this.linearFlatColorBlender.getColors(var1, var2, var3, var4, var5);
        } else {
            return blender.getColors(var1, var2, var3, var4, var5);
        }
    }
}
