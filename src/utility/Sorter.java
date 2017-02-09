package utility;

import core.Part.Part;
import dataStructures.Bag;
import dataStructures.Node;

public class Sorter {
	
	private Comparable[] array;
	private Comparable[] aux;
	
	public Sorter(Comparable[] array){
		this.array = array;
	}
	
	public Sorter(Bag<Node<Part>> bag){
		array = new Comparable[bag.size()];
		int counter = 0;
		for (Comparable item : bag) {
			array[counter++] = item;
		}
	}
	
	public Bag<Node<Part>> mergeSort(){
		aux = new Comparable[array.length];
		mergeSort(0, array.length-1);
		Bag<Node<Part>> b = new Bag<Node<Part>>(); 
		for (int i = 0; i < array.length; i++) {
			b.add((Node<Part>)array[i]);
		}
		return b;
	}
	
	private void mergeSort(int lo, int hi){
		if (hi <= lo) return;
		int mid = lo + (hi - lo)/2;
		mergeSort(lo, mid);
		mergeSort(mid+1, hi);
		merge(lo, mid, hi);
	}

	private void merge(int lo, int mid, int hi){
		int i = lo, j = mid+1;
		
		for (int k = lo; k <= hi; k++) {
			aux[k] = array[k];
		}
		for (int k = lo; k <= hi; k++) {
			if (i > mid) array[k] = aux[j++];
			else if (j > hi) array[k] = aux[i++];
			else if (less(aux[j], aux[i])) array[k] = aux[j++];
			else array[k] = aux[i++];
		}
	}
	
	private boolean less(Comparable v, Comparable w){
		return v.compareTo(w) < 0;
	}
	
	private void exch(int i, int j){
		Comparable t = array[i]; array[i] = array[j]; array[j] = t;
	}

}
