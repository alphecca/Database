package dbms;

public class dbException extends Exception {
	public dbException (String msg) {
		System.out.println(msg);
	}
	public  dbException (String msg, String obj) {
		System.out.printf(msg,obj);
		System.out.println();
	}
	public dbException (String msg, int obj) {
		System.out.printf(msg,obj);
		System.out.println();
	}
}
