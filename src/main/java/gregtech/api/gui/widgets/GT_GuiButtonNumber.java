package gregtech.api.gui.widgets;

import net.minecraft.client.gui.GuiButton;

public class GT_GuiButtonNumber extends GuiButton {
    int value;

    public GT_GuiButtonNumber(int id,int value ,int x, int y, int with, int hight, String name) {
        super(id, x, y, with, hight, name);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
