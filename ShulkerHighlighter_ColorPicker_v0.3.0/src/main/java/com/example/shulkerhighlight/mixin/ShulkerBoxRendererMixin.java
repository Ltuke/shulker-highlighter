package com.example.shulkerhighlight.mixin;

import com.example.shulkerhighlight.ShulkerHighlighter;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Affects only the client-side shulker *block entity* renderer.
 * The original block and its dye remain untouched.
 *
 * Recolor mode uses vanilla white shulker texture and a custom RGB model tint.
 * Outline mode adds a depth-tested cube outline to the existing render queue.
 */
@Mixin(ShulkerBoxRenderer.class)
public abstract class ShulkerBoxRendererMixin {
    @Unique private boolean shulkerhighlighter$recoloring;

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    private void shulkerhighlighter$begin(ShulkerBoxRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        shulkerhighlighter$recoloring = ShulkerHighlighter.matches(state.color) && "recolor".equals(ShulkerHighlighter.mode);
    }

    /** Supply a WHITE texture for recolor mode so any custom RGB hue will be visible. */
    @ModifyArg(
        method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/ShulkerBoxRenderer;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IILnet/minecraft/core/Direction;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/resources/model/sprite/SpriteId;I)V"),
        index = 7
    )
    private SpriteId shulkerhighlighter$chooseTexture(SpriteId original) {
        return shulkerhighlighter$recoloring ? Sheets.getShulkerBoxSprite(DyeColor.WHITE) : original;
    }

    /** Argument 5 to submitModel is the ARGB tint (-1 means white). */
    @ModifyArg(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIFLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;Lnet/minecraft/client/resources/model/sprite/SpriteId;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;IIILnet/minecraft/client/resources/model/sprite/SpriteId;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;I)V"),
        index = 5
    )
    private int shulkerhighlighter$customTint(int original) {
        return shulkerhighlighter$recoloring ? (0xFF000000 | ShulkerHighlighter.rgb) : original;
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ShulkerBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    private void shulkerhighlighter$end(ShulkerBoxRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        shulkerhighlighter$recoloring = false;
        if (ShulkerHighlighter.matches(state.color) && "outline".equals(ShulkerHighlighter.mode)) {
            // Vanilla lines render type uses depth testing: NO x-ray through walls.
            // Shape is close to the visible shulker model, with a slight inset.
            collector.submitShapeOutline(
                poseStack,
                Shapes.box(-0.025, -0.025, -0.025, 1.025, 1.025, 1.025),
                RenderTypes.lines(),
                0xFF000000 | ShulkerHighlighter.rgb,
                3.0F,
                true
            );
        }
    }
}
