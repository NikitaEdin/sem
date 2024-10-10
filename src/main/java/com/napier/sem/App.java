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

        ////////////////////////// SQL QUERIES //////////////////////////

        // Get Department
        Department dept = a.getDepartment("Sales");
        if(dept != null) {
            System.out.println("Dept: " + dept.dept_no + ", name: " + dept.dept_name);
            System.out.println("Manager name: " + dept.manager.first_name);

            // Salaries in department
            System.out.println("Salaries in department " + dept.dept_name +": " + a.getSalariesByDepartment(dept).size() + "\n");
        }


        // Get all salaries
//        System.out.println("All salaries: " + a.getAllEmployeeSalaries().size() + "\n");

        // Get employees by department
//        System.out.println("Employees in department Development: " + a.getEmployeesByDepartment("Development").size() + "\n");


        // Get salaries within the same department as the Department Manager(DM) -  ("Yuchang Weedman") - DM for Customer Service
//        System.out.println("All salaries in the department of Yuchang Weedman: " + a.getEmployeesByDepartmentManager("Yuchang", "Weedman").size() + "\n");

        // Get salaries by role
//        System.out.println("Salaries by role Manager: " + a.getEmployeesByRole("Manager").size() + "\n");

        // Add Employee
//        System.out.println("Adding new employee...");
//        Employee e = new Employee();
//        e.emp_no = 101;
//        e.first_name = "John";
//        e.last_name = "Smith";
//        e.salary = 10000;
//        e.title = "Software Engineer"; // software engineer
//        boolean employeeAdded = a.addEmployee(e, "2000-01-01", "2024-09-01", "M");
//        System.out.println((employeeAdded ? "Employee added." : "Failed to add employee.")   + "\n" );

        // Update Employee details
//        System.out.println("Getting employee...");
//        Employee updateEmployee = a.getEmployee(10003);
//        if(updateEmployee != null){
//            a.displayEmployee(updateEmployee);
//            // adjust records
//            updateEmployee.last_name = "Bob";
//            // push changes
//            boolean updated = a.updateEmployee(updateEmployee, "1959-12-03", "1986-08-28", "M");
//            if(updated){
//                // verify changes
//                System.out.println("Getting updated employee...\n" );
//                a.displayEmployee(a.getEmployee(10003));
//            }else{
//                System.out.println("Failed to update employee\n");
//            }
//        }else{
//            System.out.println("Failed to get employee.");
//        }


        // Delete employee
//        System.out.println("Getting employee...");
//        Employee deleteEmployee = a.getEmployee(10003);
//        a.displayEmployee(deleteEmployee);
//
//        boolean employeeDeleted = a.deleteEmployee(10003);
//        System.out.println("Getting employee...");
//        deleteEmployee = a.getEmployee(10003);
//        if(deleteEmployee == null){
//            System.out.println("Employee deleted, verified.\n");
//        }else{
//            System.out.println("Failed to delete employee.\n");
//        }


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

    public Employee getEmployee_simple(int ID) {
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

    public Employee getEmployee(int ID) {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, " +
                            "d.dept_no, d.dept_name, " +
                            "m.emp_no AS manager_emp_no, m.first_name AS manager_first_name, m.last_name AS manager_last_name " +
                            "FROM employees e " +
                            "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "JOIN departments d ON de.dept_no = d.dept_no " +
                            "LEFT JOIN dept_manager dm ON d.dept_no = dm.dept_no " + // Optional join for manager
                            "LEFT JOIN employees m ON dm.emp_no = m.emp_no " + // Join to get manager details
                            "WHERE e.emp_no = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Check one is returned
            if (rset.next()) {

                // Create Employee object
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");

                // Set Department information
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                emp.dept = dept;

                // Set Manager information if available (using LEFT JOIN)
                if (rset.getString("manager_emp_no") != null) {
                    Employee manager = new Employee();
                    manager.emp_no = rset.getInt("manager_emp_no");
                    manager.first_name = rset.getString("manager_first_name");
                    manager.last_name = rset.getString("manager_last_name");
                    emp.manager = manager;
                } else {
                    emp.manager = null; // No manager found
                }
                return emp;

            }  else {
                return null;
            }
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
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";


            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while(rset.next()){
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }  return employees;
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
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, departments.dept_name, departments.dept_no " +
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

                // Create Department for employee
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                // add Department ref to employee
                e.dept = dept;

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
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, departments.dept_name, departments.dept_no " +
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
                // Create Employee obj and assign data from query
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salaries.salary");

                // Create Department for employee
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");
                // add Department ref to employee
                e.dept = dept;

                // Add employee to list
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

        // Default 'to_date' for active salary records
        String toDate = "9999-01-01";

        try {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.first_name + "', '" + emp.last_name + "', " +
                            "'" + birtdate + "', '" + gender + "', '" + hireDate + "')";
            stmt.execute(strUpdate);

            // Insert employee's title into the titles table
            String strInsertTitle =
                    "INSERT INTO titles (emp_no, title, from_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.title + "', '" + hireDate + "')";
            stmt.executeUpdate(strInsertTitle);

            // Insert employee's salary into the salaries table
            // Insert employee's salary into the salaries table with a default to_date
            String strInsertSalary =
                    "INSERT INTO salaries (emp_no, salary, from_date, to_date) " +
                            "VALUES (" + emp.emp_no + ", " + emp.salary + ", '" + hireDate + "', '" + toDate + "')";
            stmt.executeUpdate(strInsertSalary);

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


        System.out.println("\nEmployee Salaries in " + salaries.get(0).dept.dept_name + " department:");
        for(Employee e : salaries){
            System.out.printf("%-10d %-15s %-15s %-9d %-15s%n",  e.emp_no, e.first_name, e.last_name, e.salary, e.dept.dept_name);
        }
    }


    /**
     * Prints a list of employees.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees){
        if(employees == null || employees.isEmpty()) {
            System.out.println("No employees to display.");
            return;
        }


        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
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
                            + (emp.dept != null ? "Department: " + emp.dept.dept_name : "") + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }

    public Department getDepartment(String name) {
        Department dept = null;

        try {
            // Fetch the department
            Statement stmt = con.createStatement();
            String strQuery =
                    "SELECT d.dept_no, d.dept_name, e.emp_no, e.first_name, e.last_name, e.gender, e.hire_date, e.birth_date " +
                            "FROM departments d " +
                            "INNER JOIN dept_manager dm ON d.dept_no = dm.dept_no " +
                            "INNER JOIN employees e ON dm.emp_no = e.emp_no " +
                            "WHERE d.dept_name = '" + name + "' AND dm.to_date = '9999-01-01'";


            // Execute SQL statement
            ResultSet rs = stmt.executeQuery(strQuery);

            if (rs.next()) {
                // Create Department
                dept = new Department();
                dept.dept_no = rs.getString("dept_no");
                dept.dept_name = rs.getString("dept_name");

                // Create Employee
                Employee manager = new Employee();
                manager.emp_no = rs.getInt("emp_no");
                manager.first_name = rs.getString("first_name");
                manager.last_name = rs.getString("last_name");
                manager.birth_date = rs.getDate("birth_date").toString();
                manager.gender = rs.getString("gender");
                manager.hire_date = rs.getDate("hire_date").toString();

                // Set the manager in the department
                dept.manager = manager;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve department details");
        }

        return dept;
    }


    public ArrayList<Employee> getSalariesByDepartment(Department dept){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT e.emp_no, e.first_name, e.last_name, e.gender, e.hire_date, e.birth_date, s.salary, t.title " +
                            "FROM employees e " +
                            "JOIN dept_emp de ON e.emp_no = de.emp_no " +
                            "JOIN salaries s ON e.emp_no = s.emp_no " +
                            "JOIN titles t ON e.emp_no = t.emp_no " +
                            "WHERE de.dept_no = '"+ dept.dept_no+"' AND s.to_date = '9999-01-01'";


            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            ArrayList<Employee> salaries = new ArrayList<Employee>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no = rset.getInt("emp_no");
                e.first_name = rset.getString("first_name");
                e.last_name = rset.getString("last_name");
                e.salary = rset.getInt("salary");
                e.birth_date = rset.getDate("birth_date").toString();
                e.gender = rset.getString("gender");
                e.hire_date = rset.getDate("hire_date").toString();
                e.title = rset.getString("title");
                e.dept = dept;
                salaries.add(e);
            }
            return salaries;
        }  catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee salaries");
            return null;
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