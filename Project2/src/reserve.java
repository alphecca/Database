import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

import dataType.*;

public class reserve {

	public static void main(String[] args) throws IOException, SQLException {

		System.out.println("============================================================\r\n" + 
				"1. print all buildings\r\n" + 
				"2. print all performances\r\n" + 
				"3. print all audiences\r\n" + 
				"4. insert a new building\r\n" + 
				"5. remove a building\r\n" + 
				"6. insert a new performance\r\n" + 
				"7. remove a performance\r\n" + 
				"8. insert a new audience\r\n" + 
				"9. remove an audience\r\n" + 
				"10. assign a performance to a building\r\n" + 
				"11. book a performance\r\n" + 
				"12. print all performances which assigned at a building\r\n" + 
				"13. print all audiences who booked for a performance\r\n" + 
				"14. print ticket booking status of a performance\r\n" + 
				"15. exit\r\n" + 
				"============================================================\r\n" 
				);
		DAO dao = new DAO();
		
		while(true) {
			
			System.out.println("Select your action:");
			BufferedReader in = new BufferedReader(new InputStreamReader (System.in));
			String cmd = in.readLine();
			
			if(cmd.equals("1")) {
				dao.printBuildingInfo();
			}else if(cmd.equals("2")) {
				dao.printPerformanceInfo();
			}else if(cmd.equals("3")) {
				dao.printAudienceInfo();
			}else if(cmd.equals("4")) {
				System.out.println("Building name: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				String b_name = in.readLine();
				System.out.println("Building location: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				String b_loc = in.readLine();
				System.out.println("Building capacity: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int b_cap = Integer.parseInt(in.readLine());
				if(b_cap < 1) {
					System.out.println("Capacity should be larger than 0");
					continue;
				}
				
				try{
					BUILDING location = new BUILDING(b_name, b_loc, b_cap);

					boolean flag = dao.insertBuilding(location);
					if(flag)
						System.out.println("A building is successfully inserted");
					else  
						;
				}catch(Exception e){
				}
			}else if(cmd.equals("5")) {
				System.out.println("Building ID: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				int b_id = Integer.parseInt(in.readLine());
				
				boolean flag = dao.deleteBuilding(b_id);
				if(flag)
					System.out.println("A building is successfully removed");
				else 
					;
				
			}else if(cmd.equals("6")) {
				System.out.println("Performance name: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				String c_name = in.readLine();
				System.out.println("Performance type: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				String c_type = in.readLine();
				System.out.println("Performance price: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int c_price = Integer.parseInt(in.readLine());
				if(c_price < 0 ) {
					System.out.println("Price should be 0 or more");
					continue;
				}
				
				
				try {
					PERFORMANCE concert = new PERFORMANCE(c_name, c_type, c_price);
					boolean flag = dao.insertPerformance(concert);
					if(flag)
						System.out.println("A performance is successfully inserted");
					else  
						;
				}catch(Exception e) {
					
				}				
			}else if(cmd.equals("7")) {
				System.out.println("Performance ID: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				int p_id = Integer.parseInt(in.readLine());
				
				boolean flag = dao.deletePerformance(p_id);
				if(flag)
					System.out.println("A performance is successfully removed");
				else 
					;
				
			}else if(cmd.equals("8")) {
				
				System.out.println("Audience name: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				String a_name = in.readLine();
				System.out.println("Audience gender: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				String a_gen = in.readLine();
				if(!a_gen.equals("M") && !a_gen.equals("F")) {
					System.out.println("Gender should be 'M' or 'F'");
					continue;
				}
				System.out.println("Audience age: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int a_age = Integer.parseInt(in.readLine());
				if(a_age <= 0) {
					System.out.println("Age should be more than 0");
					continue;
				}
				
				try {
					AUDIENCE audience = new AUDIENCE(a_name, a_gen, a_age);
					boolean flag = dao.insertAudience(audience);
					if(flag)
						System.out.println("An audience is successfully inserted");
					else
						;
				}catch(Exception e) {
					
				}
				
			}else if(cmd.equals("9")) {
				System.out.println("Audience ID: " );
				in = new BufferedReader(new InputStreamReader (System.in));
				int a_id = Integer.parseInt(in.readLine());
				
				boolean flag = dao.deleteAudience(a_id);
				if(flag)
					System.out.println("An audience is successfully removed");
				else 
					;
				
			}else if(cmd.equals("10")) {
				System.out.println("Building ID: ");
				in = new BufferedReader(new InputStreamReader (System.in)); 
				int b_id = Integer.parseInt(in.readLine());
				System.out.println("Performance ID: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int p_id = Integer.parseInt(in.readLine());

				boolean flag = dao.matching(b_id, p_id);
				if(flag)
					System.out.println("Successfully assign a performance");
				else ;
				
			}else if(cmd.equals("11")) {
				System.out.println("Performance ID: ");
				in = new BufferedReader(new InputStreamReader (System.in)); 
				int p_id = Integer.parseInt(in.readLine());
				System.out.println("Audience ID: ");
				in = new BufferedReader(new InputStreamReader (System.in)); 
				int a_id = Integer.parseInt(in.readLine());
				System.out.println("Seat number: ");
				in = new BufferedReader(new InputStreamReader (System.in)); 
				ArrayList<Integer> sl = new ArrayList<Integer>();
				String tmp = in.readLine();
				tmp = tmp.replaceAll(" ", "");
				String[] tmp2 = tmp.split(",");
				for(int i=0;i<tmp2.length;i++)
					sl.add(Integer.parseInt(tmp2[i]));
				int flag = dao.reserving(p_id, a_id, sl);
				if(flag >= 0 ) {
					System.out.println("Successfully book a performance");
					System.out.println("Total ticket price is "+flag);
				}else
					;
				
			}else if(cmd.equals("12")) {
				System.out.println("Building ID: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int b_id = Integer.parseInt(in.readLine());
				
				dao.printMatching(b_id);
				
			}else if(cmd.equals("13")) {
				System.out.println("Performance ID: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int p_id = Integer.parseInt(in.readLine());
				
				dao.printPerAudience(p_id);				
				
			}else if(cmd.equals("14")) {
				System.out.println("Performance ID: ");
				in = new BufferedReader(new InputStreamReader (System.in));
				int p_id = Integer.parseInt(in.readLine());
				
				dao.printReservation(p_id);				
				
			}else if(cmd.equals("15")) {
				System.out.println("Bye!");
				System.exit(0);
			}else {
				System.out.println("Invalid action");
			}			
		}
		
	}

}
