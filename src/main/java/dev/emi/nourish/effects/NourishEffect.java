package dev.emi.nourish.effects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import dev.emi.nourish.NourishComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NourishEffect {
	public List<NourishEffectCondition> conditions = new ArrayList<NourishEffectCondition>();
	public List<Pair<Identifier, Integer>> status_effects = new ArrayList<Pair<Identifier, Integer>>();

	public void apply(PlayerEntity player) {
		for (Pair<Identifier, Integer> effect: status_effects) {
			player.addStatusEffect(new NourishStatusEffectInstance(Registry.STATUS_EFFECT.get(effect.getLeft()), 100, effect.getRight()));
		}
	}

	public boolean test(NourishComponent comp) {
		for (NourishEffectCondition condition: conditions) {
			if (!condition.test(comp)) return false;
		}
		return true;
	}
}