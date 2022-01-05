package ru.vidtu.virtualaspectratio;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * The ModMenu screen integration.
 * @author VidTu
 */
public class VARModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return s -> new VARConfigScreen(s);
    }
}
