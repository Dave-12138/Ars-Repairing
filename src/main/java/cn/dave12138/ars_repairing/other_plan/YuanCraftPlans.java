package cn.dave12138.ars_repairing.other_plan;

import cn.dave12138.ars_repairing.reflection.RepairPlan;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

//原石工艺
public class YuanCraftPlans {
    /**
     * 奇物 神秘磁力
     */
    @RepairPlan("item.primogemcraft.qwsmcl")
    public static boolean repairSmCl(Player player, ItemStack stack, int repairLevel, int extraDurability) {
//        什么意思，神秘磁力_生效？
        double old = stack.getOrCreateTag().getDouble("smcl_sx");
        stack.getOrCreateTag().putDouble("smcl_sx", Math.max(old - (repairLevel + extraDurability), 0));
//        damage就不用改了，它会自动变过去
        return true;
    }

    /**
     * 奇物 错误代码
     */
    @RepairPlan({"item.primogemcraft.jingqueyouyadaima",
            "item.primogemcraft.luanqibazaodedaima",
            "item.primogemcraft.meiyouzhushidaima",
            "item.primogemcraft.wuxiandiguidedaima",
            "item.primogemcraft.youdianqiqiaodedaima",
            "item.primogemcraft.zhongguizhongjudedaima"})
    public static boolean repairCodes(Player player, ItemStack stack, int repairLevel, int extraDurability) {
        CompoundTag tag = player.getPersistentData();
        for (int i = 0; i < 6; i++) {
            String tagName = "daima%d_naijiu".formatted(i + 1);
            tag.putBoolean(tagName, true);
        }
        return true;
    }

    /**
     * 经验书
     */
    @RepairPlan({"item.primogemcraft.liulangzhedejingyan",
            "item.primogemcraft.dayingxiongdejingyan",
            "item.primogemcraft.maoxianjiadejingyan"})
    public static boolean repairKnowledgeBook(ItemStack stack, int repairLevel, int extraDurability) {
        final String TAG_NAME = "naijiu_xianzhi";
        double old = stack.getOrCreateTag().getDouble(TAG_NAME);
        stack.getOrCreateTag().putDouble(TAG_NAME, Math.min(old + repairLevel + extraDurability, stack.getMaxDamage()));
        return true;
    }

}
