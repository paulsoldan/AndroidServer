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
}
