package com.karasu256.projectk.mixin;

import com.karasu256.projectk.recipe.InBiomeInBlockCraftingRecipe;
import com.karasu256.projectk.recipe.IngredientStack;
import com.karasu256.projectk.recipe.ProjectKRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void handleTransformation(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
        if (this.level().isClientSide || isRemoved()) return;

        if (!damageSource.is(DamageTypeTags.IS_FIRE)) return;

        List<InBiomeInBlockCraftingRecipe> recipes = this.level().getRecipeManager()
                .getAllRecipesFor(ProjectKRecipes.IN_BIOME_IN_BLOCK_CRAFTING.get())
                .stream()
                .map(holder -> holder.value())
                .toList();

        if (recipes.isEmpty()) return;

        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);

        for (InBiomeInBlockCraftingRecipe recipe : recipes) {
            if (!recipe.matchesContext(this.level(), state, pos)) continue;
            if (this.projectK$tryCraft(recipe, ci)) return;
        }
    }

    @Unique
    private boolean projectK$tryCraft(InBiomeInBlockCraftingRecipe recipe, CallbackInfoReturnable<Boolean> ci) {
        AABB aabb = this.getBoundingBox().inflate(recipe.radius());
        List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, aabb, e -> !e.isRemoved());

        Map<ItemEntity, Integer> consume = new LinkedHashMap<>();
        for (IngredientStack requirement : recipe.inputs()) {
            int remaining = requirement.count();
            for (ItemEntity entity : items) {
                ItemStack stack = entity.getItem();
                if (stack.isEmpty()) continue;
                if (!requirement.ingredient().test(stack)) continue;

                int used = consume.getOrDefault(entity, 0);
                int available = stack.getCount() - used;
                if (available <= 0) continue;

                int take = Math.min(available, remaining);
                if (take > 0) {
                    consume.put(entity, used + take);
                    remaining -= take;
                }

                if (remaining == 0) break;
            }

            if (remaining > 0) return false;
        }

        if (consume.isEmpty()) return false;

        for (Map.Entry<ItemEntity, Integer> entry : consume.entrySet()) {
            ItemEntity entity = entry.getKey();
            ItemStack stack = entity.getItem();
            int amount = entry.getValue();
            if (amount >= stack.getCount()) {
                entity.discard();
            } else {
                stack.shrink(amount);
            }
        }

        ItemStack resultStack = recipe.result().copy();
        ItemEntity result = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), resultStack);
        this.level().addFreshEntity(result);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 20, 0.2, 0.2, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 10, 0.2, 0.2, 0.2, 0.02);
        }
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 0.5f, 1.0f);

        ci.setReturnValue(false);
        ci.cancel();
        return true;
    }
}
