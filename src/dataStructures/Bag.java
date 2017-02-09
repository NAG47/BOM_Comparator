package dataStructures;

import java.util.Iterator;
import core.Part.*;

public class Bag<Item> implements Iterable<Item> {
	
	private Node first, last;
	private int size;
	
	private class Node{
		Item item;
		Node next;
	}
	
	public void add(Item item){
		size++;
		Node oldlast = last;
		last = new Node();
		last.item = item;
		last.next = null;
		if(isEmpty()){
			first = last;
		} else {
			oldlast.next = last;
		}	
	}
	
	public boolean add(Item item, boolean safetyMode){
		size++;
		Node oldlast = last;
		last = new Node();
		last.item = item;
		last.next = null;
		if(isEmpty()){
			first = last;
		} else {
			oldlast.next = last;
		}
		if (safetyMode) {
			for (Item p : this) {
				if (p == item) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	public void remove(Item item){
		size--;
		if (first.item == item) {
			first = first.next;
		} else {
			Node nPrime = first.next;
			Node nSecondary = first;
			while(nPrime != null){
				if (nPrime.item == item) {
					nSecondary.next = nPrime.next;
					break;
				}
				nPrime = nPrime.next;
				nSecondary = nSecondary.next;
			}
		}
	}
	
	public int size(){
		return size;
	}
	
	public boolean isEmpty(){
		return first == null;
	}
	
	public void show(){
		if (first != null) {
			Node a = first;
			while(a.next != null){
				System.out.println(a.item);
				a = a.next;
			}
			System.out.println(a.item);
		}
	}
	
	public Item get(){
		return first.item;
	}
	
	public Item get(Item item){
		for(Item i : this){
			if (i.equals(item)) {
				return i;
			}
		}
		return null;
	}
	
	public Bag<Item> removeDuplicates(){
		HashTable<String, Item> ht = new HashTable<String, Item>(2000);
		for(Item n : this){
			dataStructures.Node<Part> node = (dataStructures.Node<Part>)n;
			ht.put(node.edgedTo + "-" + node, n);
		}
		return  ht.toList();
	}

	public Iterator<Item> iterator() {
		return new ListIterator();
	}
	
	private class ListIterator implements Iterator<Item>{
		
		private Node current = first;
		
		public boolean hasNext(){
			return current != null;
		}
		
		public Item next(){
			Item item = current.item;
			current = current.next;
			return item;
		}

		@Override
		public void remove() {
		}
	}

}
