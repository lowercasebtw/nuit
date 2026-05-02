package me.flashyreese.mods.nuit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.flashyreese.mods.nuit.SkyboxManager;
import me.flashyreese.mods.nuit.util.Utils;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @WrapOperation(method = "computeFogColor", at = @At(value = "INVOKE", target = "Lorg/joml/Vector4f;set(FFFF)Lorg/joml/Vector4f;"))
    private static Vector4f nuit$redirectSetShaderFogColor(final Vector4f instance, final float x, final float y, final float z, final float w, final Operation<Vector4f> original) {
        float alpha = w;
        if (SkyboxManager.getInstance().isEnabled()) {
            alpha =  Utils.alphaBlendFogDensity(SkyboxManager.getInstance().getActiveSkyboxes(), w);
        }

        return original.call(instance, x, y, z, alpha);
    }
}
