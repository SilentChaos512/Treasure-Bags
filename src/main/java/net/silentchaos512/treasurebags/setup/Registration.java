package net.silentchaos512.treasurebags.setup;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.treasurebags.TreasureBags;

public final class Registration {
    public static DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    public static DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);

    private Registration() {}

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        ModItems.register();
        ModRecipes.register();
    }

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, TreasureBags.MOD_ID);
    }
}
