package me.flashyreese.mods.sodiumextra.client;

import me.flashyreese.mods.sodiumextra.common.util.EvictingQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.Queue;

public class ClientTickHandler {
    private final Queue<Integer> averageFps = new EvictingQueue<>(200);

    public void onClientInitialize() {
        MinecraftForge.EVENT_BUS.addListener(this::onTick);
    }

    public void onTick(TickEvent.ClientTickEvent event) {
        this.averageFps.add(MinecraftClient.getInstance().currentFps);
    }

    public int getAverageFps() {
        int actualAverageFPS = 0;
        for (int fps : this.averageFps) {
            actualAverageFPS += fps;
        }
        return actualAverageFPS / this.averageFps.size();
    }

    public int getLowestFps() {
        int temp = -1;
        for (int fps : this.averageFps) {
            if (temp == -1 || fps < temp) {
                temp = fps;
            }
        }
        return temp;
    }

    public int getHighestFps() {
        int temp = -1;
        for (int fps : this.averageFps) {
            if (temp == -1 || fps > temp) {
                temp = fps;
            }
        }
        return temp;
    }
}
