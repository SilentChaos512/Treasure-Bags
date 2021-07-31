package net.silentchaos512.treasurebags.events;

import net.minecraft.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
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
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer)) return;

        ServerPlayer playerMP = (ServerPlayer) player;

        sendBagTypesToClient(playerMP);
        BagTypeManager.getErrorMessages(playerMP).forEach(text -> player.sendMessage(text, Util.NIL_UUID));
    }

    private static void sendBagTypesToClient(ServerPlayer playerMP) {
        Collection<IBagType> bagTypes = BagTypeManager.getValues();
        TreasureBags.LOGGER.info("Sending {} bag types to {}", bagTypes.size(), playerMP.getScoreboardName());
        SyncBagTypesPacket msg = new SyncBagTypesPacket(bagTypes);
        Network.channel.sendTo(msg, playerMP.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
