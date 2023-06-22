package net.silentchaos512.treasurebags.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.loot.BagDropLootModifier;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;

public final class TbLoot {
    public static DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, TreasureBags.MOD_ID);

    public static DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TreasureBags.MOD_ID);

    public static final RegistryObject<LootItemFunctionType> SELECT_BAG_RARITY = LOOT_FUNCTIONS.register("select_bag_rarity", () ->
            new LootItemFunctionType(new SelectBagRarity.Serializer())
    );
    public static final RegistryObject<LootItemFunctionType> SET_BAG_TYPE = LOOT_FUNCTIONS.register("set_bag_type", () ->
            new LootItemFunctionType(new SetBagTypeFunction.Serializer())
    );

    public static final RegistryObject<Codec<BagDropLootModifier>> BAG_DROPS =
            LOOT_MODIFIERS.register("bag_drops", BagDropLootModifier.CODEC);

    private TbLoot() {}
}
