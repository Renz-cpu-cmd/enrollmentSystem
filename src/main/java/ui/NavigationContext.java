package ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Lightweight data carrier passed between screens during navigation.
 */
public class NavigationContext {
	private final Map<String, Object> attributes = new HashMap<>();

	/**
	 * Stores a value under the given key. Null values remove the attribute.
	 */
	public NavigationContext put(String key, Object value) {
		Objects.requireNonNull(key, "key");
		if (value == null) {
			attributes.remove(key);
		} else {
			attributes.put(key, value);
		}
		return this;
	}

	/**
	 * Retrieves an attribute, or null when missing.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) attributes.get(key);
	}

	/**
	 * Returns true when the context contains the key.
	 */
	public boolean contains(String key) {
		return attributes.containsKey(key);
	}

	/**
	 * Provides an immutable snapshot of stored attributes.
	 */
	public Map<String, Object> asMap() {
		return Collections.unmodifiableMap(new HashMap<>(attributes));
	}
}
