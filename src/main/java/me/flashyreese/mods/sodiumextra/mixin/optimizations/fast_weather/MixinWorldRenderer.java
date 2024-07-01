package me.flashyreese.mods.sodiumextra.mixin.optimizations.fast_weather;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import me.flashyreese.mods.sodiumextra.client.render.vertex.formats.WeatherVertex;
import me.flashyreese.mods.sodiumextra.common.util.Utils;
import net.caffeinemc.mods.sodium.api.util.ColorABGR;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.render.WorldRenderer.getLightmapCoordinates;

@Mixin(value = WorldRenderer.class, priority = 1500)
public class MixinWorldRenderer {
    @Shadow
    @Final
    private static Identifier RAIN;
    @Shadow
    @Final
    private static Identifier SNOW;
    @Unique
    private final Long2ReferenceMap<Biome> biomeLong2ReferenceMap = new Long2ReferenceOpenHashMap<>();
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int ticks;
    @Shadow
    @Final
    private float[] NORMAL_LINE_DX;
    @Shadow
    @Final
    private float[] NORMAL_LINE_DZ;


    @Inject(method = "renderWeather", at = @At(value = "HEAD"), cancellable = true)
    public void sodiumExtra$renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        assert this.client.world != null;
        float rainGradient = this.client.world.getRainGradient(tickDelta);
        if (rainGradient > 0.0F) {
            manager.enable();
            World world = this.client.world;
            int cameraPosX = MathHelper.floor(cameraX);
            int cameraPosY = MathHelper.floor(cameraY);
            int cameraPosZ = MathHelper.floor(cameraZ);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = null;
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            int range = MinecraftClient.isFancyGraphicsOrBetter() ? 10 : 5;

            RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
            int precipitationType = -1;
            float time = (float) this.ticks + tickDelta;
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (int z = cameraPosZ - range; z <= cameraPosZ + range; ++z) {
                for (int x = cameraPosX - range; x <= cameraPosX + range; ++x) {
                    int positionIndex = (z - cameraPosZ + 16) * 32 + x - cameraPosX + 16;
                    double offsetX = (double) this.NORMAL_LINE_DX[positionIndex] * 0.5;
                    double offsetZ = (double) this.NORMAL_LINE_DZ[positionIndex] * 0.5;
                    mutable.set(x, cameraY, z);

                    long biomePacked = Utils.packPosition(x, z);
                    Biome biome = this.biomeLong2ReferenceMap.computeIfAbsent(biomePacked, key -> world.getBiome(mutable).value());
                    if (biome.hasPrecipitation()) {
                        int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z);
                        int minY = cameraPosY - range;
                        int maxY = cameraPosY + range;
                        if (minY < topY) {
                            minY = topY;
                        }

                        if (maxY < topY) {
                            maxY = topY;
                        }

                        int adjustedTopY = Math.max(topY, cameraPosY);

                        if (minY != maxY) {
                            Random random = Random.create((long) x * x * 3121 + x * 45238971L ^ (long) z * z * 418711 + z * 13761L);
                            mutable.set(x, minY, z);
                            Biome.Precipitation precipitation = biome.getPrecipitation(mutable);
                            if (precipitation == Biome.Precipitation.RAIN) {
                                if (precipitationType != 0) {
                                    if (precipitationType >= 0) {
                                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                                    }

                                    precipitationType = 0;
                                    RenderSystem.setShaderTexture(0, RAIN);
                                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                                }

                                int ticksModulus = this.ticks & 131071;
                                int randomOffset = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
                                float dropLength = 3.0F + random.nextFloat();
                                float dropTimeOffset = -((float) (ticksModulus + randomOffset) + tickDelta) / 32.0F * dropLength;
                                float dropTextureOffset = dropTimeOffset % 32.0F;

                                double relativeX = (double) x + 0.5 - cameraX;
                                double relativeZ = (double) z + 0.5 - cameraZ;
                                float distance = (float) Math.sqrt(relativeX * relativeX + relativeZ * relativeZ) / (float) range;
                                float alpha = ((1.0F - distance * distance) * 0.5F + 0.5F) * rainGradient;
                                mutable.set(x, adjustedTopY, z);
                                int color = ColorABGR.pack(1.0F, 1.0F, 1.0F, alpha);
                                int light = getLightmapCoordinates(world, mutable);

                                VertexBufferWriter writer = VertexBufferWriter.of(bufferBuilder);
                                write(writer, cameraX, cameraY, cameraZ, x, z, offsetX, offsetZ, minY, maxY, 0, dropTextureOffset, color, light);
                            } else if (precipitation == Biome.Precipitation.SNOW) {
                                if (precipitationType != 1) {
                                    if (precipitationType >= 0) {
                                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                                    }

                                    precipitationType = 1;
                                    RenderSystem.setShaderTexture(0, SNOW);
                                    bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                                }

                                float snowFallSpeed = -((float) (this.ticks & 511) + tickDelta) / 512.0F;
                                float snowTextureOffsetX = (float) (random.nextDouble() + (double) time * 0.01 * (double) ((float) random.nextGaussian()));
                                float snowTextureOffsetY = (float) (random.nextDouble() + (double) (time * (float) random.nextGaussian()) * 0.001);

                                double relativeX = (double) x + 0.5 - cameraX;
                                double relativeZ = (double) z + 0.5 - cameraZ;
                                float distance = (float) Math.sqrt(relativeX * relativeX + relativeZ * relativeZ) / (float) range;
                                float alpha = ((1.0F - distance * distance) * 0.3F + 0.5F) * rainGradient;

                                mutable.set(x, adjustedTopY, z);
                                int light = getLightmapCoordinates(world, mutable);
                                int blockLight = light >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295);
                                int skyLight = light & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 65295);
                                int adjustedBlockLight = (blockLight * 3 + LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE) / 4;
                                int adjustedSkyLight = (skyLight * 3 + LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE) / 4;

                                VertexBufferWriter writer = VertexBufferWriter.of(bufferBuilder);
                                int color = ColorABGR.pack(1.0F, 1.0F, 1.0F, alpha);
                                int packedLight = Utils.packLight(adjustedSkyLight, adjustedBlockLight);
                                write(writer, cameraX, cameraY, cameraZ, x, z, offsetX, offsetZ, minY, maxY, snowTextureOffsetX, snowFallSpeed + snowTextureOffsetY, color, packedLight);
                            }
                        }
                    }
                }
            }

            if (precipitationType >= 0) {
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            manager.disable();
        }
        ci.cancel();
    }

    @Inject(method = "reload()V", at = @At(value = "TAIL"))
    private void postReload(CallbackInfo ci) {
        this.biomeLong2ReferenceMap.clear();
    }

    @Unique
    private void write(VertexBufferWriter writer, double cameraX, double cameraY, double cameraZ, int x, int z, double offsetX, double offsetZ, int minY, int maxY, float textureOffsetX, float textureOffsetY, int color, int light) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long buffer = stack.nmalloc(4 * WeatherVertex.STRIDE);
            long ptr = buffer;

            WeatherVertex.put(ptr,
                    (float) ((double) x - cameraX - offsetX + 0.5), (float) ((double) maxY - cameraY), (float) ((double) z - cameraZ - offsetZ + 0.5),
                    0.0F + textureOffsetX, (float) minY * 0.25F + textureOffsetY,
                    color,
                    light
            );
            ptr += WeatherVertex.STRIDE;

            WeatherVertex.put(ptr,
                    (float) ((double) x - cameraX + offsetX + 0.5), (float) ((double) maxY - cameraY), (float) ((double) z - cameraZ + offsetZ + 0.5),
                    1.0F + textureOffsetX, (float) minY * 0.25F + textureOffsetY,
                    color,
                    light
            );
            ptr += WeatherVertex.STRIDE;

            WeatherVertex.put(ptr,
                    (float) ((double) x - cameraX + offsetX + 0.5), (float) ((double) minY - cameraY), (float) ((double) z - cameraZ + offsetZ + 0.5),
                    1.0F + textureOffsetX, (float) maxY * 0.25F + textureOffsetY,
                    color,
                    light
            );
            ptr += WeatherVertex.STRIDE;

            WeatherVertex.put(ptr,
                    (float) ((double) x - cameraX - offsetX + 0.5), (float) ((double) minY - cameraY), (float) ((double) z - cameraZ - offsetZ + 0.5),
                    0.0F + textureOffsetX, (float) maxY * 0.25F + textureOffsetY,
                    color,
                    light
            );
            ptr += WeatherVertex.STRIDE;

            writer.push(stack, buffer, 4, WeatherVertex.FORMAT);
        }
    }
}
