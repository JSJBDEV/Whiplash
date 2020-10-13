package gd.rf.acro.whiplash.items;

import gd.rf.acro.whiplash.Whiplash;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

public class ElementalCoreItem extends Item {
    public ElementalCoreItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getStackInHand(hand).getItem()== Whiplash.ELEMENTAL_CORE_ITEM && world.getBlockState(user.getBlockPos())==Blocks.AIR.getDefaultState())
        {
            world.setBlockState(user.getBlockPos(), Blocks.BARREL.getDefaultState());
            BarrelBlockEntity entity = (BarrelBlockEntity) world.getBlockEntity(user.getBlockPos());
            entity.setLootTable(LootTables.BURIED_TREASURE_CHEST, RandomUtils.nextLong());
            user.setStackInHand(hand,ItemStack.EMPTY);
        }
        return super.use(world, user, hand);
    }
}
