package com.xiiilab.autumnutils.resettable;

import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XIII-th on 27.11.2017
 */
@Processor
public class ResetProcessor extends AbstractAnnotationProcessor<Resettable> {

    private Map<Class<?>, ControllerResettable> _resettableMap;
    private ControllerResettable _anyResettable;

    public ResetProcessor() {
        _resettableMap = new HashMap<>();
        _anyResettable = new ControllerResettable();
    }

    @Override
    public Class<Resettable> getSupportedAnnotationType() {
        return Resettable.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void doBeforeScanning(ContextInitializer initializer) {
        initializer.scanFor(Resettable.class);
    }

    @Override
    public void processType(Class<?> type, Resettable annotation, Object component, Context context,
            ContextInitializer initializer, ContextDestroyer contextDestroyer) {
        if (component instanceof IResettable)
            addResettable((IResettable) component, annotation.stage(), annotation.controllers());
        else
            throw new IllegalStateException(
                    "Class " + type + " must implements interface " + IResettable.class);
    }

    public void reset(Class<?> cls, GameStage stage) {
        ControllerResettable controllerResettable = _resettableMap.get(cls);
        if (controllerResettable != null)
            controllerResettable.reset(stage);
        _anyResettable.reset(stage);
    }

    public void addResettable(IResettable resettable, GameStage stage, Class... controllers) {
        if (controllers.length == 0)
            _anyResettable.addResettable(stage, resettable);
        else
            for (Class cls : controllers) {
                ControllerResettable controllerResettable = _resettableMap.get(cls);
                if (controllerResettable == null)
                    _resettableMap.put(cls, controllerResettable = new ControllerResettable());
                controllerResettable.addResettable(stage, resettable);
            }
    }

}
