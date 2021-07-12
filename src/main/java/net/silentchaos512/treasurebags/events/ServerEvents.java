package net.silentchaos512.treasurebags.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.network.Network;
import net.silentchaos512.treasurebags.network.SyncBagTypesPacket;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = TreasureBags.MOD_ID)
public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!(player instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity playerMP = (ServerPlayerEntity) player;

        sendBagTypesToClient(playerMP);
        BagTypeManager.getErrorMessages(playerMP).forEach(text -> player.sendMessage(text, Util.NIL_UUID));
    }

    private static void sendBagTypesToClient(ServerPlayerEntity playerMP) {
        Collection<IBagType> bagTypes = BagTypeManager.getValues();
        TreasureBags.LOGGER.info("Sending {} bag types to {}", bagTypes.size(), playerMP.getScoreboardName());
        SyncBagTypesPacket msg = new SyncBagTypesPacket(bagTypes);
        Network.channel.sendTo(msg, playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
