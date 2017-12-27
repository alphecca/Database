package dataType;

public class PERFORMANCE {
	public int id;
	public String name;
	public String type;
	public int price;
	
	public PERFORMANCE () {
		
	}
	public PERFORMANCE (String name, String type, int price) throws Exception{
		if(name.length() > 200)
			this.name = name.substring(0, 200);
		else
			this.name = name;
		if(type.length() > 200)
			this.type = type.substring(0, 200);
		else
			this.type = type;
		if(price >= 0)
			this.price = price;
		else {
			System.out.println("Price should be 0 or more");
			throw new Exception();
		}
	}

}
