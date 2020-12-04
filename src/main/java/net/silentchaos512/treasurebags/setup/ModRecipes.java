package net.silentchaos512.treasurebags.setup;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.RegistryObject;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.crafting.ingredient.TreasureBagIngredient;
import net.silentchaos512.treasurebags.crafting.recipe.ShapedTreasureBagRecipe;
import net.silentchaos512.treasurebags.crafting.recipe.ShapelessTreasureBagRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final RegistryObject<IRecipeSerializer<?>> SHAPED_BAG = register("shaped_bag", () -> new ExtendedShapedRecipe.Serializer<>(
            ShapedTreasureBagRecipe::new,
            ShapedTreasureBagRecipe::deserialize,
            ShapedTreasureBagRecipe::decode,
            ShapedTreasureBagRecipe::encode
    ));
    public static final RegistryObject<IRecipeSerializer<?>> SHAPELESS_BAG = register("shapeless_bag", () -> new ExtendedShapelessRecipe.Serializer<>(
            ShapelessTreasureBagRecipe::new,
            ShapelessTreasureBagRecipe::deserialize,
            ShapelessTreasureBagRecipe::decode,
            ShapelessTreasureBagRecipe::encode
    ));

    private ModRecipes() {}

    static void register() {
        CraftingHelper.register(TreasureBagIngredient.Serializer.NAME, TreasureBagIngredient.Serializer.INSTANCE);
    }

    private static RegistryObject<IRecipeSerializer<?>> register(String name, Supplier<IRecipeSerializer<?>> serializer) {
        return register(TreasureBags.getId(name), serializer);
    }

    private static RegistryObject<IRecipeSerializer<?>> register(ResourceLocation id, Supplier<IRecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(id.getPath(), serializer);
    }
}
