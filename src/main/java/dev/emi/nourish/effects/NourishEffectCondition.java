package dev.emi.nourish.effects;

import java.util.ArrayList;
import java.util.List;

import dev.emi.nourish.NourishComponent;
import dev.emi.nourish.groups.NourishGroup;

public class NourishEffectCondition {
	public List<NourishGroup> groups = new ArrayList<NourishGroup>();
	public float below = 2.0f;
	public float above = -1.0f;

	public boolean test(NourishComponent comp) {
		float f = 0;
		for (NourishGroup group: groups) {
			f += comp.getValue(group);
		}
		f /= groups.size();
		return f < below && f > above;
	}
}