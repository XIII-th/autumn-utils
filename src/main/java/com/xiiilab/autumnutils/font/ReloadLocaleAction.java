package com.xiiilab.autumnutils.font;

import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.mvc.component.i18n.LocaleService;
import com.xiiilab.autumnutils.FreeTypeSkinService;

/**
 * This component required for notifying custom ({@link FreeTypeSkinService}) about font reloading request
 * <br>
 * Created by XIII-th on 05.01.2018
 */
@Component
public class ReloadLocaleAction extends LocaleService.LocaleChangeAction {

    private final FreeTypeSkinService _skinService;

    public ReloadLocaleAction(LocaleService localeService, FreeTypeSkinService skinService) {
        super(localeService);
        _skinService = skinService;
        localeService.setActionOnLocaleChange(this);
    }

    @Override
    public void run() {
        _skinService.init();
        super.run();
    }
}
