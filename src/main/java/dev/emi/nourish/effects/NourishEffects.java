package dev.emi.nourish.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.tuple.Pair;

import dev.emi.nourish.effects.NourishEffect.NourishAttribute;
import dev.emi.nourish.groups.NourishGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

public class NourishEffects {
	
	public static List<NourishEffect> loadEffects(Map<String, NourishGroup> byName, JsonArray arr) {
		List<NourishEffect> effects = new ArrayList<NourishEffect>();
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
							NourishGroup g = byName.get(group.getAsString());
							if (g == null) {
								System.err.println("[nourish] Group name " + group.getAsString() + " does not exist");
							}
							cond.groups.add(g);
						}
						effect.conditions.add(cond);
					}
					if (o.has("status_effects")) {
						JsonArray statuses = o.getAsJsonArray("status_effects");
						for (JsonElement status: statuses) {
							JsonObject ob = status.getAsJsonObject();
							String s = ob.get("status").getAsString();
							int lvl = ob.get("level").getAsInt();
							effect.status_effects.add(Pair.of(new Identifier(s), lvl));
						}
					}
					if (o.has("attributes")) {
						JsonArray attributes = o.getAsJsonArray("attributes");
						for (JsonElement attribute : attributes) {
							JsonObject ob = attribute.getAsJsonObject();
							Identifier id = new Identifier(ob.get("name").getAsString());
							String operation = ob.get("operation").getAsString();
							EntityAttributeModifier.Operation op =
								operation.equals("addition") ? EntityAttributeModifier.Operation.ADDITION :
								operation.equals("multiply_base") ? EntityAttributeModifier.Operation.MULTIPLY_BASE :
								operation.equals("multiply_total") ? EntityAttributeModifier.Operation.MULTIPLY_TOTAL :
								null;
							if (op == null) {
								throw new IllegalArgumentException("Entity attribute operation is not valid: " + operation);
							}
							double amount = ob.get("amount").getAsDouble();
							effect.attributes.add(new NourishAttribute(id, op, amount));
						}
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
		return effects;
	}
}