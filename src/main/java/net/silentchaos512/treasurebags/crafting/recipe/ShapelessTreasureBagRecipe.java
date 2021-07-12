package net.silentchaos512.treasurebags.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.lib.crafting.recipe.ExtendedShapelessRecipe;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.setup.ModRecipes;

public final class ShapelessTreasureBagRecipe extends ExtendedShapelessRecipe {
    private ResourceLocation typeName;

    public ShapelessTreasureBagRecipe(ShapelessRecipe recipe) {
        super(recipe);
    }

    public static void deserialize(JsonObject json, ShapelessTreasureBagRecipe recipe) {
        recipe.typeName = IBagType.nameFromJson(json.get("result").getAsJsonObject());
    }

    public static void decode(PacketBuffer buffer, ShapelessTreasureBagRecipe recipe) {recipe.typeName = buffer.readResourceLocation();}

    public static void encode(PacketBuffer buffer, ShapelessTreasureBagRecipe recipe) {buffer.writeResourceLocation(recipe.typeName);}

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        return this.getBaseRecipe().matches(inv, worldIn);
    }

    @Override
    public ItemStack getResultItem() {
        ItemStack copy = this.getBaseRecipe().getResultItem().copy();
        return TreasureBagItem.setBagType(copy, this.typeName);
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        ItemStack result = this.getResultItem();
        if (!(result.getItem() instanceof TreasureBagItem)) {
            TreasureBags.LOGGER.warn("Result of a treasure bag recipe is not a treasure bag? Recipe '{}' crafts {}",
                    this.getId(), result);
        }
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_BAG.get();
    }
}
