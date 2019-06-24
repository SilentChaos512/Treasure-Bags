package net.silentchaos512.loot.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.loot.TreasureBags;
import net.silentchaos512.loot.lib.EntityGroup;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = TreasureBags.MOD_ID)
public final class EventHandler {
    private EventHandler() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // Mostly copied from Scaling Health
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        World world = entity.world;
        if (world.isRemote) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;

        // Mob loot disabled?
        if (!world.getGameRules().getBoolean("doMobLoot")) return;

        PlayerEntity player = getPlayerThatCausedDeath(event.getSource());

        // Get the bonus drops loot table for this mob type
        EntityGroup group = EntityGroup.from(entity);
        /*if (TreasureBags.LOGGER.isDebugEnabled()) {
            String playerName = player != null ? player.getScoreboardName() : "null";
            TreasureBags.LOGGER.debug("{} killed mob of type {}", playerName, group);
        }*/

        LootTable lootTable = server.getLootTableManager().getLootTableFromLocation(group.getLootTable());
        LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world)
                .withParameter(LootParameters.THIS_ENTITY, entity)
                .withParameter(LootParameters.POSITION, entity.getPosition())
                .withParameter(LootParameters.DAMAGE_SOURCE, event.getSource())
                .withNullableParameter(LootParameters.KILLER_ENTITY, player)
                .withNullableParameter(LootParameters.LAST_DAMAGE_PLAYER, player)
                .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, event.getSource().getImmediateSource());
        if (player != null) {
            contextBuilder.withLuck(player.getLuck());
        }
        List<ItemStack> list = lootTable.generate(contextBuilder.build(LootParameterSets.ENTITY));
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
    private static PlayerEntity getPlayerThatCausedDeath(DamageSource source) {
        if (source == null) return null;

        // Player is true source?
        Entity entitySource = source.getTrueSource();
        if (entitySource instanceof PlayerEntity) {
            return (PlayerEntity) entitySource;
        }

        // Player's pet is true source?
        if (entitySource instanceof TameableEntity) {
            TameableEntity tamed = (TameableEntity) entitySource;
            if (tamed.isTamed() && tamed.getOwner() instanceof PlayerEntity) {
                return (PlayerEntity) tamed.getOwner();
            }
        }

        // No player responsible
        return null;
    }
}
