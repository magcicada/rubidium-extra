package me.flashyreese.mods.sodiumextra.mixin.sodium.embeddium;

import org.embeddedt.embeddium.api.options.OptionIdentifier;
import org.embeddedt.embeddium.api.options.structure.OptionGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OptionGroup.Builder.class, remap = false)
public abstract class OptionGroupMixin {
    @Shadow
    private OptionIdentifier<Void> id;
    @Shadow
    public abstract OptionGroup.Builder setId(OptionIdentifier<Void> id);

    @Inject(
            method = "build",
            at = @At(
                    value = "HEAD"
            )
    )
    public void embeddiumextra$fixShit(CallbackInfoReturnable<OptionGroup> cir) {
        if (this.id == null) {
            setId(OptionIdentifier.EMPTY);
        }
    }
}
