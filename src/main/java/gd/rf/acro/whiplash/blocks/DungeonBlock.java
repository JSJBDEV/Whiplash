package gd.rf.acro.whiplash.blocks;

import gd.rf.acro.whiplash.Whiplash;
import gd.rf.acro.whiplash.items.CrystalItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

public class DungeonBlock extends Block {
    String elem;
    public DungeonBlock(Settings settings, String element) {
        super(settings);
        elem=element;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        super.onSteppedOn(world, pos, entity);
        if(entity instanceof Monster)
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            if(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND)==ItemStack.EMPTY)
            {
                ItemStack crystal = new ItemStack(Items.AIR);
                CompoundTag tag = new CompoundTag();
                if(livingEntity.getScoreboardTags().contains("miniboss"))
                {
                    tag.putInt("power", RandomUtils.nextInt(5,15)+1);
                }
                else
                {
                    tag.putInt("power", RandomUtils.nextInt(0,10)+1);
                }

                switch (elem)
                {
                    case "fire":
                        crystal=new ItemStack(Whiplash.FIRE_CRYSTAL);
                        break;
                    case "earth":
                        crystal=new ItemStack(Whiplash.EARTH_CRYSTAL);
                        break;
                    case "water":
                        crystal=new ItemStack(Whiplash.WATER_CRYSTAL);
                        break;
                }
                crystal.setTag(tag);
                entity.equipStack(EquipmentSlot.OFFHAND,crystal);
            }
        }
    }
}
