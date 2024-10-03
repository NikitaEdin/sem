# USE CASE: 1 Produce a Report on the Salary of All Employees

## CHARACTERISTIC INFORMATION

### Goal in Context
As an *HR advisor*, I want *to produce a report on the salary of all employees* so that *I can support financial reporting of the organisation.*

### Scope
Company.

### Level
Primary task.

### Preconditions
HR database contains up-to-date employee salary data.

### Success End Condition
A salary report for all employees is available for HR to provide to finance.

### Failed End Condition
No report is produced.

### Primary Actor
HR Advisor.

### Trigger
A request for salary information is sent to HR by finance.

## MAIN SUCCESS SCENARIO

1. Finance requests salary information for all employees.
2. HR advisor accesses the system to retrieve salary data for all employees.
3. HR advisor generates the report from the database.
4. HR advisor provides the report to finance.

## EXTENSIONS

1. **Database is unavailable**:
    1. HR advisor informs finance about the system issue and delays the report.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0