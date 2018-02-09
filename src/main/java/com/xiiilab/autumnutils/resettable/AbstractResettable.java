package com.xiiilab.autumnutils.resettable;

/**
 * Base resettable class with enable marker
 * Created by XIII-th on 12.12.2017
 */
public abstract class AbstractResettable implements IResettable {

    private volatile boolean _isEnabled;

    /**
     * Override this method to disable and reset component state (release resources, close connections, etc).
     * This method automatically called by {@link ResetProcessor} if implementation annotated with {@link Resettable}
     */
    @Override
    public void reset() {
        _isEnabled = false;
    }

    /**
     * @return true, if component ready to work
     */
    public boolean isEnabled() {
        return _isEnabled;
    }

    /**
     * Set marker to enable state and call {@link AbstractResettable#onEnable()} method
     */
    public void enable() {
        _isEnabled = true;
        onEnable();
    }

    /**
     * This method called when component set enabled. This is good place to prepare component
     */
    protected void onEnable() {
    }
}
