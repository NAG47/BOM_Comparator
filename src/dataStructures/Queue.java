package dataStructures;

import java.util.Iterator;

public class Queue<Item> implements Iterable<Item> {
	
	private Node first;
	private Node last;
	private int N;
	
	private class Node{
		Item item;
		Node next;
	}
	
	public boolean isEmpty(){
		return first == null;
	}
	
	public boolean contains(Item item){
		for (Item i : this) {
			if (i == item) {
				return true;
			}
		}
		return false;
	}

	public int size(){
		return N;
	}
	
	public void enqueue(Item item){
		Node oldlast = last;
		last = new Node();
		last.item = item;
		last.next = null;
		if(isEmpty()){
			first = last;
		} else {
			oldlast.next = last;
		}
		N++;	
	}
	
	public Item peek(){
		return first.item;
	}
	
	public Item dequeue(){
		Item item = first.item;
		first = first.next;
		N--;
		if(isEmpty()){
			last = null;
		}
		return item;
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
