package dataType;

public class BUILDING {
	public int id;
	public String name;
	public String location;
	public int capacity;
	
	public BUILDING () {
		
	}
	public BUILDING (String name, String location, int capacity) throws Exception{
		if(name.length() > 200)
			this.name = name.substring(0, 200);
		else
			this.name = name;
		if(location.length() > 200)
			this.location = location.substring(0, 200);
		else
			this.location = location;
		
		if(capacity < 1) {
			System.out.println("Capacity should be larger than 0");
			throw new Exception();
		}else 
			this.capacity = capacity;
		
	}
}
