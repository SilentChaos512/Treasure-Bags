package net.silentchaos512.treasurebags.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.treasurebags.TreasureBags;
import net.silentchaos512.treasurebags.api.IEntityGroup;
import net.silentchaos512.treasurebags.setup.EntityGroups;

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
        Level world = entity.level;
        if (world.isClientSide) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;

        // Mob loot disabled?
        if (!world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;

        Player player = getPlayerThatCausedDeath(event.getSource());

        // Get the bonus drops loot table for this mob type
        EntityGroups.getGroups(entity).forEach(group -> doDropsForGroup(event, entity, world, server, player, group));
    }

    private static void doDropsForGroup(LivingDropsEvent event, LivingEntity entity, Level world, MinecraftServer server, Player player, IEntityGroup group) {
        LootTable lootTable = server.getLootTables().get(group.getLootTable());
        LootContext.Builder contextBuilder = new LootContext.Builder((ServerLevel) world)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.ORIGIN, entity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, event.getSource().getDirectEntity());
        if (player != null) {
            contextBuilder.withLuck(player.getLuck());
        }
        List<ItemStack> list = lootTable.getRandomItems(contextBuilder.create(LootContextParamSets.ENTITY));
        list.forEach(stack -> event.getDrops().add(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stack)));
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
    private static Player getPlayerThatCausedDeath(DamageSource source) {
        if (source == null) return null;

        // Player is true source?
        Entity entitySource = source.getEntity();
        if (entitySource instanceof Player) {
            return (Player) entitySource;
        }

        // Player's pet is true source?
        if (entitySource instanceof TamableAnimal) {
            TamableAnimal tamed = (TamableAnimal) entitySource;
            if (tamed.isTame() && tamed.getOwner() instanceof Player) {
                return (Player) tamed.getOwner();
            }
        }

        // No player responsible
        return null;
    }
}
