package org.example.service;

import org.example.exception.ExpenseErrorException;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExpenseService {
    private FileService fileService;

    public ExpenseService() throws ExpenseErrorException{
        try {
            this.fileService = new FileService();
        }catch(ExpenseErrorException e){
            throw new ExpenseErrorException(e.getMessage());
        }
    }

    public void addExpense(String[] data) throws ExpenseErrorException{
        Expense newExpense = new Expense();

        newExpense.setCategory(ExpenseCategory.OTHER);
        newExpense.setDate(LocalDateTime.now());
        for(int i = 0; i < data.length; i++){
            switch (data[i]) {
                case "--description" -> {
                    i++;
                    newExpense.setDescription(data[i]);
                }
                case "--amount" -> {
                    i++;
                    try {
                        BigDecimal amount = new BigDecimal(data[i]);
                        if(amount.compareTo(BigDecimal.ZERO) < 0)
                            throw new NumberFormatException();
                        newExpense.setAmount(amount);
                    } catch (NumberFormatException e) {
                        throw new ExpenseErrorException("Bad format of amount.");
                    }
                }
                case "--category" -> {
                    i++;
                    ExpenseCategory category = ExpenseCategory.getCategory(data[i]);
                    if (category == null) throw new ExpenseErrorException("Incorrect category.");
                    newExpense.setCategory(category);
                }
                case "--date" -> {
                    i++;
                    if(checkValidDate(data[i]))
                        newExpense.setDate(LocalDateTime.parse(data[i], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    else throw new ExpenseErrorException("Bad format of date.");
                }
                default -> throw new ExpenseErrorException("Invalid parameters.");
            }
        }
        newExpense.setId(fileService.getAvailableId());

        fileService.addExpense(newExpense);
    }

    private boolean checkValidDate(String date){
        return date.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\\s(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$");
    }

    public void listExpenses(String[] params) throws ExpenseErrorException{
        if(params.length != 0 && ExpenseCategory.getCategory(params[1]) == null)
            throw new ExpenseErrorException("Category not found.");
        List<String[]> expenses = new ArrayList<>(fileService.getExpensesList().stream()
                .filter(e -> params.length <= 1  || (ExpenseCategory.getCategory(params[1]) != null &&
                        e.getCategory().equals(ExpenseCategory.getCategory(params[1]))))
                .map(Expense::getExpenseStringArr).toList());

        if(expenses.isEmpty()) {
            System.out.println("No expenses found");
            return;
        }

        int[] widths = new int[expenses.get(0).length];
        expenses.add(0, new String[]{"ID", "Date", "Description", "Amount", "Category"});

        for(String[] expense : expenses){
            for(int i = 0; i < expense.length; i++){
                widths[i] = Math.max(widths[i], expense[i].length());
            }
        }

        for(String[] expense : expenses){
            for(int i = 0; i < expense.length; i++){
                System.out.printf("%-" + (widths[i] + 2) + "s", expense[i]);
            }
            System.out.println();
        }
    }

    public void updateExpense(String[] data) throws ExpenseErrorException{
        int id = 0;
        for(int i = 0; i < data.length - 1; i++){
            if(data[i].equals("--id")){
                try {
                    id = Integer.parseInt(data[++i]);
                }catch(NumberFormatException e){
                    throw new ExpenseErrorException("Incorrect id.");
                }
            }
        }

        Expense expense = fileService.getExpense(id).orElseThrow(
                () -> new ExpenseErrorException("No expense with given id."));

        for(int i = 0; i < data.length; i++){
            switch(data[i]){
                case "--description" -> {
                    i++;
                    expense.setDescription(data[i]);
                }
                case "--amount" -> {
                    i++;
                    try{
                        BigDecimal amount = new BigDecimal(data[i]);
                        if(amount.compareTo(BigDecimal.ZERO) < 0)
                            throw new NumberFormatException();
                        expense.setAmount(amount);
                    } catch (NumberFormatException e) {
                        throw new ExpenseErrorException("Bad format of amount.");
                    }
                }
                case "--category" -> {
                    i++;
                    ExpenseCategory category = ExpenseCategory.getCategory(data[i]);
                    if(category == null) throw new ExpenseErrorException("Incorrect category.");
                    expense.setCategory(category);
                }
                case "--date" -> {
                    i++;
                    if(!checkValidDate(data[i]))
                        throw new ExpenseErrorException("Bad format of date.");
                    expense.setDate(LocalDateTime.parse(data[i], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
                case "--id" -> { i++; }
                default -> throw new ExpenseErrorException("Invalid parameters.");
            }
        }

        fileService.updateExpense(expense);
    }

    public void deleteExpense(String id) throws ExpenseErrorException{
        try{
            fileService.deleteExpense(Integer.parseInt(id));
        }catch(NumberFormatException e){
            throw new ExpenseErrorException("Incorrect id.");
        }
    }

    public void showSummary(String[] filters) throws ExpenseErrorException{
        List<Expense> expenses = fileService.getExpensesList();
        BigDecimal sum = new BigDecimal(0);

        if(filters.length == 0)
            sum = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        else
            try {
                switch (filters[0]) {
                    case "--month" -> {
                        int month = Integer.parseInt(filters[1]);
                        if(month < 1 || month > 12)
                            throw new NumberFormatException();
                        sum = expenses.stream()
                            .filter(e -> e.getDate().getMonthValue() == month
                                    && e.getDate().getYear() == LocalDateTime.now().getYear())
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    case "--category" -> {
                        ExpenseCategory category = ExpenseCategory.getCategory(filters[1]);
                        if(category == null)
                            throw new ExpenseErrorException("Incorrect category.");

                        sum = expenses.stream()
                            .filter(e -> e.getCategory() == category)
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                }
            }catch(NumberFormatException e){
                throw new ExpenseErrorException("Invalid arguments.");
            }

        System.out.println("Total expenses: " + sum);
    }

    public void exportToCSV() throws ExpenseErrorException{
        try {
            fileService.exportData();
        } catch(ExpenseErrorException e){
          throw new ExpenseErrorException(e.getMessage());
        }
    }

    public void setLimit(String[] params) throws ExpenseErrorException{
        int month = 0;
        BigDecimal limit = BigDecimal.ZERO;
        try {
            month = Integer.parseInt(params[1]);
            limit = new BigDecimal(params[2]);

            if(month < 1 || month > 12 || limit.compareTo(BigDecimal.ZERO) < 0)
                throw new ExpenseErrorException("Invalid month or the limit.");
        }catch(NumberFormatException e){
            throw new ExpenseErrorException("Invalid parameters.");
        }

        fileService.setLimit(month, limit);
    }

    public void removeLimit(String[] params) throws ExpenseErrorException{
        if(params.length == 0)
            fileService.clearLimits(0);
        else
            try {
                int month = Integer.parseInt(params[1]);

                if(month < 1 || month > 12)
                    throw new ExpenseErrorException("Invalid month.");

                fileService.clearLimits(month);
            }catch(NumberFormatException e){
                throw new ExpenseErrorException("Invalid parameters.");
            }
    }

    public void showLimits(){
        fileService.showLimits();
    }
}
