package net.silentchaos512.loot.init;

import net.silentchaos512.loot.TreasureBags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.loot.item.TreasureBagItem;

import java.util.ArrayList;
import java.util.Collection;

public final class ModItems {
    public static TreasureBagItem treasureBag;

    static final Collection<ItemBlock> blocksToRegister = new ArrayList<>();

    private ModItems() {}

    public static void registerAll(RegistryEvent.Register<Item> event) {
        // Workaround for Forge event bus bug
        if (!event.getName().equals(ForgeRegistries.ITEMS.getRegistryName())) return;

        // Register block items first
        blocksToRegister.forEach(ForgeRegistries.ITEMS::register);

        // Then register your items here
        treasureBag = register("treasure_bag", new TreasureBagItem());
    }

    private static <T extends Item> T register(String name, T item) {
        ResourceLocation id = new ResourceLocation(TreasureBags.MOD_ID, name);
        item.setRegistryName(id);
        ForgeRegistries.ITEMS.register(item);
        return item;
    }

    private static <E extends Enum<E> & IItemProvider & IStringSerializable> void registerFromEnum(Class<E> enumClass) {
        for (E e : enumClass.getEnumConstants()) {
            register(e.getName(), e.asItem());
        }
    }
}
