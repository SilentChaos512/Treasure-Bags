package net.silentchaos512.treasurebags.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.config.Config;
import net.silentchaos512.treasurebags.item.TreasureBagItem;
import net.silentchaos512.treasurebags.network.SyncBagTypesPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class BagTypeManager implements ResourceManagerReloadListener {
    public static final BagTypeManager INSTANCE = new BagTypeManager();
    private static final Marker MARKER = MarkerManager.getMarker("BagTypeManager");
    private static final String RESOURCES_PATH = "treasurebags_types";

    private static final Map<ResourceLocation, IBagType> MAP = new LinkedHashMap<>();
    private static final Collection<ResourceLocation> ERROR_LIST = new ArrayList<>();

    private BagTypeManager() {}

    @Nullable
    public static IBagType getValue(ResourceLocation id) {
        return MAP.get(id);
    }

    public static Collection<IBagType> getValues() {
        return MAP.values();
    }

    @Nullable
    public static IBagType typeFromBag(ItemStack stack) {
        if (!(stack.getItem() instanceof TreasureBagItem)) return null;
        return TreasureBagItem.getBagType(stack);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(RESOURCES_PATH, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        MAP.clear();
        ERROR_LIST.clear();
        TreasureBags.LOGGER.info(MARKER, "Reloading bag type files");

        synchronized (MAP) {
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(RESOURCES_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    if (TreasureBags.LOGGER.isTraceEnabled()) {
                        TreasureBags.LOGGER.trace(MARKER, "Found bag type file: {}, reading as '{}'", id, name);
                    }

                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        TreasureBags.LOGGER.error("Could not read bag type {}", name);
                        TreasureBags.LOGGER.error(ex);
                        ERROR_LIST.add(name);
                    }

                    if (json != null) {
                        // Deserialize bag type JSON, register it if its group is not disabled
                        IBagType type = deserialize(name, json);

                        if (!Config.Common.disabledBagGroups.get().contains(type.getGroup())) {
                            addBagType(type);
                        } else {
                            TreasureBags.LOGGER.debug("Skipping bag type \"{}\" as its group (\"{}\") is disabled in the config",
                                    type.getId(),
                                    type.getGroup());
                        }
                    }
                }
            }
        }
    }

    private static void addBagType(IBagType type) {
        MAP.put(type.getId(), type);
    }

    private static IBagType deserialize(ResourceLocation name, JsonObject json) {
        return BagType.Serializer.deserialize(name, json);
    }

    public static void handleSyncPacket(SyncBagTypesPacket packet, Supplier<NetworkEvent.Context> context) {
        MAP.clear();
        packet.getBagTypes().forEach(type -> MAP.put(type.getId(), type));
        TreasureBags.LOGGER.info("Read {} bag types from server", MAP.size());
        context.get().setPacketHandled(true);
    }

    public static Collection<Component> getErrorMessages(ServerPlayer player) {
        Collection<Component> messages = new ArrayList<>();

        // Bag type files that failed to load
        if (!ERROR_LIST.isEmpty()) {
            String listStr = ERROR_LIST.stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
            messages.add(errorMessage("The following bag types failed to load, check your log file:"));
            messages.add(Component.literal(listStr));
        }

        // Loot tables that failed to load or are missing
        int missingLootTables = countMissingLootTables(player);
        if (missingLootTables > 0) {
            String counter = missingLootTables == 1
                    ? "1 bag type has a missing or invalid loot table"
                    : missingLootTables + " bag types have missing or invalid loot tables";
            messages.add(errorMessage(counter));
        }

        return messages;
    }

    private static int countMissingLootTables(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return 0;

        Collection<ResourceLocation> lootTables = server.getLootData().getKeys(LootDataType.TABLE);
        return (int) MAP.values().stream().filter(bagType -> !lootTables.contains(bagType.getLootTable())).count();
    }

    private static Component errorMessage(String str) {
        return Component.literal("[Treasure Bags] ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(str).withStyle(ChatFormatting.WHITE));
    }
}
