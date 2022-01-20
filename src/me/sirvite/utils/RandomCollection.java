package me.sirvite.utils;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCollection<E> {
  private final NavigableMap<Double, E> map = new TreeMap<>();
  
  private double total = 0.0D;
  
  public void add(double weight, E result) {
    if (weight <= 0.0D)
      return; 
    this.total += weight;
    this.map.put(Double.valueOf(this.total), result);
  }
  
  public E next() {
    double value = ThreadLocalRandom.current().nextDouble() * this.total;
    return (E)this.map.ceilingEntry(Double.valueOf(value)).getValue();
  }
  
  public boolean isEmpty() {
    return (this.map.size() == 0);
  }
}
