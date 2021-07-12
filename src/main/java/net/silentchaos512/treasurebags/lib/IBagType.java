package net.silentchaos512.treasurebags.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.treasurebags.setup.ModItems;

public interface IBagType {
    ResourceLocation getId();

    String getGroup();

    Rarity getRarity();

    boolean canDropFromMob(Entity entity);

    int getBagColor();

    int getBagOverlayColor();

    int getBagStringColor();

    ITextComponent getCustomName();

    ResourceLocation getLootTable();

    boolean isVisible();

    default ItemStack getItem() {
        return ModItems.TREASURE_BAG.get().stackOfType(this);
    }

    static ResourceLocation nameFromJson(JsonObject json) {
        String typeStr = JSONUtils.getAsString(json, "bag_type");
        ResourceLocation typeName = ResourceLocation.tryParse(typeStr);
        if (typeName == null) {
            throw new JsonParseException("Invalid or empty bag type: '" + typeStr + "'");
        }
        return typeName;
    }
}
