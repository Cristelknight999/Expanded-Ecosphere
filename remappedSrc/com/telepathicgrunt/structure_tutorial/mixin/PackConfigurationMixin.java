package com.telepathicgrunt.structure_tutorial.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(MinecraftServer.class)
public class PackConfigurationMixin {

	@Redirect(
			method = "configurePackRepository",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", ordinal = 1)
	)
	private static boolean packConfiguration(Set<String> packs, Object pack) {
		String packName = (String) pack;
		if(packName.startsWith("globalOpt:")){
			return false;
		}else{
			return packs.add(packName);
		}
	}
}
