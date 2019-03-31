package net.silentchaos512.loot.lib;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.loot.TreasureBags;

import java.util.Locale;

/**
 * A rough group for mobs. Very similar to what Scaling Health uses, but no blights and every type
 * has a loot table.
 */
public enum MobType {
    PEACEFUL,
    HOSTILE,
    BOSS,
    PLAYER;

    private final ResourceLocation lootTable;

    MobType() {
        lootTable = TreasureBags.getId("entity_group/" + this.name().toLowerCase(Locale.ROOT));
    }

    public static MobType from(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer)
            return PLAYER;
        if (!entity.isNonBoss())
            return BOSS;
        if (entity instanceof IMob)
            return HOSTILE;
        return PEACEFUL;
    }

    public ResourceLocation getLootTable() {
        return lootTable;
    }
}
