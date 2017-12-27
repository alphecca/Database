package dbms;

import java.util.ArrayList;

public class refConstraint {
	//when parsing, get reference constraints easily
	ArrayList<String> colList = new ArrayList<String>();
	String refTbn;
	ArrayList<String> refColList = new ArrayList<String>();
	
	public refConstraint() { ;}
	public refConstraint(ArrayList<String> colList, String refTbn, ArrayList<String> refColList) {
		this.colList = colList;
		this.refTbn = refTbn;
		this.refColList = refColList;
	}
}
