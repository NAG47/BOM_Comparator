package dataStructures;

import core.Part.Part;

public class DFP {
	
	private final Node<Part> s;
	private Queue<Node<Part>> nodes = new Queue<Node<Part>>();
	
	public DFP(Graph<Part> G, Node<Part> s){
		this.s = s;
		Reverse_dfs(G, s);
		dfs(G, s);
	}
	
	public Iterable<Node<Part>> getNodes(){
		return nodes;
	}
	
	private Queue<Node<Part>> dfs(Graph<Part> G, Node<Part> v){
		v.marked = true;
		
		nodes.enqueue(v);
		for (Node<Part> w : G.adj(v)) {
			if (!w.marked) {
				w.edgedTo = v;
				dfs(G, w);
			}
		}
		return nodes;
	}
	
	private void Reverse_dfs(Graph<Part> G, Node<Part> v){
		v.marked = false;
		for (Node<Part> w : G.adj(v)) {
			if (w.marked) {
				w.edgedTo = v;
				Reverse_dfs(G, w);
			}
		}
	}
	
	public boolean hasPathTo(Node<Part> v){
		return v.marked;
	}
	
	public Iterable<Node<Part>> pathTo(Node<Part> v){
		if(!hasPathTo(v)){
			return null;
		}
		Stack<Node<Part>>path = new Stack<Node<Part>>();
		for (Node<Part> x = v; x != s; x = x.edgedTo) {
			path.push(x);
		}
		path.push(s);
		return path;
	}

}
