package com.xiiilab.autumnutils.platform.log;

import static com.xiiilab.autumnutils.platform.log.LogBuilder.*;

/**
 * Компонент для укладки данных лога в платформозависимый контейнер
 * <br>
 * Created by XIII-th on 08.02.2018
 */
public abstract class AbstractLogConsumer<Container> {

    protected Container _container;
    protected Object _event;
    protected Class<?> _cls;
    protected Throwable _throwable;

    public AbstractLogConsumer(Container container) {
        _container = container;
    }

    /**
     * Контейнер с данными лога
     */
    public Container getContainer() {
        return _container;
    }

    /**
     * Получение события, которое было обозначено при обращении к логу.
     * Устанавливается атоматически в {@link PlatformLogComponent#event(Object, Class)}
     */
    public Object getEvent() {
        return _event;
    }

    /**
     * Получение класса, который обратился к логу.
     * Устанавливается атоматически в {@link PlatformLogComponent#event(Object, Class)}
     */
    public Class<?> getCls() {
        return _cls;
    }

    /**
     * Получение исключения, если оно было установлено в {@link LogBuilder#add(Throwable)}
     */
    public Throwable getThrowable() {
        return _throwable;
    }

    /**
     * Установка с преобразованием значения в {@link String}, {@link Long} или {@link Double}
     * в зависимости от типа {@code v}. Преобразованное значение будет передано в
     * {@link AbstractLogConsumer#putString(Object, String)}, {@link AbstractLogConsumer#putLong(Object, Long)} или
     * {@link AbstractLogConsumer#putDouble(Object, Double)} соответственно
     */
    public void put(Object key, Object v) {
        if (EVENT.equals(key))
            _event = v;
        else if (CLASS.equals(key))
            _cls = (Class<?>) v;
        else if (ERROR.equals(key))
            _throwable = (Throwable) v;

        if (v instanceof Float || v instanceof Double)
            putDouble(key, ((Number) v).doubleValue());
        else if (v instanceof Integer || v instanceof Short || v instanceof Byte)
            putLong(key, ((Number) v).longValue());
        else
            putString(key, String.valueOf(v));
    }

    protected abstract void putString(Object key, String v);

    protected abstract void putDouble(Object key, Double v);

    protected abstract void putLong(Object key, Long v);
}
