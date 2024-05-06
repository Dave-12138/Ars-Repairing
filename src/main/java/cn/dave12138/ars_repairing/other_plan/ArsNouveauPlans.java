package cn.dave12138.ars_repairing.other_plan;

import cn.dave12138.ars_repairing.reflection.RepairPlan;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArsNouveauPlans {
    private static final String POTION_FLASK_NAME = "item.ars_nouveau.potion_flask";
    private static final String POTION_FLASK_NAME_2 = "item.ars_nouveau.potion_flask_extend_time";
    private static final String POTION_FLASK_NAME_3 = "item.ars_nouveau.potion_flask_amplify";

    @RepairPlan({POTION_FLASK_NAME, POTION_FLASK_NAME_2, POTION_FLASK_NAME_3})
    public static boolean repairFlask(Player player, ItemStack stack, int repairLevel, int extraDurability) {
//        因为是新生魔艺的附属mod，可以直接调用新生魔艺里的实现
        if (stack.getItem() instanceof PotionFlask flask) {
            var data = new PotionFlask.FlaskData(stack);
            if (data.getCount() > 0 && !flask.isMax(stack)) {
//                等级和额外值无所谓了，就一次修1
                data.setCount(data.getCount() + 1);
                return true;
            }
        }
        return false;
    }
}
