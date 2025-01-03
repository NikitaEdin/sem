package com.napier.sem;

/**
 * Represents an employee
 */
public class Employee
{
    /**
     * Employee number
     */
    public int emp_no;

    /**
     * Employee's first name
     */
    public String first_name;

    /**
     * Employee's last name
     */
    public String last_name = "";
    public String birth_date = "";
    public String gender = "M";
    public String hire_date = "";

    /**
     * Employee's job title
     */
    public String title = "";

    /**
     * Employee's salary
     */
    public int salary = 0;

    /**
     * Employee's current department
     */
    public Department dept;

    /**
     * Employee's manager
     */
    public Employee manager;
}