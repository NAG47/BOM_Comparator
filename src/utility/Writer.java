package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import core.BOM.*;
import core.Part.Part;
import dataStructures.Bag;
import dataStructures.DFP;
import dataStructures.Node;

public class Writer {
	
	private File file;
	private FileWriter fw;
	public PrintWriter pw;
	private BOM B1, B2;
	
	public Writer(String f) throws IOException{
		file = new File(f);
		fw = new FileWriter(file);
		pw = new PrintWriter(fw);
	}
	
	public void getBOMs(BOM B1, BOM B2){
		this.B1 = B1;
		this.B2 = B2;
	}
	
/*	public void write(String txt){
		pw.write(txt);
		pw.write("\n");
	}*/
	
	public void close(){
		pw.flush();
		pw.close();
	}
	
	public void writeChangeList(Bag<Node<Part>> matched, Bag<Node<Part>> deleted, Bag<Node<Part>> added) throws IOException{
		matched = matched.removeDuplicates();
		deleted = deleted.removeDuplicates();
		added = added.removeDuplicates();
		String user = System.getProperty("user.home");
		File file = new File(user + "/Desktop/BOM_Comparator/Output/Step.csv");
		FileWriter fWriter = new FileWriter(file);
		PrintWriter writer = new PrintWriter(fWriter);
		writer.write("Indent Level," + "Number," + "Name," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + "Item type num," + "Vendor number," + "Safety critical," + "Material num," + "Quantity," + "Location," +
		",,," + "Qty," + "Revision," + "Revision iteration," + "Indent Level," + "Number," + "Name," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + "Item type num," + "Vendor number," + "Safety critical," + "Material num," + "Quantity," + "Location," +
		",,," + "Qty," + "Revision," + "Revision iteration");
		for (Node<Part> current : matched) {
			writer.println();
			writer.write(current.item.getPos() + "," + current.item.getID() + "," + formatForCSV(current.item.getName()) + "," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + current.item.getMKNum() + "," + "Vendor number," + "Safety critical," + current.item.getMatNum() + "," + current.item.getMatQty() + "," + "Location," +
					",,," + current.item.getQty() + "," + "Revision," + "Revision iteration," + current.item.getPos() + "," + current.item.getID() + "," + formatForCSV(current.item.getName()) + "," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + current.item.getMKNum() + "," + "Vendor number," + "Safety critical," + current.item.getMatNum() + "," + current.item.getMatQty() + "," + "Location," +
					",,," + current.item.getQty() + "," + "Revision," + "Revision iteration,");
		}
		for (Node<Part> current : added) {
			writer.println();
			writer.write(",,,,,,,,,,,,,,,,,,,," + current.item.getPos() + "," + current.item.getID() + "," + formatForCSV(current.item.getName()) + "," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + current.item.getMKNum() + "," + "Vendor number," + "Safety critical," + current.item.getMatNum() + "," + current.item.getMatQty() + "," + "Location," +
					",,," + current.item.getQty() + "," + "Revision," + "Revision iteration,");
		}
		for (Node<Part> current : deleted) {
			writer.println();
			writer.write(current.item.getPos() + "," + current.item.getID() + "," + formatForCSV(current.item.getName()) + "," + "Level 2 Num," + "Level 2 Name," + "Functional Group Code," + "Last Modified," + "State," + current.item.getMKNum() + "," + "Vendor number," + "Safety critical," + current.item.getMatNum() + "," + current.item.getMatQty() + "," + "Location," +
					",,," + current.item.getQty() + "," + "Revision," + "Revision iteration,");
		}
		writer.flush();
		writer.close();
	}
	
	public void writeBOM(BOM b) throws IOException{
		DFP dfp = new DFP(b, b.getRoot());
		String user = System.getProperty("user.home");
		File file = new File(user + "/Desktop/BOM_Comparator/Output/Manipulated.csv");
		FileWriter fWriter = new FileWriter(file);
		PrintWriter writer = new PrintWriter(fWriter);
		for(Node<Part> d : dfp.getNodes()){
			writer.println();
			writer.write(d.item.getPos() + "," + d.item.getID() + ",,,,,,," + d.item.getMKNum() + ",,," + d.item.getMatNum() + "," + d.item.getMatQty() + ",,,,," + d.item.getQty() + ",,");
		}
		writer.close();
	}
	
	public Double formatForCSV(Double d){
		return Math.ceil(d * 1000)/1000;
	}
	
	public String formatForCSV(String name){
		if (name.contains(",")) {
			char[] oldString = name.toCharArray();
			char[] newString = new char[oldString.length + 2];
			newString[0] = '"';
			for (int i = 1; i < oldString.length+1; i++) {
				newString[i] = oldString[i-1];
			}
			newString[newString.length-1] = '"';
			return String.valueOf(newString);
		}
		return name;
	}
	
	public File writeMass(Bag<Node<Part>> deleted, Bag<Node<Part>> added, Bag<Node<Part>> qtyChanged) throws IOException{
		deleted = deleted.removeDuplicates();
		added = added.removeDuplicates();
		qtyChanged = qtyChanged.removeDuplicates();
		String user = System.getProperty("user.home");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd_h-mm-ss_a");
		String name;
		if (B1 != null) {
			name = B1.getRoot().item.getID() + "-" + B2.getRoot().item.getID() + "_" + sdf.format(date);
		} else name =  B2.getRoot().item.getID() + "_" + sdf.format(date);
		File file = new File(user + "/Desktop/BOM_Comparator/Output/Upload/" + name + ".csv");
		FileWriter fWriter = new FileWriter(file);
		PrintWriter writer = new PrintWriter(fWriter);
		try {
			writer.write("TRID11,RSPF11,PITM11,CITM11,QPER11,EDTF11,EDTT11,FOCD11,FOCF11,FOPF11,FONO11,USSQ11,Action,Level 1 Parent,Parent Name,Part Name,Level 1 Name,Item Type,Feature Flag, AS400 QTY");
			for(Node<Part> current : added){
				if (current.item.getPos() != 0) {
					String partName = formatForCSV(current.item.getName());
					String parentName = formatForCSV(current.edgedTo.item.getName());
					String lvl1Name = formatForCSV(B2.getMainParent(current).item.getName());
					writer.println();
					writer.write("E0PS0600," + current.item.getPos() + "," + current.edgedTo.item.getID() + "," + current.item.getID() + "," + formatForCSV(current.item.getQty()) + ",####" + ",,,,,,,ADD," + B2.getMainParent(current).item.getID() + "," + parentName + "," + partName + "," + lvl1Name + "," + current.item.getMKNum()); 
				} else {
					writer.println();
					writer.write("E0PS0600," + current.item.getPos() + ",," + current.item.getID() + "," + formatForCSV(current.item.getQty()) + ",####" + ",,,,,,,ADD,,," + formatForCSV(current.item.getName()) + ",," + current.item.getMKNum()); 
				}
			}
			for(Node<Part> current : deleted){
				String partName = formatForCSV(current.item.getName());
				String parentName = formatForCSV(current.edgedTo.item.getName());
				String lvl1Name = formatForCSV(B1.getMainParent(current).item.getName());
				writer.println();
				writer.write("E0PS0500," + current.item.getPos() + "," + current.edgedTo.item.getID() + "," + current.item.getID() + "," + formatForCSV(current.item.getQty()) + ",####" + ",,,,,,,DELETE," + B1.getMainParent(current).item.getID() + "," + parentName + "," + partName + "," + lvl1Name + "," + current.item.getMKNum());
			}
			for(Node<Part> current : qtyChanged){
				String partName = formatForCSV(current.item.getName());
				String parentName = formatForCSV(current.edgedTo.item.getName());
				String lvl1Name = formatForCSV(B1.getMainParent(current).item.getName());
				writer.println();
				writer.write("E0PS0700," + current.item.getPos() + "," + current.edgedTo.item.getID() + "," + current.item.getID() + "," + formatForCSV(current.item.getQty()) + ",####" + ",,,,,,,QTY," + B1.getMainParent(current).item.getID() + "," + parentName + "," + partName + "," + lvl1Name + "," + current.item.getMKNum() + ",," + formatForCSV(current.item.correctQty));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
		return file;
	}

}
