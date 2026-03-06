package net.jmoiron.gcyrextras.api.registries;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.jmoiron.gcyrextras.GcyrExtras;
import net.jmoiron.gcyrextras.data.lang.GcyrExtrasLang;

public final class GcyrExtrasRegistries {

    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GcyrExtras.MOD_ID);

    static {
        REGISTRATE.addDataGenerator(ProviderType.LANG, GcyrExtrasLang::init);
    }

    private GcyrExtrasRegistries() {}
}
