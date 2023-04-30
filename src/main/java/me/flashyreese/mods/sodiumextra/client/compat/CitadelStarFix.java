package me.flashyreese.mods.sodiumextra.client.compat;

import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CitadelStarFix {
    @SubscribeEvent
    public void onGetStarBrightness(EventGetStarBrightness event) {
        if (!SodiumExtraClientMod.options().detailSettings.stars) {
            event.setBrightness(0.0f);
            event.setResult(Event.Result.ALLOW);
        }
    }
}
