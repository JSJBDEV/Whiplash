package gd.rf.acro.whiplash.mixin;


import gd.rf.acro.whiplash.Whiplash;
import gd.rf.acro.whiplash.items.CrystalItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DamageEffectsMixin {
    @Inject(method = "damage", at = @At("TAIL"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable cir) {
        if(source.getAttacker()!=null && source.getAttacker() instanceof LivingEntity && !source.getName().equals("elemental_magic"))
        {
            LivingEntity attacker = (LivingEntity) source.getAttacker();
            LivingEntity defender = ((LivingEntity)(Object)this);
            if(attacker.getScoreboardTags().contains("trap"))
            {
                defender.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,100,10));
            }
            if(attacker.getScoreboardTags().contains("infection"))
            {
                defender.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON,100,2));
            }
            addElemental(attacker,defender);
        }

    }

    private void addElemental(LivingEntity attacker, LivingEntity defender)
    {
        float extraDamage = 0;
        if(attacker.getEquippedStack(EquipmentSlot.OFFHAND).getItem() instanceof CrystalItem)
        {
            extraDamage = attacker.getEquippedStack(EquipmentSlot.OFFHAND).getTag().getInt("power");
            if(defender.getEquippedStack(EquipmentSlot.OFFHAND).getItem() instanceof CrystalItem)
            {
                extraDamage=extraDamage * getDiff(attacker.getEquippedStack(EquipmentSlot.OFFHAND).getItem(),defender.getEquippedStack(EquipmentSlot.OFFHAND).getItem());
            }
        }
        defender.damage(new EntityDamageSource("elemental_magic", attacker).setUsesMagic(),extraDamage);
    }
    private float getDiff(Item att, Item def)
    {
        if(att== Whiplash.FIRE_CRYSTAL)
        {
            if(def==Whiplash.EARTH_CRYSTAL)
            {
                return 2;
            }
            if(def==Whiplash.WATER_CRYSTAL)
            {
                return 0.5f;
            }
        }
        if(att== Whiplash.EARTH_CRYSTAL)
        {
            if(def==Whiplash.WATER_CRYSTAL)
            {
                return 2;
            }
            if(def==Whiplash.FIRE_CRYSTAL)
            {
                return 0.5f;
            }
        }
        if(att== Whiplash.WATER_CRYSTAL)
        {
            if(def==Whiplash.FIRE_CRYSTAL)
            {
                return 2;
            }
            if(def==Whiplash.EARTH_CRYSTAL)
            {
                return 0.5f;
            }
        }
        return 0.75f;
    }


}
