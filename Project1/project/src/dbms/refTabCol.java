package dbms;

public class refTabCol {
	String refTbn;
	String refCol;
	String alias;
	boolean isRef;//if Y, ref. tb&col. If N, just a value
	
	
	public refTabCol() {
		isRef = true;
		refTbn = new String();
		refCol = new String();
		alias = new String();
	}
	public refTabCol(String refTbn, String refCol) {
		this.isRef = true;
		this.refTbn = refTbn;
		this.refCol = refCol;
	}
	public refTabCol(String refTbn, String refCol, String alias) {
		this.isRef = true;
		this.refTbn = refTbn;
		this.refCol = refCol;
		this.alias = alias;
	}
	public void setTC(String refTbn, String refCol) {
		isRef = true;
		this.refTbn = refTbn;
		this.refCol = refCol;
	}
	public void setTA(String refTbn, String alias) {
		this.refTbn = refTbn;
		this.alias = alias;
	}
	public void setCA(String refCol, String alias) {
		this.refCol = refCol;
		this.alias = alias;
	}
	public void setTCA(String refTbn, String refCol, String alias) {
		isRef = true;
		this.refTbn = refTbn;
		this.refCol = refCol;
		this.alias = alias;
	}
	public void setT(String refTbn) {
		this.isRef = true;
		this.refTbn = refTbn;
	}
	public void setC(String refCol) {
		this.refCol = refCol;
	}
	public void setA(String alias) {
		this.alias = alias;
	}
	
	public void setIsRef(boolean isRef) {
		this.isRef = isRef;
	}
	public boolean isEmpty() {
		if(isRef)
			return (refTbn.isEmpty() || refCol.isEmpty() || alias.isEmpty());
		else return (refCol.isEmpty());		
	}
}
