package dataStructures;

import java.util.Iterator;

public class SymbolTable<Key, Value> implements Iterable<Value> {
	
	private Node first;
	
	private class Node{
		Key key;
		Value val;
		Node next;
		
		public Node(Key key, Value val, Node next){
			this.key = key;
			this.val = val;
			this.next = next;
		}	
	
		public String toString(){
			return "< " + key + ", " + val + ">";
		}
	}
	
	public Value get(Key key){
		for(Node x = first; x != null; x = x.next){
			if(key.equals(x.key)){
				return x.val;
			}
		}
		return null;
	}
	
	public void put(Key key, Value val){
		if (first == null) {
			first = new Node(key, val, first);
			return;
		}
		
		for (Node x = first; x != null; x = x.next){
			if(key.equals(x.key)){
				x.val = val;
				return;
			}
		}
		first = new Node(key, val, first);
	}
	
	public Iterator<Value> iterator() {
		return new ListIterator();
	}
	
	private class ListIterator implements Iterator<Value>{
		
		private Node current = first;
		
		public boolean hasNext(){
			return current != null;
		}
			
		public Value next(){
			Value item = current.val;
			current = current.next;
			return item;
		}

		@Override
		public void remove() {
		}
	}
	
	

}
