package net.amygdalum.util.graph;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class GraphNode<T extends Comparable<T>> {

	private T key;
	private Map<DataDescriptor<?>, Object> data;
	private GraphNode<T>[] predecessors;
	private GraphNode<T>[] successors;

	@SuppressWarnings("unchecked")
	public GraphNode(T key) {
		this.key = key;
		this.data = new HashMap<>();
		this.predecessors = new GraphNode[0];
		this.successors = new GraphNode[0];
	}

	public T getKey() {
		return key;
	}

	public <S> S getData(DataDescriptor<S> desc) {
		return desc.from(data);
	}

	public <S> void setData(DataDescriptor<S> desc, S data) {
		desc.to(this.data, data);
	}

	public void addPredecessor(GraphNode<T> pre) {
		int index = binarySearchFor(predecessors, pre.getKey());
		if (index < predecessors.length && predecessors[index] == pre) {
			return;
		}

		@SuppressWarnings("unchecked")
		GraphNode<T>[] newpredecessors = new GraphNode[predecessors.length + 1];

		System.arraycopy(predecessors, 0, newpredecessors, 0, index);
		newpredecessors[index] = pre;
		System.arraycopy(predecessors, index, newpredecessors, index + 1, predecessors.length - index);
		predecessors = newpredecessors;
	}

	public GraphNode<T>[] getPredecessors() {
		return predecessors;
	}

	public ListIterator<GraphNode<T>> predecessorsFor(T key) {
		return new PredecessorIterator<>(this, key);
	}

	public void addSuccessor(GraphNode<T> suc) {
		int index = binarySearchFor(successors, suc.getKey());
		if (index < successors.length && successors[index] == suc) {
			return;
		}

		@SuppressWarnings("unchecked")
		GraphNode<T>[] newsuccessors = new GraphNode[successors.length + 1];

		System.arraycopy(successors, 0, newsuccessors, 0, index);
		newsuccessors[index] = suc;
		System.arraycopy(successors, index, newsuccessors, index + 1, successors.length - index);
		successors = newsuccessors;
	}

	public GraphNode<T>[] getSuccessors() {
		return successors;
	}

	public ListIterator<GraphNode<T>> successorsFor(T key) {
		return new SuccessorIterator<>(this, key);
	}

	private static <S extends Comparable<S>> int binarySearchFor(GraphNode<S>[] array, S key) {
		if (array.length == 0) {
			return 0;
		} else if (key.compareTo(array[0].key) <= 0) {
			return 0;
		} else if (key.compareTo(array[array.length - 1].key) > 0) {
			return array.length;
		}
		int low = 0;
		int high = array.length - 1;
		while (low+1 < high) {
			int pivot = (low + high) / 2;
			S pivotKey = array[pivot].key;
			int comp = key.compareTo(pivotKey);
			if (comp <= 0) {
				high = pivot;
			} else {
				low = pivot;
			}
		}
		if (array[low].key.equals(key)) {
			return low;
		} else if(array[high].key.equals(key)) {
			return high;
		} else {
			return high;
		}
	}

	@Override
	public String toString() {
		return Objects.toString(key);
	}

	private abstract static class ArrayIterator<T extends Comparable<T>> implements ListIterator<GraphNode<T>> {

		protected GraphNode<T> node;
		protected GraphNode<T>[] array;
		protected T key;
		protected int index;

		public ArrayIterator(GraphNode<T> node, GraphNode<T>[] array, T key, int index) {
			this.node = node;
			this.array = array;
			this.key = key;
			this.index = index;
		}
		
		@Override
		public boolean hasNext() {
			if (index >= array.length || !key.equals(array[index].key)) {
				return false;
			}
			return true;
		}

		@Override
		public GraphNode<T> next() {
			if (index >= array.length || !key.equals(array[index].key)) {
				throw new NoSuchElementException();
			}
			GraphNode<T> result = array[index];
			index++;
			return result;
		}

		@Override
		public boolean hasPrevious() {
			if (index <= 0 || !key.equals(array[index-1].key)) {
				return false;
			}
			return true;
		}

		@Override
		public GraphNode<T> previous() {
			if (index <= 0 || !key.equals(array[index-1].key)) {
				throw new NoSuchElementException();
			}
			GraphNode<T> result = array[index-1];
			index--;
			return result;
		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public int previousIndex() {
			return index-1;
		}
	}
	
	private static class PredecessorIterator<T extends Comparable<T>> extends ArrayIterator<T> {

		
		public PredecessorIterator(GraphNode<T> node, T key) {
			super(node, node.predecessors, key, binarySearchFor(node.predecessors, key));
		}

		@Override
		public void remove() {
			@SuppressWarnings("unchecked")
			GraphNode<T>[] newarray = new GraphNode[array.length - 1];

			System.arraycopy(array, 0, newarray, 0, index);
			System.arraycopy(array, index + 1, newarray, index, array.length - index - 1);
			node.predecessors = newarray;
			array = node.predecessors;
		}

		@Override
		public void set(GraphNode<T> e) {
			node.predecessors[index] = e;
			array = node.predecessors;
		}

		@Override
		public void add(GraphNode<T> e) {
			@SuppressWarnings("unchecked")
			GraphNode<T>[] newarray = new GraphNode[array.length + 1];

			System.arraycopy(array, 0, newarray, 0, index);
			newarray[index] = e;
			System.arraycopy(array, index, newarray, index + 1, array.length - index);
			node.predecessors = newarray;
			array = node.predecessors;
		}

	}

	private static class SuccessorIterator<T extends Comparable<T>> extends ArrayIterator<T> {
		
		public SuccessorIterator(GraphNode<T> node, T key) {
			super(node, node.successors, key, binarySearchFor(node.successors, key));
		}

		@Override
		public void remove() {
			@SuppressWarnings("unchecked")
			GraphNode<T>[] newarray = new GraphNode[array.length - 1];

			System.arraycopy(array, 0, newarray, 0, index);
			System.arraycopy(array, index + 1, newarray, index, array.length - index - 1);
			node.successors = newarray;
			array = node.successors;
		}

		@Override
		public void set(GraphNode<T> e) {
			node.successors[index] = e;
			array = node.successors;
		}

		@Override
		public void add(GraphNode<T> e) {
			@SuppressWarnings("unchecked")
			GraphNode<T>[] newarray = new GraphNode[array.length + 1];

			System.arraycopy(array, 0, newarray, 0, index);
			newarray[index] = e;
			System.arraycopy(array, index, newarray, index + 1, array.length - index);
			node.successors = newarray;
			array = node.successors;
		}

	}
	
}
