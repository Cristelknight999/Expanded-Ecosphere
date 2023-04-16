package de.cristelknight999.wwoo.mixin;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoLavaMixin {

    @Inject(
            method = "createFluidPicker",
            at = @At(value = "HEAD"), cancellable = true)

    private static void modifyHoglin(NoiseGeneratorSettings noiseGeneratorSettings, CallbackInfoReturnable<Aquifer.FluidPicker> cir) {
        int i = noiseGeneratorSettings.seaLevel();
        Aquifer.FluidStatus fluidStatus2 = new Aquifer.FluidStatus(i, noiseGeneratorSettings.defaultFluid());
        cir.setReturnValue((j, k, l) -> fluidStatus2);
    }

}