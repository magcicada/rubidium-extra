package me.flashyreese.mods.sodiumextra.mixin.sky;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @WrapWithCondition(
            method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gl/VertexBuffer;draw(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/gl/ShaderProgram;)V",
                    ordinal = 0
            )
    )
    public boolean redirectSetSkyShader(VertexBuffer instance, Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program) {
        return SodiumExtraClientMod.options().detailSettings.sky;
    }

    @Inject(method = "renderEndSky", at = @At(value = "HEAD"), cancellable = true)
    public void preRenderEndSky(MatrixStack matrices, CallbackInfo ci) {
        if (!SodiumExtraClientMod.options().detailSettings.sky) {
            ci.cancel();
        }
    }
}
