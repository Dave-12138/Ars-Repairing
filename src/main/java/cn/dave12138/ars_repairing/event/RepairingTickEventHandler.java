package cn.dave12138.ars_repairing.event;

import cn.dave12138.ars_repairing.ArsRepairing;
import cn.dave12138.ars_repairing.Config;
import cn.dave12138.ars_repairing.RepairPlans;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class RepairingTickEventHandler {
    public static final TagKey<Item> NO_REPAIR = ItemTags.create(new ResourceLocation(ArsRepairing.MOD_ID, "no_repair"));

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            if (!event.getObject().getCapability(RepairingTimer.CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(ArsRepairing.MOD_ID, RepairingTimer.NAME), new RepairingTimer.Provider());
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
//        这行我也不知道啥意思，搁等价交换抄的
        if (event.phase == TickEvent.Phase.END) {
            if (event.side.isServer()) {
                Player player = event.player;
//                "修复升级"等级,最高也就3
                double repairLevel = PerkUtil.countForPerk(RepairingPerk.INSTANCE, player);
                if (repairLevel > 0) {
                    player.getCapability(RepairingTimer.CAPABILITY).ifPresent(repairingTimer -> {
                        repairingTimer.startRepair();
                        if (repairingTimer.canRepair()) {
//                            用传参传等级可以节省一丁点微不足道的性能。。。吗？
                            repairAll(player, repairLevel);
                        }
                        repairingTimer.startRepair();
                    });
                }
//                差点把tick调用忘了
                player.getCapability(RepairingTimer.CAPABILITY).ifPresent(RepairingTimer::tick);
            }
        }


    }

    private static void repairAll(Player player, double repairLevel) {
        CapabilityRegistry.getMana(player).ifPresent(mana -> {
//        没魔力就一个也不修了
            int cost = Config.getRepairingCost();
            if (mana.getCurrentMana() > cost) {
                player.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        tryRepair(player, stack, repairLevel, cost);
                    }
                });
            }
        });
    }

    private static void tryRepair(Player player, ItemStack stack, double repairLevel, int cost) {
//        空槽不修，满耐久不修，主手正在用也不修不然可能打断操作
        if (stack.isEmpty() || stack.getDamageValue() <= 0 || (stack == player.getMainHandItem() && player.swinging)) {
            return;
        }
//        有ars_repairing:no_repair标签不修
        if (stack.is(NO_REPAIR)) {
            return;
        }
//        配置是缓存着的，怎么会有额外性能开销呢
//        单物品修一次的消耗
        //        额外修正
        int extraDurability = Config.getRepairingBonus();
        CapabilityRegistry.getMana(player).ifPresent(mana -> {
            if (mana.getCurrentMana() > cost) {
                if (RepairPlans.repair(player, stack, (int) repairLevel, extraDurability)) {
                    mana.removeMana(cost);
                }
            }
        });

//         魔艺自带的修理逻辑，感觉不如修复护符。。。强度
//        RepairingPerk.attemptRepair(stack, player);
    }

}
