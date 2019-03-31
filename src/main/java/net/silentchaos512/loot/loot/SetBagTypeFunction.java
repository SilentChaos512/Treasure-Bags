package net.silentchaos512.loot.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.item.TreasureBagItem;

import java.util.Random;

public class SetBagTypeFunction extends LootFunction {
    private final ResourceLocation typeId;

    private SetBagTypeFunction(ResourceLocation typeId, LootCondition[] conditionsIn) {
        super(conditionsIn);
        this.typeId = typeId;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        return TreasureBagItem.setBagType(stack, this.typeId);
    }

    public static class Serializer extends LootFunction.Serializer<SetBagTypeFunction> {
        public static final Serializer INSTANCE = new Serializer(new ResourceLocation(TreasureBags.MOD_ID, "set_bag_type"), SetBagTypeFunction.class);

        Serializer(ResourceLocation location, Class<SetBagTypeFunction> clazz) {
            super(location, clazz);
        }

        @Override
        public void serialize(JsonObject object, SetBagTypeFunction functionClazz, JsonSerializationContext serializationContext) {
            object.addProperty("type", functionClazz.typeId.toString());
        }

        @Override
        public SetBagTypeFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            String str = JsonUtils.getString(object, "type", "");
            ResourceLocation id = ResourceLocation.tryCreate(str);
            if (id == null) throw new JsonParseException("Bag type is invalid or missing: '" + str + "'");

            return new SetBagTypeFunction(id, conditionsIn);
        }
    }
}
