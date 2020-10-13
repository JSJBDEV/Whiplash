package gd.rf.acro.whiplash;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;

public class BossMobUtils {
    public static void rollForBossTags(LivingEntity entity, boolean forceBoss)
    {
        int isBoss=0;
        if(!forceBoss)
        {
            isBoss = RandomUtils.nextInt(0,10);
        }
        if(isBoss==0)
        {
            
            int abilities = RandomUtils.nextInt(1,5);
            StringBuilder endName = new StringBuilder(entity.getName().asString());
            for (int i = 0; i < abilities; i++)
            {
                int ability = RandomUtils.nextInt(0,13);
                switch (ability)
                {
                    case 0: //Strong
                        StatusEffectInstance strength = new StatusEffectInstance(StatusEffects.STRENGTH,9999,2);
                        entity.addStatusEffect(strength);
                        endName.insert(0, "strong ");
                        break;
                    case 1: //Speedy
                        StatusEffectInstance speed = new StatusEffectInstance(StatusEffects.STRENGTH,9999,2);
                        entity.addStatusEffect(speed);
                        endName.insert(0, "speedy ");
                        break;
                    case 2: //Tank
                        StatusEffectInstance tank = new StatusEffectInstance(StatusEffects.ABSORPTION,9999,4);
                        entity.addStatusEffect(tank);
                        endName.insert(0, "tank ");
                        break;
                    case 3: //Invisible
                        StatusEffectInstance invis = new StatusEffectInstance(StatusEffects.INVISIBILITY,9999,1);
                        entity.addStatusEffect(invis);
                        endName.insert(0, "invisible ");
                        break;

                    case 4: //of flames
                        if(entity.getEquippedStack(EquipmentSlot.MAINHAND).getItem()==Items.BOW)
                        {
                            
                            ItemStack bow = entity.getEquippedStack(EquipmentSlot.MAINHAND);
                            bow.addEnchantment(Enchantments.FLAME,3);
                            entity.equipStack(EquipmentSlot.MAINHAND,bow);
                        }
                        else
                        {
                            ItemStack sword = new ItemStack(Items.IRON_SWORD);
                            sword.addEnchantment(Enchantments.FIRE_ASPECT,3);
                            entity.equipStack(EquipmentSlot.MAINHAND,sword);
                        }
                        endName.append(" of flames and");
                        break;
                    case 5: //of pushing
                        if(entity.getEquippedStack(EquipmentSlot.MAINHAND).getItem()==Items.BOW)
                        {
                            ItemStack bow = entity.getEquippedStack(EquipmentSlot.MAINHAND);
                            bow.addEnchantment(Enchantments.PUNCH,3);
                            entity.equipStack(EquipmentSlot.MAINHAND,bow);
                        }
                        else
                        {
                            ItemStack sword = new ItemStack(Items.IRON_SWORD);
                            sword.addEnchantment(Enchantments.KNOCKBACK,3);
                            entity.equipStack(EquipmentSlot.MAINHAND,sword);
                        }
                        endName.append(" of pushing and");
                        break;
                    case 6: //Incredible
                        entity.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.DIAMOND));
                        endName.insert(0, "incredible ");
                        break;
                    case 7: //Poison
                        entity.addScoreboardTag("infection");
                        endName.insert(0, "infectious ");
                        break;
                    case 8: //frost
                        entity.addScoreboardTag("trap");
                        endName.append(" of trapping and");
                        break;
                    case 9: //reviving
                        entity.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.TOTEM_OF_UNDYING));
                        endName.insert(0, "reviving ");
                        break;
                    case 10: //of birdkeeping
                        PhantomEntity phantomEntity = new PhantomEntity(EntityType.PHANTOM,entity.getEntityWorld());
                        phantomEntity.teleport(entity.getX(),entity.getY(),entity.getZ());
                        entity.getEntityWorld().spawnEntity(phantomEntity);
                        endName.append(" of birdkeeping and");
                        break;
                    case 11:
                        abilities+=2;
                        break;
                    case 12: //necromancer
                        World world = entity.world;
                        BlockPos pos = entity.getBlockPos();
                        world.getServer().getCommandManager().execute(world.getServer().getCommandSource().withSilent(), "setblock " + pos.getX() + " " + pos.getY()+1 + " " + pos.getZ() + " minecraft:spawner{SpawnData:{id:\"minecraft:zombie\"}} replace");

                        endName.insert(0, "necromancer ");
                        break;


                }
            }
            if(endName.toString().endsWith("and"))
            {
                endName = new StringBuilder(endName.substring(0, endName.length() - 3));
            }
            entity.addScoreboardTag("miniboss");
            entity.setCustomName(new LiteralText(endName.toString()));
            entity.setCustomNameVisible(true);
        }
    }
}
