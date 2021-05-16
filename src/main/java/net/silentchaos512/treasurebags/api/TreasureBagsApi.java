package net.silentchaos512.treasurebags.api;

import net.silentchaos512.treasurebags.setup.EntityGroups;

public class TreasureBagsApi {
    public static void createEntityGroup(IEntityGroup group) {
        EntityGroups.create(group);
    }
}
