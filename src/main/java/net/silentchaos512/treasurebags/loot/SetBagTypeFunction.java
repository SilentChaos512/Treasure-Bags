package net.silentchaos512.treasurebags.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.setup.ModLoot;

import net.minecraft.loot.functions.ILootFunction.IBuilder;

public final class SetBagTypeFunction extends LootFunction {
    private final ResourceLocation typeId;

    private SetBagTypeFunction(ResourceLocation typeId, ILootCondition[] conditionsIn) {
        super(conditionsIn);
        this.typeId = typeId;
    }

    public static IBuilder builder(ResourceLocation bagTypeId, ILootCondition... conditions) {
        return () -> new SetBagTypeFunction(bagTypeId, conditions);
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        return TreasureBagItem.setBagType(stack, this.typeId);
    }

    @Override
    public LootFunctionType getType() {
        return ModLoot.SET_BAG_TYPE;
    }

    public static class Serializer extends LootFunction.Serializer<SetBagTypeFunction> {
        @Override
        public void serialize(JsonObject json, SetBagTypeFunction function, JsonSerializationContext serializationContext) {
            super.serialize(json, function, serializationContext);
            json.addProperty("type", function.typeId.toString());
        }

        @Override
        public SetBagTypeFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            String str = JSONUtils.getAsString(object, "type", "");
            ResourceLocation id = ResourceLocation.tryParse(str);
            if (id == null) throw new JsonParseException("Bag type is invalid or missing: '" + str + "'");

            return new SetBagTypeFunction(id, conditionsIn);
        }
    }
}
