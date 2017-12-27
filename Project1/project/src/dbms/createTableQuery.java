package dbms;


import java.util.ArrayList;

public class createTableQuery {
	String tableName;
	ArrayList<colAttribute > columnFamily = new ArrayList<colAttribute >();
	ArrayList<ArrayList<String> > pkConstraints = new ArrayList<ArrayList<String >>();
	ArrayList<refConstraint> refConstraints = new ArrayList<refConstraint>();
	ArrayList<colAttribute> columnList = new ArrayList<colAttribute>();//temporary list - only used for parsing container 
	
	public void setTableName(String tbn) {
		tableName= tbn;
	}
	public void run() throws dbException {
		
		//pre-process to make column attribute lists 'columnFamily'
		addColumnFamily();
		updatePkConstraint();
		updateFkConstraint();
		
		//check if duplicate table name
		Table tb = DBMSController.load(tableName);
		if(tb != null) {
			throw new dbException(DBMSMessage.TableExistenceError);
		}
		
		boolean result = DBMSController.create(new Table(tableName, columnFamily, null));
		if(result) System.out.printf(DBMSMessage.CreateTableSuccess+"\n",tableName);
		
	}
		
	//get [column list/pk constraints list/reference constraints list] during parsing
	//By not checking column constraints during parsing, parse process could be more neat
	public void addColumnList(colAttribute colAtt) throws dbException{
		columnList.add(colAtt);
	}
	public void addPkConstraint(ArrayList<String> pkC) throws dbException {
		pkConstraints.add(pkC);
	}
	public void addRefConstraint(refConstraint refC) throws dbException{
		refConstraints.add(refC);
	}

	private void addColumnFamily() throws dbException {
		for(colAttribute colAtt: columnList) {
			//Duplicate column Name => error
			for(colAttribute colMem : columnFamily) {
				if(colMem.colName.equals(colAtt.colName))
					throw new dbException(DBMSMessage.DuplicateColumnDefError);
			}
			
			if(colAtt.dataType.length()>5 && Integer.valueOf(colAtt.dataType.substring(5, colAtt.dataType.length()-1))<1) {
				throw new dbException(DBMSMessage.CharLengthError);
			}else
				
			columnFamily.add(colAtt);
		}
	}	
	private void updatePkConstraint() throws dbException{
		ArrayList<String > result = new ArrayList<String>();
		
		if(pkConstraints.size()>1)
			throw new dbException(DBMSMessage.DuplicatePrimaryKeyDefError);
		
		for(ArrayList<String> pkC : pkConstraints) {
			for(String pkCol : pkC) {
				boolean flag = false;
				for(colAttribute colName : columnFamily) {
					if(colName.colName.equals(pkCol)) {
						flag=true;
						break;
					}
				}
				if(!flag)//no such column
					throw new dbException(DBMSMessage.NonExistingColumnDefError,pkCol);//"[PK] no such column");
				else result.add(pkCol);
			}
		}
		
		for(String value : result) {
			boolean flag = false;
			for(int i=0;i<columnFamily.size();i++) {
				colAttribute myCol = columnFamily.get(i);
				if(myCol.colName.equals(value)) {
					flag = true;
					if(myCol.isPk) throw new dbException(DBMSMessage.DuplicateColumnDefError);
					myCol.isPk = true;
					if(myCol.nullable) myCol.nullable = false;
				}
			}
			if(!flag) throw new dbException(DBMSMessage.NonExistingColumnDefError,value);
		}
	}
	
	private void updateFkConstraint() throws dbException{

		ArrayList<fkConstraint> result = new ArrayList<fkConstraint>();
		
		for(refConstraint refC : refConstraints) {
			
			if(refC.colList.size() != refC.refColList.size())
				throw new dbException(DBMSMessage.ReferenceTypeError);
			for(int i=0;i<refC.colList.size();i++) {
				String myCol = refC.colList.get(i);
				boolean flag = false;
				for(colAttribute tmp : columnFamily) {
					if(tmp.colName.equals(myCol)) flag=true;
				}
				if(!flag) throw new dbException(DBMSMessage.NonExistingColumnDefError,myCol);
				for(fkConstraint tmp : result) {
					if(tmp.colName.equals(myCol)) flag= false;
				}
				if(!flag) throw new dbException(DBMSMessage.MultipleReference);
				result.add(new fkConstraint(myCol, refC.refTbn, refC.refColList.get(i)));
			}
		}
		
		for(fkConstraint value : result) {
			boolean flag= false;
			for(int i=0;i<columnFamily.size();i++) {
				if(columnFamily.get(i).colName.equals(value.colName)) {
					flag = true;
					columnFamily.get(i).isFk = true;
					break;
				}
			}
			if(!flag) throw new dbException(DBMSMessage.NonExistingColumnDefError,value.colName);
		}
		for(fkConstraint value: result) {
			for(int i=0;i<columnFamily.size();i++) {
				colAttribute myCol = columnFamily.get(i);
				if(myCol.colName.equals(value.colName)) {
					myCol.isFk = true;
					myCol.refColumn = value.refColumn;
					if(value.refTable.equals(this.tableName))
						throw new dbException(DBMSMessage.ReferenceColumnItself);
					myCol.refTable = value.refTable;
				}
			}
		}
	}
}
