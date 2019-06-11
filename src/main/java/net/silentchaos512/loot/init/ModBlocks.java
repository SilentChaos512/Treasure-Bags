package net.silentchaos512.loot.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.loot.TreasureBags;

public final class ModBlocks {
    private ModBlocks() {}

    public static void registerAll(RegistryEvent.Register<Block> event) {
        // Workaround for Forge event bus bug
        if (!event.getName().equals(ForgeRegistries.BLOCKS.getRegistryName())) return;
    }

    private static <T extends Block> T register(String name, T block) {
        BlockItem item = new BlockItem(block, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
        return register(name, block, item);
    }

    private static <T extends Block> T register(String name, T block, BlockItem item) {
        ResourceLocation id = new ResourceLocation(TreasureBags.MOD_ID, name);
        block.setRegistryName(id);
        ForgeRegistries.BLOCKS.register(block);

        item.setRegistryName(id);
        ModItems.blocksToRegister.add(item);

        return block;
    }
}
