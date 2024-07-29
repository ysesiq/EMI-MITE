package emi.dev.emi.emi.registry;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import emi.dev.emi.emi.runtime.EmiLog;
import emi.shims.java.net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EmiIngredientSerializers {
	public static final Map<Class<?>, EmiIngredientSerializer<?>> BY_CLASS = Maps.newHashMap();
	public static final Map<String, EmiIngredientSerializer<?>> BY_TYPE = Maps.newHashMap();

	public static void clear() {
		BY_CLASS.clear();
		BY_TYPE.clear();
	}

	public static @Nullable JsonElement serialize(EmiIngredient ingredient) {
		if (ingredient == null || !BY_CLASS.containsKey(ingredient.getClass())) {
			return null;
		}
		try {
			return ((EmiIngredientSerializer) BY_CLASS.get(ingredient.getClass())).serialize(ingredient);
		} catch (Exception e) {
			EmiLog.error("Exception serializing stack " + ingredient);
			e.printStackTrace();
			return null;
		}
	}

	public static EmiIngredient deserialize(JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return EmiStack.EMPTY;
		}
		try {
			String type;
			if (element.isJsonObject()) {
				JsonObject json = element.getAsJsonObject();
				type = json.get("type").getAsString();
				if (type.equals("emi:item")) {
					json.addProperty("type", "item");
					if (!json.has("id")) {
						json.addProperty("id", JsonHelper.getString(json, "item", ""));
					}
				} else if (type.equals("emi:fluid")) {
					json.addProperty("type", "fluid");
					if (!json.has("id")) {
						json.addProperty("id", JsonHelper.getString(json, "fluid", ""));
					}
				} else if (type.equals("emi:item_tag")) {
					json.addProperty("type", "tag");
					json.addProperty("registry", "minecraft:item");
					if (!json.has("id")) {
						json.addProperty("id", JsonHelper.getString(json, "tag", ""));
					}
				}
				type = json.get("type").getAsString();
			} else {
				String[] split = element.getAsString().split(":");
				type = split[0];
				if (!BY_TYPE.containsKey(type) && type.startsWith("#")) {
					type = "tag";
				}
			}
			return ((EmiIngredientSerializer) BY_TYPE.get(type)).deserialize(element);
		} catch (Exception e) {
			EmiLog.error("Exception deserializing stack " + element);
			e.printStackTrace();
			return EmiStack.EMPTY;
		}
	}
}
