package net.silentchaos512.loot.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;
import net.silentchaos512.loot.network.Network;
import net.silentchaos512.loot.network.SyncBagTypesPacket;

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
        BagTypeManager.getErrorMessages(playerMP).forEach(playerMP::sendMessage);
    }

    private static void sendBagTypesToClient(ServerPlayerEntity playerMP) {
        Collection<IBagType> bagTypes = BagTypeManager.getValues();
        TreasureBags.LOGGER.info("Sending {} bag types to {}", bagTypes.size(), playerMP.getScoreboardName());
        SyncBagTypesPacket msg = new SyncBagTypesPacket(bagTypes);
        Network.channel.sendTo(msg, playerMP.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
