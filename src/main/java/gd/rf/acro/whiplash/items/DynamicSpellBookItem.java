package gd.rf.acro.whiplash.items;

import gd.rf.acro.whiplash.CastingUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class DynamicSpellBookItem extends Item {

    public DynamicSpellBookItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        CompoundTag tag = user.getStackInHand(hand).getTag();
        if(user.isSneaking())
        {
            tag.putString("handoff","nil");
            user.getStackInHand(hand).setTag(tag);
        }
        else
        {
            if(tag.contains("style") && tag.contains("element") && tag.contains("drain") && tag.contains("effect") && tag.contains("power"))
            {
                CastingUtils.cast(user,tag.getString("style"),tag.getString("element"),tag.getInt("drain"),tag.getInt("effect"),tag.getInt("power"));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        PlayerEntity player = (PlayerEntity) entity;
        if (stack.hasTag())
        {
            CompoundTag tag = stack.getTag();
            if (stack.getTag().getString("handoff").equals("barrier"))
            {
                if (player.experienceLevel >= tag.getInt("drain"))
                {

                    Entity target = world.getEntityById(tag.getInt("target"));

                    if(target instanceof LivingEntity)
                    {
                        LivingEntity livingEntity = (LivingEntity) target;
                        if(world.getTimeOfDay()%20==0)
                        {
                            player.experienceLevel -= tag.getInt("drain");
                        }
                        BlockPos p = livingEntity.getBlockPos();
                        float[] v = CastingUtils.getColourForElement(tag.getString("element"));
                        DustParticleEffect particle = new DustParticleEffect(v[0], v[1], v[2], 2);
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(tag.getInt("effect")),50,tag.getInt("power")));
                        for (int i = p.getX() - 1; i < p.getX() + 3; i++) {
                            world.addParticle(particle, i, p.getY(), p.getZ() + 2, 0, 0, 0);
                            world.addParticle(particle, i, p.getY() + 2, p.getZ() + 2, 0, 0, 0);
                            world.addParticle(particle, i, p.getY() + 1, p.getZ() + 2, 0, 0, 0);
                            world.addParticle(particle, i, p.getY(), p.getZ() - 1, 0, 0, 0);
                            world.addParticle(particle, i, p.getY() + 2, p.getZ() - 1, 0, 0, 0);
                            world.addParticle(particle, i, p.getY() + 1, p.getZ() - 1, 0, 0, 0);

                        }
                        for (int i = p.getZ() - 1; i < p.getZ() + 3; i++) {
                            world.addParticle(particle, p.getX() + 2, p.getY(), i, 0, 0, 0);
                            world.addParticle(particle, p.getX() + 2, p.getY() + 2, i, 0, 0, 0);
                            world.addParticle(particle, p.getX() + 2, p.getY() + 1, i, 0, 0, 0);
                            world.addParticle(particle, p.getX() - 1, p.getY(), i, 0, 0, 0);
                            world.addParticle(particle, p.getX() - 1, p.getY() + 2, i, 0, 0, 0);
                            world.addParticle(particle, p.getX() - 1, p.getY() + 1, i, 0, 0, 0);

                        }
                    }
                    else
                    {
                        //the entity id no longer exists (entity is dead or world was reloaded)
                        tag.putString("handoff","nil");
                        stack.setTag(tag);
                    }
                }
                else
                {
                    //the player ran out of mana
                    tag.putString("handoff","nil");
                    stack.setTag(tag);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(stack.hasTag())
        {
            CompoundTag tag = stack.getTag();
            tooltip.add(new LiteralText("Element: "+tag.getString("element")));
            tooltip.add(new LiteralText("Style: "+tag.getString("style")));
            tooltip.add(new LiteralText("Effect: "+StatusEffect.byRawId(tag.getInt("effect")).getName().getString()));
            tooltip.add(new LiteralText("Power: "+tag.getInt("power")));
            tooltip.add(new LiteralText("Levels/second: "+tag.getInt("drain")));
        }
    }
}
