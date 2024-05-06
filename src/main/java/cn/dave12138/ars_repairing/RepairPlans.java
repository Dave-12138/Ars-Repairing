package cn.dave12138.ars_repairing;

import cn.dave12138.ars_repairing.reflection.RepairPlan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RepairPlans {
    //    暂时不记录类，用不着
    public static void registerPlanClass(Class<?> planClass) {

//        Logger.getLogger("===").log(Level.WARNING, ("===========register:" + planClass.getName()));
        Arrays.stream(planClass.getMethods()).filter(method -> method.isAnnotationPresent(RepairPlan.class))
                .forEach(method -> {
                    String[] keys = method.getAnnotation(RepairPlan.class).value();
                    for (String key : keys) PLAN_MAP.put(key, method);
                });
    }

    private static final Map<String, Method> PLAN_MAP = new HashMap<>();
    private static final Map<String, int[]> PLAN_PARAMS_MAP = new HashMap<>();

    private static Method findMethod(String descriptionId) {
        return PLAN_MAP.get(descriptionId);
    }


    private static int[] getParamMap(String descriptionId) {
        return PLAN_PARAMS_MAP.get(descriptionId);
    }

    private static void setParamMap(String descriptionId, int[] paramMap) {
        PLAN_PARAMS_MAP.put(descriptionId, paramMap);
    }


    public static boolean repair(Player player, ItemStack stack, int repairLevel, int extraDurability) {
        String translationKey = stack.getItem().getDescriptionId();
//        Logger.getLogger("===").log(Level.WARNING, "===========name:" + translationKey);
        try {
            Method method = findMethod(translationKey);
//            Logger.getLogger("===").log(Level.WARNING, "===========method:" + (method == null ? "null" : method.getName()));
            if (method != null) {
                Parameter[] methodParams = method.getParameters();
//                先看看有没有函数参数映射缓存
                int[] params = getParamMap(translationKey);
                Object[] spareParams = new Object[]{player, stack, repairLevel, extraDurability};
                if (params == null) {
                    params = new int[methodParams.length];
                    String[] spareParamsNames = new String[]{"player", "stack", "repairLevel", "extraDurability"};
                    for (int i = 0; i < params.length; i++) {
                        for (int j = 0; j < spareParams.length; j++) {
                            Object spareParam = spareParams[j];
//                          每个参数只填一次                      取名字，编译时开了 -parameters 才能这么用                取类型凑合一下
                            if (spareParam != null && (methodParams[i].getName().equals(spareParamsNames[j]) || methodParams[i].getType().equals(spareParam.getClass()))) {
                                params[i] = j;
                                spareParams[j] = null;
                            }
                        }
                    }
//                    保存参数顺序映射
                    setParamMap(translationKey, params);
                }
//                参数转成目标函数参数，反射调用
                Object returnVal = method.invoke(null, Arrays.stream(params).mapToObj(i -> spareParams[i]).toArray());
//                有返回值按true为修成功 false修失败，没返回值就当成功了
                if (method.getReturnType().getName().equals(boolean.class.getName())) {
                    return (boolean) returnVal;
                }
                return true;
            }
        } catch (Exception e) {
            // do nothing
        }
        return normalItemRepair(stack, repairLevel, extraDurability);
    }

    public static boolean normalItemRepair(ItemStack stack, int repairLevel, int extraDurability) {
        int oldDamage = stack.getDamageValue();
        int dif = Math.min(stack.getDamageValue(), extraDurability + repairLevel);
        stack.setDamageValue(oldDamage - dif);
        return stack.getDamageValue() < oldDamage;
    }
}
