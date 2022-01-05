package ru.vidtu.virtualaspectratio;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * The Mixin plugin that determines what {@link net.minecraft.client.render.GameRenderer} mixin should be loaded
 * dependending on the OptiFabric installation.
 * @author VidTu
 */
public class VARMixinPlugin implements IMixinConfigPlugin {
    public static final Logger LOG = LogManager.getLogger("VARMixinPlugin");
    /** Is OptiFabric mod loaded? */
    public static boolean optiFabric;
    /** Is OptiFabric mod loaded and current mappings is <code>named</code>? */
    public static boolean namedMappings;
    @Override
    public void onLoad(String mixinPackage) {
        optiFabric = FabricLoader.getInstance().isModLoaded("optifabric");
        if (optiFabric) {
            LOG.info("Using OptiFabric, searching for compat mapped class.");
            String mappings = FabricLoader.getInstance().getMappingResolver().getCurrentRuntimeNamespace();
            if ("named".equalsIgnoreCase(mappings)) {
                LOG.info("Mappings is 'named'. (Probably dev environment)");
                namedMappings = true;
            } else if ("intermediary".equalsIgnoreCase(mappings)) {
                LOG.info("Mappings is 'intermediary'. (Probably normal environment)");
                namedMappings = false;
            } else {
                LOG.warn("Mappings: is '" + mappings + "'. (UNKNOWN MAPPINGS; TRYING DEFAULT 'intermediary'; UNPREDICTABLE BEHAVIOUR)");
            }
        } else {
            LOG.info("Not using OptiFabric.");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equalsIgnoreCase("ru.vidtu.virtualaspectratio.mixins.OFGameRendererMixin")) return optiFabric && !namedMappings;
        if (mixinClassName.equalsIgnoreCase("ru.vidtu.virtualaspectratio.mixins.OFDeobfGameRendererMixin")) return optiFabric && namedMappings;
        if (mixinClassName.equalsIgnoreCase("ru.vidtu.virtualaspectratio.mixins.GameRendererMixin")) return !optiFabric;
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
