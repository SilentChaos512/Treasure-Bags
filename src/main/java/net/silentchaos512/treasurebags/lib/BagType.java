package net.silentchaos512.treasurebags.lib;

import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.Rarity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.EnumSet;

public final class BagType implements IBagType {
    private final ResourceLocation name;
    private String group = "none";
    private Rarity rarity;
    private final Collection<EntityGroup> dropsFromGroups = EnumSet.noneOf(EntityGroup.class);
    private int bagColor;
    private int bagOverlayColor;
    private int bagStringColor;
    private ITextComponent customName;
    private ResourceLocation lootTable;
    private boolean visible;

    private BagType(ResourceLocation name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getId() {
        return name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public boolean canDropFromMob(Entity entity) {
        return dropsFromGroups.contains(EntityGroup.from(entity));
    }

    @Override
    public int getBagColor() {
        return bagColor;
    }

    @Override
    public int getBagOverlayColor() {
        return bagOverlayColor;
    }

    @Override
    public int getBagStringColor() {
        return bagStringColor;
    }

    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public static final class Serializer {
        private Serializer() {}

        public static BagType deserialize(ResourceLocation name, JsonObject json) {
            BagType result = new BagType(name);

            String tableName = JSONUtils.getString(json, "lootTable");
            ResourceLocation lootTable = ResourceLocation.tryCreate(tableName);
            if (lootTable == null) {
                throw new JsonParseException("Invalid loot table: " + tableName);
            }
            result.lootTable = lootTable;
            result.rarity = deserializeRarity(JSONUtils.getString(json, "rarity"));
            result.customName = ITextComponent.Serializer.getComponentFromJson(json.get("displayName"));
            result.bagColor = Color.from(json, "bagColor", 0xFFFFFF).getColor();
            result.bagOverlayColor = Color.from(json, "bagOverlayColor", 0xFFFFFF).getColor();
            result.bagStringColor = Color.from(json, "bagStringColor", 0xFFFFFF).getColor();
            result.visible = JSONUtils.getBoolean(json, "visible", true);
            result.group = JSONUtils.getString(json, "group");

            JsonElement dropsFromJson = json.get("dropsFromGroups");
            if (dropsFromJson != null && dropsFromJson.isJsonArray()) {
                JsonArray array = dropsFromJson.getAsJsonArray();
                for (JsonElement e : array) {
                    EntityGroup group = EntityGroup.byName(e.getAsString());
                    if (group != null) {
                        result.dropsFromGroups.add(group);
                    } else {
                        throw new JsonParseException("Unknown entity group: " + e.getAsString());
                    }
                }
            }

            return result;
        }

        public static BagType read(PacketBuffer buffer) {
            BagType bagType = new BagType(buffer.readResourceLocation());
            bagType.lootTable = buffer.readResourceLocation();
            bagType.rarity = buffer.readEnumValue(Rarity.class);
            bagType.customName = buffer.readTextComponent();
            bagType.bagColor = buffer.readVarInt();
            bagType.bagOverlayColor = buffer.readVarInt();
            bagType.bagStringColor = buffer.readVarInt();
            bagType.visible = buffer.readBoolean();

            int groupsCount = buffer.readByte();
            for (int i = 0; i < groupsCount; ++i) {
                bagType.dropsFromGroups.add(buffer.readEnumValue(EntityGroup.class));
            }

            return bagType;
        }

        public static void write(IBagType bagType, PacketBuffer buffer) {
            buffer.writeResourceLocation(bagType.getId());
            buffer.writeResourceLocation(bagType.getLootTable());
            buffer.writeEnumValue(bagType.getRarity());
            buffer.writeTextComponent(bagType.getCustomName());
            buffer.writeVarInt(bagType.getBagColor());
            buffer.writeVarInt(bagType.getBagOverlayColor());
            buffer.writeVarInt(bagType.getBagStringColor());
            buffer.writeBoolean(bagType.isVisible());

            if (bagType instanceof BagType) {
                Collection<EntityGroup> groups = ((BagType) bagType).dropsFromGroups;
                buffer.writeByte(groups.size());
                groups.forEach(buffer::writeEnumValue);
            }
        }

        public static Rarity deserializeRarity(String name) {
            for (Rarity rarity : Rarity.values()) {
                if (name.equalsIgnoreCase(rarity.name())) {
                    return rarity;
                }
            }
            throw new JsonSyntaxException("Unknown rarity: " + name);
        }
    }
}
