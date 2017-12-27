import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataType.*;

public class DAO {
	final String serverName = "147.46.15.147";
	final String dbName = "";
	final String userName = "";
	final String password = ""; //moon quiz사이트 참조
	final String url = "jdbc:mariadb://" + serverName + "/" + dbName;
	Connection conn;
	final String format = "--------------------------------------------------------------------------------";

	public DAO () throws SQLException {

		 conn = DriverManager.getConnection(url, userName, password);
		//port는 default 3306이라 지정할 필요 없습니다.
	}

	public void printBuildingInfo() throws SQLException{//1
		String sql =
				"(select  " +
				"	bdg.id as id, " +
				"	bdg.name as name, " +
				"	bdg.location as location, " +
				"	bdg.capacity as capacity, " +
				"	0 as assigned " +
				"from building as bdg " +
				"where  " +
				"	bdg.id not in (select b_id	from matching ) " +
				"	) " +
				"UNION " +
				"(select " +
				"	bdg.id as id, " +
				"	bdg.name as name, " +
				"	bdg.location as location, " +
				"	bdg.capacity as capacity, " +
				"	foo.p_num as assigned " +
				"	from building as bdg, " +
				"		(select b_id, count(p_id) as p_num " +
				"		from matching " +
				"		group by b_id " +
				"		) as foo " +
				"	where foo.b_id = bdg.id " +
				")"
				+ "	order by id";

		System.out.println(format);
		System.out.println("id\tname\t\tlocation\t\tcapacity\t\tassigned\t\t");
		System.out.println(format);
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			String location = rs.getString("location");
			int capacity = rs.getInt("capacity");
			int assigned = rs.getInt("assigned");

			System.out.println(id+"\t"+name+"\t\t"+location+"\t\t"+capacity+"\t\t"+assigned+"\t\t");
		}
		System.out.println(format);
	}
	public void printPerformanceInfo() throws SQLException{//2
		String sql =
				"(select " +
				"	pfm.id as id, " +
				"	pfm.name as name, " +
				"	pfm.type as type, " +
				"	pfm.price as price, " +
				"	0 as booked " +
				"	from " +
				"		performance as pfm " +
				"	where pfm.id not in "
				+ "	(select distinct p_id"
				+ "	 from reservation"
				+ "	 where status='y') " +
				") " +
				"UNION " +
				"(select " +
				"	pfm.id as id, " +
				"	pfm.name as name, " +
				"	pfm.type as type, " +
				"	pfm.price as price, " +
				"	foo.a_num as booked " +
				"	from  " +
				"	performance as pfm, " +
				"	(select p_id, " +
				"	count(a_id) as a_num " +
				"	from reservation " +
				"	where status='y' " +
				"	group by p_id " +
				"		) as foo  " +
				"		where pfm.id = foo.p_id " +
				"	)"
				+ "		order by id";

		System.out.println(format);
		System.out.println("id\tname\t\t\ttype\t\tprice\t\tbooked\t\t");
		System.out.println(format);
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			String type = rs.getString("type");
			int price = rs.getInt("price");
			int booked = rs.getInt("booked");

			System.out.println(id+"\t"+name+"\t\t"+type+"\t\t"+price+"\t\t"+booked+"\t\t");
		}
		System.out.println(format);
	}

	public void printAudienceInfo() throws SQLException{//3
		String sql =
				" select" +
				"	aud.id as id, " +
				"	aud.name as name, " +
				"	aud.gender as gender, " +
				"	aud.age as age " +
				"	from audience as aud"
				+ "	order by id";

		System.out.println(format);
		System.out.println("id\tname\t\t\tgender\t\tage\t\t");
		System.out.println(format);
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			int age = rs.getInt("age");

			System.out.println(id+"\t"+name+"\t\t"+gender+"\t\t"+age+"\t\t");
		}
		System.out.println(format);
	}

	public void printMatching(int b_id) throws SQLException{//12
		if(!buildingIsExist(b_id)) {
			System.out.println("Building "+b_id+" doesn’t exist");
			return ;
		}

		String sql =
				"( " +
				"	select " +
				"		pfm.p_id as id, " +
				"		pfm.name as name, " +
				"		pfm.type as type, " +
				"		pfm.price as price, " +
				"		count(pfm.seat_id) as booked " +
				" " +
				"	from (select performance.id as p_id, performance.name, performance.type, performance.price, reservation.seat_id, " +
				"		reservation.b_id, reservation.status " +
				"				 from performance, reservation " +
				"			where performance.id = reservation.p_id) as pfm " +
				"	where " +
				"		pfm.b_id = ? and pfm.status = 'y' " +
				"	group by pfm.p_id " +
				") " +
				"UNION " +
				"( " +
				"	select " +
				"		pfm.p_id as id, " +
				"		pfm.name as name, " +
				"		pfm.type as type, " +
				"		pfm.price as price, " +
				"		0 as booked " +
				"	from (select performance.id as p_id, performance.name, performance.type, performance.price " +
				"			from reservation, performance where reservation.p_id = performance.id and reservation.b_id = ?) as pfm " +
				"	where " +
				"		pfm.p_id not in (select res.p_id as id from reservation as res where res.b_id = ? and res.status='y') " +
				")"
				+ "	order by id";

		System.out.println(format);
		System.out.println("id\tname\t\t\ttype\t\tprice\t\tbooked\t\t");
		System.out.println(format);

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, b_id);
		stmt.setInt(2, b_id);
		stmt.setInt(3, b_id);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			String type = rs.getString("type");
			int price = rs.getInt("price");
			int booked = rs.getInt("booked");

			System.out.println(id+"\t"+name+"\t\t"+type+"\t\t"+price+"\t\t"+booked+"\t\t");
		}
		System.out.println(format);
	}

	public void printPerAudience(int p_id) throws SQLException{//13
		//1 check p_id exists
		if(!performanceIsExist(p_id)) {
			System.out.println("Performance "+p_id+" doesn’t exist");
			return ;
		}

		String sql =
				"select " +
				"	aud.id as id, " +
				"	aud.name as name, " +
				"	aud.gender as gender, " +
				"	aud.age as age " +
				"from " +
				"	audience as aud, " +
				"	(select distinct a_id " +
				"	from reservation " +
				"	where p_id = ? and status='y' " +
				"	) as foo " +
				"where " +
				"	foo.a_id = aud.id"
				+ "		order by id";

		System.out.println(format);
		System.out.println("id\tname\t\tgender\t\tage\t\t");
		System.out.println(format);
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1,p_id);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			String id = rs.getString("id");
			String name = rs.getString("name");
			String gender = rs.getString("gender");
			int age = rs.getInt("age");

			System.out.println(id+"\t"+name+"\t\t"+gender+"\t\t"+age+"\t\t");
		}
		System.out.println(format);
	}

	public void printReservation(int p_id) throws SQLException{//14
		//1 p_id exists check
		if(!performanceIsExist(p_id)) {
			System.out.println("Performance "+p_id+" doesn’t exist");
			return;
		}
		//2 matching(p_id~b_id) done check
		String sql_m =
				"select mat.b_id as id " +
				"from matching as mat " +
				"where mat.p_id = ?"
				+ "		order by id";
		PreparedStatement stmt_m = conn.prepareStatement(sql_m);
		stmt_m.setInt(1, p_id);
		ResultSet rs_m = stmt_m.executeQuery();
		String exists = new String();
		while(rs_m.next())
			exists = rs_m.getString("id");
		if(exists == null || exists.isEmpty()) {
			System.out.println("Performance "+p_id+" isn't assigned");
			return;
		}
		//
		String sql_s =
				"select " +
				"	res.seat_id as seat_number, " +
				"	res.a_id as audience_id " +
				"from reservation as res " +
				"where res.p_id = ?";

		System.out.println(format);
		System.out.println("seat_number\t\t\taudience_id\t\t\t");
		System.out.println(format);
		PreparedStatement stmt_s = conn.prepareStatement(sql_s);
		stmt_s.setInt(1, p_id);
		ResultSet rs_s = stmt_s.executeQuery();
		while(rs_s.next()) {
			int seat_number = rs_s.getInt("seat_number");
			int tmp = rs_s.getInt("audience_id");
			String audience_id = (tmp==0)? "" : String.valueOf(tmp);
			System.out.println(seat_number+"\t\t\t"+audience_id+"\t\t\t");
		}
		System.out.println(format);
	}

	public boolean insertBuilding(BUILDING b) throws SQLException{//4
		String sql_b =
				"insert into building "
				+ "		(name, location, capacity) "
				+ "		values(?, ?, ?)";

		PreparedStatement stmt_b = conn.prepareStatement(sql_b);

		stmt_b.setString(1, b.name);
		stmt_b.setString(2, b.location);
		stmt_b.setInt(3, b.capacity);

		int success = stmt_b.executeUpdate();
		if(success < 0 )
			return false;

		return true;
	}

	public boolean insertPerformance(PERFORMANCE concert) throws SQLException{
		String sql =
				"insert into performance "
				+ "		(name, type, price) "
				+ "		values(?,?,?)";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setString(1, concert.name);
		stmt.setString(2, concert.type);
		stmt.setInt(3, concert.price);

		int success = stmt.executeUpdate();
		if(success >= 0 )
			return true;
		else return false;
	}
	public boolean insertAudience(AUDIENCE audience) throws SQLException{
		String sql =
				"insert into audience "
				+ "		(name, gender, age) "
				+ "		values(?, ?, ?)";

		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setString(1, audience.name);
		stmt.setString(2, audience.gender);
		stmt.setInt(3, audience.age);

		int success = stmt.executeUpdate();
		if(success >= 0 )
			return true;
		else return false;
	}

	public boolean deleteBuilding(int b_id) throws SQLException{
		//1. check b_id exists
		if(!buildingIsExist(b_id)) {
			System.out.println("Building "+b_id+" doesn’t exist");
			return false;
		}
		//
		String sql = "delete from building where building.id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setInt(1, b_id);

		int success = stmt.executeUpdate();
		if(success >= 0 )
			return true;
		else return false;
	}

	public boolean deletePerformance(int p_id) throws SQLException{
		//1. check p_id exists
		if(!performanceIsExist(p_id)) {
			System.out.println("Performance "+p_id+" doesn’t exist");
			return false;
		}
		//
		String sql = "delete from performance where performance.id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setInt(1, p_id);

		int success = stmt.executeUpdate();
		if(success >= 0 )
			return true;
		else return false;
	}

	public boolean deleteAudience(int a_id) throws SQLException{
		//1. check a_id exists
		if(!audienceIsExist(a_id)) {
			System.out.println("Audience "+a_id+" doesn’t exist");
			return false;
		}
		//cascade status = 'n'
				String sql_w = "update reservation"
						+ "		set status = 'n'"
						+ "		where a_id = ?";
		PreparedStatement stmt_w = conn.prepareStatement(sql_w);
		stmt_w.setInt(1, a_id);
		int success_w= stmt_w.executeUpdate();
		if(success_w < 0)
			return false;
		//
		String sql = "delete from audience where audience.id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);

		stmt.setInt(1, a_id);

		int success = stmt.executeUpdate();
		if(success < 0 )
			return false;


		return true;
	}

	public boolean matching(int b_id, int p_id ) throws SQLException{//10
		//1. check b_id exists
		if(!buildingIsExist(b_id)) {
			System.out.println("Building "+b_id+" doesn’t exist");
			return false;
		}
		//2. check p_id exists
		if(!performanceIsExist(p_id)) {
			System.out.println("Performance "+p_id+" doesn’t exist");
			return false;
		}
		//3. check performance already assigned
		String sql_p =
				"select count(*) as cnt "
				+ "	from matching "
				+ "	where matching.p_id = ?";

		PreparedStatement stmt_p = conn.prepareStatement(sql_p);
		stmt_p.setInt(1, p_id);

		ResultSet rs_p = stmt_p.executeQuery();
		int assigned = 0;
		while(rs_p.next()) {
			assigned = rs_p.getInt("cnt");
		}
		if(assigned!=0) {
			System.out.println("Performance "+p_id+" is already assigned");
			return false;
		}
		//4
		String sql_m =
				"insert into matching (b_id, p_id) "
				+ "values (?, ?)";
		PreparedStatement stmt_m = conn.prepareStatement(sql_m);
		stmt_m.setInt(1, b_id);
		stmt_m.setInt(2, p_id);

		int success = stmt_m.executeUpdate();
		if(success < 0) return false;
		//5 update reservation table
		int capacity = 0;
		String sql_t =
				"select capacity from building where id=?";
		PreparedStatement stmt_t = conn.prepareStatement(sql_t);
		stmt_t.setInt(1, b_id);
		ResultSet rs_t = stmt_t.executeQuery();
		while(rs_t.next()) {
			capacity = rs_t.getInt("capacity");
		}
		//6 cascade: make reservation table
		String sql_r =
				"insert into reservation (b_id, p_id, seat_id)"
				+ "	values(?,?,?)";
		for(int i=0;(success>=0)&&(i<capacity);i++) {
			PreparedStatement stmt_r = conn.prepareStatement(sql_r);
			stmt_r.setInt(1, b_id);
			stmt_r.setInt(2, p_id);
			stmt_r.setInt(3, i+1);
			success = stmt_r.executeUpdate();
		}
		if(success < 0 ) return false;
		return true;
	}
	public int reserving(int p_id, int a_id, ArrayList<Integer> sl) throws SQLException{//11
		int b_id = 0;
		int capacity = 0;
		int price = 0;
		int age = 0;
		//1 p_id exist check & get price
		String sql_p =
				"select id as id, price"
				+ " 	from performance "
				+ "		where performance.id = ?"
				+ " order by id";
		PreparedStatement stmt_p = conn.prepareStatement(sql_p);
		stmt_p.setInt(1, p_id);
		ResultSet rs_p = stmt_p.executeQuery();
		if(!rs_p.next()) {
			System.out.println("Performance "+p_id+" doesn't exist");
			return -1;
		}else {
			price = rs_p.getInt("price");
		}
		//2 p_id b_id matching check & get capacity
		String sql_m =
				"select p_id as id, "
				+ "		matching.b_id as b_id, "
				+ "		capacity as capacity "
				+ "		from matching, building "
				+ "		where matching.b_id = building.id"
				+ "		and matching.p_id = ?"
				+ "		order by id";

		PreparedStatement stmt_m = conn.prepareStatement(sql_m);
		stmt_m.setInt(1, p_id);

		ResultSet rs_m = stmt_m.executeQuery();
		if(!rs_m.next()) {
			System.out.println("Performance "+p_id+" isn't assigned");
			return -1;
		}else {
			capacity = rs_m.getInt("capacity");
			b_id = rs_m.getInt("b_id");
		}
		//3 a_id exist check
		String sql_a =
				"select id as id, age"
				+ "		from audience "
				+ "		where id = ?"
				+ " order by id";
		PreparedStatement stmt_a = conn.prepareStatement(sql_a);
		stmt_a.setInt(1, a_id);
		ResultSet rs_a = stmt_a.executeQuery();
		if(!rs_a.next()) {
			System.out.println("Audience "+a_id+" doesn’t exist");
			return -1;
		}else {
			age = rs_a.getInt("age");
		}
		//4 check seat_number
		for(int i=0;i<sl.size();i++) {
			int sn = sl.get(i);
			//  in correct range
			if(sn <= 0 || sn > capacity) {
				System.out.println("Seat number out of range");
				return -1;
			}
			//	in status='n'(vacant)
			String sql_v =
					"select status from reservation "
					+ "		where b_id = ? and p_id = ? and seat_id = ?";
			PreparedStatement stmt_v = conn.prepareStatement(sql_v);
			stmt_v.setInt(1, b_id);
			stmt_v.setInt(2, p_id);
			stmt_v.setInt(3, sn);
			ResultSet rs_v = stmt_v.executeQuery();
			if(rs_v.next())
				if(!rs_v.getString("status").equals("n")) {
					System.out.println("The seat is already taken");
					return -1;
				}
		}
		//5	commit [status='y', a_id] & accumulate price
		double rev_price = 0;
		double sum=0;
		if(age>7 && age<13) rev_price = price*0.5;
		if(age>12 && age<19) rev_price= price*0.8;
		if(age>18) rev_price = price;
		int success=0;


		for(int i=0;(i<sl.size())&&(success>=0);i++) {
			int sn = sl.get(i);
			String sql_u =
					"update reservation "
					+ "		set  status = 'y', a_id = ?"
					+ "		where b_id = ? and p_id = ? and seat_id = ?";
			PreparedStatement stmt_u = conn.prepareStatement(sql_u);
			stmt_u.setInt(1, a_id);
			stmt_u.setInt(2, b_id);
			stmt_u.setInt(3, p_id);
			stmt_u.setInt(4, sn);

			success = stmt_u.executeUpdate();

			sum+=rev_price;
		}
		//System.out.println("total price: "+sum);
		//TODO 티켓 하나당 가격이 p_id, a_id 같으면 유일한 것 맞나?
		sum = Math.round(sum);
		return (int)sum;//return price
	}

	////////////////////////
	private boolean buildingIsExist(int b_id) throws SQLException {
		String sql =
				"select " +
				"	count(*) as cnt " +
				" from building " +
				" where id = ?;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, b_id);

		ResultSet rs = stmt.executeQuery();
		int exists=0;
		while(rs.next())
			exists = rs.getInt("cnt");
		if (exists==0) return false;
		return true;
	}
	private boolean performanceIsExist(int p_id) throws SQLException {
		String sql =
				"select "
				+ " 	count(*) as cnt	"
				+ "		from performance "
				+ "		where id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, p_id);
		ResultSet rs = stmt.executeQuery();
		int exists=0;
		while(rs.next())
			exists = rs.getInt("cnt");
		if (exists==0) return false;
		return true;

	}
	private boolean audienceIsExist(int a_id) throws SQLException {
		String sql =
				"select "
				+ "		count(*) as cnt "
				+ "		from audience "
				+ "		where id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, a_id);
		ResultSet rs = stmt.executeQuery();

		int exists= 0 ;
		while(rs.next())
			exists = rs.getInt("cnt");
		if (exists==0) return false;
		return true;
	}

}
