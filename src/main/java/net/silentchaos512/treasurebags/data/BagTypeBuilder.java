package net.silentchaos512.treasurebags.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;
import net.silentchaos512.treasurebags.lib.StandardEntityGroups;
import net.silentchaos512.utils.Color;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;

public class BagTypeBuilder {
    final ResourceLocation bagTypeId;
    private final String group;
    private final Rarity rarity;
    private final ResourceLocation lootTable;
    private final Collection<IEntityGroup> dropsFromGroups = new LinkedHashSet<>();
    private boolean noMobDrops = false;
    private Component displayName;
    private int bagColor;
    private int bagOverlayColor;
    private int bagStringColor;

    @Deprecated
    public BagTypeBuilder(ResourceLocation bagTypeId, Rarity rarity, ResourceLocation lootTable) {
        this(bagTypeId, "default", rarity, lootTable);
    }

    public BagTypeBuilder(ResourceLocation bagTypeId, String group, Rarity rarity, ResourceLocation lootTable) {
        this.bagTypeId = bagTypeId;
        this.group = group;
        this.rarity = rarity;
        this.lootTable = lootTable;
        this.displayName = Component.translatable(String.format("bag.%s.%s", this.bagTypeId.getNamespace(), this.bagTypeId.getPath()));
    }

    /**
     * Makes the bag type drop from no mob groups and suppresses the warning associated with that
     *
     * @return The builder
     */
    public BagTypeBuilder noMobDrops() {
        this.dropsFromGroups.clear();
        this.noMobDrops = true;
        return this;
    }

    /**
     * Makes the bag type drop from all mob groups
     *
     * @return The builder
     */
    public BagTypeBuilder dropsFromAllMobs() {
        return dropsFrom(StandardEntityGroups.values());
    }

    public BagTypeBuilder dropsFrom(IEntityGroup... groups) {
        this.dropsFromGroups.addAll(Arrays.asList(groups));
        return this;
    }

    public BagTypeBuilder displayName(Component text) {
        this.displayName = text;
        return this;
    }

    public BagTypeBuilder colors(int bag, int overlay, int string) {
        this.bagColor = bag;
        this.bagOverlayColor = overlay;
        this.bagStringColor = string;
        return this;
    }

    public BagTypeBuilder colors(Color bag, Color overlay, Color string) {
        return colors(bag.getColor(), overlay.getColor(), string.getColor());
    }

    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("group", this.group);
        json.add("displayName", Component.Serializer.toJsonTree(this.displayName));
        json.addProperty("lootTable", this.lootTable.toString());
        json.addProperty("rarity", this.rarity.name().toLowerCase(Locale.ROOT));
        json.addProperty("bagColor", Color.format(this.bagColor & 0xFFFFFF));
        json.addProperty("bagOverlayColor", Color.format(this.bagOverlayColor & 0xFFFFFF));
        json.addProperty("bagStringColor", Color.format(this.bagStringColor & 0xFFFFFF));

        JsonArray dropsFromArray = new JsonArray();
        if (!dropsFromGroups.isEmpty()) {
            dropsFromGroups.forEach(group -> dropsFromArray.add(TreasureBags.shortenId(group.getId())));
        } else if (!noMobDrops) {
            BagTypesProvider.LOGGER.warn("Bag type '{}' has no dropsFromGroups. This may be unintentional.", this.bagTypeId);
        }
        json.add("dropsFromGroups", dropsFromArray);
        return json;
    }
}
