package net.silentchaos512.loot.init;

import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.silentchaos512.loot.loot.SetBagTypeFunction;

public class ModLoot {
    public static void init() {
        LootFunctionManager.registerFunction(SetBagTypeFunction.Serializer.INSTANCE);
    }
}
