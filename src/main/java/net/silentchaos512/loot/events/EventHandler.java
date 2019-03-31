package net.silentchaos512.loot.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.lib.MobType;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TreasureBags.MOD_ID)
public final class EventHandler {
    private EventHandler() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // Mostly copied from Scaling Health
        if (!(event.getEntity() instanceof EntityLivingBase)) return;

        EntityLivingBase entity = (EntityLivingBase) event.getEntity();
        World world = entity.world;
        if (world.isRemote) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;

        // Mob loot disabled?
        if (!world.getGameRules().getBoolean("doMobLoot")) return;

        EntityPlayer player = getPlayerThatCausedDeath(event.getSource());

        // Get the bonus drops loot table for this mob type
        MobType type = MobType.from(entity);
        if (TreasureBags.LOGGER.isDebugEnabled()) {
            String playerName = player != null ? player.getScoreboardName() : "null";
            TreasureBags.LOGGER.debug("{} killed mob of type {}", playerName, type);
        }

        LootTable lootTable = server.getLootTableManager().getLootTableFromLocation(type.getLootTable());
        LootContext.Builder contextBuilder = new LootContext.Builder((WorldServer) world)
                .withDamageSource(event.getSource())
                .withLootedEntity(entity);
        if (player != null) contextBuilder.withLuck(player.getLuck()).withPlayer(player);
        List<ItemStack> list = lootTable.generateLootForPools(TreasureBags.RANDOM, contextBuilder.build());
        list.forEach(stack -> event.getDrops().add(entity.entityDropItem(stack)));
    }

    /**
     * Get the player that caused a mob's death. Could be a FakePlayer or null. Copied from Scaling
     * Health.
     *
     * @return The player that caused the damage, or the owner of the tamed animal that caused the
     * damage.
     */
    @SuppressWarnings("ChainOfInstanceofChecks")
    @Nullable
    private static EntityPlayer getPlayerThatCausedDeath(DamageSource source) {
        if (source == null) return null;

        // Player is true source?
        Entity entitySource = source.getTrueSource();
        if (entitySource instanceof EntityPlayer) {
            return (EntityPlayer) entitySource;
        }

        // Player's pet is true source?
        if (entitySource instanceof EntityTameable) {
            EntityTameable tamed = (EntityTameable) entitySource;
            if (tamed.isTamed() && tamed.getOwner() instanceof EntityPlayer) {
                return (EntityPlayer) tamed.getOwner();
            }
        }

        // No player responsible
        return null;
    }
}
