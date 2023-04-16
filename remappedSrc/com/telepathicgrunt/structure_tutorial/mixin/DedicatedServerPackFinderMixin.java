package com.telepathicgrunt.structure_tutorial.mixin;

import com.telepathicgrunt.structure_tutorial.CommonClass;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.Main;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Main.class)
public class DedicatedServerPackFinderMixin {

	@ModifyArg(
			method = "main",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V"),
			index = 1
	)
	private static ResourcePackProvider[] addServerPackFinders(ResourcePackProvider[] arg) {
		return ArrayUtils.addAll(arg, CommonClass.getRepositorySource(ResourceType.SERVER_DATA, true), CommonClass.getRepositorySource(ResourceType.SERVER_DATA, false));
	}
}
