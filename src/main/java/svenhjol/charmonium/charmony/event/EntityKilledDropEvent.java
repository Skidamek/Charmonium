package svenhjol.charmonium.charmony.event;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unused")
public class EntityKilledDropEvent extends CharmEvent<EntityKilledDropEvent.Handler> {
    public static final EntityKilledDropEvent INSTANCE = new EntityKilledDropEvent();
    
    private EntityKilledDropEvent() {}
    
    public InteractionResult invoke(LivingEntity entity, DamageSource source) {
        for (var handler : getHandlers()) {
            var result = handler.run(entity, source);
            
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        
        return InteractionResult.PASS;
    }
    
    @FunctionalInterface
    public interface Handler {
        InteractionResult run(LivingEntity entity, DamageSource source);
    }
}