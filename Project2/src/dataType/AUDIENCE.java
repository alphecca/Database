package dataType;

public class AUDIENCE {
	public int id;
	public String name;
	public String gender;
	public int age;
	
	public AUDIENCE () {
		
	}
	public AUDIENCE (String name, String gender, int age) throws Exception{
		if(name.length() > 200)
			this.name = name.substring(0, 200);
		else
			this.name = name;
		
		if(gender.equals("M") || gender.equals("F"))
			this.gender = gender;
		else {
			System.out.println("Gender should be 'M' or 'F'");
			throw new Exception();
		}
		if(age >=1 )
			this.age = age;
		else {
			System.out.println("Age should be more than 0");
			throw new Exception();
		}
	}
}
