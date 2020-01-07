package com.jdbc.DAO;
import java.sql.*;
import java.util.*;

import com.jdbc.Model.Employee;
public class EmployeeDAO {
	private static Scanner sc = new Scanner(System.in);
	private static Connection con = null;
	private static PreparedStatement st = null;
	private static String url = "jdbc:mysql://localhost:3306/DatabaseName", username = "username", password = "passowrd";
	// establish the connection
	private static void openConnection()throws Exception {
		con = DriverManager.getConnection(url, username, password);
	}
	// close all the open connections
	private static void closeAllConnections()throws Exception {
		st.close();
		con.close();
	}
	// Create a new Employee object
	public static int createEmployee(Employee employee)throws Exception {
		openConnection();
		String query = "insert into employee values(?, ?, ?)";
		st = con.prepareStatement(query);
		st.setInt(1, employee.getId());
		st.setString(2, employee.getName());
		st.setDouble(3, employee.getSalary());
		
		int count = st.executeUpdate();
		closeAllConnections();
		
		return count;
	}
    // Read employee object based on id
	public static Employee getEmployee(int id)throws Exception{
		openConnection();
		String query = "select * from employee where id = ?";
		st = con.prepareStatement(query);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		Employee employee = new Employee();
		if(rs.next()) {
		employee.setId(rs.getInt("id"));
		employee.setName(rs.getString("name"));
		employee.setSalary(rs.getDouble("salary"));
		}
		closeAllConnections();
		
		return employee;
	}
	// Update employee salary based on id
	public static int updateEmployee(int id, double salary)throws Exception {
		openConnection();
		con.setAutoCommit(false);
		String query = "update employee set salary = ? where id = ?";
		st = con.prepareStatement(query);
		st.setDouble(1, salary);
		st.setInt(2, id);
		int count = st.executeUpdate();
		System.out.println("Are you sure you want to procees ? Yes/No");
		boolean isOk = sc.nextLine().toLowerCase().equals("yes") ? true : false;
		if(isOk) {
			con.commit();
			System.out.println("Transaction complete");
		}
		else {
			con.rollback();
			System.out.println("Transaction aborted");
		}
		closeAllConnections();
		
		return count;
	}
	// Delete employee object based on id
	public static int deleteEmployee(int id)throws Exception{
		openConnection();
		String query = "delete from employee where id = ?";
		st = con.prepareStatement(query);
		st.setInt(1, id);
		
		int count = st.executeUpdate();
		closeAllConnections();
		
		return count;
	}
	// function to get average salary of two employees
	public static double getAverageSalary(int id1, int id2)throws Exception{
		openConnection();
		String query = "{? = call getAverage(?, ?)}";
		CallableStatement st = con.prepareCall(query);
		st.setInt(2, id1);
		st.setInt(3, id2);
		st.registerOutParameter(1, Types.DECIMAL);
		st.execute();
		double avg = st.getDouble(1);
		con.close();
		st.close();
		return avg;
	}
	// function to get employee details based on id
	public static Employee getEmployeeDetails(int id1)throws Exception {
		openConnection();
		String query = "{call getEmployeeDetails2(?)}";
		CallableStatement st = con.prepareCall(query);
		st.setInt(1, id1);
		st.execute();
		ResultSet rs = st.getResultSet();
		Employee emp = new Employee();
		if(rs.next()) {
		emp.setId(id1);
		emp.setName(rs.getString("name"));
		emp.setSalary(rs.getDouble("salary"));
		}
		st.close();
		con.close();
		return emp;
	}
}
