package dev.tr7zw.entityculling.mixin;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.entityculling.access.EntityRendererInter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> implements EntityRendererInter<T, S> {

    @Shadow protected abstract boolean shouldShowName(T entity, double d);

    @Shadow protected abstract void renderNameTag(S entityRenderState, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f);

    @Override
    public boolean shadowShouldShowName(T entity) {
        return shouldShowName(entity, 0);
    }

    @Override
    public void shadowRenderNameTag(S entity, Component component, PoseStack poseStack,
                                    MultiBufferSource multiBufferSource, int light, float f) {
        renderNameTag(entity, component, poseStack, multiBufferSource, light
        // spotless:off
                //#if MC >= 12005
                    , f);
                //#else
                //$$);
                //#endif
                //spotless:on
    }


}
