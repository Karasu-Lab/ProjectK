package mcp.mobius.waila.api.component;

import F;
import I;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaHelper;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_9779;

/**
 * Component that renders an {@link class_1799}.
 */
@ApiSide.ClientOnly
public class ItemComponent implements ITooltipComponent {

    public static final ItemComponent EMPTY = new ItemComponent(class_1799.field_8037);

    public ItemComponent(class_1799 stack) {
        this.stack = stack;
    }

    public ItemComponent(class_1935 item) {
        this(new class_1799(item));
    }

    public final class_1799 stack;

    @Override
    public int getWidth() {
        return stack.method_7960() ? 0 : 18;
    }

    @Override
    public int getHeight() {
        return stack.method_7960() ? 0 : 18;
    }

    @Override
    public void render(class_332 ctx, int x, int y, class_9779 delta) {
        ctx.method_51427(stack, x + 1, y + 1);
        renderItemDecorations(ctx, stack, x + 1, y + 1);
    }

    static void renderItemDecorations(class_332 ctx, class_1799 stack, int x, int y) {
        var client = class_310.method_1551();
        var count = stack.method_7947();

        ctx.method_51432(client.field_1772, stack, x + 1, y + 1, "");
        if (count <= 1) return;

        var countText = WailaHelper.suffix(count);
        var actualW = client.field_1772.method_1727(countText);
        var scale = (actualW <= 16) ? 1f : 16f / actualW;

        var pose = ctx.method_51448();
        pose.method_22903();
        pose.method_22904(0.0, 0.0, 250.0);
        pose.method_22905(scale, scale, 1f);

        client.field_1772.method_27522(countText,
            ((x + 17 - (actualW * scale)) / scale),
            ((y + 17 - (client.field_1772.field_2000 * scale)) / scale),
            0xFFFFFF, true,
            pose.method_23760().method_23761(), ctx.method_51450(), class_327.class_6415.field_33993, 0, 0xF000F0, client.field_1772.method_1726());

        pose.method_22909();
    }

}
