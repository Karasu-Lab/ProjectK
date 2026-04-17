package com.karasu256.projectk.block.custom;
 
import com.karasu256.projectk.registry.IProjectKPropertiesProvider;
import com.karasu256.projectk.registry.ProjectKProperties;
import net.minecraft.world.level.block.Block;
 
public abstract class AbstractProjectKBlock extends Block implements IProjectKPropertiesProvider<Block> {
    private final ProjectKProperties<Block> pkProperties;

    public AbstractProjectKBlock(Properties properties, ProjectKProperties<Block> pkProperties) {
        super(properties);
        this.pkProperties = pkProperties;
    }

    @Override
    public ProjectKProperties<Block> getProjectKProperties() {
        return pkProperties;
    }
}
