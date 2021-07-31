package net.silentchaos512.treasurebags.lib;

import com.google.gson.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Rarity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;
import net.silentchaos512.treasurebags.setup.EntityGroups;
import net.silentchaos512.utils.Color;

import java.util.Collection;
import java.util.HashSet;

public final class BagType implements IBagType {
    private final ResourceLocation name;
    private String group = "none";
    private Rarity rarity;
    private final Collection<IEntityGroup> dropsFromGroups = new HashSet<>();
    private int bagColor;
    private int bagOverlayColor;
    private int bagStringColor;
    private Component customName;
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
        for (IEntityGroup group : this.dropsFromGroups) {
            if (group.matches(entity)) {
                return true;
            }
        }
        return false;
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
    public Component getCustomName() {
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

            String tableName = GsonHelper.getAsString(json, "lootTable");
            ResourceLocation lootTable = ResourceLocation.tryParse(tableName);
            if (lootTable == null) {
                throw new JsonParseException("Invalid loot table: " + tableName);
            }
            result.lootTable = lootTable;
            result.rarity = deserializeRarity(GsonHelper.getAsString(json, "rarity"));
            result.customName = Component.Serializer.fromJson(json.get("displayName"));
            result.bagColor = Color.from(json, "bagColor", 0xFFFFFF).getColor();
            result.bagOverlayColor = Color.from(json, "bagOverlayColor", 0xFFFFFF).getColor();
            result.bagStringColor = Color.from(json, "bagStringColor", 0xFFFFFF).getColor();
            result.visible = GsonHelper.getAsBoolean(json, "visible", true);
            result.group = GsonHelper.getAsString(json, "group");

            JsonElement dropsFromJson = json.get("dropsFromGroups");
            if (dropsFromJson != null && dropsFromJson.isJsonArray()) {
                JsonArray array = dropsFromJson.getAsJsonArray();
                for (JsonElement e : array) {
                    ResourceLocation id = TreasureBags.getIdWithDefaultNamespace(e.getAsString());
                    if (id != null) {
                        IEntityGroup group = EntityGroups.getOrCreate(id, Serializer::getOrCreateEntityGroup);
                        result.dropsFromGroups.add(group);
                    } else {
                        throw new JsonParseException("Invalid entity group name: " + e.getAsString());
                    }
                }
            }

            return result;
        }

        public static BagType read(FriendlyByteBuf buffer) {
            BagType bagType = new BagType(buffer.readResourceLocation());
            bagType.lootTable = buffer.readResourceLocation();
            bagType.rarity = buffer.readEnum(Rarity.class);
            bagType.customName = buffer.readComponent();
            bagType.bagColor = buffer.readVarInt();
            bagType.bagOverlayColor = buffer.readVarInt();
            bagType.bagStringColor = buffer.readVarInt();
            bagType.visible = buffer.readBoolean();

            int groupsCount = buffer.readByte();
            for (int i = 0; i < groupsCount; ++i) {
                bagType.dropsFromGroups.add(EntityGroups.getOrCreate(buffer.readResourceLocation(), Serializer::getOrCreateEntityGroup));
            }

            return bagType;
        }

        public static void write(IBagType bagType, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(bagType.getId());
            buffer.writeResourceLocation(bagType.getLootTable());
            buffer.writeEnum(bagType.getRarity());
            buffer.writeComponent(bagType.getCustomName());
            buffer.writeVarInt(bagType.getBagColor());
            buffer.writeVarInt(bagType.getBagOverlayColor());
            buffer.writeVarInt(bagType.getBagStringColor());
            buffer.writeBoolean(bagType.isVisible());

            if (bagType instanceof BagType) {
                Collection<IEntityGroup> groups = ((BagType) bagType).dropsFromGroups;
                buffer.writeByte(groups.size());
                groups.forEach(group -> buffer.writeResourceLocation(group.getId()));
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

        private static IEntityGroup getOrCreateEntityGroup(ResourceLocation id) {
            return new TagEntityGroup(id, EntityTypeTags.bind(id.toString()));
        }
    }
}
