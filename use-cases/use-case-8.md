# USE CASE: 8 Delete an Employee's Details

## CHARACTERISTIC INFORMATION

### Goal in Context
As an *HR advisor*, I want *to delete an employee's details* so that *the company is compliant with data retention legislation.*

### Scope
Company.

### Level
Primary task.

### Preconditions
Employee is no longer employed, and data retention requirements are fulfilled.

### Success End Condition
The employee's details are successfully deleted from the system.

### Failed End Condition
The employee's details are not deleted.

### Primary Actor
HR Advisor.

### Trigger
Employee leaves the company, and retention period expires.

## MAIN SUCCESS SCENARIO

1. HR advisor confirms the employee is no longer with the company.
2. HR advisor checks that the data retention period has been met.
3. HR advisor accesses the system and deletes the employee’s details.
4. System confirms the successful deletion of records.

## EXTENSIONS

1. **Employee is still within the retention period**:
    1. HR advisor sets a reminder to delete details after the retention period ends.

## SUB-VARIATIONS
None.

## SCHEDULE
**DUE DATE**: Release 1.0