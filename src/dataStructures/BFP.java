package dataStructures;

import core.Part.Part;

public class BFP {
	
	//private boolean[] marked;
	//private int[] edgeTo;
	private final Node<Part> s;
	
	public BFP(Graph<Part> G, Node<Part> s){
		//edgeTo = new int[G.V()];
		this.s = s;
		bfs(G, s);
	}
	
	private void bfs(Graph<Part> G, Node<Part> s){
		Queue<Node<Part>> queue = new Queue<Node<Part>>();
		s.marked = true;
		queue.enqueue(s);
		while(!queue.isEmpty()){
			Node<Part> v = queue.dequeue();
			for (Node<Part> w : G.adj(v)) {
				if (!w.marked) {
					w.edgedTo = v;
					w.marked = true;
					System.out.println();
					for (int i = 0; i < ((Part)w.item).getPos(); i++) {
						System.out.print("  ");
					}
					System.out.print(w);
					queue.enqueue(w);
				}
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
