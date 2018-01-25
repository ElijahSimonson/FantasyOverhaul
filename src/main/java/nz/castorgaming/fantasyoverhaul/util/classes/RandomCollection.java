package nz.castorgaming.fantasyoverhaul.util.classes;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {

	private NavigableMap<Double, E> map;
	private Random random;
	private double total;

	public RandomCollection() {
		this(new Random());
	}

	public RandomCollection(Random random) {
		map = new TreeMap<Double, E>();
		total = 0.0;
		this.random = random;
	}

	public void add(double weight, E result) {
		if (weight > 0.0) {
			total += weight;
			map.put(total, result);
		}
	}

	public E next() {
		double value = random.nextDouble() * total;
		return map.ceilingEntry(value).getValue();
	}

}
