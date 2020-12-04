package net.silentchaos512.treasurebags;

import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
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
import net.silentchaos512.treasurebags.setup.ModItems;
import net.silentchaos512.treasurebags.setup.ModLoot;
import net.silentchaos512.treasurebags.setup.Registration;

import java.util.Collections;

class SideProxy {
    private static final ResourceLocation STARTING_INVENTORY = TreasureBags.getId("starting_inventory");

    SideProxy() {
        Registration.register();
        Config.init();
        Network.init();
        ModLoot.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGenerators::gatherData);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        InitialSpawnItems.add(STARTING_INVENTORY, p -> {
            if (p instanceof ServerPlayerEntity) {
                return LootUtils.gift(STARTING_INVENTORY, (ServerPlayerEntity) p);
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

        private void onItemColors(ColorHandlerEvent.Item event) {
            ItemColors colors = event.getItemColors();
            if (colors == null) {
                TreasureBags.LOGGER.error("ItemColors is null!", new NullPointerException("wat?"));
                return;
            }

            try {
                colors.register(TreasureBagItem::getColor, ModItems.TREASURE_BAG);
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
