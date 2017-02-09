package dataStructures;

import javax.swing.tree.DefaultMutableTreeNode;

import core.Part.Part;

public class Node<Item> implements Comparable{
	
	public Item item;
	public Bag<Node<Item>> adj = new Bag<Node<Item>>();
	public boolean marked;
	public Node<Item> edgedTo;
	public int indent = 0;
	public DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
	
	public String toString(){
		return ""+item;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj){
		Node<Item> p = (Node<Item>) obj;
		if (item.equals(p.item)) {
			return true;
		} 
		return false;
	}
	
	public boolean getMarked(){
		return marked;
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof Node<?>) {
			Node<Part> part = (Node<Part>) arg0;
			if (((Part) this.item).getID().compareTo(part.item.getID()) < 0) {
				return -1;
			}
			else return 1;
		}
		return 0;
	}
	
	

}
