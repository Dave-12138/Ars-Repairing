package cn.dave12138.ars_repairing;

//import cn.dave12138.ars_repairing.registry.ModRegistry;

import cn.dave12138.ars_repairing.other_plan.ArsNouveauPlans;
import cn.dave12138.ars_repairing.other_plan.VampirismPlans;
import cn.dave12138.ars_repairing.other_plan.YuanCraftPlans;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArsRepairing.MOD_ID)
public class ArsRepairing {
    public static final String MOD_ID = "ars_repairing";

    private static final Logger LOGGER = LogManager.getLogger();

    public ArsRepairing() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
//        ModRegistry.registerRegistries(modbus);
//        ArsNouveauRegistry.registerGlyphs();
        modbus.addListener(this::setup);
//        modbus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    //    public static ResourceLocation prefix(String path) {
//        return new ResourceLocation(MOD_ID, path);
//    }
//
    private void setup(final FMLCommonSetupEvent event) {
//        ArsNouveauRegistry.registerSounds();
//        这里应该用反射扫描批量注册的，可惜java根本没办法很方便的这么写
//        换C#直接秒了（
        RepairPlans.registerPlanClass(ArsNouveauPlans.class);
        RepairPlans.registerPlanClass(YuanCraftPlans.class);
        RepairPlans.registerPlanClass(VampirismPlans.class);
    }
//
//    private void doClientStuff(final FMLClientSetupEvent event) {
//
//    }
//
//    // You can use SubscribeEvent and let the Event Bus discover methods to call
//    @SubscribeEvent
//    public void onServerStarting(ServerStartingEvent event) {
//        // do something when the server starts
//        LOGGER.info("HELLO from server starting");
//    }

}
