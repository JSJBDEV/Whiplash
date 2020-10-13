package gd.rf.acro.whiplash.blocks;

import gd.rf.acro.whiplash.BossMobUtils;
import gd.rf.acro.whiplash.Whiplash;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class BossSpawnerBlock extends Block {
    public BossSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        SkeletonEntity entity = new SkeletonEntity(EntityType.SKELETON,world);
        entity.teleport(pos.getX(),pos.getY(),pos.getZ());
        if(RandomUtils.nextBoolean()){
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        }else
        {
            entity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        entity.equipStack(EquipmentSlot.HEAD,new ItemStack(Whiplash.ELEMENTAL_CORE_ITEM));
        entity.equipStack(EquipmentSlot.CHEST,new ItemStack(Items.DIAMOND_CHESTPLATE));
        entity.equipStack(EquipmentSlot.LEGS,new ItemStack(Items.DIAMOND_LEGGINGS));
        entity.equipStack(EquipmentSlot.FEET,new ItemStack(Items.DIAMOND_BOOTS));
        entity.setEquipmentDropChance(EquipmentSlot.HEAD,1);
        entity.setPersistent();
        BossMobUtils.rollForBossTags(entity,true);
        world.spawnEntity(entity);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
