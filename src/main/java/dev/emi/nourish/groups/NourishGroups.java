package dev.emi.nourish.groups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.emi.nourish.effects.NourishEffects;
import net.minecraft.util.Identifier;

public class NourishGroups {
	public static List<NourishGroup> groups = new ArrayList<NourishGroup>();
	public static Map<String, NourishGroup> byName = new HashMap<String, NourishGroup>();

	public static void init() {
		try {
			File dir = new File("config");
			dir.mkdirs();
			File config = new File("config/nourish.json");
			if (!config.exists()) {
				config.createNewFile();
				OutputStream os = new FileOutputStream(config);
				InputStream is = NourishGroups.class.getClassLoader().getResourceAsStream("config.json");
				byte[] buffer = new byte[1024];
				while (true) {
					int bytesRead = is.read(buffer);
					if (bytesRead == -1)
						break;
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.close();
			}
			List<NourishGroup> secondaries = new ArrayList<NourishGroup>();
			Gson gson = new Gson();
			JsonObject o = gson.fromJson(new FileReader("config/nourish.json"), JsonObject.class);
			JsonArray list = (JsonArray) o.get("groups");
			for (JsonElement j: list) {
				try {
					NourishGroup g = gson.fromJson(j, NourishGroup.class);
					g.identifier = new Identifier("nourish", g.name);
					if (g.secondary) {
						secondaries.add(g);
						byName.put(g.name, g);
					} else {
						groups.add(g);
						byName.put(g.name, g);
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
			NourishEffects.init((JsonArray) o.get("effects"));
		} catch (Exception e) {
			System.err.print("[nourish] Could not load Nourish groups file:");
			e.printStackTrace();
		}
	}
}