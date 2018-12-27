package net.amygdalum.util.graph;

import java.util.Map;

public class DataDescriptor<T> {
	
	private Class<T> clazz;
	private String key;
	
	public DataDescriptor(Class<T> clazz, String key) {
		this.clazz = clazz;
		this.key = key;
	}

	public T from(Map<DataDescriptor<?>, Object> repo) {
		return clazz.cast(repo.get(this));
	}

	public void to(Map<DataDescriptor<?>, Object> repo, T data) {
		repo.put(this, data);
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 13 + key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DataDescriptor<?> that = (DataDescriptor<?>) obj;
		return this.clazz == that.clazz
			&& this.key.equals(that.key);
	}

	
	
}
