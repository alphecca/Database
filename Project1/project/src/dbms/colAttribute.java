package dbms;

public class colAttribute {
	String colName;
	String dataType;
	boolean nullable;
	boolean isPk;
	boolean isFk;
	String refTable;
	String refColumn;
	
	public colAttribute(String colName, String dataType, boolean nullable) {
		this.colName = colName;
		this.dataType = dataType;
		this.nullable = nullable;
		isPk = isFk = false;
	}
	public colAttribute(String colName, String dataType, boolean nullable, boolean isPk, boolean isFk, String refTable, String refColumn) {
		this.colName = colName;
		this.dataType = dataType;
		this.nullable = nullable;
		this.isPk = isPk;
		this.isFk = isFk;
		this.refColumn = refColumn;
		this.refTable = refTable;
	}
	public void setPrimaryKey(boolean isPk) {
		this.isPk = isPk;
	}
	public void setForeignKey(String refTable, String refColumn) {
		this.isFk = true;
		this.refTable = refTable;
		this.refColumn = refColumn;
	}
}

