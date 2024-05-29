package svenhjol.charmonium.charmony.event;

import net.minecraft.world.InteractionResult;

@SuppressWarnings("unused")
public class MouseScrollEvent {
    public static final OnScreenEvent ON_SCREEN = new OnScreenEvent();
    public static final OffScreenEvent OFF_SCREEN = new OffScreenEvent();

    public static class OnScreenEvent extends CharmEvent<Handler> {
        private OnScreenEvent() {}

        public InteractionResult invoke(double direction) {
            for (var handler : getHandlers()) {
                var result = handler.run(direction);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            return InteractionResult.PASS;
        }
    }

    public static class OffScreenEvent extends CharmEvent<Handler> {
        private OffScreenEvent() {}

        public InteractionResult invoke(double direction) {
            for (var handler : getHandlers()) {
                var result = handler.run(direction);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            return InteractionResult.PASS;
        }
    }

    @FunctionalInterface
    public interface Handler {
        InteractionResult run(double direction);
    }
}
