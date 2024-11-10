package de.cristelknight.wwee.mixin;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hoglin.class)
public abstract class HoglinMixin {

    @Shadow public abstract void setImmuneToZombification(boolean bl);

    @Inject(
            method = "finalizeSpawn",
            at = @At(value = "HEAD"))

    public void modifyHoglin(@NotNull ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, EntitySpawnReason entitySpawnReason, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if(serverLevelAccessor.dimensionType().natural() &&  entitySpawnReason.equals(EntitySpawnReason.CHUNK_GENERATION)){
            setImmuneToZombification(true);
        }
    }

}