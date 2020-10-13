package gd.rf.acro.whiplash.blocks;

import gd.rf.acro.whiplash.BossMobUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class DeactivatedSpawnerBlock extends Block {
    public DeactivatedSpawnerBlock(Settings settings) {
        super(settings);

    }

    private static final String[] mobs = {"minecraft:zombie","minecraft:skeleton","minecraft:spider"};
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        world.getServer().getCommandManager().execute(world.getServer().getCommandSource().withSilent(), "setblock "
                + pos.getX()
                + " "
                + pos.getY()
                + " "
                + pos.getZ()
                + " minecraft:spawner{SpawnData:{id:'"
                +mobs[RandomUtils.nextInt(0,mobs.length)]
                +"'}} replace");
        }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        world.getBlockTickScheduler().schedule(pos,this,1);
    }
}
