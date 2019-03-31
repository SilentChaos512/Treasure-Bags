package net.silentchaos512.loot.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.utils.Color;

public final class BagType implements IBagType {
    private final ResourceLocation name;
    private int bagColor;
    private int bagOverlayColor;
    private String customName;
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
    public int getBagColor() {
        return bagColor;
    }

    @Override
    public int getBagOverlayColor() {
        return bagOverlayColor;
    }

    @Override
    public String getCustomName() {
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

    static final class Serializer {
        private Serializer() {}

        static BagType deserialize(ResourceLocation name, JsonObject json) {
            BagType result = new BagType(name);

            String tableName = JsonUtils.getString(json, "lootTable");
            ResourceLocation lootTable = ResourceLocation.tryCreate(tableName);
            if (lootTable == null) {
                throw new JsonParseException("Invalid loot table: " + tableName);
            }
            result.lootTable = lootTable;

            result.customName = JsonUtils.getString(json, "displayName", "");
            result.bagColor = Color.from(json, "bagColor", 0xFFFFFF).getColor();
            result.bagOverlayColor = Color.from(json, "bagOverlayColor", 0xFFFFFF).getColor();
            result.visible = JsonUtils.getBoolean(json, "visible", true);

            return result;
        }
    }
}
