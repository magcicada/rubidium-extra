package me.flashyreese.mods.sodiumextra.client.gui;

import me.flashyreese.mods.sodiumextra.SodiumExtraMod;
import me.flashyreese.mods.sodiumextra.client.ClientTickHandler;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.mixin.gui.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = SodiumExtraMod.MOD_ID)
public class HudRenderImpl {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @SubscribeEvent
    public static void onRenderGameOverlayEvent(final RenderGuiEvent.Post event) {
        onHudRender(event.getPoseStack(), event.getPartialTick());
    }

    public static void onHudRender(MatrixStack matrixStack, float tickDelta) {
        if (!CLIENT.options.debugEnabled) {
            //Gotta love hardcoding
            if (SodiumExtraClientMod.options().extraSettings.showFps && SodiumExtraClientMod.options().extraSettings.showCoords) {
                renderFPS(matrixStack);
                renderCoords(matrixStack);
            } else if (SodiumExtraClientMod.options().extraSettings.showFps) {
                renderFPS(matrixStack);
            } else if (SodiumExtraClientMod.options().extraSettings.showCoords) {
                renderCoords(matrixStack);
            }
            if (!SodiumExtraClientMod.options().renderSettings.lightUpdates) {
                renderLightUpdatesWarning(matrixStack);
            }
        }
    }

    //Should I make this OOP or just leave as it :> I don't think I will be adding any more than these 2.
    private static void renderFPS(MatrixStack matrices) {
        int currentFPS = MinecraftClientAccessor.getCurrentFPS();

        Text text = Text.translatable("sodium-extra.overlay.fps", currentFPS);

        if (SodiumExtraClientMod.options().extraSettings.showFPSExtended)
            text = Text.literal(String.format("%s %s", text.getString(), Text.translatable("sodium-extra.overlay.fps_extended", ClientTickHandler.getHighestFps(), ClientTickHandler.getAverageFps(),
                    ClientTickHandler.getLowestFps()).getString()));

        int x, y;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT -> {
                x = 2;
                y = 2;
            }
            case TOP_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 2;
            }
            case BOTTOM_LEFT -> {
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 2;
            }
            case BOTTOM_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 2;
            }
            default ->
                    throw new IllegalStateException("Unexpected value: " + SodiumExtraClientMod.options().extraSettings.overlayCorner);
        }

        drawString(matrices, text, x, y);
    }

    private static void renderCoords(MatrixStack matrices) {
        if (CLIENT.player == null) return;
        if (CLIENT.hasReducedDebugInfo()) return;
        Vec3d pos = CLIENT.player.getPos();

        Text text = Text.translatable("sodium-extra.overlay.coordinates", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z));

        int x, y;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT -> {
                x = 2;
                y = 12;
            }
            case TOP_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 12;
            }
            case BOTTOM_LEFT -> {
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 12;
            }
            case BOTTOM_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 12;
            }
            default ->
                    throw new IllegalStateException("Unexpected value: " + SodiumExtraClientMod.options().extraSettings.overlayCorner);
        }

        drawString(matrices, text, x, y);
    }

    private static void renderLightUpdatesWarning(MatrixStack matrices) {
        Text text = Text.translatable("sodium-extra.overlay.light_updates");

        int x, y;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT -> {
                x = 2;
                y = 22;
            }
            case TOP_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 22;
            }
            case BOTTOM_LEFT -> {
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 22;
            }
            case BOTTOM_RIGHT -> {
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 22;
            }
            default ->
                    throw new IllegalStateException("Unexpected value: " + SodiumExtraClientMod.options().extraSettings.overlayCorner);
        }

        drawString(matrices, text, x, y);
    }

    private static void drawString(MatrixStack matrices, Text text, int x, int y) {
        switch (SodiumExtraClientMod.options().extraSettings.textContrast) {
            case NONE -> CLIENT.textRenderer.draw(matrices, text, x, y, 0xffffffff);
            case BACKGROUND -> {
                DrawableHelper.fill(matrices, x - 1, y - 1, x + CLIENT.textRenderer.getWidth(text) + 1, y + CLIENT.textRenderer.fontHeight, -1873784752);
                CLIENT.textRenderer.draw(matrices, text, x, y, 0xffffffff);
            }
            case SHADOW -> CLIENT.textRenderer.drawWithShadow(matrices, text, x, y, 0xffffffff);
        }
    }
}
