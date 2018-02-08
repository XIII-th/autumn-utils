package com.xiiilab.autumnutils.platform.log;

/**
 * Реализация, которую можно использовать для сбора данных лога в строку. Контейнером выступает {@link StringBuilder}
 * <br>
 * Created by XIII-th on 08.02.2018
 */
public class DefaultLogConsumer extends AbstractLogConsumer<StringBuilder> {

    public DefaultLogConsumer(int capacity) {
        super(new StringBuilder(capacity * 5 /*добавляется по 5 элементов на ключ*/));
    }

    @Override
    protected void putString(Object key, String v) {
        addPair(key, v);
    }

    @Override
    protected void putDouble(Object key, Double v) {
        addPair(key, v);
    }

    @Override
    protected void putLong(Object key, Long v) {
        addPair(key, v);
    }

    private void addPair(Object k, Object v) {
        _container.append('(').append(k).append('=').append(v).append(')');
    }
}
