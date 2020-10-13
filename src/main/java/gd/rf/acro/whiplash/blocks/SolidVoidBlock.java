package gd.rf.acro.whiplash.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SolidVoidBlock extends Block {
    public SolidVoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        super.onSteppedOn(world, pos, entity);
        if(entity instanceof PlayerEntity)
        {
            entity.damage(DamageSource.OUT_OF_WORLD,5f);
        }
    }
}
