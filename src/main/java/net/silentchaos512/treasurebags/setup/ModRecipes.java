package net.silentchaos512.treasurebags.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.crafting.ingredient.TreasureBagIngredient;
import net.silentchaos512.treasurebags.crafting.recipe.ShapedTreasureBagRecipe;
import net.silentchaos512.treasurebags.crafting.recipe.ShapelessTreasureBagRecipe;

import java.util.function.Supplier;

public final class ModRecipes {
    public static final RegistryObject<RecipeSerializer<?>> SHAPED_BAG = register("shaped_bag", () -> new ExtendedShapedRecipe.Serializer<>(
            ShapedTreasureBagRecipe::new,
            ShapedTreasureBagRecipe::deserialize,
            ShapedTreasureBagRecipe::decode,
            ShapedTreasureBagRecipe::encode
    ));
    public static final RegistryObject<RecipeSerializer<?>> SHAPELESS_BAG = register("shapeless_bag", () -> new ExtendedShapelessRecipe.Serializer<>(
            ShapelessTreasureBagRecipe::new,
            ShapelessTreasureBagRecipe::deserialize,
            ShapelessTreasureBagRecipe::decode,
            ShapelessTreasureBagRecipe::encode
    ));

    private ModRecipes() {}

    static void register() {
        CraftingHelper.register(TreasureBagIngredient.Serializer.NAME, TreasureBagIngredient.Serializer.INSTANCE);
    }

    private static RegistryObject<RecipeSerializer<?>> register(String name, Supplier<RecipeSerializer<?>> serializer) {
        return register(TreasureBags.getId(name), serializer);
    }

    private static RegistryObject<RecipeSerializer<?>> register(ResourceLocation id, Supplier<RecipeSerializer<?>> serializer) {
        return Registration.RECIPE_SERIALIZERS.register(id.getPath(), serializer);
    }
}
