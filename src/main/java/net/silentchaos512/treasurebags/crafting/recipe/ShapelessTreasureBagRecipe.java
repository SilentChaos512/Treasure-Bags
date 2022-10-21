package net.silentchaos512.treasurebags.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.setup.TbRecipes;

public final class ShapelessTreasureBagRecipe extends ExtendedShapelessRecipe {
    private ResourceLocation typeName;

    public ShapelessTreasureBagRecipe(ShapelessRecipe recipe) {
        super(recipe);
    }

    public static void deserialize(JsonObject json, ShapelessTreasureBagRecipe recipe) {
        recipe.typeName = IBagType.nameFromJson(json.get("result").getAsJsonObject());
    }

    public static void decode(FriendlyByteBuf buffer, ShapelessTreasureBagRecipe recipe) {recipe.typeName = buffer.readResourceLocation();}

    public static void encode(FriendlyByteBuf buffer, ShapelessTreasureBagRecipe recipe) {buffer.writeResourceLocation(recipe.typeName);}

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        return this.getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack getResultItem() {
        ItemStack copy = this.getBaseRecipe().getResultItem().copy();
        return TreasureBagItem.setBagType(copy, this.typeName);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack result = this.getResultItem();
        if (!(result.getItem() instanceof TreasureBagItem)) {
            TreasureBags.LOGGER.warn("Result of a treasure bag recipe is not a treasure bag? Recipe '{}' crafts {}",
                    this.getId(), result);
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TbRecipes.SHAPELESS_BAG.get();
    }
}
