package mcp.mobius.waila.api.component;

import mcp.mobius.waila.api.ITooltipComponent;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.ApiSide;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_9779;

/**
 * Component that renders a furnace-like progress arrow.
 */
@ApiSide.ClientOnly
public class ProgressArrowComponent implements ITooltipComponent {

    /**
     * @param progress the progress between 0.0f and 1.0f.
     */
    public ProgressArrowComponent(float progress) {
        this.progress = class_3532.method_15363(progress, 0.0f, 1.0f);
    }

    private final float progress;

    @Override
    public int getWidth() {
        return 22;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public void render(class_332 ctx, int x, int y, class_9779 delta) {
        // Draws the "empty" background arrow
        ctx.method_25302(WailaConstants.COMPONENT_TEXTURE, x, y, 0, 16, 22, 16);

        if (progress > 0) {
            // Draws the "full" foreground arrow based on the progress
            ctx.method_25302(WailaConstants.COMPONENT_TEXTURE, x, y, 0, 0, (int) (progress * 22), 16);
        }
    }

}
