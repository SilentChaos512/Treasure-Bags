package net.silentchaos512.treasurebags.setup;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.lib.registry.ItemRegistryObject;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.item.TreasureBagItem;

import java.util.function.Supplier;

public final class TbItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TreasureBags.MOD_ID);

    public static final ItemRegistryObject<TreasureBagItem> TREASURE_BAG = register("treasure_bag", () ->
            new TreasureBagItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    private TbItems() {}

    private static <T extends Item> ItemRegistryObject<T> register(String name, Supplier<T> item) {
        return new ItemRegistryObject<>(ITEMS.register(name, item));
    }
}
