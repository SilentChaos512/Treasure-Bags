package net.silentchaos512.loot.lib;

import net.minecraft.item.ItemStack;
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
}
