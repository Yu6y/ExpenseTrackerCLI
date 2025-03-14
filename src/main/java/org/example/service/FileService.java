package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.exception.ExpenseErrorException;
import org.example.model.Expense;
import org.example.model.ExpenseData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FileService{
    private Path path = Paths.get("./data.json");
    private ExpenseData expenseData;
    private ObjectMapper mapper;

    public FileService() throws ExpenseErrorException{
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        expenseData = new ExpenseData();
        checkFile();
    }

    private void checkFile() throws ExpenseErrorException{
        if(!Files.exists(path)){
            try{
                Files.createFile(path);
            }catch(IOException e){
                throw new ExpenseErrorException("An error occurred while creating file.");
            }
        }
        else
            loadData();
    }

    private void loadData() throws ExpenseErrorException{
        try {
            if(Files.size(path) > 0)
                expenseData = mapper.readValue(new File(path.getFileName().toString()), ExpenseData.class);
        }catch(IOException e){
            System.out.println(e.getMessage());
            throw new ExpenseErrorException("Cannot read expenses from file.");
        }
    }

    public int getAvailableId() throws ExpenseErrorException{
        Set<Integer> unavailableIds = new HashSet<>();

        for(Expense task: expenseData.getExpensesList())
            unavailableIds.add(task.getId());

        for(int i = 0; i != Integer.MAX_VALUE; i++){
            if(!unavailableIds.contains(i))
                return i;
        }

        throw new ExpenseErrorException("Expenses list is full.");
    }

    public void addExpense(Expense expense) throws ExpenseErrorException{
        expenseData.addToExpenseToList(expense);
        saveData();
        System.out.println("Expense added successfully (ID: " + expense.getId() + ")");

        BigDecimal monthLimit = expenseData.getMonthLimit()[expense.getDate().getMonthValue() - 1];
        System.out.println("Month limit: "  + monthLimit);
        BigDecimal cos = expenseData.getExpensesList().stream().filter(e ->
                        e.getDate().getMonthValue() == expense.getDate().getMonthValue())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Sum: " + cos);
        int limit =cos.compareTo(monthLimit);

        if(monthLimit.compareTo(BigDecimal.ZERO) > 0 && (limit == 0 || limit > 0))
            System.out.println("Warning: Expense limit exceeded.");
    }

    private void saveData() throws ExpenseErrorException{
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.US_ASCII,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
            String out = mapper.writeValueAsString(expenseData);
            writer.write(out);
        }catch(IOException e){
            System.out.println(e.getMessage());
            throw new ExpenseErrorException("Cannot save data.");
        }
    }

    public List<Expense> getExpensesList(){
        return expenseData.getExpensesList();
    }

    public Optional<Expense> getExpense(int id){
        return expenseData.getExpensesList().stream().filter(e -> e.getId() == id).findFirst();
    }

    public void updateExpense(Expense expense) throws ExpenseErrorException{
        try {
            List<Expense> list = expenseData.getExpensesList();
            int listIndex = list.indexOf(list.stream().filter(e ->
                    e.getId() == expense.getId()).findFirst().get());

            expenseData.updateExpenseToList(listIndex, expense);
        }catch(Exception e){
            throw new ExpenseErrorException("An error occurred while updating expense.");
        }

        saveData();

        System.out.println("Expense updated successfully (ID: " + expense.getId() + ")");
    }

    public void deleteExpense(int id) throws ExpenseErrorException{
        expenseData.removeExpense(getExpense(id).orElseThrow(() ->
                new ExpenseErrorException("Expense with given id does not exist.")));

        saveData();

        System.out.println("Expense deleted successfully (ID: " + id + ")");
    }

    public void exportData() throws ExpenseErrorException {
        Path exportPath = Paths.get("export.csv");

        if (Files.exists(exportPath)) {
            try {
                Files.delete(exportPath);
            } catch (IOException e) {
                throw new ExpenseErrorException("An error occurred while deleting the file.");
            }
        }

        try {
            Files.createFile(exportPath);
            try (BufferedWriter writer = Files.newBufferedWriter(exportPath, StandardCharsets.US_ASCII,
                    StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("ID,Date,Description,Amount,Category\n");

                for (Expense expense : expenseData.getExpensesList()) {
                    writer.write(expense.toString() + "\n");
                }
            }
        } catch (IOException e) {
            throw new ExpenseErrorException("An error occurred while creating the file.");
        }

        System.out.println("Exported data available at: " + exportPath.toAbsolutePath());
    }

    public void setLimit(int month, BigDecimal limit) throws ExpenseErrorException {
        expenseData.setMonthLimit(month, limit);

        saveData();
        System.out.println("Limit added successfully.");
        System.out.println("New limits: ");
        for(int i = 0; i < 12; i++){
            System.out.println(Month.of(i + 1) + ": " + expenseData.getMonthLimit()[i]);
        }
    }

    public void clearLimits(int month)throws ExpenseErrorException{
        if(month > 0)
            expenseData.setMonthLimit(month, BigDecimal.ZERO);
        else
            for(int i = 1; i <= 12; i++)
                expenseData.setMonthLimit(i, BigDecimal.ZERO);

        saveData();
        System.out.println("Limit clear successfully.");
        System.out.println("New limits: ");
        for(int i = 0; i < 12; i++){
            System.out.println(Month.of(i + 1) + ": " + expenseData.getMonthLimit()[i]);
        }
    }

    public void showLimits(){
        System.out.println("Current limits: ");
        for(int i = 0; i < 12; i++){
            System.out.println(Month.of(i + 1) + ": " + expenseData.getMonthLimit()[i]);
        }
    }
}
