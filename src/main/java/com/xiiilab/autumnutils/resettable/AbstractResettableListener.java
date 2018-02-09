package com.xiiilab.autumnutils.resettable;

import com.github.czyzby.autumn.processor.event.EventDispatcher;
import com.github.czyzby.autumn.processor.event.EventListener;
import com.github.czyzby.autumn.processor.event.MessageListener;

import static com.github.czyzby.autumn.annotation.OnEvent.KEEP;

/**
 * Class for automatically enabled and disabled components with subscription on one event
 * (posted with {@link EventDispatcher}).
 * <br>
 * Created by XIII-th on 12.12.2017
 * @see AbstractResettableMessageListener
 */
public abstract class AbstractResettableListener<E> extends AbstractResettableMessageListener
        implements MessageListener, EventListener<E> {

    @Override
    public boolean processEvent(E e) {
        if (isEnabled())
            onEvent(e);
        return KEEP;
    }

    /**
     * Event handling method will be called only if component enabled ({@link AbstractResettable#isEnabled()} == true)
     */
    protected void onEvent(E e) {
    }
}
