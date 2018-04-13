package com.xiiilab.autumnutils.rules;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.github.czyzby.autumn.annotation.Processor;
import com.github.czyzby.autumn.context.Context;
import com.github.czyzby.autumn.context.ContextDestroyer;
import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.processor.AbstractAnnotationProcessor;
import com.github.czyzby.autumn.processor.event.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIII-th on 29.10.2017
 */
@Processor
public class RulesProcessor extends AbstractAnnotationProcessor<GameRule> {

    private final EventDispatcher _dispatcher;
    private HashMap<IGameRule, Integer> _tempSortingMap;
    private List<IGameRule> _rules;

    private boolean _enabled;

    public RulesProcessor(EventDispatcher dispatcher) {
        _dispatcher = dispatcher;
    }

    @Override
    public Class<GameRule> getSupportedAnnotationType() {
        return GameRule.class;
    }

    @Override
    public boolean isSupportingTypes() {
        return true;
    }

    @Override
    public void doBeforeScanning(ContextInitializer initializer) {
        initializer.scanFor(GameRule.class);
        _tempSortingMap = new HashMap<>();
    }

    @Override
    public void processType(Class<?> type, GameRule annotation, Object component, Context context,
                            ContextInitializer initializer, ContextDestroyer contextDestroyer) {
        if (component instanceof IGameRule)
            _tempSortingMap.put((IGameRule) component, annotation.order());
        else
            throw new IllegalStateException("Class " + type + " annotated as " + annotation +
                    " does not implement " + IGameRule.class);
    }

    @Override
    public void doAfterScanning(ContextInitializer initializer, Context context, ContextDestroyer destroyer) {
        // отсортируем правила
        _rules = new ArrayList<>(_tempSortingMap.size());
        Stream.of(_tempSortingMap).
                sorted(ComparatorCompat.comparingInt(Map.Entry::getValue)).
                forEach(entry -> _rules.add(entry.getKey()));
        _tempSortingMap = null;
    }

    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    public void checkRules() {
        if (_enabled)
            for (IGameRule rule : _rules)
                for (GameRuleEvent event : rule.checkRuleApplicable())
                    _dispatcher.postEvent(event);
    }
}
