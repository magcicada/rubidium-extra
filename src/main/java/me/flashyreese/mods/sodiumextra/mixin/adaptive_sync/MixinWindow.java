package me.flashyreese.mods.sodiumextra.mixin.adaptive_sync;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Window.class)
public class MixinWindow {
    @WrapOperation(method = "setVsync", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapInterval(I)V", remap = false))
    private void setSwapInterval(int interval, Operation<Void> original) {
        if (SodiumExtraClientMod.options().extraSettings.useAdaptiveSync) {
            if (GLFW.glfwExtensionSupported("GLX_EXT_swap_control_tear") || GLFW.glfwExtensionSupported("WGL_EXT_swap_control_tear")) {
                original.call(-1);
            } else {
                SodiumExtraClientMod.LOGGER.warn("Adaptive vsync not supported, falling back to vanilla vsync state!");
                SodiumExtraClientMod.options().extraSettings.useAdaptiveSync = false;
                SodiumExtraClientMod.options().writeChanges();
                original.call(interval);
            }
        } else {
            original.call(interval);
        }
    }
}