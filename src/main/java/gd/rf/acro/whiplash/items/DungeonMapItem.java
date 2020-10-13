package gd.rf.acro.whiplash.items;

import gd.rf.acro.whiplash.Utils;
import gd.rf.acro.whiplash.Whiplash;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

public class DungeonMapItem extends Item {
    public DungeonMapItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient && user.getStackInHand(hand).getItem()== Whiplash.DEBUG_ITEM)
        {
            BlockPos dungeonLoc = new BlockPos(user.getX()+(RandomUtils.nextInt(0,20000)-10000),20,user.getZ()+(RandomUtils.nextInt(0,20000)-10000));
            Utils.makeDungeon((ServerWorld)world,dungeonLoc);
            user.sendMessage(new LiteralText("The map reveals a dungeon around ["+dungeonLoc.getX()+","+dungeonLoc.getZ()+"]!"),false);
            user.setStackInHand(hand,ItemStack.EMPTY);

        }
        return super.use(world, user, hand);
    }


}
