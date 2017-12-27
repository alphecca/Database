package dbms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExpTree {
	predicate node;
	ArrayList<ExpTree> child;
	
	public ExpTree(){
		this.node = new predicate();
		this.child = new ArrayList<ExpTree>();
		
	}
	public ExpTree(predicate node) {
		this.node = node;
		child = new ArrayList<ExpTree>();
	}
	public boolean isEmpty() {
		return (node.isEmpty());
	}
	public void setOperator(char operator) {
		String tmp = new String();
		tmp+=operator;
		this.node.setOperator(tmp);
	}
	public void setOperator(String operator) {
		this.node.setOperator(operator);
	}
	public void setOperand(ArrayList<predicate> operand) {
		for(int i=0;i<operand.size();i++) {
			child.add(new ExpTree(operand.get(i)));
		}
	}
	public void setNode(predicate node) {
		this.node = node;
	}
	public void setChild(ArrayList<ExpTree> tree) {
		this.child = tree;
	}
	public int calculate(Map<String, String> nvMap, Map<String, String> ntMap, Set<String> colSet) throws dbException{
		//input: colName-Value, colName-colType, [tbn].[col]
		//output: true(1) false(0) unknown(2)
		String myop = this.node.operator;

		if(myop.equals("and")) {
			int result = 1;
			for(int i=0;i<this.child.size();i++) {
				int tmp = result * child.get(i).calculate(nvMap, ntMap, colSet);
				if(tmp == 4) result = 2;
				else result = tmp;
			}
			return result;
		}else if(myop.equals("or")) {
			int result = 0;
			for(int i=0;i<this.child.size();i++) {
				int tmp = child.get(i).calculate(nvMap, ntMap, colSet);
				if(tmp == result) result = tmp;
				else if((tmp+result)%2==0) result = 2;
				else result = 1;				
			}
			return result;
		}else if(myop.equals("not")) {
			int tmp = 0;
			if(this.child.size()==1)
				tmp =  child.get(0).calculate(nvMap, ntMap, colSet);
			int result = (tmp==1)? 0 : 1;
			return result;			
		}else if(myop.equals("is not null") || myop.equals("is null")) { //null predicate
			refTabCol myval = this.node.compOp1;
			
			if(myval.isRef) { // operand is column name
				if(myval.refTbn.isEmpty()) {//doesn't have tbn => interfere
			
					for(Iterator<String> itr = colSet.iterator();itr.hasNext();) {
						String candidate = itr.next();
						String[] candidate_tbn = candidate.split("\\.");
						if(candidate_tbn[1].equals(myval.refCol))
							if(!myval.refTbn.isEmpty())
								throw new dbException(DBMSMessage.WhereAmbiguousReference);
							else myval.refTbn = candidate_tbn[0];
					}
				}
			
				int result = 0;
				String comp1 = myval.refTbn+"."+myval.refCol;
				if(nvMap.get(comp1).equals("null"))
					if(myop.equals("is null")) result = 1;
					else result = 0;
				else
					if(myop.equals("is null")) result = 0;
					else result = 1;
				return result;
			}else { // operand is value
				int result = 0;
				if(myval.refCol.equals("null"))
					if(node.operator.equals("is null")) result = 1;
					else result = 0;
				return result;				
			}
			
		}else { // comparison predicate
			
			refTabCol myval1 = this.node.compOp1;
			refTabCol myval2 = this.node.compOp2;
			String comp1, comp2;
			String type1, type2;
			
			if(myval1.isRef) { // operand1 is column name
				if(myval1.refTbn.isEmpty()) {//doesn't have tbn => interfere
					for(Iterator<String> itr = colSet.iterator();itr.hasNext();) {
						String candidate = itr.next();
						String[] candidate_tbn = candidate.split("\\.");
						if(candidate_tbn[1].equals(myval1.refCol))
							if(!myval1.refTbn.isEmpty())
								throw new dbException(DBMSMessage.WhereAmbiguousReference);
							else myval1.refTbn = candidate_tbn[0];
					}
				}
				
				if(!nvMap.containsKey(myval1.refTbn+"."+myval1.refCol))
					throw new dbException(DBMSMessage.WhereColumnNotExist);
				
				comp1 = nvMap.get(myval1.refTbn+"."+myval1.refCol);
				type1 = ntMap.get(myval1.refTbn+"."+myval1.refCol);
				if(type1.charAt(0)=='c') type1 = "char";
			}else { //operand1 is value
				comp1 = myval1.refCol;
				type1 = typeChecker(myval1.refCol);
				if(type1.equals("char"))
					comp1 = myval1.refCol.substring(1, myval1.refCol.length()-1);
			}
			if(myval2.isRef) { //operand2 is column name
				if(myval2.refTbn.isEmpty()) {//doesn't have tbn => interfere
					for(Iterator<String> itr = colSet.iterator();itr.hasNext();) {
						String candidate = itr.next();
						String[] candidate_tbn = candidate.split("\\.");
						if(candidate_tbn[1].equals(myval2.refCol))
							if(!myval2.refTbn.isEmpty())
								throw new dbException(DBMSMessage.WhereAmbiguousReference);
							else myval2.refTbn = candidate_tbn[0];
					}
				}
				if(!nvMap.containsKey(myval2.refTbn+"."+myval2.refCol))
					throw new dbException(DBMSMessage.WhereColumnNotExist);
				
				comp2 = nvMap.get(myval2.refTbn+"."+myval2.refCol);
				type2 = ntMap.get(myval2.refTbn+"."+myval2.refCol);
				if(type2.charAt(0)=='c') type2 = "char";
			}else {
				comp2 = myval2.refCol;
				type2 = typeChecker(myval2.refCol);
				if(type2.equals("char"))
					comp2 = myval2.refCol.substring(1, myval2.refCol.length()-1);
			}
			
			
			if(!type1.equals(type2))
				throw new dbException(DBMSMessage.WhereIncomparableError);
			int result = 0;
			if(type1.equals("char")) {
				result= charCompare(comp1, comp2, myop);
			}
			else if(type2.equals("int"))
				result = intCompare(comp1, comp2, myop);
			else if(type2.equals("date")) {
				result = dateCompare(comp1, comp2, myop);
			}
			
			return result;
		}
	}
	
	private String typeChecker(String value) {
		
		if(value.length()>0) {
			if(value.startsWith("'")) return "char";
			else {
				if(value.length()==10 && value.charAt(4)=='-') return "date";
				else return "int";
			}
		}
		return null;
	}
	private int charCompare(String comp1, String comp2, String myop) {
		int result = 0;
		if(comp1.equals("null") || comp2.equals("null")) {
			result = 2; //unknown
		}else {
		if(myop.equals("<")) result = (comp1.compareTo(comp2)<0)? 1:0;
		else if(myop.equals(">")) result = (comp1.compareTo(comp2)>0)? 1:0;
		else if(myop.equals("=")) result = (comp1.compareTo(comp2)==0)? 1:0;
		else if(myop.equals("<=")) result = (comp1.compareTo(comp2)>0)? 0:1;
		else if(myop.equals(">=")) result = (comp1.compareTo(comp2)<0)? 0:1;
		else if(myop.equals("!="))  result = (comp1.compareTo(comp2)==0)? 0:1;
		else result=2;
		}
		return result;
	}
	private int intCompare(String comp1, String comp2, String myop) {
		int result = 0;
		if(comp1.equals("null") || comp2.equals("null")) {
			
			result = 2; //unknown
		}else {

		int op1, op2;
		op1 = op2 = 0;
		op1 = Integer.valueOf(comp1);
		op2 = Integer.valueOf(comp2);

		if(myop.equals("<")) result =(op1<op2)? 1 : 0;
		else if(myop.equals(">")) result = (op1>op2)? 1:0;
		else if(myop.equals("=")) result = (op1==op2)? 1:0;
		else if(myop.equals("<=")) result = (op1<=op2)? 1:0;
		else if(myop.equals(">=")) result = (op1>=op2)? 1:0;
		else if(myop.equals("!=")) result = (op1!=op2)? 1:0;
		else result=2;
		}
		return result;
	}
	private int dateCompare(String comp1, String comp2, String myop){
		int result = 0;
		if(comp1.equals("null") || comp2.equals("null")) {
			result = 2; //unknown
			return result;
		}
		
		try {
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = transFormat.parse(comp1);
			Date date2 = transFormat.parse(comp2);
			if(myop.equals("<")) result = (date1.before(date2))? 1:0;
			else if(myop.equals(">")) result = (date1.after(date2))? 1:0;
			else if(myop.equals("=")) result = (date1.equals(date2))? 1:0;
			else if(myop.equals("<=")) result = (date1.after(date2))? 0:1;
			else if(myop.equals(">=")) result = (date1.before(date2))? 0:1;
			else if(myop.equals("!=")) result = (date1.equals(date2))? 0:1;
			else result=2;
		}catch(Exception e) {
			;
		}
		return result;
	}
}
