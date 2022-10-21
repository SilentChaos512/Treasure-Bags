package net.silentchaos512.treasurebags.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.silentchaos512.treasurebags.setup.TbItems;

public interface IBagType {
    ResourceLocation getId();

    String getGroup();

    Rarity getRarity();

    boolean canDropFromMob(Entity entity);

    int getBagColor();

    int getBagOverlayColor();

    int getBagStringColor();

    Component getCustomName();

    ResourceLocation getLootTable();

    boolean isVisible();

    default ItemStack getItem() {
        return TbItems.TREASURE_BAG.get().stackOfType(this);
    }

    static ResourceLocation nameFromJson(JsonObject json) {
        String typeStr = GsonHelper.getAsString(json, "bag_type");
        ResourceLocation typeName = ResourceLocation.tryParse(typeStr);
        if (typeName == null) {
            throw new JsonParseException("Invalid or empty bag type: '" + typeStr + "'");
        }
        return typeName;
    }
}
