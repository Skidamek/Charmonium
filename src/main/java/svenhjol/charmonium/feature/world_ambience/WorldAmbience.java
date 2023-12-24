package svenhjol.charmonium.feature.world_ambience;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import svenhjol.charmonium.feature.world_ambience.sounds.*;
import svenhjol.charmonium.sound.ISoundType;
import svenhjol.charmonium.sound.SoundHandler;
import svenhjol.charmonium.sound.WorldSound;
import svenhjol.charmony.annotation.Configurable;
import svenhjol.charmony.client.ClientFeature;
import svenhjol.charmony.api.event.ClientEntityJoinEvent;
import svenhjol.charmony.api.event.ClientEntityLeaveEvent;
import svenhjol.charmony.api.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

public class WorldAmbience extends ClientFeature {
    public static final List<ResourceLocation> VALID_CAVE_DIMENSIONS = new ArrayList<>();
    private static final ISoundType<WorldSound> ALIEN = new Alien();
    private static final ISoundType<WorldSound> BLEAK = new Bleak();
    private static final ISoundType<WorldSound> CAVE_DRONE = new CaveDrone();
    private static final ISoundType<WorldSound> CAVE_DEPTH = new CaveDepth();
    private static final ISoundType<WorldSound> DEEPSLATE = new Deepslate();
    private static final ISoundType<WorldSound> DRY = new Dry();
    private static final ISoundType<WorldSound> GEODE = new Geode();
    private static final ISoundType<WorldSound> GRAVEL = new Gravel();
    private static final ISoundType<WorldSound> HIGH = new High();
    private static final ISoundType<WorldSound> MANSION = new Mansion();
    private static final ISoundType<WorldSound> MINESHAFT = new Mineshaft();
    private static final ISoundType<WorldSound> NIGHT_PLAINS = new NightPlains();
    private static final ISoundType<WorldSound> SNOWSTORM = new Snowstorm();
    private static final ISoundType<WorldSound> UNDERGROUND_WATER = new UndergroundWater();
    private static final ISoundType<WorldSound> VILLAGE = new Village();
    private Handler handler;

    @Configurable(name = "Volume scaling", description = "Affects the volume of all situational ambient sounds. 1.0 is full volume.", requireRestart = false)
    public static double volumeScaling = 0.55D;

    @Configurable(name = "Above ground for ambience silencing", description = "Number of blocks above the ground that biome ambience will be silenced.\n" +
        "Set to zero to disable.", requireRestart = false)
    public static int cullSoundAboveGround = 32;

    @Configurable(name = "Alien", description = "If true, plays ambient sounds while anywhere in the End.", requireRestart = false)
    public static boolean alien = true;

    @Configurable(name = "Bleak", description = "If true, plays ambient sounds in cold and/or barren overworld environments.", requireRestart = false)
    public static boolean bleak = true;

    @Configurable(name = "Cave depth", description = "If true, plays more intense cave sounds when below Y 0 and light level is lower than the cave light level.", requireRestart = false)
    public static boolean caveDepth = true;

    @Configurable(name = "Cave drone", description = "If true, plays a low drone sound when in a cave below the cave drone cutoff..", requireRestart = false)
    public static boolean caveDrone = true;

    @Configurable(name = "Cave drone cutoff", description = "Height at which the cave drone will be silenced.", requireRestart = false)
    public static int caveDroneCutoff = 48;

    @Configurable(name = "Cave light level", description = "Light level at which cave ambience will be dampened.", requireRestart = false)
    public static int caveLightLevel = 10;

    @Configurable(name = "Deepslate", description = "If true, plays ambient sounds when the player is underground and near deepslate blocks.", requireRestart = false)
    public static boolean deepslate = true;

    @Configurable(name = "Dry", description = "If true, plays ambient sounds in dry and/or hot overworld environments.", requireRestart = false)
    public static boolean dry = true;

    @Configurable(name = "Geode", description = "If true, plays ambient sounds from a nearby amethyst geode.", requireRestart = false)
    public static boolean geode = true;

    @Configurable(name = "Gravel", description = "If true, plays ambient sounds when the player is underground and near gravel blocks.", requireRestart = false)
    public static boolean gravel = true;

    @Configurable(name = "High", description = "If true, plays ambient sounds when high up in the overworld.", requireRestart = false)
    public static boolean high = true;

    @Configurable(name = "Mansion", description = "If true, plays ambient sounds while inside a woodland mansion.", requireRestart = false)
    public static boolean mansion = true;

    @Configurable(name = "Mineshaft", description = "If true, plays ambient sounds from a nearby mineshaft.", requireRestart = false)
    public static boolean mineshaft = true;

    @Configurable(name = "Night plains", description = "If true, plays ambient sounds in plains environments at night.", requireRestart = false)
    public static boolean nightPlains = true;

    @Configurable(name = "Snowstorm", description = "If true, plays ambient sounds when in a cold biome during a thunderstorm.", requireRestart = false)
    public static boolean snowstorm = true;

    @Configurable(name = "Underground water", description = "If true, plays water sounds from a nearby water source when underground.", requireRestart = false)
    public static boolean undergroundWater = true;

    @Configurable(name = "Village", description = "If true, plays ambient sounds when a player is inside a village.", requireRestart = false)
    public static boolean village = true;

    @Configurable(name = "Valid cave ambience dimensions", description = "Dimensions in which cave ambience (drone and depth) will be played.", requireRestart = false)
    public static List<String> caveDimensions = List.of(
        "minecraft:overworld"
    );

    @Override
    public String description() {
        return "Plays ambient sound according to features of the world around the player.";
    }

    @Override
    public void runWhenEnabled() {
        ClientEntityJoinEvent.INSTANCE.handle(this::handleClientEntityJoin);
        ClientEntityLeaveEvent.INSTANCE.handle(this::handleClientEntityLeave);
        ClientTickEvent.INSTANCE.handle(this::handleClientTick);

        caveDimensions.forEach(dim -> VALID_CAVE_DIMENSIONS.add(new ResourceLocation(dim)));
    }

    public static int getCaveLightLevel() {
        return Math.min(15, Math.max(0, caveLightLevel));
    }

    public static int getCaveDroneCutoff() {
        return Math.min(256, Math.max(-64, caveDroneCutoff));
    }

    /**
     * Can be called by other mods to add cave ambience to a custom dimension at runtime.
     */
    @SuppressWarnings("unused")
    public static void addCaveAmbienceToDimension(Level level) {
        var dimension = level.dimension().location();
        if (!VALID_CAVE_DIMENSIONS.contains(dimension)) {
            VALID_CAVE_DIMENSIONS.add(dimension);
        }
    }

    private void handleClientTick(Minecraft client) {
        if (handler != null && !client.isPaused()) {
            handler.tick();
        }
    }

    private void handleClientEntityLeave(Entity entity, Level level) {
        if (entity instanceof LocalPlayer && handler != null) {
            handler.stop();
        }
    }

    private void handleClientEntityJoin(Entity entity, Level level) {
        if (entity instanceof LocalPlayer player) {
            trySetupSoundHandler(player);
        }
    }

    private void trySetupSoundHandler(Player player) {
        if (!(player instanceof LocalPlayer)) return;

        handler = new Handler(player);
        handler.updatePlayer(player);
    }

    public static class Handler extends SoundHandler<WorldSound> {
        public Handler(@NotNull Player player) {
            super(player);

            ALIEN.addSounds(this);
            BLEAK.addSounds(this);
            CAVE_DRONE.addSounds(this);
            CAVE_DEPTH.addSounds(this);
            DEEPSLATE.addSounds(this);
            DRY.addSounds(this);
            GEODE.addSounds(this);
            GRAVEL.addSounds(this);
            HIGH.addSounds(this);
            MANSION.addSounds(this);
            MINESHAFT.addSounds(this);
            NIGHT_PLAINS.addSounds(this);
            SNOWSTORM.addSounds(this);
            UNDERGROUND_WATER.addSounds(this);
            VILLAGE.addSounds(this);
        }
    }
}
