# USE CASE: 2 Produce a Report on the Salary of Employees in a Department

## CHARACTERISTIC INFORMATION

### Goal in Context
As an *HR advisor*, I want *to produce a report on the salary of employees in a department* so that *I can support financial reporting of the organisation.*

### Scope
Company.

### Level
Primary task.

### Preconditions
We know the department. Database contains current salary data of employees.

### Success End Condition
A report of employee salaries in a department is available for HR to provide to finance.

### Failed End Condition
No report is produced.

### Primary Actor
HR Advisor.

### Trigger
A request for salary information by department is sent to HR by finance.

## MAIN SUCCESS SCENARIO

1. Finance requests salary information for a specific department.
2. HR advisor identifies the department.
3. HR advisor extracts current salary information of employees within that department.
4. HR advisor generates the report.
5. HR advisor provides the report to finance.

## EXTENSIONS

1. **Department does not exist**:
    1. HR advisor informs finance that no such department exists.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0