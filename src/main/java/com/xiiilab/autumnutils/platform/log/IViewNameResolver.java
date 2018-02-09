package com.xiiilab.autumnutils.platform.log;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.FunctionalInterface;

/**
 * Реализация будет использоваться для получения названия экрана в {@link PlatformLogComponent#view(Class)}
 * <br>
 * Created by XIII-th on 07.02.2018
 */
@FunctionalInterface
public interface IViewNameResolver extends Function<Class<?>, String> {
}
