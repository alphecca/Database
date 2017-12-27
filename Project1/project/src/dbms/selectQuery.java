package dbms;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator; 

public class selectQuery {
	ArrayList<refTabCol> selectList; //can be duplicate, so not a map
	ArrayList<refTabCol> fromList;
	ExpTree whereClause;
	Map<String, String> tbnSet;//alias-realname(tbn)
	
	public selectQuery() {
		selectList = new ArrayList<refTabCol>();
		fromList = new ArrayList<refTabCol>();
		whereClause = new ExpTree();
		
		tbnSet = new HashMap<String,String>();
	}
	public void setSelectList(ArrayList<refTabCol> selectList) {
		this.selectList = selectList;
	}
	public void setFromList(ArrayList<refTabCol> fromList) {
		this.fromList = fromList;
	}
	public void setWhereClause(ExpTree whereClause) {
		this.whereClause = whereClause;
	}
	public void print(ArrayList<String> aliasList, ArrayList<tupleElement> tupleList) {
		String outline = "";
		String colline = "";
		String tupline = "";
		 
		for(int i=0;i<aliasList.size();i++) {
			colline+="| "+aliasList.get(i)+" ";
			outline+="+-";
			for(int j=0;j<10;j++)//assume aliasList.get(i).length() =12
				outline+="-";
		}
		colline+=" |\n";
		outline+="-+\n";
		for(int i=0;i<tupleList.size();i++) {
			tupleElement tmp = tupleList.get(i);
			for(int j=0;j<tmp.size();j++) {
				tupline +="| "+tmp.get(j)+" ";
				for(int k=0;k<(12-tmp.get(j).length());k++) {//assume aliasList.get(i).length() =12
					tupline+=" ";
				}
			}
			tupline+="|\n";
		}
		System.out.println(outline+colline+outline+tupline+outline);
	}
	public void run() throws dbException{
		//1. validate <from list>
		fromToMap();
		
		//2. make Cartesian product of <from list> => new Table myTB
		//NOTE myTable has colName as form of [alias.col], but
		//		when print, remove [alias.]
		Map<String, String> copied_tbnSet = new HashMap<String,String>();
		for (String key:this.tbnSet.keySet()) {//java deep copy
			copied_tbnSet.put(key, new String(this.tbnSet.get(key)));
		}
		
		Table myTable = makeCartesian(copied_tbnSet);
		//myTable : tbn="myTable", colName="[tbn].[colN]", tupleL=tuples		
	
		//3. validate <select list>
		if((selectList.size()==1) && (selectList.get(0).isRef && selectList.get(0).refTbn.equals("*"))){
			selectList.clear();
			for(int i=0;i<myTable.columnFamily.size();i++) {
				String tmp =myTable.columnFamily.get(i).colName;
				String[] tmp2 = tmp.split("\\.");
				if(tmp2.length>=2)
					selectList.add(new refTabCol(tmp2[0], tmp2[1], tmp2[1]));
			}
		}else {
			for(int i=0;i<selectList.size();i++) {				
				if(selectList.get(i).refTbn.isEmpty()) { //no tableName => interfere
					ArrayList<String>refTbAlias = new ArrayList<String>();
					for (String key:tbnSet.keySet()) {
						Table tb = DBMSController.load(tbnSet.get(key));
						
						for(int j=0;j<tb.columnFamily.size();j++) {
							if(selectList.get(i).refCol.equals(tb.columnFamily.get(j).colName)) {
								String adding_tbn = tb.tableName;
								for(String atb : tbnSet.keySet()) {
									if(adding_tbn.equals(tbnSet.get(atb)))
										refTbAlias.add(atb);//set as alias name
								}
								
								break;
							}
						}
					}
					if(refTbAlias.size() != 1)
						throw new dbException(DBMSMessage.SelectColumnResolveError, selectList.get(i).refCol);
					refTabCol tmp = new refTabCol();
					tmp.setT(refTbAlias.get(0));
					tmp.setC(selectList.get(i).refCol);
					if(selectList.get(i).alias.isEmpty()) 
						tmp.setA(selectList.get(i).refCol);
					else
						tmp.setA(selectList.get(i).alias);
					
					selectList.set(i, tmp);
				}else {//has tableName
					
					Table tb = DBMSController.load(tbnSet.get(selectList.get(i).refTbn));
					if(tb ==null)//no such table
						throw new dbException(DBMSMessage.SelectColumnResolveError,selectList.get(i).refCol);
					boolean ss = false;
					for(int j=0;j<tb.columnFamily.size();j++) {
						if((selectList.get(i).refCol).equals(tb.columnFamily.get(j).colName))
							ss = true;
					}
					if(!ss)//no such column
						throw new dbException(DBMSMessage.SelectColumnResolveError,selectList.get(i).refCol);
					if(selectList.get(i).alias.isEmpty()) {
						refTabCol tmp = new refTabCol();
						tmp.setTCA(selectList.get(i).refTbn, selectList.get(i).refCol, selectList.get(i).refCol);
						selectList.set(i, tmp);
					}				
				}
			}
		}
		
		
		//4. <where clause> exists?
		ArrayList<String> printAlias = new ArrayList<String>();
		ArrayList<tupleElement> printTuple = new ArrayList<tupleElement>();
		if(whereClause == null || whereClause.isEmpty()) {//4-1. If not, printSelectList(all myTB tuples)
			ArrayList<Integer> pos = new ArrayList<Integer>();
			for(int i=0;i<selectList.size();i++) {
				printAlias.add(selectList.get(i).alias);
				String findCol = selectList.get(i).refTbn+"."+selectList.get(i).refCol;
				for(int j=0;j<myTable.columnFamily.size();j++) {
				
					if(findCol.equals(myTable.columnFamily.get(j).colName)){
				
						pos.add(j);
						break;
					}
				}
			}			
			for(int i=0;i<myTable.tupleFamily.size();i++) {
				tupleElement tuple = myTable.tupleFamily.get(i);
				printTuple.add(printSelectList(pos, tuple));
			}
		}else {//4-2. o.w. (= where clause exists)
			ArrayList<Integer> pos = new ArrayList<Integer>();
			for(int i=0;i<selectList.size();i++) {// store order of selection
				printAlias.add(selectList.get(i).alias);
				String findCol = selectList.get(i).refTbn+"."+selectList.get(i).refCol;
				for(int j=0;j<myTable.columnFamily.size();j++) {
					if(findCol.equals(myTable.columnFamily.get(j).colName)){
						pos.add(j);
					}
				}
			}
			
			//printSelectList(expTree.caculate(myTB tuple))
			
			Map<String, String> ntMap = new HashMap<String, String>();
			for(int i=0;i<myTable.columnFamily.size();i++) //colName-colType
				ntMap.put(myTable.columnFamily.get(i).colName, myTable.columnFamily.get(i).dataType);
			
			Set<String> colSet = new HashSet<String>();
			for(int i=0;i<myTable.columnFamily.size();i++)//store candidate of column name(in predicates)
				colSet.add(myTable.columnFamily.get(i).colName);
			
			for(int i=0;i<myTable.tupleFamily.size();i++) {
				tupleElement tuple = new tupleElement();
				tuple = myTable.tupleFamily.get(i);
				
				//colName-Value, colName-colType 
				Map<String, String> nvMap = new HashMap<String, String>();
				for(int j=0;j<tuple.size();j++) {
					nvMap.put(myTable.columnFamily.get(j).colName, tuple.get(j));
				}
				int aa =whereClause.calculate(nvMap, ntMap, colSet);
				boolean ab = (aa==1)? true:false;
				
				if(ab)
					if(printSelectList(pos,tuple) != null)
						printTuple.add(printSelectList(pos, tuple));
			}
		}
		print(printAlias, printTuple);
		return ;
	}

	private void fromToMap() throws dbException{
		
		for(int i=0;i<fromList.size();i++) {
			
			String myTbn = fromList.get(i).refTbn;
			String myAl = fromList.get(i).alias;
			
			Table tmp = DBMSController.load(myTbn);
			if(tmp == null)
				throw new dbException(DBMSMessage.SelectTableExistenceError, myTbn);
			
			if(myAl == null || myAl.isEmpty()) myAl = myTbn;
			
			if(tbnSet.get(myAl)!=null)
				//same table with same alias (at least same table need to have a different alias)
				throw new dbException(DBMSMessage.SelectTableExistenceError, myTbn);
			tbnSet.put(myAl, myTbn);
		}		
	}
	private tupleElement printSelectList(ArrayList<Integer> pos, tupleElement myTuple){
		
		tupleElement result = new tupleElement();
		
		for(int i=0;i<pos.size();i++) {
		
			result.add(myTuple.get(pos.get(i)));
		}
		return result;
	}
	private Table makeCartesian(Map<String,String> tbnS) throws dbException{
		
		Table result = new Table();
		result.tableName = "JoinTable";
		
		Table a = new Table();//new table
		Table b = new Table();
		String a_tmp = new String();//alias tmp
		String tmp = new String();//tbn tmp
		for (String key:tbnS.keySet()) { //only one-time loop
			a_tmp = key;
			tmp = tbnS.remove(key);
			break;
		}
		
		if(tbnS.isEmpty()) {//base case
			a = DBMSController.load(tmp);
			for(int i=0;i<a.columnFamily.size();i++) {
				colAttribute item = a.columnFamily.get(i);
				item.colName = a_tmp+"."+item.colName; //[alias].[colN]
				result.columnFamily.add(item);
			}
			result.tupleFamily = a.tupleFamily;
			return result;
		}else {
			b = makeCartesian(tbnS);//spanning table(old). always tbn="JoinTable"
			a = DBMSController.load(tmp);
			ArrayList<colAttribute> cl = new ArrayList<colAttribute>();
			cl=b.columnFamily;
			for(int i=0;i<a.columnFamily.size();i++) {
				colAttribute item = a.columnFamily.get(i);
				item.colName = a_tmp+"."+item.colName; //[alias].[colN]
				cl.add(item);
			}
			result.columnFamily = cl;
			
			ArrayList<tupleElement> tl = new ArrayList<tupleElement>();
			for(int i=0;i<b.tupleFamily.size();i++) {
				tupleElement element1 = b.tupleFamily.get(i);
				if(a.tupleFamily.size()==0) tl.add(element1);
				else {
					for(int j=0;j<a.tupleFamily.size();j++) {
						tupleElement item1 = new tupleElement();
						tupleElement element2 = a.tupleFamily.get(j);
						for(int k=0;k<element1.size();k++) {
							item1.add(element1.get(k));
						}
						for(int k=0;k<element2.size();k++) {
							item1.add(element2.get(k));
						}
						tl.add(item1);					
					}
				}
			}
			result.tupleFamily = tl;
			return result;
		}
	}
}
