package net.silentchaos512.treasurebags.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.setup.TbLoot;

public final class SetBagTypeFunction extends LootItemConditionalFunction {
    private final ResourceLocation typeId;

    private SetBagTypeFunction(ResourceLocation typeId, LootItemCondition[] conditionsIn) {
        super(conditionsIn);
        this.typeId = typeId;
    }

    public static Builder builder(ResourceLocation bagTypeId, LootItemCondition... conditions) {
        return new Builder() {
            @Override
            protected Builder getThis() {
                return this;
            }

            @Override
            public LootItemFunction build() {
                return new SetBagTypeFunction(bagTypeId, conditions);
            }
        };
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
        return TreasureBagItem.setBagType(stack, this.typeId);
    }

    @Override
    public LootItemFunctionType getType() {
        return TbLoot.SET_BAG_TYPE.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetBagTypeFunction> {
        @Override
        public void serialize(JsonObject json, SetBagTypeFunction function, JsonSerializationContext serializationContext) {
            super.serialize(json, function, serializationContext);
            json.addProperty("type", function.typeId.toString());
        }

        @Override
        public SetBagTypeFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            String str = GsonHelper.getAsString(object, "type", "");
            ResourceLocation id = ResourceLocation.tryParse(str);
            if (id == null) throw new JsonParseException("Bag type is invalid or missing: '" + str + "'");

            return new SetBagTypeFunction(id, conditionsIn);
        }
    }
}
