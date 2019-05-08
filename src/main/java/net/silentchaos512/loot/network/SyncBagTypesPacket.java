package net.silentchaos512.loot.network;

import net.minecraft.network.PacketBuffer;
import net.silentchaos512.loot.lib.BagType;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncBagTypesPacket {
    private List<IBagType> types;

    public SyncBagTypesPacket() {
        this(BagTypeManager.getValues());
    }

    public SyncBagTypesPacket(Collection<IBagType> types) {
        this.types = new ArrayList<>(types);
    }

    public static SyncBagTypesPacket fromBytes(PacketBuffer buffer) {
        SyncBagTypesPacket packet = new SyncBagTypesPacket();
        packet.types = new ArrayList<>();
        int count = buffer.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.types.add(BagType.Serializer.read(buffer));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeVarInt(this.types.size());
        this.types.forEach(type -> BagType.Serializer.write(type, buffer));
    }

    public List<IBagType> getBagTypes() {
        return this.types;
    }
}
