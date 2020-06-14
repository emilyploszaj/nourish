package dev.emi.nourish.effects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import dev.emi.nourish.NourishComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;

public class NourishEffect {
	public List<NourishEffectCondition> conditions = new ArrayList<NourishEffectCondition>();
	public List<Pair<StatusEffect, Integer>> status_effects = new ArrayList<Pair<StatusEffect, Integer>>();

	public void apply(PlayerEntity player) {
		for (Pair<StatusEffect, Integer> effect: status_effects) {
			player.addStatusEffect(new NourishStatusEffectInstance(effect.getLeft(), 400, effect.getRight()));
		}
	}

	public boolean test(NourishComponent comp) {
		for (NourishEffectCondition condition: conditions) {
			if (!condition.test(comp)) return false;
		}
		return true;
	}
}