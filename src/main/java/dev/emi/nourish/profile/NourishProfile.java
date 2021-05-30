package dev.emi.nourish.profile;

import java.util.List;
import java.util.Map;

import dev.emi.nourish.effects.NourishEffect;
import dev.emi.nourish.groups.NourishGroup;

public class NourishProfile {
	public String name;
	public List<NourishGroup> groups;
	public Map<String, NourishGroup> byName;
	public List<NourishEffect> effects;

	public NourishProfile(String name, List<NourishGroup> groups, Map<String, NourishGroup> byName, List<NourishEffect> effects) {
		this.name = name;
		this.groups = groups;
		this.byName = byName;
		this.effects = effects;
	}
}
