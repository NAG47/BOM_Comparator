package core.Part;

import java.text.DecimalFormat;


public class Part implements Comparable<Object>{
	
	private String id;
	private String name;
	private int pos;
	private String MKNum;
	private String matNum;
	private Double matQty;
	private String paymentNum;
	private Double qty;
	public Double correctQty = 0.0; //FOr testing
	private String view;
	private String weldNum;
	private boolean matched = false;
	private DecimalFormat format = new DecimalFormat("##.000");
	private String machineType;
	private Double scrapFactorPercentage;
	
	public Part(int pos, String id, String name, String MKNum, String matNum, Double matQty, String view, String weldNum, String paymentNum, Double qty){
		this.pos = pos;
		this.id = id;
		this.name = name;
		this.MKNum = MKNum;
		this.matNum = matNum;
		this.matQty = matQty;
		this.view = view;
		this.weldNum = weldNum;
		this.paymentNum = paymentNum;
		this.qty = qty;
	}
	
	//IM Part
	public Part(String id, String name, String MKNum){
		this.id = id;
		this.name = name;
		this.MKNum = MKNum;
	}
	
	//SF part
	public Part(String id, String machineType, Double scrapFactorPercentage){
		this.id = id;
		this.machineType = machineType;
		this.scrapFactorPercentage = scrapFactorPercentage;
	}
	
	public boolean getMatched(){
		return matched;
	}
	
	public String getMachineType(){
		return machineType;
	}
	
	public Double getScrapFactor(){
		return scrapFactorPercentage;
	}
	
	public void setMatched(boolean m){
		matched = m;
	}
	
	public void setQty(Double qty){
		this.qty = qty;
	}
	
	public String getWeldNum(){
		return weldNum;
	}
	
	public void setMKNum(String num){
		MKNum = num;
	}
	
	public String getMKNum(){
		return MKNum;
	}
	
	public String getMatNum(){
		return matNum;
	}
	
	public String getPaymentNum(){
		return paymentNum;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		this.name = n;
	}
	
	public Double getQty(){
		return qty;
	}
	
	public void addQty(double q){
		qty = qty + q;
	}
	
	public Double getMatQty(){
		return matQty;
	}
	
	public void setID(String ID){
		id = ID;
	}
	
	public String getID(){
		return id;
	}
	
	public void setPos(int num){
		this.pos = num;
	}
	
	public int getPos(){
		return pos;
	}
	
	public String getView(){
		return view;
	}
	
	public void clear(){
		this.MKNum = "";
		this.matNum = "";
		this.matQty = 0.0;
		this.paymentNum = "";
		this.view = "";
		this.weldNum = "";
	}
	
	public Double formatDouble(Double d){
		d = Math.ceil(d * 1000) / 1000;
		Double db = Double.parseDouble(format.format(d));
		return db;
	}
	
	public boolean compareQty(Part part){
		try {
			double change = formatDouble(part.getQty()) - formatDouble(this.getQty());
			if (Math.abs(change) != 0) { 
				part.correctQty = this.getQty();
				this.correctQty = part.getQty();
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println(this + " - " + part.getQty() + ", " + this.getQty());
		}
		return false;
	}
	
	public boolean equals(Object obj){
		//System.out.println(this + " =? " + p);
		Part p = (Part) obj;
		if (this.pos == p.getPos()) {
			if(this.id.equals(p.getID())){
				return true;
			}
			return false;
		}
		return false;
	}
	
	public String toString(){
		return id; //DO NOT CHANGE THIS
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof Part) {
			Part part = (Part) arg0;
			if (this.getID().compareTo(part.getID()) < 0) {
				return -1;
			}
			else return 1;
		}
		return 0;
	}

}
