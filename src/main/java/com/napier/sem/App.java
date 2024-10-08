package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App  {

    private Connection con = null;

    /** Main entry */
    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();


        // Get all salaries
        //a.displaySalaries(a.getAllEmployeeSalaries());

        // Get salaries by department
        //a.displaySalariesByDepartment(a.getEmployeesByDepartment("Sales"));


        // Get salaries within the same department as the Department Manager(DM)
        // ("Yuchang Weedman") - DM for Customer Service
        //a.displaySalariesByDepartment(a.getEmployeesByDepartmentManager("Yuchang", "Weedman"));

        // Get salaries by role
        //a.displaySalariesByRole(a.getEmployeesByRole("Manager"));

        // Add Employee
//        Employee e = new Employee();
//        e.first_name = "John";
//        e.last_name = "Smith";
//        e.salary = 10000;
//        e.title = "Software Engineer"; // software engineer
//        e.dept_name = "Development";
//        e.manager = "Bob Ross";
//        boolean employeeAdded = a.addEmployee(e, "2000-01-01", "2024-09-01", "M");
//        System.out.println(employeeAdded ? "Employee added." : "Failed to add employee.");

        // Update Employee details
//        System.out.println("Getting employee...");
//        Employee e = a.getEmployee(10003);
//        a.displayEmployee(e);
//        // adjust records
//        e.last_name = "Smith";
//        // push changes
//        boolean updated = a.updateEmployee(e, "1959-12-03", "1986-08-28", "M");
//        if(updated){
//            // verify changes
//            System.out.println("Getting updated employee...");
//            a.displayEmployee(a.getEmployee(10003));
//        }else{
//            System.out.println("Failed to update employee");
//        }


        // Delete employee
        System.out.println("Getting employee...");
        Employee e = a.getEmployee(10003);
        a.displayEmployee(e);

        boolean employeeDeleted = a.deleteEmployee(10003);
        System.out.println("Getting employee...");
        e = a.getEmployee(10003);
        if(e == null){
            System.out.println("Employee deleted, verified.");
        }




        // Disconnect from database
        a.disconnect();
    }

    /** Connect to Database driver */
    public void connect(){
        try{
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i){
            System.out.println("Connecting to database...");
            try{
                // Wait a bit for db to start
                Thread.sleep(5000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }catch (SQLException sqle){
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }catch (InterruptedException ie){
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /** Disconnect SQL driver */
    public void disconnect(){
        if(con != null){
            try{
                // Close connection
                con.close();
            }catch (Exception e){
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID) {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT emp_no, first_name, last_name "
                            + "FROM employees "
                            + "WHERE emp_no = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public ArrayList<Employee> getAllEmployeeSalaries() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries " +
                            "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' " +
                            "ORDER BY employees.emp_no";


            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> salaries = new ArrayList<Employee>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salaries.salary");
                salaries.add(e);
            }  return salaries;
        }  catch (Exception e)  {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries");
            return null;
        }
    }

    public ArrayList<Employee> getEmployeesByDepartment(String department){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, departments.dept_name " +
                            "FROM employees " +
                            "JOIN dept_emp ON employees.emp_no = dept_emp.emp_no " +
                            "JOIN departments ON dept_emp.dept_no = departments.dept_no " +
                            "JOIN salaries ON employees.emp_no = salaries.emp_no " +
                            "WHERE departments.dept_name = '" + department + "' " +
                            "AND salaries.to_date = '9999-01-01';";


            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> salaries = new ArrayList<Employee>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salaries.salary");
                e.dept_name = rset.getString("dept_name");
                salaries.add(e);
            }
            return salaries;
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries");
            return null;
        }
    }

    public ArrayList<Employee> getEmployeesByDepartmentManager(String first_name, String last_name){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, departments.dept_name " +
                            "FROM employees " +
                            "JOIN dept_emp ON employees.emp_no = dept_emp.emp_no " +
                            "JOIN departments ON dept_emp.dept_no = departments.dept_no " +
                            "JOIN salaries ON employees.emp_no = salaries.emp_no " +
                            "WHERE dept_emp.dept_no = ( " +
                            "    SELECT dept_emp.dept_no " +
                            "    FROM employees " +
                            "    JOIN dept_emp ON employees.emp_no = dept_emp.emp_no " +
                            "    WHERE employees.first_name = '" + first_name +"' AND employees.last_name = '" + last_name +"' " + // Parameterized query
                            "    LIMIT 1 " +
                            ") " +
                            "AND salaries.to_date = '9999-01-01';";



            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> salaries = new ArrayList<Employee>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salaries.salary");
                e.dept_name = rset.getString("dept_name");
                salaries.add(e);
            }
            if(salaries.isEmpty()){
                System.out.println("No salaries found or given department manager is not valid");
            }

            return salaries;
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries for department manager " + first_name + " " + last_name + ".");
            return null;
        }
    }


    public ArrayList<Employee> getEmployeesByRole(String role){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, titles.title " +
                            "FROM employees " +
                            "JOIN salaries ON employees.emp_no = salaries.emp_no " +
                            "JOIN titles ON employees.emp_no = titles.emp_no " +
                            "WHERE titles.title = '" + role + "' " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND titles.to_date = '9999-01-01' "+
                            ";";


            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> salaries = new ArrayList<Employee>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salaries.salary");
                e.title = rset.getString("titles.title");
                salaries.add(e);
            }
            return salaries;
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries with role " + role + ".");
            return null;
        }
    }

    public boolean deleteEmployee(int emp_no) {
        try {
            Statement stmt = con.createStatement();
            String strDelete =
                    "DELETE FROM employees " +
                            "WHERE emp_no = " + emp_no;

            stmt.executeUpdate(strDelete);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to delete employee");
            return false;
        }

        System.out.println("Employee (" + emp_no + ") deleted successfully.");
        return true;
    }


    public boolean addEmployee(Employee emp, String birtdate, String hireDate, String gender){
        // Validation check
        if(emp == null){ return false;}

        try {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.first_name + "', '" + emp.last_name + "', " +
                            "'"+ birtdate +"', '"+gender+"', '" + hireDate +"')";
            stmt.execute(strUpdate);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to add given employee.");
            return false;
        }
        return true;
    }

    public boolean updateEmployee(Employee emp, String birtdate, String hireDate, String gender) {
        if(emp == null || emp.emp_no <= 0){ return false;}

        try {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "UPDATE employees SET " +
                            "first_name = '" + emp.first_name + "', " +
                            "last_name = '" + emp.last_name + "', " +
                            "birth_date = '" + birtdate + "', " +
                            "gender = '" + gender + "', " +
                            "hire_date = '" + hireDate + "' " +
                            "WHERE emp_no = " + emp.emp_no;

            stmt.executeUpdate(strUpdate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to update employee details");
            return false;
        }
        System.out.println("Successfully updated employee record.");
        return true;
    }



    public void displaySalariesByRole(ArrayList<Employee> salaries) {
        if(!getHasItems(salaries)) return;

        System.out.println("\nEmployee Salaries for " + salaries.get(0).title + " role/title:");
        System.out.printf("%-10s %-15s %-15s %-9s %-15s\n",  "ID", "First Name", "Last Name", "Salary", "Title");
        for(Employee e : salaries){
            System.out.printf("%-10d %-15s %-15s %-9d %-15s%n",  e.emp_no, e.first_name, e.last_name, e.salary, e.title);
        }
    }
    public void displaySalariesByDepartment(ArrayList<Employee> salaries) {
        if(!getHasItems(salaries)) return;


        System.out.println("\nEmployee Salaries in " + salaries.get(0).dept_name + " department:");
        for(Employee e : salaries){
            System.out.printf("%-10d %-15s %-15s %-9d %-15s%n",  e.emp_no, e.first_name, e.last_name, e.salary, e.dept_name);
//            System.out.println("ID: " +e.emp_no + ", first name: " + e.first_name + ", last name: " + e.last_name + ", salary: " + e.salary + ", department: " + e.dept_name );
        }
    }
    public void displaySalaries(ArrayList<Employee> salaries) {
        if(!getHasItems(salaries)) return;

        System.out.println(); //new line
        for(Employee e : salaries){
            System.out.println("ID: " +e.emp_no + ", first name: " + e.first_name + ", last name: " + e.last_name + ", salary: " + e.salary );
        }
    }
    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    "ID: " + emp.emp_no + " "
                            + "Name: " + emp.first_name + " "
                            + emp.last_name + "\n"
                            + "Title: " + emp.title + "\n"
                            + "Salary: " + emp.salary + "\n"
                            + "Department: " + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }


    private boolean getHasItems(ArrayList<Employee> salaries) {
        if(salaries == null || salaries.isEmpty()){
            System.out.println("No record to display.");
            return false;
        }
        return true;
    }
}