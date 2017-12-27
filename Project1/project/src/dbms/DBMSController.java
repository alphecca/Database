package dbms;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationFailureException;
import com.sleepycat.je.OperationStatus;

public class DBMSController {
	static Cursor cursor;
	static Environment myDbEnvironment;
	static Database myDatabase;
	
	private static void open() {
		myDbEnvironment = null;
		myDatabase = null;
		
		//Open DB environment or if not, create one
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		myDbEnvironment = new Environment(new File("db/"), envConfig);
		
		//Open DB or if not, create one
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(false);
		myDatabase = myDbEnvironment.openDatabase(null, "sampleDatabase", dbConfig);
		
	}
	
	private static void close() {
		
		if(myDatabase != null) myDatabase.close();
		if(myDbEnvironment != null) myDbEnvironment.close();
		
	}
	private static void fk_create(Table tb) throws dbException{
		// FK Exception handling when create
		// (1) ReferenceTableExistenceError & NonExistingColumnDefError
		// (2) ReferenceNonPrimaryKeyError
		// (3) ReferenceTypeError : type check 

		ArrayList<colAttribute> myColFam = tb.columnFamily;
		for(Iterator<colAttribute> itr=myColFam.iterator(); itr.hasNext();) {
			colAttribute myCol = itr.next();
			if(myCol.isFk) {
				String refTabN = myCol.refTable;
				String refColN = myCol.refColumn;
				Table refTab = load(refTabN);
				if(refTab == null) {
					throw new dbException(DBMSMessage.ReferenceTableExistenceError); 
				}
				colAttribute refCol = refTab.getColumn(refColN);
				if(refCol == null) {
					throw new dbException(DBMSMessage.NonExistingColumnDefError, refColN);
				}
				if(!refCol.isPk) {
					throw new dbException(DBMSMessage.ReferenceNonPrimaryKeyError); 
				}
				if(!refCol.dataType.equals(myCol.dataType)) {
					throw new dbException(DBMSMessage.ReferenceTypeError);
				}
			}
		}	
	}
	
	public static boolean create(Table tb) throws dbException {
		
		fk_create(tb);//check appropriate FK
		try {	//input DB
			
			open();
			cursor = myDatabase.openCursor(null, null);

			cursor.getFirst(new DatabaseEntry(), new DatabaseEntry(), LockMode.DEFAULT);
			
			DatabaseEntry key = new DatabaseEntry(tb.tableName.getBytes("UTF-8"));
			String valueString = AttToString(tb.columnFamily);
			
			valueString += ValToString(tb.tupleFamily);
			
			
			
			DatabaseEntry data = new DatabaseEntry(valueString.getBytes("UTF-8"));
			
			cursor.put(key, data);
			return true;
		}catch(DatabaseException e){
			return false;
		}catch(UnsupportedEncodingException e ) {
			e.printStackTrace();
			return false;
		}finally {
			cursor.close();
			close();
		}
	}
public static Table load(String tableName) throws dbException{
	
		open();
		
		cursor = myDatabase.openCursor(null, null);
		
		boolean flag = false;
		String keyString=new String();
		String dataString=new String();
		
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		
		try {
			cursor.getFirst(foundKey, foundData, LockMode.DEFAULT);
			do {
				if(foundKey.getData()==null) break;
				keyString = new String(foundKey.getData(),"UTF-8");
				dataString = new String(foundData.getData(), "UTF-8");
				if(keyString.equals(tableName)) {
					flag = true;
					break;
				}
			} while(cursor.getNext(foundKey, foundData, LockMode.DEFAULT)==OperationStatus.SUCCESS); 
		
		}catch(DatabaseException de) {
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		cursor.close();
		close();
		
		if(!flag) return null;
		else {
			if(stringToVal(dataString) != null)
				return new Table(keyString, stringToAtt(dataString), stringToVal(dataString));
			else return new Table(keyString, stringToAtt(dataString));
		}
	}

	public static void drop(boolean flag, String tbn) throws dbException{
		
		Table drop = load(tbn);
		if(drop == null) throw new dbException(DBMSMessage.NoSuchTable);
		
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		
		//check reference among tables
		if(flag) {
			try {
				open();
				cursor = myDatabase.openCursor(null, null);
				cursor.getFirst(foundKey, foundData, LockMode.DEFAULT);
				//boolean flag = true;
				
				do {
					//String keyString = new String(foundKey.getData(),"UTF-8");
					String dataString = new String();
					if(foundData.getData() != null)
						dataString = new String(foundData.getData(),"UTF-8");
					ArrayList<colAttribute> columnFam = stringToAtt(dataString);
					
					for(colAttribute col : columnFam) {
						
						if(col.isFk && col.refTable.equals(tbn)) {
							flag=false;
							break;
						}
					}
					
					if(!flag) break;
				}while(cursor.getNext(foundKey, foundData, LockMode.DEFAULT)==OperationStatus.SUCCESS);
	
				if(!flag) {
					
					throw new dbException(DBMSMessage.DropReferencedTableError, tbn);
				}
			}catch(DatabaseException de) {
				
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}finally {
				cursor.close();
				close();
			}
		
		}
		//if okay, drop this table
		try {
			open();
			cursor = myDatabase.openCursor(null, null);
			cursor.getFirst(foundKey, foundData, LockMode.DEFAULT);
			do {
				String keyString = new String();
				if(foundKey.getData() != null)keyString = new String(foundKey.getData(),"UTF-8");
				
				if(keyString.equals(tbn)) break;	
			}while(cursor.getNext(foundKey, foundData, LockMode.DEFAULT)==OperationStatus.SUCCESS) ;
			
			cursor.delete();
			
		}catch(DatabaseException de) {
			
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}finally {
			cursor.close();
			close();
		}
		//myDatabase.delete(null, key);
		
		
	}
	public static void desc(String tbn) throws dbException {
		Table tb = load(tbn);
		if(tb == null) throw new dbException(DBMSMessage.NoSuchTable);
		System.out.println("-------------------------------------------------");
		System.out.println("table_name ["+tbn+"]");
		System.out.printf("%-18s%-18s%-18s%-18s\n","column_name","type","null","key");
		for(colAttribute col : tb.columnFamily) {
			String isNull = col.nullable? "Y":"N";
			String isKey = col.isPk? "PRI" : "";
			isKey += col.isFk? (isKey.length()>0? "/FOR": "FOR") : ""; 
			System.out.printf("%-18s%-18s%-18s%-18s\n",col.colName,col.dataType,isNull,isKey);
		}
		System.out.println("-------------------------------------------------");
	}
	public static ArrayList<String> getAll() throws dbException{
		open();
		cursor = myDatabase.openCursor(null, null);
		
		ArrayList<String> result = new ArrayList<String>();
		DatabaseEntry foundKey = new DatabaseEntry();
		DatabaseEntry foundData = new DatabaseEntry();
		cursor.getFirst(foundKey, foundData, LockMode.DEFAULT);
		
		try {
			cursor.getFirst(foundKey, foundData, LockMode.DEFAULT);
			do {
				String tmp=new String();
				if(foundKey.getData()!= null) { 
					tmp = new String(foundKey.getData(),"UTF-8");
					result.add(tmp);			
				}
			}while(cursor.getNext(foundKey, foundData, LockMode.DEFAULT)==OperationStatus.SUCCESS) ;
		}catch(DatabaseException de) {
			
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
				
		cursor.close();
		close();
		
		return result;
	}
	public static void show() throws dbException{
		
		ArrayList<String> result = getAll();
		
		if(result.size()>0) {
			System.out.println("----------------");
			for(String tmp : result) System.out.println(tmp);
			System.out.println("----------------");
		}else {
			throw new dbException(DBMSMessage.ShowTablesNoTable);
		}
		
		
	}
	private static  String AttToString(ArrayList<colAttribute> list) {
		String result = new String();
		for(int i=0;i<list.size();i++) {
			colAttribute tmp = list.get(i);
			result += tmp.colName+";";
			result += tmp.dataType+";";
			result += tmp.nullable+";";
			result += tmp.isPk+";";
			result += tmp.isFk+";";
			result += tmp.refTable+";";
			result += tmp.refColumn+";";					
		}
		return result;
	}
	private static String ValToString(ArrayList<tupleElement> list) {
		if(list ==null) return ";;";
		String result = ";";
		for(int i=0;i<list.size();i++) {
			tupleElement tmp = list.get(i);
			result += ";";
			for(int j=0;j<tmp.size();j++) {
				result += tmp.get(j);
				result += ";";
			}
		}
		return result;
	}
	private static ArrayList<colAttribute> stringToAtt(String str){
		
		ArrayList<colAttribute> result = new ArrayList<colAttribute>();
		String[] tmp = str.split(";;;");
		String[] arr = tmp[0].split(";");
		//System.out.println(arr.length+"##");
		for(int i=0;i<arr.length;i+=7) {
			boolean nullable=false, isPk=false, isFk=false;
			if(arr[i+2].equals("true")) nullable=true;
			if(arr[i+3].equals("true")) isPk = true;
			if(arr[i+4].equals("true")) isFk = true;
			result.add(new colAttribute(arr[i],arr[i+1],nullable,isPk,isFk,arr[i+5],arr[i+6]));
		}
		
		return result;
	}
	private static ArrayList<tupleElement> stringToVal(String str){
		
		ArrayList<tupleElement> result = new ArrayList<tupleElement>();
		
		String[] tmp = str.split(";;;");
		if(tmp.length==1) 
			return null;
		
		String[] tupleFam = tmp[1].split(";;");
		for(int i=0; i<tupleFam.length;i++) {
			tupleElement member = new tupleElement();
			String[] element = tupleFam[i].split(";");
			for(int j=0;j<element.length;j++) {
				member.add(element[j]);
			}
			result.add(member);
		}
		
		return result;
		
	}
}
