package me.flashyreese.mods.nuit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import me.flashyreese.mods.nuit.SkyboxManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class MixinLevelRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static float nuit$tickDelta;

    @Unique
    private static GpuBufferSlice nuit$fogParameters;

    @Inject(method = "addSkyPass", at = @At(value = "HEAD"))
    private void nuit$preAddSkyPass(final FrameGraphBuilder frameGraphBuilder, final CameraRenderState cameraRenderState, final GpuBufferSlice fogParameters, final CallbackInfo ci) {
        nuit$tickDelta = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        nuit$fogParameters = fogParameters;
    }

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "lambda$addSkyPass$0", require = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFog(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER), cancellable = true)
    private static void nuit$renderCustomSkyboxes(CallbackInfo ci, @Local(argsOnly = true) SkyRenderer skyRenderer) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            skyboxManager.renderSkyboxes(
                    (SkyRendererAccessor) skyRenderer,
                    RenderSystem.getModelViewStack(),
                    nuit$tickDelta,
                    Minecraft.getInstance().gameRenderer.getMainCamera(),
                    nuit$fogParameters,
                    Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource()
            );
            ci.cancel();
        }
    }
}
