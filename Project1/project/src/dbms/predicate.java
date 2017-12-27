package dbms;

public class predicate {
	
	refTabCol compOp1;
	refTabCol compOp2;
	String operator;//is null, <=, >=, =, <, >, !=
	
	public predicate() {
		compOp1 = new refTabCol();
		compOp2 = new refTabCol();
		operator = new String();
	}
	public boolean isEmpty() {
		return ((operator.isEmpty()) && (compOp1.isEmpty()) && (compOp2.isEmpty()) );
	}
	public predicate (String operator) {
		this.operator = operator;
	}
	public predicate (refTabCol compOp1, String operator, refTabCol compOp2) {
		this.compOp1 = compOp1;
		this.compOp2 = compOp2;
		this.operator = operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public void setOperand(refTabCol compOp1, refTabCol compOp2) {
		this.compOp1 = compOp1;
		this.compOp2 = compOp2;
	}
}
