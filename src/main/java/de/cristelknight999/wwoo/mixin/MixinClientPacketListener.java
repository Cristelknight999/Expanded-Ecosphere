package de.cristelknight999.wwoo.mixin;

import de.cristelknight999.wwoo.WWOO;
import de.cristelknight999.wwoo.config.configs.CommentedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void showUpdateMessage(ClientboundLoginPacket arg, CallbackInfo ci) {
        if (this.minecraft.player == null) {
            return;
        }
        CommentedConfig config = CommentedConfig.getConfig();
        if(config.showUpdates() || (config.showBigUpdates() && WWOO.getUpdater().isBig())){
            WWOO.getUpdater().getUpdateMessage().ifPresent(msg ->
                    this.minecraft.player.displayClientMessage(msg, false));
        }

        //minecraft.setScreen(new DeathScreen(Component.literal("Download or you will really die!"), true));
    }
}
