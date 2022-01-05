package ru.vidtu.virtualaspectratio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.GameRenderer;

import java.lang.reflect.Modifier;
import java.nio.file.Files;

/**
 * A configuration class for VirtualAspectRatio mod.
 * Read and written via {@link #GSON}.
 * @author VidTu
 * @see VARConfigScreen
 */
public class VARConfig {
    /** GSON wrapper with <code>static</code> support. */
    public static final transient Gson GSON = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    /** Is the mod enabled? */
    public static boolean enabled = true;
    /** Is the mod affects player first? */
    public static boolean hand = true;
    /**
     * The actual aspect ratio (width to height) property.
     * This is not used by renderer, but calculated into {@link #aspectRatioLog}.<br>
     * Value range: <code>0.0 - 1.0</code>
     */
    public static float aspectRatio = 0.5F;
    /**
     * Should the mod use original display(screen) ratio as starting point.
     * (<code>0.5</code> for {@link #aspectRatio}; <code>1</code> for {@link #aspectRatioLog})
     */
    public static boolean respectDisplayRatio = true;

    /**
     * Calculated ratio used by renderer.
     * Named for logarithm, though calculation is proceeded w/o it.
     * Never saved. (<code>transient</code>)
     * Value range: <code>0.1 - 10.0</code>
     * @see #calculateRatio()
     */
    public static transient float aspectRatioLog;
    /**
     * Is the mod disallowed by the current server?
     * Never saved. (<code>transient</code>)
     * @see VirtualAspectRatio#PACKET_IDENTIFIER
     */
    public static transient boolean disallowed = false;

    /**
     * Load the configuration.
     */
    public static void load() {
        try {
            var p = FabricLoader.getInstance().getConfigDir().resolve("virtualaspectratio.json");
            if (Files.exists(p)) {
                GSON.fromJson(Files.readString(p), VARConfig.class);
            }
        } catch (Throwable t) {
            VirtualAspectRatio.LOG.warn("Unable to load VAR config", t);
        }
        calculateRatio();
    }

    /**
     * Calculate the {@link #aspectRatioLog} from the {@link #aspectRatio}.
     */
    public static void calculateRatio() {
        aspectRatioLog = aspectRatio < 0.5D?aspectRatio * 1.8F + 0.1F:(aspectRatio - 0.5F) * 18F + 1F;
    }

    /**
     * Save the configuration.
     */
    public static void save() {
        try {
            var p = FabricLoader.getInstance().getConfigDir().resolve("virtualaspectratio.json");
            Files.createDirectories(p.getParent());
            Files.writeString(p, GSON.toJson(new VARConfig()));
        } catch (Throwable t) {
            VirtualAspectRatio.LOG.warn("Unable to save VAR config", t);
        }
    }
}
