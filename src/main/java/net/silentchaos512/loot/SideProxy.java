package net.silentchaos512.loot;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.silentchaos512.loot.init.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.loot.item.TreasureBagItem;
import net.silentchaos512.loot.lib.BagTypeManager;

class SideProxy {
    SideProxy() {
        TreasureBags.LOGGER.debug("SideProxy init");

        ModLoot.init();

        // Add listeners for common events
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcProcess);

        // Add listeners for registry events
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModBlocks::registerAll);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModItems::registerAll);

        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        TreasureBags.LOGGER.debug("SideProxy commonSetup");
    }

    private void imcEnqueue(InterModEnqueueEvent event) {
        TreasureBags.LOGGER.debug("SideProxy imcEnqueue");
    }

    private void imcProcess(InterModProcessEvent event) {
        TreasureBags.LOGGER.debug("SideProxy imcProcess");
    }

    private void serverAboutToStart(FMLServerAboutToStartEvent event) {
        event.getServer().getResourceManager().addReloadListener(BagTypeManager.INSTANCE);
//        SGearPartsCommand.register(event.getServer().getCommandManager().getDispatcher());
    }

    static class Client extends SideProxy {
        Client() {
            TreasureBags.LOGGER.debug("SideProxy.Client init");
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            MinecraftForge.EVENT_BUS.addListener(this::onItemColors);
        }

        private void clientSetup(FMLClientSetupEvent event) {
            TreasureBags.LOGGER.debug("SideProxy.Client clientSetup");
        }

        private void onItemColors(ColorHandlerEvent.Item event) {
            ItemColors colors = event.getItemColors();
            if (colors == null) {
                TreasureBags.LOGGER.error("ItemColors is null!", new NullPointerException("wat?"));
                return;
            }

            try {
                colors.register(TreasureBagItem::getColor, ModItems.treasureBag);
            } catch (NullPointerException ex) {
                TreasureBags.LOGGER.error("Something went horribly wrong with ItemColors (Forge bug?)", ex);
            }
        }
    }

    static class Server extends SideProxy {
        Server() {
            TreasureBags.LOGGER.debug("SideProxy.Server init");
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) {
            TreasureBags.LOGGER.debug("SideProxy.Server serverSetup");
        }
    }
}
