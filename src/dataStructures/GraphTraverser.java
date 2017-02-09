package dataStructures;

import java.io.IOException;

import core.BOM.BOM;
import core.Part.Part;
import testFunctions.Function;
import testFunctions.Show;

public class GraphTraverser<Item> {
	
	private Graph<Item> graph;
	
	public GraphTraverser(Graph<Item> g){
		graph = g;
	}
	
	public void breadthFirstTraversal(Node<Item> s, Function f){
		Queue<Node<Item>> q = new Queue<Node<Item>>();
		q.enqueue(s);
		
		while(!q.isEmpty()){
			Node<Item> current = q.dequeue();
			f.execute(current.item);
			for (Node<Item> node : current.adj) {
				q.enqueue(node);
			}
		}
	}
	
	public void depthFirstTraversal(Node<Item> s, Function f){
		f.execute(s.item);
		for (Node<Item> node : s.adj) {
			depthFirstTraversal(node, f);
		}
	}
	
	public static void main(String[] args) throws IOException {
		BOM b = new BOM("C:/Users/Nuan/Desktop/BOM_Comparator/Input/BOMs/WindChill BOM/A531E20.csv", null);
		GraphTraverser<Part> g = new GraphTraverser<Part>(b);
		g.depthFirstTraversal(b.getRoot(), new Show<Part>());
	}

}
