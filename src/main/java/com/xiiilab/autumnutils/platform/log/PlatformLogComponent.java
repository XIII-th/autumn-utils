package com.xiiilab.autumnutils.platform.log;

import com.annimon.stream.Stream;
import com.badlogic.gdx.Gdx;

import java.util.Map;

/**
 * Компонент лога, который адаптирован для сбора метрик аналитики в формализованном формате
 * <br>
 * Created by XIII-th on 28.12.2017
 */
public abstract class PlatformLogComponent {

    protected IViewNameResolver _viewNameResolver;

    public PlatformLogComponent() {
        _viewNameResolver = this::defaultViewNameResolving;
    }

    /**
     * Установка объекта, который будет использоваться для получения названия экрана
     * @see IViewNameResolver
     */
    public void setViewNameResolver(IViewNameResolver viewNameResolver) {
        _viewNameResolver = viewNameResolver;
    }

    /**
     * Запрос объекта для записи информации о событии {@code event}
     * @param event объект, который является основным идентификатором события лога
     * @param cls класс, от чъего имени создаётся событие {@code event}
     * @return новый объект {@link LogBuilder} для сбора параметров
     */
    public LogBuilder event(Object event, Class<?> cls) {
        return new LogBuilder(this, event, cls);
    }

    /**
     * Сообщение об активации нового экрана. Можно использовать для сообщений об активации диалогов и
     * других графических элементов
     * @param controllerCls экран, который сейчас показывается пользователю
     * @see IViewNameResolver
     */
    public void view(Class<?> controllerCls) {
        Gdx.app.debug(getClass().getSimpleName(), "Current screen: " + _viewNameResolver.apply(controllerCls));
    }

    /**
     * Некий идентификатор пользователя, по которому можно будет различать события одного пользователя от событий другого
     * @param identifier не рекомендуется использовать почту, пароли и другую персональную информацию
     */
    public void setUserIdentifier(String identifier) {
        Gdx.app.debug(getClass().getSimpleName(), "Setup user identifier " + identifier);
    }

    /**
     * Отправка события на сервер аналитики
     * @param params параметры события
     * @see LogBuilder#local()
     */
    protected void post(Map<Object, Object> params) {
        postLocal(params);
    }

    /**
     * Вывод событий лога в консоль отладки. Примечание: сообщения в консоли будут отображаться только
     * при установке уровня логов {@code debug}
     * @param params параметры события
     * @see LogBuilder#local()
     * @see com.badlogic.gdx.Application#debug(String, String)
     */
    protected void postLocal(Map<Object, Object> params) {
        AbstractLogConsumer<StringBuilder> logConsumer = getConsumer(StringBuilder.class, params);

        String tag = logConsumer.getCls().getSimpleName(), message = logConsumer.getContainer().toString();
        if (logConsumer.getThrowable() == null)
            Gdx.app.debug(tag, message);
        else
            Gdx.app.debug(tag, message, logConsumer.getThrowable());
    }

    /**
     * В качестве названия выступает название класса. БЕсполезно использовать совместно с запутыванием кода
     */
    private String defaultViewNameResolving(Class<?> cls) {
        return cls.getSimpleName();
    }

    /**
     * Получение подготовленых параметров лога
     * @param cls класс контейнера для которого нужно вернуть {@link AbstractLogConsumer}. Используется как идентификатор
     * @param params параметры лога
     * @param <Container> тип платформозависимого контейнера
     * @return {@link AbstractLogConsumer}, в контейнер которого уже уложены данные
     * @see PlatformLogComponent#createLogConsumer(Class, int)
     */
    protected <Container> AbstractLogConsumer<Container> getConsumer(Class<Container> cls, Map<Object, Object> params) {
        AbstractLogConsumer<Container> consumer = createLogConsumer(cls, params.size());
        Stream.of(params).forEach(entry -> consumer.put(entry.getKey(), entry.getValue()));
        return consumer;
    }

    /**
     * Создание {@link AbstractLogConsumer}, в зависимости от типа {@code cls}
     * @param cls класс контейнера для которого нужно вернуть {@link AbstractLogConsumer}. Используется как идентификатор
     * @param capacity количество параметров лога, для которого нужно выделить пространство
     * @param <Container> тип платформозависимого контейнера
     * @return новый {@link AbstractLogConsumer} с контейнером типа {@code cls}
     */
    protected <Container> AbstractLogConsumer<Container> createLogConsumer(Class<Container> cls, int capacity) {
        return (AbstractLogConsumer<Container>) new DefaultLogConsumer(capacity);
    }
}
