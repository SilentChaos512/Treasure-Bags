package net.silentchaos512.loot.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.loot.init.ModItems;

public interface IBagType {
    ResourceLocation getId();

    int getBagColor();

    int getBagOverlayColor();

    String getCustomName();

    ResourceLocation getLootTable();

    boolean isVisible();

    default ItemStack getItem() {
        return ModItems.treasureBag.stackOfType(this);
    }

    static ResourceLocation nameFromJson(JsonObject json) {
        String typeStr = JSONUtils.getString(json, "bag_type");
        ResourceLocation typeName = ResourceLocation.tryCreate(typeStr);
        if (typeName == null) {
            throw new JsonSyntaxException("Invalid or empty bag type: '" + typeStr + "'");
        }
        return typeName;
    }
}
