package net.silentchaos512.treasurebags.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.lib.BagTypeManager;

import java.util.Objects;

public final class Network {
    private static final ResourceLocation NAME = TreasureBags.getId("network");

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, "1"))
                .serverAcceptedVersions(s -> Objects.equals(s, "1"))
                .networkProtocolVersion(() -> "1")
                .simpleChannel();

        channel.messageBuilder(SyncBagTypesPacket.class, 1)
                .decoder(SyncBagTypesPacket::fromBytes)
                .encoder(SyncBagTypesPacket::toBytes)
                .consumer(BagTypeManager::handleSyncPacket)
                .add();
    }

    private Network() {}

    public static void init() {}
}
