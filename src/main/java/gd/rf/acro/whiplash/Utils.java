package gd.rf.acro.whiplash;

import gd.rf.acro.whiplash.gajduk.textmaze.Maze;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;

public class Utils {

    public static void fillRegion(World world, BlockPos start, Block block)
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                for (int k = 0; k < 9; k++) {
                    world.setBlockState(start.add(i,j,k),block.getDefaultState());
                }
            }
        }
    }
    public static void hallway(World world, BlockPos pos, Block block)
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                world.setBlockState(pos.add(i,0,j), Whiplash.SOLID_VOID_BLOCK.getDefaultState());
                if(RandomUtils.nextBoolean())
                {
                    world.setBlockState(pos.add(i,1,j), block.getDefaultState());
                }
                world.setBlockState(pos.add(i,7,j), block.getDefaultState());
            }
        }
    }
    public static void room(World world, BlockPos pos, Block block)
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                world.setBlockState(pos.add(i,1,j), block.getDefaultState());
                world.setBlockState(pos.add(i,7,j), block.getDefaultState());
                if(RandomUtils.nextInt(0,70)==0)
                {
                    world.setBlockState(pos.add(i,2,j),Blocks.BARREL.getDefaultState());
                    BarrelBlockEntity entity = (BarrelBlockEntity) world.getBlockEntity(pos.add(i,2,j));
                    entity.setLootTable(LootTables.SIMPLE_DUNGEON_CHEST,RandomUtils.nextLong());
                }
                if(i == 4 && j == 4)
                {
                    world.setBlockState(pos.add(4,3,4), block.getDefaultState());
                    world.setBlockState(pos.add(3,2,4), block.getDefaultState());
                    world.setBlockState(pos.add(4,2,3), block.getDefaultState());
                    world.setBlockState(pos.add(5,2,4), block.getDefaultState());
                    world.setBlockState(pos.add(4,2,5), block.getDefaultState());


                    world.setBlockState(pos.add(4,2,4),Whiplash.DEACTIVATED_SPAWNER_BLOCK.getDefaultState());
                    world.setBlockState(pos.add(4,6,4),Blocks.LANTERN.getDefaultState().with(Properties.HANGING,true));

                }
            }
        }
    }
    public static void start(World world, BlockPos pos, Block block)
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                world.setBlockState(pos.add(i,1,j), block.getDefaultState());
                world.setBlockState(pos.add(i,7,j), block.getDefaultState());

            }
        }
        BlockPos cur = pos.add(4,6,4);
        int hi = world.getTopY(Heightmap.Type.WORLD_SURFACE,cur.getX(),cur.getZ())+5;
        while(cur.getY()!=hi)
        {
            world.setBlockState(cur.add(0,0,1), block.getDefaultState());
            world.setBlockState(cur.add(0,0,-1), block.getDefaultState());
            world.setBlockState(cur.add(1,0,0), block.getDefaultState());
            world.setBlockState(cur.add(-1,0,0), block.getDefaultState());
            world.setBlockState(cur, Blocks.LADDER.getDefaultState());
            cur=cur.add(0,1,0);
        }
    }
    public static void boss(World world, BlockPos pos, Block block)
    {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                world.setBlockState(pos.add(i,1,j), block.getDefaultState());
                world.setBlockState(pos.add(i,7,j), block.getDefaultState());
                if(i == 4 && j == 4)
                {
                    world.setBlockState(pos.add(4,3,4),Whiplash.BOSS_SPAWNER_BLOCK.getDefaultState());
                }

            }
        }
    }



    private static final Block[] elements = {Whiplash.DUNGEON_BRICK_EARTH,Whiplash.DUNGEON_BRICK_FIRE,Whiplash.DUNGEON_BRICK_WATER};
    public static void makeDungeon(ServerWorld world, BlockPos start)
    {
        Block block = elements[RandomUtils.nextInt(0,elements.length)];
        Maze maze = new Maze(10,10);
        String inter = maze.toString();
        String[] rows = inter.split("\n");
        //System.out.println(Arrays.toString(rows));
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                char v = rows[i].charAt(j);
                fillRegion(world,start.add(9*i,0,9*j),Blocks.AIR);
                if(v=='#')
                {
                    fillRegion(world,start.add(9*i,0,9*j),block);
                }
                if(v=='-')
                {
                    hallway(world,start.add(9*i,0,9*j),block);
                }
                if(v=='|')
                {
                    hallway(world,start.add(9*i,0,9*j),block);
                }
                if(v=='O')
                {
                    room(world,start.add(9*i,0,9*j),block);
                }
                if(v=='S')
                {
                    start(world,start.add(9*i,0,9*j),block);
                }
                if(v=='E')
                {
                    boss(world,start.add(9*i,0,9*j),block);
                }

            }
            world.getPlayers().get(0).sendMessage(new LiteralText("[row "+i+"/21 completed]"),true);
        }

    }
}
