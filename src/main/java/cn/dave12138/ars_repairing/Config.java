package cn.dave12138.ars_repairing;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsRepairing.MOD_ID)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER;
    private static final ForgeConfigSpec.IntValue REPAIRING_DELAY;
    private static final ForgeConfigSpec.IntValue REPAIRING_COST;
    private static final ForgeConfigSpec.IntValue REPAIRING_BONUS;
    static final ForgeConfigSpec SPEC;

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        REPAIRING_DELAY = BUILDER
                .comment("两次维修之间的间隔，单位Tick。 20 ticks = 1秒。魔艺原版为200。-1关闭，最小0，默认为20。")
                .defineInRange("repairingDelay", 20, -1, Integer.MAX_VALUE);
        REPAIRING_COST = BUILDER
                .comment("每次维修一个物品（格）时消耗的魔力，单位为Mana/个物品。默认 20。")
                .defineInRange("repairingCost", 20, 0, Integer.MAX_VALUE);
        REPAIRING_BONUS = BUILDER
                .comment("每次维修提升的耐久值是修复纤维等级+该值，魔艺原版为1。如果为负数可以消耗魔力倒扣耐久。这里默认为0。")
                .defineInRange("repairingBonus", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SPEC = BUILDER.build();
    }

    public static int getRepairingDelay() {
        return repairingDelay;
    }

    public static int getRepairingCost() {
        return repairingCost;
    }

    public static int getRepairingBonus() {
        return repairingBonus;
    }
    private static int repairingDelay;
    private static int repairingCost;
    private static int repairingBonus;


    private static void renewConfig() {
        repairingDelay = REPAIRING_DELAY.get();
        repairingCost = REPAIRING_COST.get();
        repairingBonus = REPAIRING_BONUS.get();
//        Logger.getLogger("======currentMOD======").log(Level.WARNING, "Config.repairingDelay=" + repairingDelay);
   }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent event) {
        renewConfig();
    }



}
