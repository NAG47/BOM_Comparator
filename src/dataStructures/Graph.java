package dataStructures;

public class Graph<Item>{
	
	private int V;
	private int E;
	private String name;
	private Node<Item> root;
	
	
	public int V(){
		return V;
	}
	
	public int E(){
		return E;
	}
	
	public Node<Item> addVertice(Item item){
		Node<Item> v = new Node<Item>();
		v.item = item;
		v.adj = new Bag<Node<Item>>();
		if (V == 0) {
			root = v;
		}
		V++;
		return v;
	}
	
	public Node<Item> getRoot(){
		return root;
	}
	
	public void addEdge(Node<Item> child, Node<Item> parent){
		child.edgedTo = parent;
		parent.adj.add(child);
		E++;
	}
	
	public Bag<Node<Item>> adj(Node<Item> v){
		return v.adj;
	}
	
	public String toString(){
		if (name != null) {
			return "Graph: " + name + "\nVertices: " + V() + "\nEdges: " + E();
		}  
		return "Graph: _name_\nVertices: " + V() + "\nEdges: " + E();
	}

	

}
