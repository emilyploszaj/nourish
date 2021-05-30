package dev.emi.nourish.profile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.emi.nourish.effects.NourishEffect;
import dev.emi.nourish.effects.NourishEffects;
import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;

public class NourishProfiles {
	public static final Gson GSON = new Gson();
	public static Map<String, NourishProfile> profiles = new HashMap<>();
	public static NourishProfile defaultProfile;

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
			JsonObject o = GSON.fromJson(new FileReader("config/nourish.json"), JsonObject.class);
			defaultProfile = loadProfile("default", o);
			profiles.put("default", defaultProfile);
			if (o.has("profiles")) {
				JsonArray arr = o.getAsJsonArray("profiles");
				for (JsonElement el : arr) {
					JsonObject obj = (JsonObject) el;
					String name = obj.get("name").getAsString();
					NourishProfile profile = loadProfile(name, obj);
					profiles.put(name, profile);
				}
			}
		} catch (Exception e) {
			System.err.print("[nourish] Could not load Nourish config file:");
			e.printStackTrace();
		}
	}

	public static NourishProfile getProfile(String s) {
		return profiles.getOrDefault(s, defaultProfile);
	}

	public static NourishProfile loadProfile(String name, JsonObject object) {
		List<NourishGroup> groups = NourishGroups.loadGroups((JsonArray) object.get("groups"));
		Map<String, NourishGroup> byName = new HashMap<>();
		for (NourishGroup group : groups) {
			byName.put(group.name, group);
		}
		List<NourishEffect> effects = NourishEffects.loadEffects(byName, (JsonArray) object.get("effects"));
		return new NourishProfile(name, groups, byName, effects);
	}
}
