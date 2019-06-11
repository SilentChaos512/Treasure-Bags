package net.silentchaos512.loot.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapedRecipe;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.item.TreasureBagItem;
import net.silentchaos512.loot.lib.IBagType;

public final class ShapedTreasureBagRecipe extends ExtendedShapedRecipe {
    public static final ExtendedShapedRecipe.Serializer<ShapedTreasureBagRecipe> SERIALIZER = new Serializer<>(
            TreasureBags.getId("shaped_bag"),
            ShapedTreasureBagRecipe::new,
            (json, recipe) -> recipe.typeName = IBagType.nameFromJson(json.get("result").getAsJsonObject()),
            (buffer, recipe) -> recipe.typeName = buffer.readResourceLocation(),
            (buffer, recipe) -> buffer.writeResourceLocation(recipe.typeName)
    );

    private ResourceLocation typeName;

    private ShapedTreasureBagRecipe(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return this.getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack copy = this.getBaseRecipe().getRecipeOutput().copy();
        return TreasureBagItem.setBagType(copy, this.typeName);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack result = this.getRecipeOutput();
        if (!(result.getItem() instanceof TreasureBagItem)) {
            TreasureBags.LOGGER.warn("Result of a treasure bag recipe is not a treasure bag? Recipe '{}' crafts {}",
                    this.getId(), result);
        }
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
