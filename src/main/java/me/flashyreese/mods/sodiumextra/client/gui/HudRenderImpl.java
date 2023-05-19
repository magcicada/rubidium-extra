package me.flashyreese.mods.sodiumextra.client.gui;

import me.flashyreese.mods.sodiumextra.SodiumExtraMod;
import me.flashyreese.mods.sodiumextra.client.ClientTickHandler;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.mixin.gui.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = SodiumExtraMod.MOD_ID)
public class HudRenderImpl {
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @SubscribeEvent
    public static void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Post event) {
        onHudRender(event.getMatrixStack(), event.getPartialTicks());
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

        Text text = new TranslatableText("sodium-extra.overlay.fps", currentFPS);

        if (SodiumExtraClientMod.options().extraSettings.showFPSExtended)
            text = new LiteralText(String.format("%s %s", text.getString(), new TranslatableText("sodium-extra.overlay.fps_extended", ClientTickHandler.getHighestFps(), ClientTickHandler.getAverageFps(),
                    ClientTickHandler.getLowestFps()).getString()));

        int x = 0, y = 0;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT:
                x = 2;
                y = 2;
                break;
            case TOP_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 2;
                break;
            case BOTTOM_LEFT:
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 2;
                break;
            case BOTTOM_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 2;
                break;
        }

        drawString(matrices, text, x, y);
    }

    private static void renderCoords(MatrixStack matrices) {
        if (CLIENT.player == null) return;
        if (CLIENT.hasReducedDebugInfo()) return;
        Vec3d pos = CLIENT.player.getPos();

        Text text = new TranslatableText("sodium-extra.overlay.coordinates", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z));

        int x = 0, y = 0;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT:
                x = 2;
                y = 12;
                break;
            case TOP_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 12;
                break;
            case BOTTOM_LEFT:
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 12;
                break;
            case BOTTOM_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 12;
                break;
        }

        drawString(matrices, text, x, y);
    }

    private static void renderLightUpdatesWarning(MatrixStack matrices) {
        Text text = new TranslatableText("sodium-extra.overlay.light_updates");

        int x = 0, y = 0;
        switch (SodiumExtraClientMod.options().extraSettings.overlayCorner) {
            case TOP_LEFT:
                x = 2;
                y = 22;
                break;
            case TOP_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = 22;
                break;
            case BOTTOM_LEFT:
                x = 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 22;
                break;
            case BOTTOM_RIGHT:
                x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                y = CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 22;
                break;
        }

        drawString(matrices, text, x, y);
    }

    private static void drawString(MatrixStack matrices, Text text, int x, int y) {
        switch (SodiumExtraClientMod.options().extraSettings.textContrast) {
            case NONE: CLIENT.textRenderer.draw(matrices, text, x, y, 0xffffffff);
            case BACKGROUND: {
                DrawableHelper.fill(matrices, x - 1, y - 1, x + CLIENT.textRenderer.getWidth(text) + 1, y + CLIENT.textRenderer.fontHeight, -1873784752);
                CLIENT.textRenderer.draw(matrices, text, x, y, 0xffffffff);
            }
            case SHADOW: CLIENT.textRenderer.drawWithShadow(matrices, text, x, y, 0xffffffff);
        }
    }
}
