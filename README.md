# ExpenseTrackerCLI - Expense Tracker CLI in Java for Managing Expenses

This is simple command-line application to track expenses.

## Features

- **Add expense**: Add new expense with description, date, amount and category.
- **Update expense**: Modify an existing expense.
- **Delete expense**: Remove an expense from the list.
- **List expenses**: View all expenses or filter them by category.
- **Show summary**: Show amount sum of all expenses or filtered by month or category.
- **Export data to csv**: Export expenses to .csv file.
- **Set limit for each month**: Set limit for specified month.
- **Show limits**: Show limits for each month.
- **Remove limit**: Remove limit for each or spiecified month.

## Usage 

mvn clean compile
mvn -q exec:java -Dexec.args="args"

### Commands

add --description 'expense desc' --amount amount [--category (food, entertainment, bills, unforeseen, other) --date 'yyyy-MM-dd HH:mm'] - to add new expense

update --id ID  "new nam [--description description] [--amount amount] [--category category] [--date yyyy-MM-dd]. At least one param is required" - to update expense with given ID 

delete --id ID - to delete expense with given ID

list [--category category] - to list all expenses or filter by category

summary [--month month] [--category category] - to show summary for all expenses or filtered by category or month of current year

setLimit --month limit - to set expenses limit for specified month

removeLimit [--month month] - to clear limits for each or specified month

showLimits - to show limits for each month

export - to export data to .csv

### More Info

Project from [roadmap.sh](https://roadmap.sh/). Project details: [details](https://roadmap.sh/projects/expense-tracker).
