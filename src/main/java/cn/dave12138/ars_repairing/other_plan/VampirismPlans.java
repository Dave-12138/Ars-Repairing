package cn.dave12138.ars_repairing.other_plan;

import cn.dave12138.ars_repairing.reflection.RepairPlan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VampirismPlans {
    @RepairPlan("item.vampirism.blood_bottle")
    public static boolean repairBloodBottle(Player player, ItemStack stack, int repairLevel, int extraDurability) {
        int old = stack.getDamageValue();
        int max = stack.getMaxDamage();
        if (old < max) {
            stack.setDamageValue(Math.min(old + repairLevel + extraDurability, max));
            return true;
        }
        return false;
    }
}
