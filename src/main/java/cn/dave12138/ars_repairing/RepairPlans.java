package cn.dave12138.ars_repairing;

import cn.dave12138.ars_repairing.reflection.RepairPlan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RepairPlans {
    //    暂时不记录类，用不着
    public static void registerPlanClass(Class<?> planClass) {
        ArsRepairing.LOGGER.info ("Ars Repairing register:" + planClass.getName());
        for (Method method : planClass.getMethods()) {
            if (method.isAnnotationPresent(RepairPlan.class)) {
                String[] keys = method.getAnnotation(RepairPlan.class).value();
                for (String key : keys) {
                    PLAN_MAP.put(key, method);
                    PLAN_PARAMS_MAP.remove(key);
                }
            }
        }
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

    private static String[] spareParamsNames;

    private static String[] getSpareParamsNames() {
        return spareParamsNames;
    }

    static {
        Method[] methodsInThisClass = RepairPlans.class.getDeclaredMethods();
        Arrays.stream(methodsInThisClass).filter(method -> "repair".equals(method.getName())).findFirst().ifPresentOrElse(
                repairMethod -> spareParamsNames = Arrays.stream(repairMethod.getParameters()).map(Parameter::getName).toList().toArray(new String[0]),
                () -> spareParamsNames = new String[]{"player", "stack", "repairLevel", "extraDurability"}//按理来说不可能出现呢这种情况吧
        );
    }

    public static boolean repair(Player player, ItemStack stack, int repairLevel, int extraDurability) {
        String translationKey = stack.getItem().getDescriptionId();
        Method method = findMethod(translationKey);
        if (method != null) {
            try {
                Parameter[] methodParams = method.getParameters();
                Object[] spareParams = {player, stack, repairLevel, extraDurability};

//                先看看有没有函数参数映射缓存
                int[] params = getParamMap(translationKey);
                if (params == null) {
                    params = createParamMap(methodParams, spareParams, getSpareParamsNames());
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

            } catch (Exception e) {
                ArsRepairing.LOGGER.debug("exception? how?");
                ArsRepairing.LOGGER.debug(e.getStackTrace());
            }
        }
        return normalItemRepair(stack, repairLevel, extraDurability);
    }

    private static int @NotNull [] createParamMap(Parameter[] methodParams, Object[] spareParams, String[] spareParamsNames) {
        int[] params;
        params = new int[methodParams.length];
        Object[] spareParamsTemp = Arrays.copyOf(spareParams, spareParams.length);
        for (int i = 0; i < params.length; i++) {
            for (int j = 0; j < spareParamsTemp.length; j++) {
                Object spareParam = spareParamsTemp[j];
//                          每个参数只填一次                      取名字，编译时开了 -parameters 才能这么用                取类型凑合一下
                if (spareParam != null && (methodParams[i].getName().equals(spareParamsNames[j]) || methodParams[i].getType().equals(spareParam.getClass()))) {
                    params[i] = j;
                    spareParamsTemp[j] = null;
                }
            }
        }
        return params;
    }

    public static boolean normalItemRepair(ItemStack stack, int repairLevel, int extraDurability) {
        int oldDamage = stack.getDamageValue();
        int dif = Math.min(stack.getDamageValue(), extraDurability + repairLevel);
        stack.setDamageValue(oldDamage - dif);
        return stack.getDamageValue() < oldDamage;
    }

}
