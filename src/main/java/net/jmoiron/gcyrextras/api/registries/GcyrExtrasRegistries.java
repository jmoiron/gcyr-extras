package net.jmoiron.gcyrextras.api.registries;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.data.lang.GcyrExtrasLang;
import net.jmoiron.gcyrextras.data.GcyrExtrasModels;

public final class GcyrExtrasRegistries {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GcyrExtras.MOD_ID);

    static {
        REGISTRATE.addDataGenerator(ProviderType.BLOCKSTATE, p -> GcyrExtrasModels.initBlockStates((com.gregtechceu.gtceu.api.registry.registrate.provider.GTBlockstateProvider) p));
        REGISTRATE.addDataGenerator(ProviderType.ITEM_MODEL, GcyrExtrasModels::initItemModels);
        REGISTRATE.addDataGenerator(ProviderType.LANG, GcyrExtrasLang::init);
    }

    private GcyrExtrasRegistries() {}
}
