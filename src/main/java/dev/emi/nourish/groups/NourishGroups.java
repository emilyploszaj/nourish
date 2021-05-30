package dev.emi.nourish.groups;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.util.Identifier;

public class NourishGroups {
	public static final Gson GSON = new Gson();

	public static List<NourishGroup> loadGroups(JsonArray list) {
		List<NourishGroup> groups = new ArrayList<NourishGroup>();
		List<NourishGroup> secondaries = new ArrayList<NourishGroup>();
		for (JsonElement j: list) {
			try {
				NourishGroup g = GSON.fromJson(j, NourishGroup.class);
				g.identifier = new Identifier("nourish", g.name);
				if (g.secondary) {
					secondaries.add(g);
				} else {
					groups.add(g);
				}
				if (g.name.toLowerCase().equals("dairy")) {// Is this activism?
					System.out.println("[nourish] Dairy is a fake food group. It still was added but it's fake and you should consider not using it");
				}
			} catch (Exception e) {
				String name = "";
				try {
					name = j.getAsJsonObject().get("name").getAsString();
					System.err.print("[nourish] Error loading a group, name could be parsed: " + name);
					e.printStackTrace();
				} catch (Exception e2) {
					System.err.print("[nourish] Error loading a group, name could not be parsed");
				}
			}
		}
		groups.addAll(secondaries);
		return groups;
	}
}