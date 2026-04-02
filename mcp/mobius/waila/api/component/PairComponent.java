package mcp.mobius.waila.api.component;

import I;
import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.__internal__.ApiSide;
import mcp.mobius.waila.api.__internal__.IApiService;
import mcp.mobius.waila.api.__internal__.IClientApiService;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_9779;

/**
 * Component that renders key-value pair that would be aligned at the colon.
 */
@ApiSide.ClientOnly
public class PairComponent implements ITooltipComponent {

    public PairComponent(class_2561 key, class_2561 value) {
        this(new WrappedComponent(key), new WrappedComponent(value));
    }

    public PairComponent(ITooltipComponent key, ITooltipComponent value) {
        this.key = key;
        this.value = value;
    }

    public final ITooltipComponent key, value;

    private int width = -1;
    private int height = -1;

    @Override
    public int getWidth() {
        if (width == -1) {
            key.getWidth(); // if there is special computation
            width = getColonOffset() + getColonWidth() + value.getWidth();
        }

        return width;
    }

    @Override
    public int getHeight() {
        if (height == -1) height = Math.max(key.getHeight(), value.getHeight());
        return height;
    }

    @Override
    public void render(class_332 ctx, int x, int y, class_9779 delta) {
        var offset = key.getHeight() < height ? (height - key.getHeight()) / 2 : 0;
        IClientApiService.INSTANCE.renderComponent(ctx, key, x, y + offset, delta);

        var font = class_310.method_1551().field_1772;
        offset = font.field_2000 < height ? (height - font.field_2000) / 2 : 0;
        ctx.method_25303(font, ": ", x + getColonOffset(), y + offset, IWailaConfig.get().getOverlay().getColor().getTheme().getDefaultTextColor());

        offset = value.getHeight() < height ? (height - value.getHeight()) / 2 : 0;
        IClientApiService.INSTANCE.renderComponent(ctx, value, x + getColonOffset() + getColonWidth(), y + offset, delta);
    }

    private int getColonOffset() {
        return IApiService.INSTANCE.getPairComponentColonOffset();
    }

    private int getColonWidth() {
        return IApiService.INSTANCE.getColonFontWidth();
    }

}
