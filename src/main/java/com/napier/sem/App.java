package com.napier.sem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDate;

import hello.Greeting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App  {

    public static Connection con = null;



    public static void main(String[] args) {
        // Create new Application and connect to database
        App app = new App();

        if (args.length < 1) {
            connect("localhost:33060", 5000);
        } else {
            connect(args[0], Integer.parseInt(args[1]));
        }

//        ArrayList<Employee> employees = app.getAllEmployeeSalaries();
//        app.outputEmployees(employees, "ManagerSalaries.md");

        SpringApplication.run(App.class, args);


        // Disconnect from database
        //disconnect();
    }

    /** Connect to Database driver */
    public static void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        boolean shouldWait = false;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                if (shouldWait) {
                    // Wait a bit for db to start
                    Thread.sleep(delay);
                }

                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());

                // Let's wait before attempting to reconnect
                shouldWait = true;
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /** Disconnect SQL driver */
    public static void disconnect(){
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
        try {
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



    @RequestMapping("employee")
    public Employee REST_getEmployee(@RequestParam(value = "id") String ID) {
        return getEmployee_simple(Integer.parseInt(ID));
    }
    // Get all salaries
    @RequestMapping("salaries")
    public ArrayList<Employee> REST_getAllSalaries() {
        return getAllEmployeeSalaries();
    }

    // Get salaries by title
    @RequestMapping("salaries_title")
    public ArrayList<Employee> REST_getSalariesByTitle(@RequestParam(value = "title") String title) {
        return getEmployeesByRole(title);
    }

    // Get department by department name
    @RequestMapping("department")
    public ArrayList<Employee> REST_getDepartment(@RequestParam(value = "dept") String dept) {
        return getEmployeesByDepartment(dept);
    }

    // Get salaries by department
    @RequestMapping("salaries_department")
    public ArrayList<Employee> REST_getSalariesByDepartment(@RequestParam(value = "dept") String dept) {
        var department = getDepartment(dept);
        return getSalariesByDepartment(department);
    }





    public Employee getEmployee(int ID) {
        Employee emp = null; // Initialize employee as null

        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();

            // Create string for SQL statement
            String strSelect = "SELECT emp_no, birth_date, first_name, last_name, gender, hire_date FROM employees WHERE emp_no = " + ID;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Check if a result is returned
            if (rset.next()) {
                emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.birth_date = rset.getString("birth_date"); // Assuming birth_date is stored as a string
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.gender = rset.getString("gender");
                emp.hire_date = rset.getString("hire_date"); // Assuming hire_date is stored as a string
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve employee.");
        }

        return emp; // Return the employee object or null if not found
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


    public void addEmployee(Employee emp) {
        // Get today's date
        String today = LocalDate.now().toString(); // Format: YYYY-MM-DD

        // Check birth_date and set to today's date if empty or "-"
        String birthDate = emp.birth_date != null && !emp.birth_date.isEmpty() && !emp.birth_date.equals("-")
                ? emp.birth_date
                : today;

        // Check hire_date and set to today's date if empty or "-"
        String hireDate = emp.hire_date != null && !emp.hire_date.isEmpty() && !emp.hire_date.equals("-")
                ? emp.hire_date
                : today;

        String sql = "INSERT INTO employees (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            // Set emp_no, which is mandatory
            pstmt.setInt(1, emp.emp_no);

            // Set birth_date
            pstmt.setDate(2, java.sql.Date.valueOf(birthDate));

            // Set first_name, which is mandatory
            pstmt.setString(3, emp.first_name);

            // Set last_name, which is mandatory
            pstmt.setString(4, emp.last_name);

            // Set gender, or null if not provided
            pstmt.setString(5, emp.gender);


            // Set hire_date
            pstmt.setDate(6, java.sql.Date.valueOf(hireDate));

            // Execute the insert
            pstmt.executeUpdate();
            System.out.println("Employee added successfully.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees) {
            if(emp == null) continue;

            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }


    public void displayEmployee(Employee emp) {
        if (emp != null) {
            if(emp.emp_no == 0 || (emp.first_name.isEmpty() && emp.last_name.isEmpty())) {
                System.out.println("Invalid Employee object, empty employee record");
            }else{
                System.out.println(
                        "ID: " + emp.emp_no + " "
                                + "Name: " + emp.first_name + " "
                                + emp.last_name + "\n"
                                + "Title: " + emp.title + "\n"
                                + "Salary: " + emp.salary + "\n"
                                + (emp.dept != null ? "Department: " + emp.dept.dept_name : "") + "\n"
                                + (emp.manager != null ? "Manager: " + emp.manager.emp_no : "")
                );
            }
        }else{
            System.out.println("Null employee, nothing to display.");
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


    /**
     * Outputs to Markdown
     *
     * @param employees
     */
    public void outputEmployees(ArrayList<Employee> employees, String filename) {
        // Check employees is not null
        if (employees == null) {
            System.out.println("No employees");
            return;
        }

        StringBuilder sb = new StringBuilder();
        // Print header
        sb.append("| Emp No | First Name | Last Name | Title | Salary | Department |                    Manager |\r\n");
        sb.append("| --- | --- | --- | --- | --- | --- | --- |\r\n");
        // Loop over all employees in the list
        for (Employee emp : employees) {
            if (emp == null) continue;
            sb.append("| " + emp.emp_no + " | " +
                    emp.first_name + " | " + emp.last_name + " | " +
                    emp.title + " | " + emp.salary + " | "
                     + emp.manager + " |\r\n");
        }
        try {
            new File("./reports/").mkdir();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./reports/" + filename)));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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