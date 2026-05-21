package dev.emi.trinkets.api.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.item.Item;

@Deprecated
public class TrinketRendererRegistry {

	private static final Map<Item, TrinketRenderer> RENDERERS = new HashMap<>();

	/**
	 * Registers a trinket renderer for the provided item
	 */
	public static void registerRenderer(Item item, TrinketRenderer trinketRenderer) {
		RENDERERS.put(item, trinketRenderer);
	}

	public static Optional<TrinketRenderer> getRenderer(Item item) {
		return Optional.ofNullable(RENDERERS.get(item));
	}
}
