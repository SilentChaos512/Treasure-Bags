package net.silentchaos512.treasurebags.setup;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.loot.SelectBagRarity;
import net.silentchaos512.treasurebags.loot.SetBagTypeFunction;

public final class TbLoot {
    public static DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, TreasureBags.MOD_ID);

    public static final RegistryObject<LootItemFunctionType> SELECT_BAG_RARITY = LOOT_FUNCTIONS.register("select_bag_rarity", () ->
            new LootItemFunctionType(new SelectBagRarity.Serializer())
    );
    public static final RegistryObject<LootItemFunctionType> SET_BAG_TYPE = LOOT_FUNCTIONS.register("set_bag_type", () ->
            new LootItemFunctionType(new SetBagTypeFunction.Serializer())
    );

    private TbLoot() {}
}
