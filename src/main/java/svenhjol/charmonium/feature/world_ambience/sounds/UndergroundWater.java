 package svenhjol.charmonium.feature.world_ambience.sounds;

 import net.minecraft.core.BlockPos;
 import net.minecraft.resources.ResourceLocation;
 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.level.block.Block;
 import net.minecraft.world.level.block.Blocks;
 import org.jetbrains.annotations.Nullable;
 import svenhjol.charmonium.Charmonium;
 import svenhjol.charmonium.charmony.feature.FeatureResolver;
 import svenhjol.charmonium.charmony.helper.WorldHelper;
 import svenhjol.charmonium.feature.world_ambience.WorldAmbience;
 import svenhjol.charmonium.sound.ISoundType;
 import svenhjol.charmonium.feature.world_ambience.client.RepeatedWorldSound;
 import svenhjol.charmonium.sound.SoundHandler;
 import svenhjol.charmonium.feature.world_ambience.client.WorldSound;

 import java.util.Optional;

 public class UndergroundWater implements ISoundType<WorldSound>, FeatureResolver<WorldAmbience> {
     public static SoundEvent SOUND;

     public UndergroundWater() {
         SOUND = SoundEvent.createVariableRangeEvent(new ResourceLocation(Charmonium.ID, "world.underground_water"));
     }

     public void addSounds(SoundHandler<WorldSound> handler) {
         if (!feature().undergroundWater()) return;

         handler.getSounds().add(new RepeatedWorldSound(handler.getPlayer()) {
             @Override
             public boolean isValidSituationCondition() {
                 Optional<BlockPos> optWater = BlockPos.findClosestMatch(player.blockPosition(), 12, 8, pos -> {
                     Block block = level.getBlockState(pos).getBlock();
                     return block == Blocks.WATER;
                 });

                 if (optWater.isPresent()) {
                     setPos(optWater.get());
                     return true;
                 }

                 return false;
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return !WorldHelper.isOutside(player)
                     && !player.isUnderWater()
                     && WorldHelper.isBelowSeaLevel(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return SOUND;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(150) + 120;
             }

             @Override
             public float getVolume() {
                 return 0.3F;
             }

             @Override
             public float getPitch() {
                 return 0.77F + (0.3F * level.random.nextFloat());
             }
         });
     }

     @Override
     public Class<WorldAmbience> typeForFeature() {
         return WorldAmbience.class;
     }
 }
