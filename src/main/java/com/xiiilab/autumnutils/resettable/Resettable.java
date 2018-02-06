package com.xiiilab.autumnutils.resettable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ResetProcessor} load type with this annotation and call {@link IResettable#reset()} method
 * {@link GameStage#BEFORE} or {@link GameStage#AFTER} declared controllers {@link Resettable#controllers()} activated
 * <br>
 * Created by XIII-th on 27.11.2017
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resettable {

    /**
     * Stage of reset operation
     */
    GameStage stage();

    /**
     * Controllers require to reset components
     */
    Class<?>[] controllers() default {};
}
