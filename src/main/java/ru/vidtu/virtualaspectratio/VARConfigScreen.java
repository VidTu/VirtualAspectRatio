package ru.vidtu.virtualaspectratio;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

/**
 * A configuration screen for the VirtualAspectRatio mod.
 * @author VidTu
 * @see VARConfig
 */
public class VARConfigScreen extends Screen {
    /** The decimal format (two decimal digits) for the {@link #aspectRatio}. */
    public static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    /** The previous screen. */
    public final Screen prev;
    /** Widget for the {@link VARConfig#enabled}. */
    public CheckboxWidget enabled;
    /** Widget for the {@link VARConfig#hand}. */
    public CheckboxWidget hand;
    /** Widget for the {@link VARConfig#aspectRatio}. */
    public SliderWidget aspectRatio;
    /** Widget for the {@link VARConfig#respectDisplayRatio}. */
    public CheckboxWidget respectDisplayRatio;
    /** The warning text. (shown if {@link VARConfig#disallowed} is enabled) */
    public MultilineText warnText;

    /**
     * Construct a new configuration screen.
     * @param prev Previous screen
     */
    public VARConfigScreen(Screen prev) {
        super(new TranslatableText("virtualaspectratio.config"));
        this.prev = prev;
    }

    @Override
    public void init() {
        if (enabled != null) removed();
        enabled = addButton(new ListenerCheckboxWidget(width / 2 - textRenderer.getWidth(new TranslatableText("virtualaspectratio.enabled")) / 2 - 12,
                20, 24 + textRenderer.getWidth(new TranslatableText("virtualaspectratio.enabled")), 20,
                new TranslatableText("virtualaspectratio.enabled"), VARConfig.enabled, cb -> VARConfig.enabled = cb.isChecked()));
        hand = addButton(new ListenerCheckboxWidget(width / 2 - textRenderer.getWidth(new TranslatableText("virtualaspectratio.hand")) / 2 - 12,
                44, 24 + textRenderer.getWidth(new TranslatableText("virtualaspectratio.hand")), 20,
                new TranslatableText("virtualaspectratio.hand"), VARConfig.hand, cb -> VARConfig.hand = cb.isChecked()));
        aspectRatio = addButton(new SliderWidget(width / 2 - 75, 68, 150, 20,
                new TranslatableText("options.generic_value", new TranslatableText("virtualaspectratio.aspectratio"),
                        new LiteralText(FORMAT.format(VARConfig.aspectRatioLog))), VARConfig.aspectRatio) {
            @Override
            public void updateMessage() {
                setMessage(new TranslatableText("options.generic_value", new TranslatableText("virtualaspectratio.aspectratio"),
                        new LiteralText(FORMAT.format(value < 0.5D?value * 1.8F + 0.1F:(value - 0.5F) * 18F + 1F))));
            }

            @Override
            public void applyValue() {
                VARConfig.aspectRatio = (float) value;
                VARConfig.calculateRatio();
            }
        });
        respectDisplayRatio = addButton(new ListenerCheckboxWidget(width / 2 - textRenderer.getWidth(new TranslatableText("virtualaspectratio.respectdisplayratio")) / 2 - 12,
                92, 24 + textRenderer.getWidth(new TranslatableText("virtualaspectratio.respectdisplayratio")), 20,
                new TranslatableText("virtualaspectratio.respectdisplayratio"), VARConfig.respectDisplayRatio, cb -> VARConfig.respectDisplayRatio = cb.isChecked()));
        addButton(new ButtonWidget(width / 2 - 100, height - 24, 200, 20, new TranslatableText("gui.done"), btn -> client.openScreen(prev)));
        warnText = MultilineText.create(textRenderer, new TranslatableText("virtualaspectratio.servertoggle.off")
                .formatted(Formatting.RED).formatted(Formatting.BOLD), width - 15);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, width / 2, 10, -1);
        super.render(matrices, mouseX, mouseY, delta);
        if (VARConfig.disallowed) warnText.drawCenterWithShadow(matrices, width / 2, height / 3 * 2);
    }

    @Override
    public void removed() {
        VARConfig.save();
    }
}
