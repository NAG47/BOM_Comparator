package core.BOM;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import core.Part.*;
import dataStructures.Node;
import dataStructures.Bag;
import dataStructures.DFP;
import dataStructures.Graph;
import dataStructures.HashTable;
import dataStructures.Queue;
import dataStructures.Stack;
import utility.Writer;

public class BOM extends Graph<Part>{
	
	private Node<Part> parentalNode;
	private Writer writer;
	private Bag<Node<Part>> bagONodes = new Bag<Node<Part>>();
	private Bag<Part> listOfParts;
	private boolean correctBag = true; // false = graph was changed without changing the bag
	private boolean manipulated = false;
	
	public BOM(Writer writer) throws IOException{
		if (writer == null) {
			this.writer = new Writer("log.txt");
		} else this.writer = writer;
	}
	
	public BOM(String file, Writer writer) throws IOException{
		if (writer == null) {
			this.writer = new Writer("log.txt");
		} else this.writer = writer;
		 CSVReader reader = new CSVReader(new FileReader(file));
		 Queue<Part> queue = new Queue<Part>();
	     String [] nextLine;
	     int line = 0;
	     reader.readNext();
	     int count = 2;
	     while ((nextLine = reader.readNext()) != null) {
	    	 line++;
	    	try {
	    		Double matQty = 0.0;
				if (!nextLine[12].equals("") && !nextLine[12].equals("null")) {
					try {
						if (nextLine[12].length() > 2 && nextLine[12].substring(0, 2).equals("=\"")) {
							String qtyString = nextLine[12].substring(2);
							matQty =  Double.parseDouble(qtyString);
						} else matQty =  Double.parseDouble(nextLine[12]);  //Accounts for instances where no qty was input
					} catch (NumberFormatException e) {
						System.out.println("Two quantities found in the material quantities tab at line " + count);
					}
				}
				Double qty;
				if (nextLine[17].equals("")) {
					qty = 0.0;
				} else {
					qty = Double.parseDouble(nextLine[17].split(" each")[0]);
				}
				if (nextLine[0].equals("")) {
					break;
				}
				Part p = new Part(Integer.parseInt(nextLine[0]), nextLine[1].trim(), nextLine[2].trim(),nextLine[8].trim(), nextLine[11].trim(), matQty, nextLine[13].trim(), nextLine[14].trim(), nextLine[16].trim(), qty);
				queue.enqueue(p);
				count++;
			} catch (Exception e) {
				System.out.println("Something went wrong while reading a BOM, line: " + line); reader.close(); return;
			}
	     }
			this.arrange(queue);
			reader.close();
	}
	
	public Node<Part> getParent(){
		return parentalNode;
	}
	
	public Bag<Node<Part>> getNodes(){
		bagONodes = new Bag<Node<Part>>();
		DFP dfp = new DFP(this, parentalNode);
		for(Node<Part> d : dfp.getNodes()){
			bagONodes.add(d);
		}
		correctBag = true;
		return bagONodes;
		
	}
	
	public static boolean adjBagContainsID(Bag<Node<Part>> b, String ID){
		for(Node<Part> n : b){
			if (n.item.getID().trim().equals(ID.trim())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isMBOM(){
		String ID = parentalNode.item.getID();
		if (isNum(ID.toCharArray()[1])) {
			if (!parentalNode.item.getView().equals("Design")) {
			return true;
			}
		}
		return false;
	}
	
	public boolean isEBOM(){
		String ID = parentalNode.item.getID();
		if (isNum(ID.toCharArray()[1])) {
			if (!parentalNode.item.getView().equals("Design")) {
			return false;
			}
		}
		return true;
	}
	
	public void prune(Node<Part> removedPart){ //Removes a part and all of it's children
		correctBag = false;
		removedPart.edgedTo.adj.remove(removedPart);
		removedPart.edgedTo = null;
	}
	
	public void pruneFamily(Node<Part> part){
		correctBag = false;
		for(Node<Part> n : part.adj){
			prune(n);
		}
	}
	
	public void remove(Node<Part> removedPart){ //Removes a part, but keeps any children
		correctBag = false;
		Node<Part> replacement = removedPart.edgedTo;
		
		for(Node<Part> n : removedPart.adj){
			n.edgedTo = replacement;
			replacement.adj.add(n);
		}
		replacement.adj.remove(removedPart);
		removedPart.edgedTo = null;
		reOrganize(replacement);
	}
	
	public void reOrganize(Node<Part> root){
		Queue<Node<Part>> queue = new Queue<Node<Part>>();
		queue.enqueue(root);
		while(!queue.isEmpty()){
			Node<Part> current = queue.dequeue();
			for(Node<Part> n : current.adj){
				queue.enqueue(n);
				if (n.item.getPos() - 1 != current.item.getPos()) {
					n.item.setPos(current.item.getPos() + 1);
				}
			}
		}
	}
	
	public Node<Part> getMainParent(Node<Part> n){
		while(n.item.getPos() > 1){
			n = n.edgedTo;
		}
		return n;
	}
	
	public void show(){
	}
	
	public void addToList(Node<Part> p){
		bagONodes.add(p);
	}
	
	public void showChildrenOf(Node<Part> part){
		for(Node<Part> child : part.adj){
			System.out.println(child);
		}
	}
	
	public void compileListOfParts(){ //Removes duplicates
		HashTable<String, Part> ht = new HashTable<String, Part>(2000);
		for(Node<Part> n : getNodes()){
			ht.put(n.item.getID(), n.item);
		}
		listOfParts = ht.toList();
	}
	
	public void arrange(Queue<Part> queue){
		Stack<Node<Part>> parents = new Stack<Node<Part>>();
		for (Part p : queue) {
			Node<Part> pNode = this.addVertice(p);
			bagONodes.add(pNode);

			if (p.getPos() == 0) {
				parents.push(pNode);
				parentalNode = pNode;
			} 
			else if(p.getPos() == parents.peek().item.getPos() + 1){
				this.addEdge(pNode, parents.peek());
				parents.push(pNode);
			}
			
			else if(p.getPos() == parents.peek().item.getPos()){
				parents.pop();
				this.addEdge(pNode, parents.peek());
				parents.push(pNode);
			}
			
			else if(p.getPos() < parents.peek().item.getPos()){
				while(p.getPos() < parents.peek().item.getPos()){
					parents.pop();
				}
				
				if(p.getPos() == parents.peek().item.getPos()){
					parents.pop();
					this.addEdge(pNode, parents.peek());
					parents.push(pNode);
				} else System.out.println("UNACCOUNTED ERROR WHILE READING BOM");
			}
		}
		
	}
	
	public Bag<Part> getListOfParts(){
		return listOfParts;
	}
	
	private boolean isNum(char character){
		try {
			int num = Integer.parseInt(String.valueOf(character));
			for (int i = 0; i < 10; i++) {
				if (num == i) {
					return true;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}
	
	public void setManipulated(){
		manipulated = true;
	}
	
	public boolean getManipulated(){
		return manipulated;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}
	
}
