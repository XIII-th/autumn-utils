package com.xiiilab.autumnutils.resettable;

import com.github.czyzby.autumn.annotation.OnMessage;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.autumn.processor.event.MessageListener;

import static com.github.czyzby.autumn.annotation.OnMessage.KEEP;

/**
 * Extension of {@link AbstractResettable} for automatically enable by message (from {@link MessageDispatcher}).
 * Implementation must be annotated with {@link OnMessage} annotation with enable message
 * <br>
 * Created by XIII-th on 12.12.2017
 * @see AbstractResettable
 * @see MessageDispatcher
 */
public abstract class AbstractResettableMessageListener extends AbstractResettable implements
        MessageListener {

    @Override
    public boolean processMessage() {
        if (!isEnabled())
            enable();
        return KEEP;
    }
}
