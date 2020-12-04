package net.silentchaos512.treasurebags.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.treasurebags.lib.EntityGroup;
import net.silentchaos512.utils.Color;

import java.util.*;

public class BagTypeBuilder {
    final ResourceLocation bagTypeId;
    private final Rarity rarity;
    private final ResourceLocation lootTable;
    private final Collection<EntityGroup> dropsFromGroups = new LinkedHashSet<>();
    private boolean noMobDrops = false;
    private ITextComponent displayName;
    private int bagColor;
    private int bagOverlayColor;
    private int bagStringColor;

    public BagTypeBuilder(ResourceLocation bagTypeId, Rarity rarity, ResourceLocation lootTable) {
        this.bagTypeId = bagTypeId;
        this.rarity = rarity;
        this.lootTable = lootTable;
        this.displayName = new TranslationTextComponent(String.format("bag.%s.%s", this.bagTypeId.getNamespace(), this.bagTypeId.getPath()));
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
        return dropsFrom(EntityGroup.values());
    }

    public BagTypeBuilder dropsFrom(EntityGroup... groups) {
        this.dropsFromGroups.addAll(Arrays.asList(groups));
        return this;
    }

    public BagTypeBuilder displayName(ITextComponent text) {
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
        json.add("displayName", ITextComponent.Serializer.toJsonTree(this.displayName));
        json.addProperty("lootTable", this.lootTable.toString());
        json.addProperty("rarity", this.rarity.name().toLowerCase(Locale.ROOT));
        json.addProperty("bagColor", Color.format(this.bagColor & 0xFFFFFF));
        json.addProperty("bagOverlayColor", Color.format(this.bagOverlayColor & 0xFFFFFF));
        json.addProperty("bagStringColor", Color.format(this.bagStringColor & 0xFFFFFF));

        JsonArray dropsFromArray = new JsonArray();
        if (!dropsFromGroups.isEmpty()) {
            dropsFromGroups.forEach(group -> dropsFromArray.add(group.getName()));
        } else if (!noMobDrops) {
            BagTypesProvider.LOGGER.warn("Bag type '{}' has no dropsFromGroups. This may be unintentional.", this.bagTypeId);
        }
        json.add("dropsFromGroups", dropsFromArray);
        return json;
    }
}
