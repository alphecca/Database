package dbms;

public class fkConstraint {
	String colName;
	String refTable;
	String refColumn;
	public fkConstraint(String colName, String refTable, String refColumn) {
		this.colName = colName;
		this.refTable = refTable;
		this.refColumn = refColumn;
	}
}
