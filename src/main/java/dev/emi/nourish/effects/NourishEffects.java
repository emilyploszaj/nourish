package dev.emi.nourish.effects;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.tuple.Pair;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NourishEffects {
	public static List<NourishEffect> effects = new ArrayList<NourishEffect>();
	
	public static void init(JsonArray arr) { // Gson master :tiny_potato:
		try {
			for (JsonElement el: arr) {
				try {
					NourishEffect effect = new NourishEffect();
					JsonObject o = el.getAsJsonObject();
					JsonArray conditions = o.getAsJsonArray("conditions");
					for (JsonElement condition: conditions) {
						JsonObject c = condition.getAsJsonObject();
						NourishEffectCondition cond = new NourishEffectCondition();
						if (c.has("below")) {
							cond.below = c.get("below").getAsFloat();
						}
						if (c.has("above")) {
							cond.above = c.get("above").getAsFloat();
						}
						JsonArray groups = c.getAsJsonArray("groups");
						for (JsonElement group: groups) {
							NourishGroup g = NourishGroups.byName.get(group.getAsString());
							if (g == null) {
								System.err.println("[nourish] Group name " + group.getAsString() + " does not exist");
							}
							cond.groups.add(g);
						}
						effect.conditions.add(cond);
					}
					JsonArray statuses = o.getAsJsonArray("status_effects");
					for (JsonElement status: statuses) {
						o = status.getAsJsonObject();
						String s = o.get("status").getAsString();
						int lvl = o.get("level").getAsInt();
						if (Registry.STATUS_EFFECT.get(new Identifier(s)) == null) {
							throw new Exception();
						}
						effect.status_effects.add(Pair.of(Registry.STATUS_EFFECT.get(new Identifier(s)), lvl));
					}
					effects.add(effect);
				} catch (Exception e) {
					System.err.print("[nourish] Failed to parse nourish effect");
					e.printStackTrace();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}