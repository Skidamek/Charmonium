package svenhjol.charmonium.charmony.event;

import net.minecraft.world.entity.player.Player;

@SuppressWarnings("unused")
public class PlayerLoginEvent extends CharmEvent<PlayerLoginEvent.Handler> {
    public static final PlayerLoginEvent INSTANCE = new PlayerLoginEvent();

    private PlayerLoginEvent() {}

    public void invoke(Player player) {
        for (var handler : getHandlers()) {
            handler.run(player);
        }
    }

    @FunctionalInterface
    public interface Handler {
        void run(Player player);
    }
}
