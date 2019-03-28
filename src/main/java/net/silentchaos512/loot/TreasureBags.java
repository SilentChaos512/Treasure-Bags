package net.silentchaos512.loot;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(TreasureBags.MOD_ID)
public final class TreasureBags {
    public static final String MOD_ID = "treasurebags";
    public static final String MOD_NAME = "Treasure Bags";
    public static final String VERSION = "0.1.0";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public static TreasureBags INSTANCE;
    public static SideProxy PROXY;

    public TreasureBags() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
    }
}
