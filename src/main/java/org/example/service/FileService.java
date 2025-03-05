package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.ExpenseErrorException;
import org.example.model.Expense;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileService{
    private Path path = Paths.get("./data.json");
    private List<Expense> expensesList;
    private ObjectMapper mapper;

    public FileService() throws ExpenseErrorException{
        mapper = new ObjectMapper();
        this.expensesList = new ArrayList<>();
        checkFile();
    }

    private void checkFile() throws ExpenseErrorException{
        if(!Files.exists(path)){
            try{
                Files.createFile(path);
                try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("US-ASCII"), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    writer.write("[]");
                }
            }catch(IOException e){
                throw new ExpenseErrorException("An error occurred while creating file.");
            }
        }
        else
            loadData();
    }

    private void loadData() throws ExpenseErrorException{
        try {
            expensesList = mapper.readValue(new File("data.json"), new TypeReference<List<Expense>>() {});
        }catch(IOException e){
            throw new ExpenseErrorException("Cannot read expenses from file.");
        }
    }

    public int getAvailableId() throws ExpenseErrorException{
        Set<Integer> unavailableIds = new HashSet<>();

        for(Expense task: expensesList)
            unavailableIds.add(task.getId());

        for(int i = 0; i != Integer.MAX_VALUE; i++){
            if(!unavailableIds.contains(i))
                return i;
        }

        throw new ExpenseErrorException("Expenses list is full.");
    }

    public void addExpense(Expense expense) throws ExpenseErrorException{
        expensesList.add(expense);
        saveData();
        System.out.println("Expense added successfully (ID: " + expense.getId() + " )");
    }

    private void saveData() throws ExpenseErrorException{
        System.out.println("lspefbpgbds");
        try(BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("US-ASCII"), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
            String out = mapper.writeValueAsString(expensesList);
            System.out.println("Out " + out);
            writer.write(out);

        }catch(IOException e){
            throw new ExpenseErrorException("Cannot save data.");
        }
    }
}
