package io.github.nibbyy.slowcraft.init;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class SlowMaterial implements ToolMaterial {
    public static int durability;
    public static final SlowMaterial INSTANCE = new SlowMaterial(durability);

    public SlowMaterial(int durability) {
        SlowMaterial.durability = durability;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 1.0F;
    }

    public float getAttackDamage() {
        return 0.0F;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return BlockTags.INCORRECT_FOR_STONE_TOOL;
    }
}
