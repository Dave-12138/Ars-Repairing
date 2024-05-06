package cn.dave12138.ars_repairing.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RepairPlan {
    /**
     * 物品本地化键名
     */
    String[] value() default {};
}
