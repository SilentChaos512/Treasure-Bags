package net.silentchaos512.treasurebags;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.lib.event.InitialSpawnItems;
import net.silentchaos512.lib.util.LootUtils;
import net.silentchaos512.treasurebags.command.TreasureBagsCommand;
import net.silentchaos512.treasurebags.config.Config;
import net.silentchaos512.treasurebags.data.DataGenerators;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.network.Network;
import net.silentchaos512.treasurebags.setup.EntityGroups;
import net.silentchaos512.treasurebags.setup.TbItems;
import net.silentchaos512.treasurebags.setup.TbLoot;
import net.silentchaos512.treasurebags.setup.TbRecipes;

import java.util.Collections;

class SideProxy {
    private static final ResourceLocation STARTING_INVENTORY = TreasureBags.getId("starting_inventory");

    SideProxy() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        TbItems.ITEMS.register(modEventBus);
        TbLoot.LOOT_FUNCTIONS.register(modEventBus);
        TbRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        EntityGroups.init();
        Config.init();
        Network.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGenerators::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        InitialSpawnItems.add(STARTING_INVENTORY, p -> {
            if (p instanceof ServerPlayer) {
                return LootUtils.gift(STARTING_INVENTORY, (ServerPlayer) p);
            }
            return Collections.emptyList();
        });
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(BagTypeManager.INSTANCE);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        TreasureBagsCommand.register(event.getDispatcher());
    }

    static class Client extends SideProxy {
        Client() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onItemColors);
        }

        private void clientSetup(FMLClientSetupEvent event) {
        }

        private void onItemColors(RegisterColorHandlersEvent.Item event) {
            ItemColors colors = event.getItemColors();
            if (colors == null) {
                TreasureBags.LOGGER.error("ItemColors is null!", new NullPointerException("wat?"));
                return;
            }

            try {
                colors.register(TreasureBagItem::getColor, TbItems.TREASURE_BAG);
            } catch (NullPointerException ex) {
                TreasureBags.LOGGER.error("Something went horribly wrong with ItemColors", ex);
            }
        }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {
        }
    }
}
