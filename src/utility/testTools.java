package utility;

import java.io.IOException;

import core.BOM.*;
import core.Manipulator;

public class testTools {
	
	public static  Manipulator manip;
	public static BOM b, d;
	
	public static void main(String[] args) throws IOException {
		Writer writer = new Writer("log");
		b = new BOM("C:/Users/Nuan/Desktop/BOM_Comparator/Input/BOMs/WindChill BOM/A531E20.csv", writer);
		d = new BOM("C:/Users/Nuan/Desktop/BOM_Comparator/Output/Manipulated.csv", writer);
		writer.getBOMs(b, d);
		manip = new Manipulator(b, d, writer);
		manip.compare();
		writer.writeMass(manip.getDeleted(), manip.getAdded(), manip.getQtyChanged());
	}

}
