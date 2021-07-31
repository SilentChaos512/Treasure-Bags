package net.silentchaos512.treasurebags.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public interface IEntityGroup {
    ResourceLocation getId();

    boolean matches(Entity entity);

    ResourceLocation getLootTable();
}
