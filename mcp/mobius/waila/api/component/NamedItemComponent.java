package mcp.mobius.waila.api.component;

import I;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_9779;

/**
 * Component that renders an {@link class_1799} with its name.
 */
@ApiSide.ClientOnly
public class NamedItemComponent implements ITooltipComponent {

    public static final NamedItemComponent EMPTY = new NamedItemComponent(class_1799.field_8037);

    public NamedItemComponent(class_1799 stack) {
        this.stack = stack;

        var count = stack.method_7947();
        var name = stack.method_7964().getString();
        this.label = count > 1 ? WailaHelper.suffix(count) + " " + name : name;
    }

    public NamedItemComponent(class_1935 item) {
        this(new class_1799(item));
    }

    public final class_1799 stack;
    public final String label;

    @Override
    public int getWidth() {
        return getFont().method_1727(label) + 10;
    }

    @Override
    public int getHeight() {
        return getFont().field_2000;
    }

    @Override
    public void render(class_332 ctx, int x, int y, class_9779 delta) {
        var pose = ctx.method_51448();
        pose.method_22903();
        pose.method_46416(x, y, 0);
        pose.method_22905(0.5f, 0.5f, 0.5f);
        ctx.method_51427(stack, 0, 0);
        pose.method_22909();

        ctx.method_25303(getFont(), label, x + 10, y, IApiService.INSTANCE.getFontColor());
    }

    private class_327 getFont() {
        return class_310.method_1551().field_1772;
    }

}
