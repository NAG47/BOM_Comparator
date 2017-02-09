package dataStructures;

import java.io.IOException;
import core.BOM.*;
import utility.Writer;

public class test {
	
	public static void main(String[] args) throws IOException {
		Writer writer = new Writer("log");
		BOM b = new WindChill_BOM("C:/Users/Nuan/Desktop/BOM_Comparator/Input/BOMs/WindChill BOM/A531E20.csv", writer);
		//Manipulator m = new Manipulator(b, writer);
		//m.doAllManipulations();
		b.show();
		
		writer.close();
	}

}