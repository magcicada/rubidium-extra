package me.flashyreese.mods.sodiumextra.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.flashyreese.mods.sodiumextra.EmbeddiumExtraMod;
import me.flashyreese.mods.sodiumextra.client.ClientTickHandler;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import me.flashyreese.mods.sodiumextra.mixin.gui.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = EmbeddiumExtraMod.MOD_ID)
public class SodiumExtraHud {

    private static final List<Text> TEXT_LIST = new ObjectArrayList<>();

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @SubscribeEvent
    public static void onStartTick(TickEvent.ClientTickEvent client) {
        if (client.phase == TickEvent.Phase.END) {
            return;
        }

        // Clear the textList to start fresh (this might not be ideal but hey it's still better than whatever the fuck debug hud is doing)
        TEXT_LIST.clear();
        if (SodiumExtraClientMod.options().extraSettings.showFps) {
            int currentFPS = MinecraftClientAccessor.getCurrentFPS();

            Text text = Text.translatable("sodium-extra.overlay.fps", currentFPS);

            if (SodiumExtraClientMod.options().extraSettings.showFPSExtended)
                text = Text.literal(String.format("%s %s", text.getString(), Text.translatable("sodium-extra.overlay.fps_extended", ClientTickHandler.getHighestFps(), ClientTickHandler.getAverageFps(),
                        ClientTickHandler.getLowestFps()).getString()));

            TEXT_LIST.add(text);
        }

        if (SodiumExtraClientMod.options().extraSettings.showCoords && !CLIENT.hasReducedDebugInfo() && CLIENT.player != null) {
            Vec3d pos = CLIENT.player.getPos();

            Text text = Text.translatable("sodium-extra.overlay.coordinates", String.format("%.2f", pos.x), String.format("%.2f", pos.y), String.format("%.2f", pos.z));
            TEXT_LIST.add(text);
        }

        if (!SodiumExtraClientMod.options().renderSettings.lightUpdates) {
            Text text = Text.translatable("sodium-extra.overlay.light_updates");
            TEXT_LIST.add(text);
        }
    }
    @SubscribeEvent
    public static void onHudRender(RenderGuiEvent.Post event) {
        if (!CLIENT.options.debugEnabled) {
            SodiumExtraGameOptions.OverlayCorner overlayCorner = SodiumExtraClientMod.options().extraSettings.overlayCorner;
            // Calculate starting position based on the overlay corner
            int x;
            int y = overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT ?
                    CLIENT.getWindow().getScaledHeight() - CLIENT.textRenderer.fontHeight - 2 : 2;
            // Render each text in the list
            for (Text text : TEXT_LIST) {
                if (overlayCorner == SodiumExtraGameOptions.OverlayCorner.TOP_RIGHT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
                    x = CLIENT.getWindow().getScaledWidth() - CLIENT.textRenderer.getWidth(text) - 2;
                } else {
                    x = 2;
                }
                drawString(event.getGuiGraphics(), text, x, y);
                if (overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_LEFT || overlayCorner == SodiumExtraGameOptions.OverlayCorner.BOTTOM_RIGHT) {
                    y -= CLIENT.textRenderer.fontHeight + 2;
                } else {
                    y += CLIENT.textRenderer.fontHeight + 2; // Increase the y-position for the next text
                }
            }
        }
    }

    private static void drawString(DrawContext drawContext, Text text, int x, int y) {
        int textColor = 0xffffffff; // Default text color

        if (SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.BACKGROUND) {
            drawContext.fill(x - 1, y - 1, x + CLIENT.textRenderer.getWidth(text) + 1, y + CLIENT.textRenderer.fontHeight + 1, -1873784752);
        }

        drawContext.drawText(CLIENT.textRenderer, text, x, y, textColor, SodiumExtraClientMod.options().extraSettings.textContrast == SodiumExtraGameOptions.TextContrast.SHADOW);
    }
}
