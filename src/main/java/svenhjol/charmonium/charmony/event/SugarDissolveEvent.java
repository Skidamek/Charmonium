package svenhjol.charmonium.charmony.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import svenhjol.charmonium.charmony.enums.EventResult;

@SuppressWarnings("unused")
public class SugarDissolveEvent extends CharmEvent<SugarDissolveEvent.Handler> {
    public static final SugarDissolveEvent INSTANCE = new SugarDissolveEvent();

    private SugarDissolveEvent() {}

    public EventResult invoke(Level level, BlockPos pos) {
        for (Handler handler : getHandlers()) {
            var result = handler.run(level, pos);

            // If true, cancel other events.
            if (result == EventResult.CANCEL) {
                return result;
            }
        }

        return EventResult.PASS;
    }

    @FunctionalInterface
    public interface Handler {
        EventResult run(Level level, BlockPos pos);
    }
}
