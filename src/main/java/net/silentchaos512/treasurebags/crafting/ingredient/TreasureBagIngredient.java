package net.silentchaos512.treasurebags.crafting.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.setup.ModItems;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public final class TreasureBagIngredient extends Ingredient {
    // Store bag type ID because the bag types likely don't exist yet
    private final ResourceLocation typeName;

    private TreasureBagIngredient(ResourceLocation typeName) {
        super(Stream.of(new SingleItemList(ModItems.TREASURE_BAG.get().stackOfType(typeName))));
        this.typeName = typeName;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        IBagType bagType = BagTypeManager.typeFromBag(stack);
        return bagType != null && bagType.getId().equals(this.typeName);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements IIngredientSerializer<TreasureBagIngredient> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation NAME = TreasureBags.getId("bag");

        @Override
        public TreasureBagIngredient parse(PacketBuffer buffer) {
            return new TreasureBagIngredient(buffer.readResourceLocation());
        }

        @Override
        public TreasureBagIngredient parse(JsonObject json) {
            return new TreasureBagIngredient(IBagType.nameFromJson(json));
        }

        @Override
        public void write(PacketBuffer buffer, TreasureBagIngredient ingredient) {
            buffer.writeResourceLocation(ingredient.typeName);
        }
    }
}
