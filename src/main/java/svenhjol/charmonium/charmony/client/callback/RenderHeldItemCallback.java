package svenhjol.charmonium.charmony.client.callback;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * A custom Fabric event used to help render a held item, such as a map.
 */
public interface RenderHeldItemCallback {
    Event<RenderHeldItemCallback> EVENT = EventFactory.createArrayBacked(RenderHeldItemCallback.class, (listeners) -> (tickDelta, pitch, hand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light) -> {
        for (var listener : listeners) {
            var result = listener.interact(tickDelta, pitch, hand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }

        return InteractionResult.PASS;
    });

    InteractionResult interact(float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress, PoseStack poseStack, MultiBufferSource bufferSource, int light);
}
