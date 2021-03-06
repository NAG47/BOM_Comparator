package core;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import core.BOM.*;
import core.Part.MaterialPart;
import core.Part.Part;
import core.Part.PaymentPart;
import dataStructures.Bag;
import dataStructures.DFP;
import dataStructures.HashTable;
import dataStructures.Node;
import dataStructures.Queue;
import dataStructures.Stack;
import utility.Writer;
import utility.codePopup;


public class Manipulator {
	
	private BOM B1, B2;
	private HashTable<String, Part> ht = new HashTable<String, Part>(50000);
	private HashTable<String, Part> sfList = new HashTable<String, Part>();
	private Bag<Node<Part>> deleted  = new Bag<Node<Part>>(), added = new Bag<Node<Part>>(), qtyChanged = new Bag<Node<Part>>(), matched = new Bag<Node<Part>>();
	private DFP B1_dfp, B2_dfp;
	private Writer writer;
	private double sum;
	private DecimalFormat format = new DecimalFormat("##.000");
	private JFrame frame;
	private String pickListCode;
	
	public Manipulator(BOM B1, BOM B2, Writer writer){ //B1 = current, B2 = new
		this.writer = writer;
		this.B1 = B1;
		this.B2 = B2;
	}
	
	public Manipulator(BOM B2, Writer writer){
		this.writer = writer;
		this.B2 = B2;
	}
	
	public Manipulator(BOM B1, BOM B2, Writer writer, JFrame frame){ //B1 = current, B2 = new
		this.writer = writer;
		this.B1 = B1;
		this.B2 = B2;
		this.frame = frame;
	}
	
	public void doAllManipulations(BOM b) throws IOException{
		removeInvalidExtensions(b);
		if (b.isMBOM()) {
			pickListManip(b);
		}
		hoseAndPipeManip(b);
		IM_Match("C:/Users/Nuan/Desktop/BOM_Comparator/Input/IM lists/IMstatic.csv", b);
		if (b.isEBOM()) {
			removeConceptNum(b);
		}
		removeConceptFamilies(b);
		it4Delete(b);
		addDuplicates(b);
		changeTempID(b);
		hoseAssemblyManip(b);
		addMaterialChild(b);
		addPaymentChild(b);
		weldNumManip(0.023, b);
		scrapFactorManip("C:/Users/Nuan/Desktop/BOM_Comparator/Input/Scrap factor lists/Scrap factors calc - rev S.csv" , "B50", b); 
		specialCaseRemoval("DC,AL", b); 
		addDuplicates(b);
	}
	
	public boolean doAllManipulations(BOM b, boolean[] options, String[] files) throws IOException{
		if (files[0].equals("")) {
			JOptionPane.showMessageDialog(frame, "The IM text box has been left open, please submit a valid ITEM MASTER file!"); return false;
		}
		if (files[1].equals("")) {
			JOptionPane.showMessageDialog(frame, "The Scrap List text box has been left open, please submit a valid SCRAP LIST file!"); return false;
		}
		if (files[2].equals("")) {
			JOptionPane.showMessageDialog(frame, "The Type text box has been left open, please submit a valid VEHICLE TYPE!"); return false;
		}
		if (files[3].equals("")) {
			JOptionPane.showMessageDialog(frame, "The Scrap Ratio text box has been left open, please submit a valid SCRAP RATIO!"); return false;
		}
		if (!b.getManipulated()) {
			System.out.println("removing invalid extensions");
			removeInvalidExtensions(b);
			if (options[0]) {
				if (b.isMBOM()) {
					System.out.println("PickList Manipulation");
					pickListManip(b);
				}
			}
			if (options[1]) {
				System.out.println("Hose and pipe ");
				hoseAndPipeManip(b);
			}
			if (options[2]) {
				System.out.println("IM Match");
				if (ht.getSize() > 0) {
					IM_Match(b);
				} else IM_Match(files[0], b);
			}
			if (options[3]) {
				if (B2.isEBOM()) {
					System.out.println("removing concept parts");
					removeConceptNum(b);
				}
				System.out.println("removing deeper level CN");
				removeConceptFamilies(b);
			}
			if (options[4]) {
				System.out.println("IT4");
				it4Delete(b);
			}
			if (options[5]) {
				System.out.println("add Duplicates");
				addDuplicates(b);
			}
			if (options[8]) {
				System.out.println("change temp ID");
				changeTempID(b);
			}
			if (options[6]) {
				System.out.println("Hose assembly");
				hoseAssemblyManip(b);
				System.out.println("add material child");
				addMaterialChild(b);
			}
			if (options[7]) {
				System.out.println("add payment child");
				addPaymentChild(b);
			}
			if (options[9]) {
				System.out.println("weld number manipulation");
				weldNumManip(Double.parseDouble(files[3].trim()), b);
			}
			if (options[10]) {
				System.out.println("scrap factor manipulation");
				scrapFactorManip(files[1] , files[2], b);
			}
			if (options[11]) {
				System.out.println("special case removal");
				specialCaseRemoval(files[4], b);
			}
			if (options[2]) {
				System.out.println("IM Match 2");
				if (ht.getSize() > 0) {
					IM_Match(b);
				} else IM_Match(files[0], b);
			}
			if (options[5]) {
				addDuplicates(b);
			}
			b.setManipulated();
		}
		return true;
	}
	
	private void readIMList(String filename, boolean first) throws FileNotFoundException{
		File file = new File(filename);
		if (first) {
			try {
				Scanner scan = new Scanner(file);
				CSVParser parser = new CSVParser();
				int count = 0;
				while(scan.hasNextLine()){
					
					String line = scan.nextLine();
					//System.out.println(count++ + " " + line);
					String[] s = parser.parseLine(line);
					Part p = new Part(s[0], s[1], s[7]);
					ht.put(p.getID(), p);
				}
				System.out.println(count + " lines read");
				scan.close();
			} catch (IOException e) {
				if (first && e.getMessage().equals("Un-terminated quoted field at end of CSV line")) {
					Scanner scanProbe = new Scanner(file);
					if (scanProbe.nextLine().charAt(0) == '"') {
						Scanner scan2 = new Scanner(file);
						while(scan2.hasNextLine()){
							String[] s = scan2.nextLine().split("\",\"");
							for (int i = 0; i < s.length; i++) {
								if (i != 0 && i != s.length-1) {
									s[i] = s[i].replace('"', ' ').trim();
								}
							}
						}
						readIMList(filename, false);
						scan2.close();
					} else readIMList(filename, false);
					scanProbe.close();
				}
			} 
		} else {
			ht = new HashTable<String, Part>(50000);
			Scanner scan = new Scanner(file);
			CSVParser parser = new CSVParser();
			int count = 0;
			while(scan.hasNextLine()){
				
				String line = scan.nextLine();
				count++;
				String[] s;
				try {
					s = parser.parseLine(line);
					Part p = new Part(s[0], s[1], s[7]);
					ht.put(p.getID(), p);
					//count++;
				} catch (Exception e) {
					System.out.println(e.getLocalizedMessage() + " at: - " + line);
					continue;
				}
				
			}
			scan.close();
			System.out.println(count + " lines read, if this number does not match your excel file, there may be line breaks somewhere in the file. \nIf any of the line breaks caused an exception, they were skipped, meaning a small amount of data might have been lost.");
		} 
	}
	
	private void readSFList(String fileName) throws IOException{
		File file = new File(fileName);
		CSVReader reader = new CSVReader(new FileReader(file));
		String[] nextLine;
		reader.readNext();
		reader.readNext();
		
		while((nextLine = reader.readNext()) != null){
			Part p = new Part(nextLine[1], nextLine[2], Double.parseDouble(format(nextLine[4])));
			//System.out.println(p.getID()+","+p.getMachineType().trim());
			sfList.put(p.getID()+","+p.getMachineType().trim(), p);
		}
		reader.close();
	}
	
	public String format(String s){
		char[] array = s.toCharArray();
		int per = 0, dot = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '%') {
				per = i;
			} else if (array[i] == '.') {
				dot = i;
			}
		}
		StringBuilder st = new StringBuilder(s.trim());
		st.deleteCharAt(per);
		st.deleteCharAt(dot);
		
		return (new StringBuilder("0.")).append(st.toString()).toString();
	}
	
	public void IM_Match(String fileName, BOM b) throws IOException{
		if (b instanceof WindChill_BOM) {
			readIMList(fileName, true);
			for(Node<Part> n : b.getNodes()) {
				Part temp = ht.get(n.item.getID());
				if(temp != null) {
					n.item.setMKNum(temp.getMKNum());
					n.item.setName(temp.getName());
				} else {
					n.item.setMKNum("");
					n.item.setName("NoI/T");
				}
			}
		}
	}
	
	public void IM_Match(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> n : b.getNodes()) {
				Part temp = ht.get(n.item.getID());
				if(temp != null) {
					n.item.setMKNum(temp.getMKNum());
					n.item.setName(temp.getName());
				} else {
					n.item.setMKNum("");
					n.item.setName("NoI/T");
				}
			}
		}
	}
	
	public void populateBOM(BOM b){
		if (b instanceof WindChill_BOM) {
			Queue<Node<Part>> q = new Queue<Node<Part>>();
			for (Node<Part> node : b.getRoot().adj) {
				q.enqueue(node);
			}
			
			while(!q.isEmpty()){
				Node<Part> current = q.dequeue();
				added.add(current);
				for (Node<Part> node : current.adj) {
					q.enqueue(node);
				}
			}
		}
	}
	
	public void compare(){
		try {
			if (B1 == null) {
				return;
			}
			clearMarks();
			for (Node<Part> aChild : B1.getRoot().adj) {
				if (!aChild.marked) {
					for (Node<Part> bChild : B2.getRoot().adj) {
						if (aChild.equals(bChild) && !bChild.marked) {
							if (!aChild.item.compareQty(bChild.item)) {
								qtyChanged.add(bChild);
							}
							aChild.marked = true;
							bChild.marked = true;
							compare(aChild, bChild);
						}
					}
				}
			}
			
			Queue<Node<Part>> B1bfs = new Queue<Node<Part>>();
			B1bfs.enqueue(B1.getRoot());
			while(!B1bfs.isEmpty()){
				Node<Part> current = B1bfs.dequeue();
				for (Node<Part> part : current.adj) {
					if (!part.marked) {
						deleted.add(part);
					} else {
						matched.add(part);
					}
					B1bfs.enqueue(part);
				}
			}
			
			Queue<Node<Part>> B2bfs = new Queue<Node<Part>>();
			B2bfs.enqueue(B2.getRoot());
			while(!B2bfs.isEmpty()){
				Node<Part> current = B2bfs.dequeue();
				for (Node<Part> part : current.adj) {
					if (!part.marked) {
						added.add(part);
					}
					B2bfs.enqueue(part);
				}
			}
			clearMarks();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void compare(Node<Part> a, Node<Part> b){
		try {
			for (Node<Part> aChild : a.adj) {
				if (!aChild.marked) {
					for (Node<Part> bChild : b.adj) {
						if (aChild.equals(bChild) && !bChild.marked) {
							if (!aChild.item.compareQty(bChild.item)) {
								qtyChanged.add(bChild);
							}
							aChild.marked = true;
							bChild.marked = true;
							compare(aChild, bChild);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public void totalToEach(BOM b){
		if (b instanceof AS400_BOM) {
			clearMarks();
			Queue<Node<Part>> queue = gatherLeaves(b);
			
			while(!queue.isEmpty()){
				Node<Part> p = queue.dequeue();
				if (!p.marked) {
					fixQty(p);
					p.marked = true;

					if (!queue.contains(p.edgedTo) && p.edgedTo.item.getPos() != 0) {
						if (checkParent(p)) {
							queue.enqueue(p.edgedTo);
						}
					}
				}
				
				
			}
			clearMarks();
		}
	}*/
	
	private boolean checkParent(Node<Part> p) { //Checks if a part's parent has any children that are not in the queue
		for (Node<Part> node : p.edgedTo.adj) {
			if (!node.marked) {
				return false;
			}
		}
		return true;
	}
	
	private void fixQty(Node<Part> current){
		if (current.edgedTo.item.getQty() == 0) {
			current.item.setQty(current.item.getQty() / 1.0);
		} else current.item.setQty(current.item.getQty() / current.edgedTo.item.getQty());
	}
	
	private Queue<Node<Part>> gatherLeaves(BOM b){
		Queue<Node<Part>> leaves = new Queue<Node<Part>>();
		Queue<Node<Part>> q = new Queue<Node<Part>>();
		q.enqueue(b.getRoot());
		
		while(!q.isEmpty()){
			Node<Part> current = q.dequeue();
			if (current.adj.isEmpty()) {
				leaves.enqueue(current);
			} else {
				for (Node<Part> node : current.adj) {
					q.enqueue(node);
				}
			}
		}
		
		return leaves;
	}
	
	public void removeExtrasFromBag(Bag<Node<Part>> bag){
		clearMarks();
		for (Node<Part> n : bag) {
			if (bag.get(n.edgedTo) != null) {
				n.marked = true;
			}
		}
		for (Node<Part> n : bag) {
			if (n.marked) {
				bag.remove(n);
			}
		}
		clearMarks();
	}
	
	public void clearMarks(){
		for (Node<Part> n : B1.getNodes()) {
			n.marked = false;
		}
		for (Node<Part> n : B2.getNodes()) {
			n.marked = false;
		}
	}
	
	public void removeInvalidExtensions(BOM b){
		if (b instanceof WindChill_BOM) {
			for (Node<Part> current : b.getNodes()){
				for (char c : current.item.getID().toCharArray()) {
					if (c == '.') {
						char[] newID = new char[current.item.getID().length()-4];
						int count = 0;
						for (char c2 : current.item.getID().toCharArray()) {
							if (c == c2) {
								break;
							}
							newID[count++] = c2;
						}
						current.item.setID(String.valueOf(newID));
						break;
					}
				}
			}
		}
	}
	
	public void it4Delete(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				if (current.item.getMKNum().trim().equals("4")) {
					B2.pruneFamily(current);
				}
			}
		}
	}
	
	public void hoseAndPipeManip(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				char[] tempChar = current.item.getID().toCharArray();
				if (tempChar.length >= 3) {
					if (tempChar[tempChar.length-3] == '_') {
						for (int j = 1; j <= 3; j++) {
							tempChar[tempChar.length-j] = ' ';
						}
						current.item.setID(String.valueOf(tempChar).trim());
					}
				}
			}
		}
	}
	
	public void changeTempID(BOM b){
		if (b instanceof WindChill_BOM) {
			CharSequence CUST = "CUST";
			CharSequence MAT = "MAT";
			CharSequence CONS = "CONS";
			
			for(Node<Part> current : b.getNodes()){
				if(current.item.getID().trim().contains(CUST) || current.item.getID().trim().contains(MAT) || current.item.getID().trim().contains(CONS)){
					if (!current.item.getMatNum().equals("") && current.item.getMatQty() != 0) {
						current.item.setID(current.item.getMatNum());
						current.item.setQty(current.item.getMatQty());
						current.item.clear();
					} else System.out.println(current.item + " found without Material Num");
				}
			}
		}
		}
	
	public void removeConceptNum(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				if (current.item.getPos() == 1 && current.item.getID().substring(0, 2).equals("CN")) {
					b.remove(current);
				}
			}
		}
	}
	
	public void removeConceptFamilies(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				if (current.item.getID().substring(0, 2).equals("CN")) {
					b.prune(current);
				}
			}
		}
	}
	
	public void scrapFactorManip(String fileName, String type, BOM b) throws IOException{
		if (b instanceof WindChill_BOM) {
			readSFList(fileName);
			for(Node<Part> current : b.getNodes()){
				Part temp = sfList.get(current.item.getID()+","+type.trim());
				if (temp != null) {
					Double d = (current.item.getQty()/(1 -  temp.getScrapFactor()));
					d = Math.ceil(d * 1000)/1000;
					current.item.setQty(d);
				}
			}
		}
	}
	
	public void specialCaseRemoval(String prefixes, BOM b){ 
		if (b instanceof WindChill_BOM) {
			if (!prefixes.equals("")) {
				String[] pre = prefixes.split(",");
				for(Node<Part> current : b.getNodes()){
					if (current.item.getID().length() > 2) {
						for (int i = 0; i < pre.length; i++) {
							if (current.item.getID().trim().contains(pre[i])) {
								b.prune(current);
								continue;
							}
						}
					}
				}
			}
		}
	}
	
	public void pickListManip(BOM b){
		if (b instanceof WindChill_BOM) {
			if (!isNum(b.getRoot().item.getID().charAt(0))) {
				String prefix = b.getRoot().item.getID().substring(1, 4);
				String markNum = b.getRoot().item.getMKNum().trim();
				for(Node<Part> current : b.getNodes()){
					if (current.item.getID().substring(0, 3).equals(prefix)) {
						char[] IDchar = current.item.getID().toCharArray();
						char[] tempCharArray = new char[IDchar.length + 3];
						switch (prefix.charAt(0)) {
						case 'A':
							tempCharArray[0] = '1';
							break;
						case 'B':
							tempCharArray[0] = '1';
							break;
						case 'N':
							tempCharArray[0] = '1';
							break;
						case 'G':
							tempCharArray[0] = '4';
							break;
						default:
							tempCharArray[0] = '1';
							break;
						}
						tempCharArray[1] = markNum.toCharArray()[0];
						tempCharArray[2] = markNum.toCharArray()[1];
						for (int j = 0; j < IDchar.length; j++) {
							tempCharArray[3 + j] = IDchar[j];
						}
						current.item.setID(String.valueOf(tempCharArray));
					}
				}
			} else {
				codePopup window = new codePopup();
				window.getFrame().setVisible(true);
				while(window.getFrame().isVisible()){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!window.isCancelled()) {
					pickListManip(b, window.getCode());
				} else return;
				
			}
		}
			
	}
	
	public void pickListManip(BOM b, String code){
		String markNum = b.getRoot().item.getMKNum().trim();
		for(Node<Part> current : b.getNodes()){
			if (current.item.getID().substring(0, 3).equals(code.trim())) {
				char[] IDchar = current.item.getID().toCharArray();
				char[] tempCharArray = new char[IDchar.length + 3];
				tempCharArray[0] = '1';
				tempCharArray[1] = markNum.toCharArray()[0];
				tempCharArray[2] = markNum.toCharArray()[1];
				for (int j = 0; j < IDchar.length; j++) {
					tempCharArray[3 + j] = IDchar[j];
				}
				current.item.setID(String.valueOf(tempCharArray));
			}
		}
	}
	
	public void hoseAssemblyManip(BOM b){
		if (b instanceof WindChill_BOM) {
			for (Node<Part> n : b.getNodes()) {
				CharSequence parentID = n.item.getID();
				for (Node<Part> child : n.adj) {
					if (child.item.getID().trim().contains(parentID)) {
						if (child.item.getMatNum() != null && child.item.getMatQty() != null && child.item.getMatQty() != 0 && !child.item.getMatNum().equals("") && !child.item.getMatNum().equals("N/A")) {
							if (isNUMorAlphaNUM(child.item.getMatNum())) {
								
								child.item.setID(child.item.getMatNum());
								child.item.setQty(child.item.getMatQty());
								child.item.clear();
							}
						} else if (n.item.getMatNum() != null && n.item.getMatQty() != null && !n.item.getMatNum().equals("") && !child.item.getID().contains("MAT") && !child.item.getID().contains("CUST") && !child.item.getID().contains("MAT")/* && !n.item.getMatNum().equals("N/A")*/) {
							b.prune(child);
						}
					}
				}
			}
		}
	}
	
	public void weldNumManip(Double weldratio, BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				double sums = 0.0;
				if (current.item.getWeldNum() != null && !current.item.getWeldNum().equals("")) {
					if (isNUMorAlphaNUM(current.item.getWeldNum())) {
						if (!current.adj.isEmpty()) {
							Queue<Node<Part>> q = new Queue<Node<Part>>();
							for(Node<Part> p : current.adj){
								q.enqueue(p);
							}
							
							while(!q.isEmpty()) {
								Node<Part> child = q.dequeue();
								if (child.item.getWeldNum() != null && !child.item.getWeldNum().equals("")) {
									if (!child.adj.isEmpty()) {
										continue;
									}
								} else {
									for (Node<Part> p : child.adj) {
										q.enqueue(p);
									}
								}
								if (child.item.getMatNum() != null && !child.item.getMatNum().trim().equals("")) {
									String temp = child.item.getMatNum().trim();
									if (temp.charAt(0) == '9' && temp.charAt(1) == '0' && temp.charAt(2) == '0' && child.item.getMatQty() != 0.0) {
										if (child.edgedTo != current) {
											sums = sums + addUp(child, current, true);
										} else sums = sums + child.item.getMatQty() * child.item.getQty();
									}
								}
							}
							
							if (sums != 0.0) {
								Part part = new Part(current.item.getPos()+1, current.item.getWeldNum(), "", "", "", 0.0, "", "", "", Double.parseDouble(format.format(sums*weldratio)));
								Node<Part> n = b.addVertice(part);
								current.adj.add(n);
								n.edgedTo = current;
								b.addToList(n);
							}
						}
					}
				}
			}
		}
	}
	
	public Double addUp(Node<Part> part, Node<Part> parent, boolean first){ 
		if (first) {
			sum = 0.0;
			sum = sum + part.item.getMatQty()*part.item.getQty();
			if (!part.item.getID().trim().equals(parent.item.getID().trim())) {
				addUp(part.edgedTo, parent, false);
			}
		} else {
			if (!part.item.getID().trim().equals(parent.item.getID().trim())) {
				sum = sum*part.item.getQty();
				addUp(part.edgedTo, parent, false);
			}
		}
		return sum;
	}
	
	public void addMaterialChild(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				if (!current.item.getMKNum().equals("4") && !current.item.getMatNum().equals("") && !current.item.getMatNum().equals("N/A") && current.item.getMatQty() != 0) {
					if (isNUMorAlphaNUM(current.item.getMatNum())) {
						
						boolean check = true;
						for(Node<Part> c : current.adj){
							if (c.item.getID().equals(current.item.getMatNum())) check = false; 
						}
						if (check) {
							Part part = new MaterialPart(current.item.getPos()+1, current.item.getMatNum(),  Double.parseDouble(format.format(current.item.getMatQty())));
							Node<Part> n = b.addVertice(part);
							n.edgedTo = current;
							if (!current.adj.add(n, true)) {
								Bag<Node<Part>> newBag = new Bag<Node<Part>>();
								for (Node<Part> p : current.adj) {
									newBag.add(p);
								}
								current.adj = newBag;
								current.adj.add(n);
							}
							/*if (part.getID().equals("900766") && current.item.getID().equals("BN036757")) {
								System.out.println("here " + part.getQty());
								for (Node<Part> node : current.adj) {
									System.out.println(node);
								}
								System.exit(0);
							} */
						}
					}
				}
			}
		}
	}
	
	public void addPaymentChild(BOM b){
		if (b instanceof WindChill_BOM) {
			for(Node<Part> current : b.getNodes()){
				if (!current.item.getMKNum().equals("4") && !current.item.getPaymentNum().equals("") && !current.item.getPaymentNum().equals("N/A")) {
					if (isNUMorAlphaNUM(current.item.getPaymentNum())) {
						boolean check = true;
						for(Node<Part> c : current.adj){
							if (c.item.getID().equals(current.item.getPaymentNum())) check = false; 
						}
						if (check) {
							Part part = new PaymentPart(current.item.getPos()+1, current.item.getPaymentNum(), 1.0);
							Node<Part> n = b.addVertice(part);
							current.adj.add(n);
							n.edgedTo = current;
						}
					}
				}
			}
		}
	}
	
	public void addDuplicates(BOM b){
		if (b instanceof WindChill_BOM) {
			HashTable<String, Part> hashTable;
			for (Node<Part> n : b.getNodes()) {
				if (!n.adj.isEmpty()) {
					hashTable = new HashTable<String, Part>(100);
					for (Node<Part> c : n.adj) {
						if (hashTable.get(c.item.getID()) == null) {
							hashTable.put(c.item.getID(), c.item);
						} else {
							hashTable.get(c.item.getID()).addQty(c.item.getQty());
							b.prune(c);
						}
					}
				}
			}
		}
	}
	
	public boolean isNum(char character){
		try {
			int num = Integer.parseInt(String.valueOf(character));
			for (int i = 0; i < 10; i++) {
				if (num == i) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	public boolean isNUMorAlphaNUM(String string){
		int NumCount = 0;
		int AlphaCount = 0;
		char[] temp = string.toCharArray();
		for (int i = 0; i < temp.length; i++) {
			if (isNum(temp[i])) {
				NumCount++;
			} else if(!isNum(temp[i])){
				AlphaCount++;
			}
		}
		if (temp.length == 6 && NumCount == 6 && AlphaCount == 0) {
			return true;
		} else if (temp.length == 8 && NumCount + AlphaCount == 8){
			return true;
		} else return false;
	}
	
	public void setPickCode(String s){
		pickListCode = s;
	}
	
	public Bag<Node<Part>> getMatched(){
		return matched;
	}
	
	public Bag<Node<Part>> getDeleted(){
		return deleted;
	} 
	
	public Bag<Node<Part>> getAdded(){
		return added;
	}
	
	public Bag<Node<Part>> getQtyChanged(){
		return qtyChanged;
	}
	
	public boolean pathMatch(Node<Part> c, Node<Part> n){
		Stack<Node<Part>> nPath = (Stack<Node<Part>>) B2_dfp.pathTo(n);
		Stack<Node<Part>> cPath = (Stack<Node<Part>>) B1_dfp.pathTo(c);
		if (cPath.size() == nPath.size()) {
			while(!nPath.isEmpty()){
				
				if(!(nPath.pop().item.equals(cPath.pop().item))){
					return false;
				}
			}
			return true;
		}  
		return false;
	}
	
	/*public void showChanges(){
		if (!added.isEmpty()) {
			writer.write("ADDED PARTS:\n");
			for (Node<Part> node : added) {
				writer.write("" + node.item + "\n");
			}
		} else writer.write("NO PARTS WERE ADDED\n");
		if (!deleted.isEmpty()) {
			writer.write("REMOVED PARTS:\n");
			for (Node<Part> node : deleted) {
				writer.write("" + node.item + "\n");
			}
		} else writer.write("NO PARTS WERE REMOVED\n");
		
	}*/

}
