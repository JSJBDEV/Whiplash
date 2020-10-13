package gd.rf.acro.whiplash.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class ItemEscapeRope extends Item {
    public ItemEscapeRope(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getStackInHand(hand).getItem() instanceof ItemEscapeRope)
        {
            user.teleport(user.getX(),world.getTopY(Heightmap.Type.WORLD_SURFACE,user.getBlockPos().getX(),user.getBlockPos().getZ())+5,user.getZ());
            user.getStackInHand(hand).decrement(1);
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
