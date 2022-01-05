package ru.vidtu.virtualaspectratio;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * Implementation of the {@link CheckboxWidget} with a toggle listener.
 * @author VidTu
 */
public class ListenerCheckboxWidget extends CheckboxWidget {
    public final Consumer<ListenerCheckboxWidget> listener;
    public ListenerCheckboxWidget(int x, int y, int width, int height, Text message, boolean checked, Consumer<ListenerCheckboxWidget> listener) {
        super(x, y, width, height, message, checked);
        this.listener = listener;
    }

    public ListenerCheckboxWidget(int x, int y, int width, int height, Text message, boolean checked, boolean showMessage, Consumer<ListenerCheckboxWidget> listener) {
        super(x, y, width, height, message, checked, showMessage);
        this.listener = listener;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        listener.accept(this);
    }
}
