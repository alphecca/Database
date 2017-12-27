package dbms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class deleteQuery {
	String tbn;
	ExpTree whereClause;

	public void setDeleteQuery(String tbn, ExpTree whereClause) {
		this.tbn = tbn;
		this.whereClause = whereClause;
	}
	
	public void run() throws dbException {
		int cnt0 = 0; //# of requested
		int cnt1 = 0; //# of (really removed)
		//cnt2: requested, but not removed
		//1. table exists?
		
		Table tb = DBMSController.load(tbn);
		if(tb == null) {
			throw new dbException(DBMSMessage.NoSuchTable);
		}
		
		//2. whereClause exists?
		if(whereClause == null || whereClause.isEmpty()) {
		
			tb.tupleFamily = new ArrayList<tupleElement>();
			DBMSController.drop(false,tbn);
			DBMSController.create(tb);
			return;
		}
		//3. Valid whereClause check 
		
		//4. filter tupleFamily
		ArrayList<tupleElement> tupleList = tb.tupleFamily;
		ArrayList<tupleElement> surviveList = new ArrayList<tupleElement>();
		for(int i=0;i<tupleList.size();i++) {
			boolean flag = false; // no filtering (survive)
			//4-1. execute predicate
			Map<String, String> nvMap = new HashMap<String, String>();
			Map<String, String> ntMap = new HashMap<String, String>();
			Set<String> colSet = new HashSet<String>();
			for(int j=0;j<tupleList.get(i).size();j++) {
				String value = tupleList.get(i).get(j);
				nvMap.put(tb.tableName+"."+tb.columnFamily.get(j).colName, value);
				ntMap.put(tb.tableName+"."+tb.columnFamily.get(j).colName, tb.columnFamily.get(j).dataType);
				colSet.add(tb.tableName+"."+tb.columnFamily.get(j).colName);
			}
			Map<String,String> tbnSet = new HashMap<String,String>();
			tbnSet.put(tbn, tbn);
			int aa=whereClause.calculate(nvMap, ntMap, colSet);
			
			flag = (aa==1)? true : false;
			//System.out.println(i+" th tuple should be delete? ["+flag+"]");
			//4-2. FK integrity constraint check
			if(flag) {
				cnt0++;//# of requested
				ArrayList<Table> modifiedTbList = new ArrayList<Table>();
				ArrayList<String> tbnList = DBMSController.getAll();
				for(int j=0;(flag)&&(j<tbnList.size());j++) {
					Table myTb = DBMSController.load(tbnList.get(j));
					boolean fkflag = false; // exists fk constraint attributes.
					boolean mflag = false;
					ArrayList<String> pos = new ArrayList<String>();
					for(int k=0;(flag)&&(k<myTb.columnFamily.size());k++)
						pos.add("");
					for(int k=0;(flag)&&(k<myTb.columnFamily.size());k++)
						if(myTb.columnFamily.get(k).isFk && myTb.columnFamily.get(k).refTable.equals(tbn)) {
							pos.set(k,myTb.columnFamily.get(k).refColumn);
							fkflag = true;
						}

					if(fkflag) {//fk constraints should be checked
						for(int k=0;(flag)&&(k<myTb.tupleFamily.size());k++) {
							for(int l=0;(flag)&&(l<myTb.tupleFamily.get(k).size());l++) {
								
								if(!pos.get(l).equals("")) {
									if(nvMap.get(tb.tableName+"."+pos.get(l)).equals(myTb.tupleFamily.get(k).get(l))) {
										if(myTb.columnFamily.get(k).nullable) {
											//flag still true(to be filtered), just add modified table list
											myTb.tupleFamily.get(k).replace(l, "null");
											
											mflag = true;
										}else {
											//cnt2++;
											flag=false;
											break;
										}
											
									}
								}
							}
						}
						if(flag && mflag) {
							modifiedTbList.add(myTb);
						}
					}
					
				}
				
				// modify DBMS
				for(int j=0;j<modifiedTbList.size();j++) {
					DBMSController.drop(true,modifiedTbList.get(j).tableName);
					DBMSController.create(modifiedTbList.get(j));
				}
			}
			if(!flag) {
				surviveList.add(tupleList.get(i));
			}
		}
		
		
		// modify DBMS
		cnt1 = tb.tupleFamily.size()-surviveList.size();//cnt0 - cnt2;?
		tb.tupleFamily = surviveList;
		DBMSController.drop(false,tbn);
		DBMSController.create(tb);
		//5. print result
		System.out.printf(DBMSMessage.DeleteResult+"\n",cnt1);
		//cnt0 - cnt1 = cnt2 
		if(cnt0 != cnt1) System.out.printf(DBMSMessage.DeleteReferentialIntegrityPassed+"\n",cnt0-cnt1);
	}	
}
