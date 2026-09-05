package org.aiot.lang;

import org.nutz.lang.Lang;
import org.quartz.utils.CircularLossyQueue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class CriQueue<T> extends CircularLossyQueue<T> {

	Object[] arr;
	/**
	 * Constructs the circular queue with the specified capacity
	 *
	 * @param size
	 */
	public CriQueue(int size) {
		super(size);
		arr = new Object[size];
	}

	public CriQueue<T>  create(int size){
		return new CriQueue<>(size);
	}

	public T[] toArray() {
		T[] array = toArrayReverse();
		Lang.reverse(array);
		return array;
	}

	public T[] toArrayReverse() {
		return super.toArray((T[]) arr);
	}

	public List<T> toList(){
		return Arrays.asList(toArray());
	}

	public List<T> toListReverse(){
		List<T> list = Arrays.asList(toArrayReverse());
		Collections.reverse(list);
		return list;
	}

	public String toString(){
		return Arrays.toString(toArray());
	}

	public int size(){
		return depth();
	}

	public boolean isEmpty(){
		return depth() == 0;
	}

	public void add(T v){
		push(v);
	}

	public float averag(ToDoubleFunction<? super T> mapper){
		return toList().stream().filter(Objects::nonNull).collect(Collectors.averagingDouble(mapper)).floatValue();
	}
}
