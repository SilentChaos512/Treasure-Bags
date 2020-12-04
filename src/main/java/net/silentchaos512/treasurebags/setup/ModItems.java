package net.silentchaos512.treasurebags.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.treasurebags.item.TreasureBagItem;

import java.util.function.Supplier;

public final class ModItems {
    public static final ItemRegistryObject<TreasureBagItem> TREASURE_BAG = register("treasure_bag", () ->
            new TreasureBagItem(new Item.Properties().group(ItemGroup.MISC)));

    private ModItems() {}

    public static void register() {}

    private static <T extends Item> ItemRegistryObject<T> register(String name, Supplier<T> item) {
        return new ItemRegistryObject<>(Registration.ITEMS.register(name, item));
    }
}
