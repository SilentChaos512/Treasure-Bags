package net.silentchaos512.loot.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.init.ModItems;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

@JeiPlugin
public class TreasureBagsJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = TreasureBags.getId("plugin/main");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModItems.treasureBag, stack -> {
            IBagType bagType = BagTypeManager.typeFromBag(stack);
            return bagType != null ? bagType.getId().toString() : "null";
        });
    }
}
