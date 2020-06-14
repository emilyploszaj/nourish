package dev.emi.nourish.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public class NourishStatusEffectInstance extends StatusEffectInstance {

	public NourishStatusEffectInstance(StatusEffect effect, int time, int amp) {
		super(effect, time, amp, true, false, false);
	}
	
}