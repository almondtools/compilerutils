package net.amygdalum.util.text;

public interface NodeResolver<T> {

	void compile(T node);

	void link(T node);

	T resolve(T node);
	
}
