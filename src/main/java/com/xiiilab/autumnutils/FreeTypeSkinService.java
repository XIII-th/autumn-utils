package com.xiiilab.autumnutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Dispose;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.InterfaceService;
import com.github.czyzby.autumn.mvc.component.ui.dto.SkinData;
import com.github.czyzby.autumn.mvc.component.ui.processor.SkinAnnotationProcessor;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableArray;
import com.github.czyzby.kiwi.util.gdx.file.CommonFileExtension;
import com.xiiilab.autumnutils.font.FontProcessor;

import java.util.HashMap;
import java.util.Map;

import static com.github.czyzby.autumn.mvc.config.AutumnActionPriority.TOP_PRIORITY;

/**
 * Created by XIII-th on 05.01.2018
 */
@Dispose
@Component
public class FreeTypeSkinService implements Disposable {

    private final InterfaceService interfaceService;
    private final SkinAnnotationProcessor _skinAnnotationProcessor;
    private final MessageDispatcher _messageDispatcher;
    private final FontProcessor _fontProcessor;
    private final AssetService _assetService;
    private final DisposableArray<Skin> skins;

    private final Map<String, TextureAtlas> _atlasMap;

    public FreeTypeSkinService(InterfaceService interfaceService,
                               SkinAnnotationProcessor skinAnnotationProcessor,
                               MessageDispatcher messageDispatcher,
                               FontProcessor fontProcessor,
                               AssetService assetService) {
        this.interfaceService = interfaceService;
        _skinAnnotationProcessor = skinAnnotationProcessor;
        _messageDispatcher = messageDispatcher;
        _fontProcessor = fontProcessor;
        _assetService = assetService;
        skins = DisposableArray.newArray();
        _atlasMap = new HashMap<>();
    }

    @Initiate(priority = TOP_PRIORITY)
    public void init() {
        _fontProcessor.clearCache();
        for (final Entry<String, SkinData> skinData : _skinAnnotationProcessor.getSkinsData()) {
            final Skin skin = new Skin();
            _fontProcessor.setupFonts(skin);
            skin.addRegions(getAtlas(skinData.value.getPath()));
            skin.load(Gdx.files.internal(skinData.value.getPath() + CommonFileExtension.JSON));
            skins.add(skin);
            interfaceService.getParser().getData().addSkin(skinData.key, skin);
        }
        _messageDispatcher.postMessage(AutumnMessage.SKINS_LOADED);
    }

    @Override
    public void dispose() {
        for (TextureAtlas atlas : _atlasMap.values())
            atlas.dispose();
        _atlasMap.clear();
    }

    private TextureAtlas getAtlas(String atlasPath) {
        TextureAtlas atlas = _atlasMap.get(atlasPath);
        if (atlas == null) {
            String path = atlasPath + CommonFileExtension.ATLAS;
            _assetService.load(path, TextureAtlas.class);
            TextureAtlas skinAtlas = _assetService.finishLoading(path, TextureAtlas.class);
            _atlasMap.put(atlasPath, atlas = skinAtlas);
        }
        return atlas;
    }
}
