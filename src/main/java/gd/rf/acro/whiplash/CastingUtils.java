package gd.rf.acro.whiplash;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Random;

public class CastingUtils {

    public static void cast(PlayerEntity playerEntity, String style, String element, int drain,int effect,int power)
    {
        if(playerEntity.experienceLevel>=drain)
        {
            playerEntity.experienceLevel-=drain;
            switch (style)
            {
                case "ball":
                    castBall(playerEntity,playerEntity.world,getItemForElement(element),getColourForElement(element),effect,power);
                    playerEntity.sendMessage(new LiteralText(element+" creation magic: "+element+" ball of "+StatusEffect.byRawId(effect).getName().getString()),true);
                    break;
                case "beam":
                    castBeam(playerEntity,playerEntity.world,getColourForElement(element),effect,power);
                    playerEntity.sendMessage(new LiteralText(element+" creation magic: "+element+" "+StatusEffect.byRawId(effect).getName().getString()+" Beam"),true);
                    break;
                case "selforb":
                    castOrb(playerEntity,playerEntity,getColourForElement(element));
                    playerEntity.sendMessage(new LiteralText(element+" creation magic: "+element+" Barrier of "+StatusEffect.byRawId(effect).getName().getString()),true);
                    break;
                case "orb":
                    castOrb(playerEntity,getRaycastHit(playerEntity,playerEntity.world),getColourForElement(element));
                    playerEntity.sendMessage(new LiteralText(element+" creation magic: "+element+" Prison of "+StatusEffect.byRawId(effect).getName().getString()),true);
                    break;
            }
        }
    }
    public static float[] getColourForElement(String element)
    {
        switch (element)
        {
            case "fire":
                return new float[] {1,0,0};
            case "water":
                return new float[]{0,0,1};
            case "earth":
                return new float[]{0,1,0};

        }
        return new float[]{0,0,0};
    }
    private static Item getItemForElement(String element)
    {
        switch (element)
        {
            case "fire":
                return Items.FIRE_CHARGE;
            case "water":
                return Items.WET_SPONGE;
            case "earth":
                return Items.GRASS_BLOCK;

        }
        return Items.STONE;
    }

    private static void castBall(PlayerEntity user, World world, Item render, float[] rgb, int effect, int power)
    {
        double[] look = {user.getRotationVector().x*2,user.getRotationVector().y,user.getRotationVector().z*2};
        FireballEntity entity = new FireballEntity(world,user.getX()+look[0],user.getY()+look[1],user.getZ()+look[2],look[0],look[1],look[2]);
        entity.setItem(new ItemStack(render));
        entity.setFireTicks(0);
        entity.setOnFireFor(0);
        entity.explosionPower=0; 
        world.spawnEntity(entity);

        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world,user.getX()+look[0],user.getY()+look[1],user.getZ()+look[2]);
        areaEffectCloudEntity.setParticleType(new DustParticleEffect(rgb[0],rgb[1],rgb[2],1));
        areaEffectCloudEntity.setRadius(3.0F);
        areaEffectCloudEntity.setDuration(200);
        areaEffectCloudEntity.setRadiusGrowth((7.0F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffect.byRawId(effect),100,power));
        world.spawnEntity(areaEffectCloudEntity);
        areaEffectCloudEntity.startRiding(entity,true);
    }
    private static void castBeam(PlayerEntity user, World world,float[] rgb, int effect, int power)
    {
        LivingEntity livingEntity = getRaycastHit(user,world);
        Random random = new Random();
        if (livingEntity!=null) {
            double d = 1D;
            double e = livingEntity.getX() - user.getBlockPos().getX();
            double f = livingEntity.getBodyY(0.5D) - user.getEyeY();
            double g = livingEntity.getZ() - user.getZ();
            double h = Math.sqrt(e * e + f * f + g * g);
            e /= h;
            f /= h;
            g /= h;
            double j = random.nextDouble();
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(effect),100,power));
            while(j < h) {
                j += 1.8D - d + random.nextDouble() * (1.7D - d);
                world.addParticle(new DustParticleEffect(rgb[0],rgb[1],rgb[2],5), user.getBlockPos().getX() + e * j, user.getEyeY() + f * j, user.getBlockPos().getZ() + g * j, 0.0D, 0.0D, 0.0D);

            }
        }
    }

    private static void castOrb(PlayerEntity user,LivingEntity target, float[] rgb)
    {
        if(target!=null)
        {
            ItemStack book = user.getMainHandStack();
            CompoundTag tag = book.getTag();
            tag.putFloat("red",rgb[0]);
            tag.putFloat("green",rgb[1]);
            tag.putFloat("blue",rgb[2]);
            tag.putInt("target",target.getEntityId());
            tag.putString("handoff","barrier");
            book.setTag(tag);
        }

    }


    private static LivingEntity getRaycastHit(PlayerEntity user, World world)
    {
        HitResult result = user.rayTrace(100,1,true);
        return world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT,user,result.getPos().getX(),result.getPos().getY(),result.getPos().getZ(),new Box(result.getPos().getX()-2,result.getPos().getY()-2,result.getPos().getZ()-2,result.getPos().getX()+2,result.getPos().getY()+2,result.getPos().getZ()+2));

    }
}
