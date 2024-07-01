package me.flashyreese.mods.sodiumextra.client;

import com.google.common.collect.EvictingQueue;
import me.flashyreese.mods.sodiumextra.mixin.gui.MinecraftClientAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Queue;
import java.util.stream.IntStream;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = SodiumExtraClientMod.MOD_ID)
public class ClientTickHandler {
    private static int averageFps, lowestFps, highestFps;
    private static final Queue<Integer> fpsQueue = EvictingQueue.create(200);

    @SubscribeEvent
    public static void onTick(final ClientTickEvent.Post event) {
        final int currentFPS = MinecraftClientAccessor.getCurrentFPS();
        fpsQueue.add(currentFPS);

        final int[] fpsArray = fpsQueue.stream().mapToInt(Integer::intValue).toArray();
        averageFps = (int) IntStream.of(fpsArray).average().orElse(0);
        lowestFps = IntStream.of(fpsArray).min().orElse(0);
        highestFps = IntStream.of(fpsArray).max().orElse(0);
    }

    public static int getAverageFps() {
        return averageFps;
    }

    public static int getLowestFps() {
        return lowestFps;
    }

    public static int getHighestFps() {
        return highestFps;
    }
}
