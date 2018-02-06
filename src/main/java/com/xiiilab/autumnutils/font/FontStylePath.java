package com.xiiilab.autumnutils.font;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for string path to json file with font styles definitions
 * <br>
 * Created by XIII-th on 06.01.2018
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FontStylePath {
}
