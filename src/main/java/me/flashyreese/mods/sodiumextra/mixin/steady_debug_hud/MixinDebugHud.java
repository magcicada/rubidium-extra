package me.flashyreese.mods.sodiumextra.mixin.steady_debug_hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugHud.class)
public abstract class MixinDebugHud {

    @Unique
    private final List<String> leftTextCache = new ArrayList<>();
    @Unique
    private final List<String> rightTextCache = new ArrayList<>();
    @Unique
    private long nextTime = 0L;
    @Unique
    private boolean rebuild = true;

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void preRender(DrawContext context, CallbackInfo ci) {
        if (SodiumExtraClientMod.options().extraSettings.steadyDebugHud) {
            final long currentTime = Util.getMeasuringTimeMs();
            if (currentTime > this.nextTime) {
                this.rebuild = true;
                this.nextTime = currentTime + (SodiumExtraClientMod.options().extraSettings.steadyDebugHudRefreshInterval * 50L);
            } else {
                this.rebuild = false;
            }
        } else {
            this.rebuild = true;
        }
    }

    @WrapOperation(method = "drawLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;collectGameInformationText()Ljava/util/List;"))
    public List<String> sodiumExtra$redirectDrawLeftText(DebugHud instance, Operation<List<String>> original) {
        if (this.rebuild) {
            this.leftTextCache.clear();
            this.leftTextCache.addAll(original.call(instance));
        }
        return this.leftTextCache;
    }

    @WrapOperation(method = "drawRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;collectSystemInformationText()Ljava/util/List;"))
    public List<String> sodiumExtra$redirectDrawRightText(DebugHud instance, Operation<List<String>> original) {
        if (this.rebuild) {
            this.rightTextCache.clear();
            this.rightTextCache.addAll(original.call(instance));
        }
        return this.rightTextCache;
    }
}
