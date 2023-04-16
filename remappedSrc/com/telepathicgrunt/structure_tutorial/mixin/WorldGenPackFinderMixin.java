package com.telepathicgrunt.structure_tutorial.mixin;

import com.telepathicgrunt.structure_tutorial.CommonClass;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateWorldScreen.class)
public class WorldGenPackFinderMixin {

	@ModifyArg(
			method = "getDataPackSelectionSettings",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>(Lnet/minecraft/server/packs/PackType;[Lnet/minecraft/server/packs/repository/RepositorySource;)V"),
			index = 1
	)
	private ResourcePackProvider[] addClientPackFinder(ResourcePackProvider[] arg) {
		return ArrayUtils.addAll(arg, CommonClass.getRepositorySource(ResourceType.SERVER_DATA, false), CommonClass.getRepositorySource(ResourceType.SERVER_DATA, true));
	}
}
