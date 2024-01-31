package me.flashyreese.mods.sodiumextra.mixin.fog;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraGameOptions;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    @Shadow
    @Nullable
    private static BackgroundRenderer.@Nullable StatusEffectFogModifier getFogModifier(Entity entity, float tickDelta) {
        return null;
    }

    @Inject(
            method = "applyFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;onFogRender(Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lnet/minecraft/client/render/CameraSubmersionType;Lnet/minecraft/client/render/Camera;FFFFLnet/minecraft/client/render/FogShape;)V",
                    shift = At.Shift.BY
            )
    )
    private static void applyFogBase(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Share("rbSetFog") LocalBooleanRef setFog, @Share("rbFogStart") LocalFloatRef fogStartRef, @Share("rbFogEnd") LocalFloatRef fogEndRef) {
        Entity entity = camera.getFocusedEntity();
        SodiumExtraClientMod.options().renderSettings.dimensionFogDistanceMap.putIfAbsent(entity.getWorld().getDimension().effects(), 0);
        int fogDistance = SodiumExtraClientMod.options().renderSettings.multiDimensionFogControl ? SodiumExtraClientMod.options().renderSettings.dimensionFogDistanceMap.get(entity.getWorld().getDimension().effects()) : SodiumExtraClientMod.options().renderSettings.fogDistance;
        BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
        if (fogDistance == 0 || statusEffectFogModifier != null) {
            return;
        }
        if (camera.getSubmersionType() == CameraSubmersionType.NONE && (thickFog || fogType == BackgroundRenderer.FogType.FOG_TERRAIN)) {
            float fogStart = (float) SodiumExtraClientMod.options().renderSettings.fogStart / 100;
            if (fogDistance == 33) {
                RenderSystem.setShaderFogColor(1f, 1f, 1f, 0f);
                //RenderSystem.setShaderFogStart(Short.MAX_VALUE - 1 * fogStart);
                //RenderSystem.setShaderFogEnd(Short.MAX_VALUE);
            } else {
                setFog.set(true);

                fogStartRef.set(fogDistance * 16 * fogStart);
                fogEndRef.set((fogDistance + 1) * 16);
            }
        }
    }

    @ModifyArg(
            method = "applyFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;onFogRender(Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lnet/minecraft/client/render/CameraSubmersionType;Lnet/minecraft/client/render/Camera;FFFFLnet/minecraft/client/render/FogShape;)V"
            ),
            index = 5
    )
    private static float applyFogModCompatStart(float fogStart, @Share("rbSetFog") LocalBooleanRef setFog, @Share("rbFogStart") LocalFloatRef fogStartRef) {
        if (SodiumExtraClientMod.options().renderSettings.fogType == SodiumExtraGameOptions.RenderSettings.FogType.MOD_COMPAT && setFog.get()) {
            return fogStartRef.get();
        }

        return fogStart;
    }

    @ModifyArg(
            method = "applyFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/ForgeHooksClient;onFogRender(Lnet/minecraft/client/render/BackgroundRenderer$FogType;Lnet/minecraft/client/render/CameraSubmersionType;Lnet/minecraft/client/render/Camera;FFFFLnet/minecraft/client/render/FogShape;)V"
            ),
            index = 6
    )
    private static float applyFogModCompatEnd(float fogEnd, @Share("rbSetFog") LocalBooleanRef setFog, @Share("rbFogEnd") LocalFloatRef fogEndRef) {
        if (SodiumExtraClientMod.options().renderSettings.fogType == SodiumExtraGameOptions.RenderSettings.FogType.MOD_COMPAT && setFog.get()) {
            return fogEndRef.get();
        }

        return fogEnd;
    }

    @Inject(method = "applyFog", at = @At(value = "TAIL"))
    private static void applyFogDefault(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Share("rbSetFog") LocalBooleanRef setFog, @Share("rbFogStart") LocalFloatRef fogStartRef, @Share("rbFogEnd") LocalFloatRef fogEndRef) {
        if (SodiumExtraClientMod.options().renderSettings.fogType == SodiumExtraGameOptions.RenderSettings.FogType.DEFAULT && setFog.get()) {
            RenderSystem.setShaderFogStart(fogStartRef.get());
            RenderSystem.setShaderFogEnd(fogEndRef.get());
        }
    }
}
