package com.xiiilab.autumnutils.resettable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIII-th on 07.02.2018
 */
class ControllerResettable {

    private final List<IResettable> _beforeResettableList;
    private final List<IResettable> _afterResettableList;

    ControllerResettable() {
        _beforeResettableList = new ArrayList<>();
        _afterResettableList = new ArrayList<>();
    }

    public void addResettable(GameStage stage, IResettable resettable) {
        getResettableList(stage).add(resettable);
    }

    public void reset(GameStage stage) {
        for (IResettable resettable : getResettableList(stage))
            resettable.reset();
    }

    private List<IResettable> getResettableList(GameStage stage) {
        switch (stage) {
            case BEFORE:
                return _beforeResettableList;
            case AFTER:
                return _afterResettableList;
            default:
                throw new IllegalStateException("Unexpected reset game stage " + stage);
        }
    }
}
