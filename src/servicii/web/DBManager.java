package servicii.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
	public static String messageError = "Wrong password or e-mail address. Please try again!";
	private static final String URL = "jdbc:mysql://localhost:3306/countmywork";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "soldan1234";

	private static final DBManager instance = new DBManager();
	private Connection conn;

	public static DBManager getInstance() {
		return instance;
	}

	private DBManager() {
		System.out.println("Loading driver...");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded!");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		}
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("null")
	public int testLogin(String username, String password) {
		try (Statement st = conn.createStatement()) {
			List<String> user_list = new ArrayList<String>();
			System.out.println(username);
			System.out.println(password);
			st.execute(("select * from users where username='") + username + ("' and password='") + (password) + ("';"));
			ResultSet rs = st.getResultSet();
			while (rs.next()) {
				user_list.add(rs.getString("id"));
				user_list.add(rs.getString("username"));
				user_list.add(rs.getString("password"));
			}
			System.out.println(user_list);
			if ((!user_list.isEmpty()) && username.equals(user_list.get(1)) && password.equals(user_list.get(2))) {
				return 1;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return (Integer) null;
		}
	}
	
	public List<String> task(){
		try (Statement st = conn.createStatement()) {
			List<String> task_list = new ArrayList<String>();
			st.execute("select * from tasks;");
			ResultSet rs = st.getResultSet();
			while (rs.next()) {
				task_list.add(rs.getString("task_name"));
			}
			if (!task_list.isEmpty()) {
				return task_list;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String checkIn(String username, String hour, String task) {
		try (Statement st = conn.createStatement()) {
			String status="ok";
			int days=0;
			st.execute("insert into checkIn values((select id from users where username=\""+username+'"'+"),"+'"'+hour+'"'+","+'"'+task+'"'+");");
			st.execute("select * from days_worked where user_id = (select id from users where username=\""+username+'"'+");");
			ResultSet rs = st.getResultSet();
			while (rs.next()) {
				days=rs.getInt("days");
			}
			days+=1;
			st.execute("update days_worked set days=\""+days+'"'+" where user_id = (select id from users where username=\""+username+'"'+");");
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String checkOut(String username, String hour) {
		try (Statement st = conn.createStatement()) {
			String status="ok";
			String checkIn = "00:00";
			int user_id=-1;
			st.execute("insert into checkOut values((select id from users where username=\""+username+'"'+"),"+'"'+hour+'"'+");");
			st.execute("select * from checkIn where user_id_in = (select id from users where username=\""+username+'"'+");");
			ResultSet rs = st.getResultSet();
			while (rs.next()) {
				checkIn=rs.getString("checkIn");
				user_id=rs.getInt("user_id_in");
			}
			String[] in_list = checkIn.split(":");
			String[] out_list = hour.split(":");
			int h_in=Integer.parseInt(in_list[0]);
			int m_in=Integer.parseInt(in_list[1]);
			int h_out=Integer.parseInt(out_list[0]);
			int m_out=Integer.parseInt(out_list[1]);
			int h_final = 0, m_final = 0;
			if(m_out-m_in<0){
				m_final=(60+m_out)-m_in;
				h_out=h_out-1;
			}
			else{
				m_final=m_out-m_in;
			}
			h_final=h_out-h_in;
			String hw="00:00";
			st.execute("select * from hours_work where hw_user_id = (select id from users where username=\""+username+'"'+");");
			ResultSet rst = st.getResultSet();
			while (rst.next()) {
				hw=rst.getString("hw");
			}
			if(!hw.equals("00:00")){
				h_in=h_final;
				m_in=m_final;
				String[] hw_list = hw.split(":");
				int h_w=Integer.parseInt(hw_list[0]);
				int m_w=Integer.parseInt(hw_list[1]);
				if(m_in+m_w>=60){
					m_final=(m_in+m_w)-60;
					h_in=h_in+1;
				}
				else{
					m_final=m_in+m_w;
				}
				h_final=h_in+h_w;
			}
			String hour_final = "";
			hour_final = "" + h_final + ":" + m_final;
			st.execute("update hours_work set hw=\""+hour_final+'"'+" where hw_user_id = "+"(select id from users where username=\""+username+'"'+");");
			st.execute("delete from checkIn where user_id_in='" + user_id + "';");
			st.execute("delete from checkOut where user_id_out='" + user_id + "';");
			st.execute("select * from hours_work where hw_user_id = (select id from users where username=\""+username+'"'+");");
			rst = st.getResultSet();
			while (rst.next()) {
				hw=rst.getString("hw");
			}
			double salary_this_month = 0;
			st.execute("select * from salary where user_id = (select id from users where username=\""+username+'"'+");");
			rs = st.getResultSet();
			while (rs.next()) {
				salary_this_month=rs.getDouble("salary_this_month");
			}
			double salary_this_month_per_minute=salary_this_month/60;
            String[] splitHours = hw.split(":");
            h_in=Integer.parseInt(splitHours[0]);
            m_in=Integer.parseInt(splitHours[1]);
            int totalMinutes=h_in*60+m_in;
            double salary=totalMinutes*salary_this_month_per_minute;
            st.execute("update salary set earnings=\""+salary+'"'+" where user_id = "+"(select id from users where username=\""+username+'"'+");");
			return status;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<String> status(String username) {
		try (Statement st = conn.createStatement()) {
			List<String> reports=new ArrayList<String>();
			String hours = "00:00";
			int days = 0;
			double salary_this_month = 0;
			double earnings = 0;
			st.execute("select * from hours_work where hw_user_id = (select id from users where username=\""+username+'"'+");");
			ResultSet rs = st.getResultSet();
			while (rs.next()) {
				hours=rs.getString("hw");
			}
			reports.add(hours);
			st.execute("select * from days_worked where user_id = (select id from users where username=\""+username+'"'+");");
			rs = st.getResultSet();
			while (rs.next()) {
				days=rs.getInt("days");
			}
			reports.add(""+days);
			st.execute("select * from salary where user_id = (select id from users where username=\""+username+'"'+");");
			rs = st.getResultSet();
			while (rs.next()) {
				salary_this_month=rs.getDouble("salary_this_month");
				earnings=rs.getDouble("earnings");
			}
			reports.add(""+salary_this_month);
			reports.add(""+earnings);
			return reports;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
