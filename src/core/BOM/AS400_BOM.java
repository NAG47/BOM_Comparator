package core.BOM;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import core.Part.*;
import dataStructures.Queue;
import utility.Writer;

public class AS400_BOM extends BOM {

	public AS400_BOM(String file, Writer writer) throws IOException {
		super(writer);
		CSVReader reader = new CSVReader(new FileReader(file));
		Queue<Part> queue = new Queue<Part>();
	    String [] nextLine;
	    nextLine = reader.readNext();
	    
	 	try {
		  Part firstPart = new Part(0, nextLine[0], "", "", "", 0.0, "", "", "", 0.0);
		    queue.enqueue(firstPart);
		    
			Double qty;
			if (nextLine[17].equals("")) {
				qty = 0.0;
			} else {
				qty = Double.parseDouble(String.valueOf(nextLine[17].trim()));
			}
			Part part = new Part(Integer.parseInt(nextLine[4]), nextLine[1].trim(), nextLine[2], "", "", 0.0, "", "", "", qty);
			queue.enqueue(part);
	} catch (Exception e) {
		System.out.println("Something went wrong while reading a BOM, If you are trying to compare 2 WindChill BOM's, remember to set \nthe app to the correct mode!"); reader.close(); return;
	}
	    
		int count = 1;
	    Double qty;
	    try {
		   while ((nextLine = reader.readNext()) != null) {
			   count++;
				if (nextLine[17].equals("")) {
					qty = 0.0;
				} else {
					qty = Double.parseDouble(String.valueOf(nextLine[17].trim()));
				}
				Part p = new Part(Integer.parseInt(nextLine[4]), nextLine[1].trim(), nextLine[2], "", "", 0.0, "", "", "", qty);
				queue.enqueue(p);
				
		     }
	} catch (Exception e) {
		System.out.println("Something went wrong while reading a BOM, line: " + count);reader.close(); return;
	}
			this.arrange(queue);
			reader.close();
	}
	
	/*
	 * For testing
	 *
	public AS400_BOM(String file, Writer writer) throws IOException {
		super(file, writer);
	} */

}
