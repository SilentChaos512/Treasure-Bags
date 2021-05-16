package net.silentchaos512.treasurebags.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public interface IEntityGroup {
    ResourceLocation getId();

    boolean matches(Entity entity);

    ResourceLocation getLootTable();
}
