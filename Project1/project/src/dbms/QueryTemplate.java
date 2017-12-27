package dbms;

public class QueryTemplate {
	int q; //type of query
	//either below can be null, depending on the type of query(q)
	createTableQuery cq;
	insertQuery iq;
	selectQuery sq;
	deleteQuery dq;
	String tbn;
	
	public void setQueryTemplate(int n){
		q=n;
	}
	public void setQueryTemplate(createTableQuery cq){
		this.cq = cq;
	}
	public void setInsertQuery (insertQuery iq) {
		this.iq = iq;
	}
	
	public void setDeleteQuery (deleteQuery dq) {
		this.dq = dq;
	}
	public void setQueryTemplate(String tbn){
		this.tbn = tbn;
	}
	public void selectQuery(selectQuery sq) {
		this.sq = sq;
	}
	public void executeQuery() throws dbException{
		
		if(q==1) {//select
			try {
				sq.run();
			}catch(dbException e) {
				
			}
		}
	    else if(q==2) {//insert
	    	try {
	    		
	    		iq.run();
	    		
	    	}catch(dbException e) {
	    		
	    	}	    	
	    }
	    else if(q==3) {//delete Query
	    	try {
	    		
	    		dq.run();
	    		
	    	}catch(dbException e) {
	    		
	    	}
	    	
	    }
	    else if(q==4) System.out.println("\'ALTER TABLE\' requested");
		else if(q==5) //create table
				try{
				
					cq.run();
				
				}catch(dbException e){
				
				}
		else if(q==6)
			try{
				
				DBMSController.drop(true,tbn);
				System.out.printf(DBMSMessage.DropSuccess+"\n", tbn);
				
			}catch(dbException e){
				
			}
		else if(q==7)
			try {
				
				DBMSController.desc(tbn);
				
			}catch(dbException e) {
				
		  	}
		 else if(q==8)
			 try {
				
				DBMSController.show();
				
			}catch(dbException e) {
				
				}
		else return;
		
	}
}
