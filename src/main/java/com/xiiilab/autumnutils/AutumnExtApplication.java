package com.xiiilab.autumnutils;

import com.github.czyzby.autumn.context.ContextInitializer;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.github.czyzby.autumn.mvc.component.i18n.processor.AvailableLocalesAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.i18n.processor.I18nBundleAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.preferences.PreferencesService;
import com.github.czyzby.autumn.mvc.component.sfx.MusicService;
import com.github.czyzby.autumn.mvc.component.sfx.processor.MusicEnabledAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.MusicVolumeAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.SoundEnabledAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.sfx.processor.SoundVolumeAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.*;
import com.github.czyzby.autumn.scanner.ClassScanner;
import com.github.czyzby.autumn.scanner.FixedClassScanner;
import com.xiiilab.autumnutils.font.FontProcessor;
import com.xiiilab.autumnutils.font.FontStyleProcessor;
import com.xiiilab.autumnutils.font.ReloadLocaleAction;
import com.xiiilab.autumnutils.resettable.ResetProcessor;
import com.xiiilab.autumnutils.rules.RulesProcessor;

/**
 * Created by XIII-th on 05.11.2017
 */
public class AutumnExtApplication extends AutumnApplication {

    private Object[] _platformSpecificComponents;

    public AutumnExtApplication(ClassScanner componentScanner, Class<?> scanningRoot) {
        super(componentScanner, scanningRoot);
        _platformSpecificComponents = new Object[0];

        // overriding default skin service. It's main aim of overriding this method
        ClassScanner scanner = new FixedClassScanner(
                ResetProcessor.class,
                FontProcessor.class,
                FontStyleProcessor.class,
                ReloadLocaleAction.class,
                FreeTypeSkinService.class,
                RulesProcessor.class
        );
        registerComponents(scanner, getClass());
    }

    /**
     * Register platform specific implementations of components
     */
    public void setPlatformSpecificComponents(Object... platformSpecificComponents) {
        _platformSpecificComponents = platformSpecificComponents;
    }

    @Override
    protected void addDefaultComponents(ContextInitializer initializer) {
        // не используем super, чтобы добавить свой SkinService
        initializer.addComponents(
                // PROCESSORS.
                // Assets:
                new AssetService(), new SkinAssetAnnotationProcessor(),
                // Locale:
                new LocaleService(),
                // SFX:
                new MusicEnabledAnnotationProcessor(), new MusicVolumeAnnotationProcessor(),
                new SoundEnabledAnnotationProcessor(), new SoundVolumeAnnotationProcessor(),
                // Settings:
                new I18nBundleAnnotationProcessor(), new PreferenceAnnotationProcessor(),
                new SkinAnnotationProcessor(),
                new StageViewportAnnotationProcessor(), new PreferencesService(),
                // Interface:
                new ViewAnnotationProcessor(), new ViewDialogAnnotationProcessor(),
                new ViewActionContainerAnnotationProcessor(), new ViewStageAnnotationProcessor(),
                new LmlMacroAnnotationProcessor(), new LmlParserSyntaxAnnotationProcessor(),
                new AvailableLocalesAnnotationProcessor(),
                // COMPONENTS.
                // SFX:
                new MusicService(),
                // Interface:
                getInterfaceService()/*, new SkinService()*/);
        initializer.addComponents(_platformSpecificComponents);
    }
}
