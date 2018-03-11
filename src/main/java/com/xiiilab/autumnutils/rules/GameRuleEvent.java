package com.xiiilab.autumnutils.rules;

/**
 * Created by Sergey on 29.10.2017
 */

public abstract class GameRuleEvent {

    public final String eventName;

    public GameRuleEvent(String eventName) {
        this.eventName = eventName;
    }
}
