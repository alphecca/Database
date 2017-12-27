package dbms;

import java.util.ArrayList;

public class insertQuery {
	String tbn;
	ArrayList<String> colFamily = new ArrayList<String>();
	tupleElement tuple = new tupleElement();

	public insertQuery() {	
	}
	public void setTbn(String tbn) {
		this.tbn = tbn;
	}
	public void setColumnFamily (ArrayList<String> colFamily) {
		this.colFamily = colFamily;
	}
	public void setTupleElement (tupleElement tuple) {
		this.tuple = tuple;
	}
	
	public void run() throws dbException{
		//1. table exists?
		Table tb = DBMSController.load(tbn);
		if(tb == null) {
			throw new dbException(DBMSMessage.NoSuchTable);
		}
		//2. If <column name list> is designated		
		tupleElement valueList = new tupleElement();
		if(!colFamily.isEmpty()) {//designated
			
			ArrayList<String> sorted_tuple = new ArrayList<String>();
			for(int i=0;i<tb.columnFamily.size();i++)
				sorted_tuple.add("null");

			if(tuple.size() != colFamily.size() || colFamily.size()>tb.columnFamily.size())
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			
			/*/colNameList size
			if(colFamily.size() != tb.columnFamily.size())
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			System.out.println("Mismatch 2 pass");
			*/
			//colNameList name
			for(int i=0;i<colFamily.size();i++) {
				int pos=-1;
				for(int j=0;j<tb.columnFamily.size();j++) {
					if((tb.columnFamily.get(j).colName).equals(colFamily.get(i))) pos=j;
				}
				if(pos==-1)
					throw new dbException(DBMSMessage.InsertColumnExistenceError, colFamily.get(i));
				if(!sorted_tuple.get(pos).equals("null"))//try to insert duplicate column(Error)
					throw new dbException(DBMSMessage.InsertTypeMismatchError);
				
				sorted_tuple.set(pos, tuple.get(i));
			}
			 
			for(int i=0;i<sorted_tuple.size();i++) {
				
				valueList.add(sorted_tuple.get(i));
			}
			
		}else {//not designated
			if(tuple.size() != tb.columnFamily.size())
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			
			valueList = tuple;
		}
		
		
		
		//3. insert tuple <value list>
		
		String pkSet = new String();//store pk1+" "+pk2+" "+.... (in single tuple)
		for(int i=0;i<valueList.size();i++) {
			String value = valueList.get(i);
			colAttribute att = tb.columnFamily.get(i);
			
			//3-1. col Type comparison
			if(value != null) {
				value = typeChecker(att.dataType, value);
				valueList.replace(i, value);
			}
			//3-2. col nullable check
			if((!att.nullable)|| att.isPk) //must not null
				if(value.equals("null"))	
					throw new dbException(DBMSMessage.InsertColumnNonNullableError, att.colName);
			//3-3. col ref. integrity check
			if(att.isFk) {
				if(value != null && !value.equals("null")) {
					Table refTb = DBMSController.load(att.refTable);
					if(refTb == null)//no such ref.table
						throw new dbException(DBMSMessage.InsertReferentialIntegrityError);
					ArrayList<String> fkValL = new ArrayList<String>();
					for(int j=0;j<refTb.columnFamily.size();j++) {
						if((refTb.columnFamily.get(j).colName).equals(att.colName)) {
							//put ref.col's values in fkValL
							for(int k=0;k<refTb.tupleFamily.size();k++) {
								tupleElement tmp = refTb.tupleFamily.get(k);
								fkValL.add(tmp.get(j));
							}
							break;
						}
					}
					if(fkValL.isEmpty())//no such ref.col || non-nullabe value shouldn't be added since ref.col has no values
						throw new dbException(DBMSMessage.InsertReferentialIntegrityError);
					//check value is in fkValL
					int j=0;
					for(j=0;j<fkValL.size();j++) {
						if(value.equals(fkValL.get(j))) break;
					}
					if(j==fkValL.size()) //no such value in ref.col's value (=fkValL)
						throw new dbException(DBMSMessage.InsertReferentialIntegrityError);
				}
				//if value==null, just insert(o.k.)
			}
			//3-4. col primary key constraint check
			if(att.isPk)
				pkSet+=value+" ";
		}
		//3-4. (cont'd) col primary key constraint check
		ArrayList<String> pkSetList = new ArrayList<String>();//[pk1+" "+pk2+" ", pk1'+" "+pk2'+" ", ...]
		for(int i=0;i<tb.tupleFamily.size();i++) {
			String block = new String();
			for(int j=0;j<tb.tupleFamily.get(i).size();j++) {
				if(tb.columnFamily.get(j).isPk) {
					block += (tb.tupleFamily.get(i).get(j)+" ");
				}
			}
			if(!block.isEmpty()) pkSetList.add(block);	
		}
		//System.out.println("my PK: "+pkSet);
		boolean flag = true;//unique;
		for(int i=0;flag && i<pkSetList.size();i++) {
			//System.out.print("your PK: "+pkSetList.get(i));
			if(pkSetList.get(i).equals(pkSet)) flag= false;//not unique
		}
		if(!flag) //PK is not unique
			throw new dbException(DBMSMessage.InsertDuplicatePrimaryKeyError);
		//if all PASS, then	
		tb.tupleFamily.add(valueList);
		
		DBMSController.drop(false, tbn);
		DBMSController.create(tb);
		System.out.println(DBMSMessage.InsertResult);
		
	}
	public String typeChecker(String dataType, String item) throws dbException {
		
		String result = new String();
		if(dataType.length()==0)//I got something wrong 
			return "null";
		if(item.equals("null")) 
			return "null";
		
		char prefix = dataType.charAt(0);
		
		if(prefix == 'i') {//int
			try {
				Integer a = Integer.parseInt(item);
				result = a.toString();
			}catch (Exception e) {
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			}
			
		}else if(prefix == 'c') {//char
			try {
		
				String a = dataType.substring(5, dataType.length()-1);
				int mysize = Integer.parseInt(a);
		
				String myitem = item.substring(1,item.length()-1);//remove quotes
				if(myitem.length()>mysize) {
					result = myitem.substring(0, mysize);
		
				}
				else result = myitem;
			}catch(Exception e) {
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			}
		}else if(prefix == 'd') {//date
			try {
				String[] a = item.split("-");
				if(a.length != 3) throw new dbException(DBMSMessage.InsertTypeMismatchError);
				String p=a[0];
				String q=a[1];
				String r=a[2];
				if(p.length()==4 && q.length()==2 && r.length() ==2) {
					checkDate(p.charAt(0), p.charAt(1));
					checkDate(p.charAt(2), p.charAt(3));
					checkDate(r.charAt(0), r.charAt(1));
					checkDate(q.charAt(0), q.charAt(1));
					result = item;
				}else throw new dbException(DBMSMessage.InsertTypeMismatchError);
			}catch(Exception e) {
				throw new dbException(DBMSMessage.InsertTypeMismatchError);
			}
		}else {//I got something wrong
			return result;
		}
		
		return result;
	}
	private boolean checkDate(char t, char tt) {
			if((int)tt-'0' >=0 && (int)tt-'0' <=9)
				if((int)t-'0' >= 0 && (int)t-'0' <= 9)
				return true;
		return false;
	}
}
