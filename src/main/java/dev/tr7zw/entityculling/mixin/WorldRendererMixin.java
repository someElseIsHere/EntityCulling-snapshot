package dev.tr7zw.entityculling.mixin;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import dev.tr7zw.entityculling.versionless.access.Cullable;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    private void renderEntity(Entity entity, double cameraX , double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo info) {
        if (EntityCullingModBase.instance.config.skipEntityCulling) {
            return;
        }
        Cullable cullable = (Cullable) entity;
        if (!cullable.isForcedVisible() && cullable.isCulled()) {
            @SuppressWarnings("unchecked")
            EntityRenderer<Entity,EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) entityRenderDispatcher.getRenderer(entity);
            @SuppressWarnings("unchecked")
            EntityRendererInter<Entity, EntityRenderState> entityRendererInter = (EntityRendererInter<Entity, EntityRenderState>) entityRenderer;
            if (EntityCullingModBase.instance.config.renderNametagsThroughWalls && matrices != null
                    && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                double x = Mth.lerp((double) tickDelta, (double) entity.xOld, (double) entity.getX()) - cameraX;
                double y = Mth.lerp((double) tickDelta, (double) entity.yOld, (double) entity.getY()) - cameraY;
                double z = Mth.lerp((double) tickDelta, (double) entity.zOld, (double) entity.getZ()) - cameraZ;
                Vec3 vec3d = entityRenderer.getRenderOffset(entityRenderer.createRenderState());
                double d = x + vec3d.x;
                double e = y + vec3d.y;
                double f = z + vec3d.z;
                matrices.pushPose();
                matrices.translate(d, e, f);
                entityRendererInter.shadowRenderNameTag(entityRenderer.createRenderState(), entity.getDisplayName(), matrices, vertexConsumers,
                        this.entityRenderDispatcher.getPackedLightCoords(entity, tickDelta), tickDelta);
                matrices.popPose();
            }
            EntityCullingModBase.instance.skippedEntities++;
            info.cancel();
            return;
        }
        EntityCullingModBase.instance.renderedEntities++;
        cullable.setOutOfCamera(false);
    }

}
