package twitter.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Counter<T> extends HashMap<T, Integer> {

	public Counter(List<T> list) {
		super();
		for (T t : list) {
			put(t);
		}
	}

	public Counter() {
		super();
	}

	public void put(T key) {
		put(key, 1);
	}

	@Override
	public Integer put(T key, Integer value) {
		int oldValue = get(key);
		return super.put(key, value + oldValue);
	}

	@Override
	public Integer get(Object key) {
		if (super.containsKey(key)) {
			return super.get(key);
		} else {
			return 0;
		}
	}

	public List<CounterEntry<T>> getSortedResult() {
		List<CounterEntry<T>> result = new ArrayList<>(size());
		for (Entry<T, Integer> e : entrySet()) {
			result.add(new CounterEntry<T>(e.getKey(), e.getValue()));
		}
		Collections.sort(result);

		return result;
	}

	public static class CounterEntry<K> implements Entry<K, Integer>, Comparable<CounterEntry<K>>, Serializable {

		public CounterEntry(K key, int value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public Integer setValue(Integer value) {
			return value;
		}

		@Override
		public int compareTo(CounterEntry<K> o) {
			return o.value - this.value;
		}

		@Override
		public String toString() {
			return "CounterEntry [key=" + key + ", value=" + value + "]";
		}

		private K key;
		private int value;
		private static final long serialVersionUID = 1L;
	}

	private static final long serialVersionUID = 1L;
}
