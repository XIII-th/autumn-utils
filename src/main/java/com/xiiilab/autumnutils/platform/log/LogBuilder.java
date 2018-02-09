package com.xiiilab.autumnutils.platform.log;

import java.util.HashMap;
import java.util.Map;

/**
 * Компонент для сбора формализованных параметров события
 * <br>
 * Created by XIII-th on 25.12.2017
 */
public class LogBuilder {

    /**
     * Ключ, под которым всегда хранится объект события.
     * Устанавливается в {@link LogBuilder#LogBuilder(PlatformLogComponent, Object, Class)}
     */
    public static final String EVENT = "event";

    /**
     * Ключ, под которым всегда хранится класс, пороивший событие.
     * Устанавливается в {@link LogBuilder#LogBuilder(PlatformLogComponent, Object, Class)}
     */
    public static final String CLASS = "class";

    /**
     * Ключ, под которым может хранится объект исключения.
     * Устанавливается в {@link LogBuilder#add(Throwable)}
     */
    public static final String ERROR = "error";

    private final PlatformLogComponent _log;
    private final Map<Object, Object> _params;
    private boolean _local;
    private boolean _posted;

    LogBuilder(PlatformLogComponent log, Object event, Class<?> cls) {
        _log = log;
        _params = new HashMap<>();
        _params.put(EVENT, event);
        _params.put(CLASS, cls);
    }

    /**
     * Добавление объекта исключения в параметры лога. Можно добавить только одно исключение для одного события
     */
    public LogBuilder add(Throwable throwable) {
        return add(ERROR, throwable);
    }

    /**
     * Добавление параметра лога. Ключи не должна повторяться
     * @param key параметр лога
     * @param value значение параметр лога
     * @throws IllegalArgumentException при повторе параметра лога или при установке в параметр {@link LogBuilder#ERROR}
     * объекта, который не является наследником {@link Throwable}
     */
    public LogBuilder add(Object key, Object value) throws IllegalArgumentException {
        Object v = _params.get(key);
        if (v != null)
            throw new IllegalArgumentException("Key " + key + " already used for " + v +
                    ". May be you try to use reserved key word");
        else if (ERROR.equals(key) && !(value instanceof Throwable))
            throw new IllegalArgumentException("Unexpected type " + value.getClass() + " for key " + ERROR +
                    ". Only " + Throwable.class + " supported");
        _params.put(key, value);
        return this;
    }

    /**
     * Пометка события, как "локальное". Параметры такого события не будут отправлены на сервер аналитики методом
     * {@link PlatformLogComponent#post(Map)}. Вместо этого параметры события будут переданы в метод
     * {@link PlatformLogComponent#postLocal(Map)}
     */
    public LogBuilder local() {
        _local = true;
        return this;
    }

    /**
     * Публикация параметров события. Завершение работы
     * @throws IllegalStateException при повторном вызове
     */
    public void post() throws IllegalStateException {
        if (_posted)
            throw new IllegalStateException("Already posted");
        if (_local)
            _log.postLocal(_params);
        else
            _log.post(_params);
        _posted = true;
    }

    @Override
    protected void finalize() throws Throwable {
        if (!_posted)
            throw new IllegalStateException("Log event created but not posted. " +
                                            "Event: " + _params.get(EVENT) +
                                            ". Class" + _params.get(CLASS));
    }
}
