package me.flashyreese.mods.sodiumextra.mixin.stars;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @WrapOperation(
            method = "renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;getStarBrightness(F)F"
            )
    )
    public float redirectGetStarBrightness(ClientWorld instance, float f, Operation<Float> original) {
        if (SodiumExtraClientMod.options().detailSettings.stars) {
            return original.call(instance, f);
        } else {
            return 0.0f;
        }
    }
}
