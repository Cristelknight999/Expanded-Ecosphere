package com.telepathicgrunt.structure_tutorial.mixin;

import com.telepathicgrunt.structure_tutorial.CommonClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftClient.class)
public class ClientPackFinderMixin {

	@ModifyArg(
			method = "<init>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>(Lnet/minecraft/server/packs/repository/Pack$PackConstructor;[Lnet/minecraft/server/packs/repository/RepositorySource;)V"),
			index = 1
	)
	private ResourcePackProvider[] addClientPackFinder(ResourcePackProvider[] arg) {
		return ArrayUtils.addAll(arg, CommonClass.getRepositorySource(ResourceType.CLIENT_RESOURCES, true));
	}

	@ModifyArg(
			method = "makeServerStem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V"),
			index = 1
	)
	private ResourcePackProvider[] addClientPackFinder2(ResourcePackProvider[] arg) {
		return ArrayUtils.addAll(arg, CommonClass.getRepositorySource(ResourceType.SERVER_DATA, true), CommonClass.getRepositorySource(ResourceType.SERVER_DATA, false));
	}
}
