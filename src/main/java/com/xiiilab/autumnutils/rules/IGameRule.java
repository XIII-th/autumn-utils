package com.xiiilab.autumnutils.rules;

import com.annimon.stream.function.FunctionalInterface;

/**
 * Created by XIII-th on 29.10.2017
 */
@FunctionalInterface
public interface IGameRule {

    GameRuleEvent[] checkRuleApplicable();
}
