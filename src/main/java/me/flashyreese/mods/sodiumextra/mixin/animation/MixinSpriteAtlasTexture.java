package me.flashyreese.mods.sodiumextra.mixin.animation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(SpriteAtlasTexture.class)
public abstract class MixinSpriteAtlasTexture extends AbstractTexture {
    @Unique
    private final Map<Supplier<Boolean>, List<Identifier>> animatedSprites = Map.of(
            () -> SodiumExtraClientMod.options().animationSettings.water, List.of(
                    Identifier.of("minecraft", "block/water_still"),
                    Identifier.of("minecraft", "block/water_flow")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.lava, List.of(
                    Identifier.of("minecraft", "block/lava_still"),
                    Identifier.of("minecraft", "block/lava_flow")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.portal, List.of(
                    Identifier.of("minecraft", "block/nether_portal")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.fire, List.of(
                    Identifier.of("minecraft", "block/fire_0"),
                    Identifier.of("minecraft", "block/fire_1"),
                    Identifier.of("minecraft", "block/soul_fire_0"),
                    Identifier.of("minecraft", "block/soul_fire_1"),
                    Identifier.of("minecraft", "block/campfire_fire"),
                    Identifier.of("minecraft", "block/campfire_log_lit"),
                    Identifier.of("minecraft", "block/soul_campfire_fire"),
                    Identifier.of("minecraft", "block/soul_campfire_log_lit")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.blockAnimations, List.of(
                    Identifier.of("minecraft", "block/magma"),
                    Identifier.of("minecraft", "block/lantern"),
                    Identifier.of("minecraft", "block/sea_lantern"),
                    Identifier.of("minecraft", "block/soul_lantern"),
                    Identifier.of("minecraft", "block/kelp"),
                    Identifier.of("minecraft", "block/kelp_plant"),
                    Identifier.of("minecraft", "block/seagrass"),
                    Identifier.of("minecraft", "block/tall_seagrass_top"),
                    Identifier.of("minecraft", "block/tall_seagrass_bottom"),
                    Identifier.of("minecraft", "block/warped_stem"),
                    Identifier.of("minecraft", "block/crimson_stem"),
                    Identifier.of("minecraft", "block/blast_furnace_front_on"),
                    Identifier.of("minecraft", "block/smoker_front_on"),
                    Identifier.of("minecraft", "block/stonecutter_saw"),
                    Identifier.of("minecraft", "block/prismarine"),
                    Identifier.of("minecraft", "block/respawn_anchor_top"),
                    Identifier.of("minecraft", "entity/conduit/wind"),
                    Identifier.of("minecraft", "entity/conduit/wind_vertical")
            ),
            () -> SodiumExtraClientMod.options().animationSettings.sculkSensor, List.of(
                    Identifier.of("minecraft", "block/sculk"),
                    Identifier.of("minecraft", "block/sculk_catalyst_top_bloom"),
                    Identifier.of("minecraft", "block/sculk_catalyst_side_bloom"),
                    Identifier.of("minecraft", "block/sculk_shrieker_inner_top"),
                    Identifier.of("minecraft", "block/sculk_vein"),
                    Identifier.of("minecraft", "block/sculk_shrieker_can_summon_inner_top"),
                    Identifier.of("minecraft", "block/sculk_sensor_tendril_inactive"),
                    Identifier.of("minecraft", "block/sculk_sensor_tendril_active"),
                    Identifier.of("minecraft", "vibration")
            )
    );

    @WrapOperation(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;createAnimation()Lnet/minecraft/client/texture/Sprite$TickableAnimation;"))
    public Sprite.TickableAnimation sodiumExtra$tickAnimatedSprites(Sprite instance, Operation<Sprite.TickableAnimation> original) {
        Sprite.TickableAnimation tickableAnimation = original.call(instance);
        if (tickableAnimation != null && SodiumExtraClientMod.options().animationSettings.animation && this.shouldAnimate(instance.getContents().getId()))
            return tickableAnimation;
        return null;
    }

    @Unique
    private boolean shouldAnimate(Identifier identifier) {
        if (identifier != null) {
            for (Map.Entry<Supplier<Boolean>, List<Identifier>> supplierListEntry : this.animatedSprites.entrySet()) {
                if (supplierListEntry.getValue().contains(identifier)) {
                    return supplierListEntry.getKey().get();
                }
            }
        }
        return true;
    }
}
