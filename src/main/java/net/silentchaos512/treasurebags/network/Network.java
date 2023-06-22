package net.silentchaos512.treasurebags.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.lib.BagTypeManager;

import java.util.Objects;

public final class Network {
    private static final ResourceLocation NAME = TreasureBags.getId("network");
    private static final String VERSION = "2"; // TODO: Add version check like Silent Gear's

    public static SimpleChannel channel;
    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(SyncBagTypesPacket.class, 1)
                .decoder(SyncBagTypesPacket::fromBytes)
                .encoder(SyncBagTypesPacket::toBytes)
                .consumerMainThread(BagTypeManager::handleSyncPacket)
                .add();
    }

    private Network() {}

    public static void init() {}
}
