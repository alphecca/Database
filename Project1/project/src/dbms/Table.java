package dbms;

import java.util.ArrayList;

public class Table {
	
	String tableName;
	ArrayList<colAttribute > columnFamily = new ArrayList<colAttribute >();
	ArrayList<tupleElement> tupleFamily = new ArrayList<tupleElement> ();
	
	public Table() {}
	public Table(String tableName, ArrayList<colAttribute> columnFamily) {
		this.tableName = tableName;
		this.columnFamily = columnFamily;
	}
	public Table(String tableName, ArrayList<colAttribute> columnFamily, ArrayList<tupleElement> tupleFamily) {
		this.tableName = tableName;
		this.columnFamily = columnFamily;
		this.tupleFamily = tupleFamily;
	}
	public colAttribute getColumn(String colN) {
		for(int i=0;i<columnFamily.size();i++) {
			if(columnFamily.get(i).colName.equals(colN)) return columnFamily.get(i);
		}
		return null;
	}
}
