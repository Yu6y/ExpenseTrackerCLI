package org.example.service;

import org.example.exception.ExpenseErrorException;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        if(!Set.of(data).containsAll(Set.of("--description", "--amount")))
            throw new ExpenseErrorException("Incorrect arguments");

        newExpense.setCategory(ExpenseCategory.OTHER);
        for(int i = 0; i < data.length; i++){
            switch (data[i]) {
                case "--description" -> {
                    i++;
                    newExpense.setDescription(data[i]);
                }
                case "--amount" -> {
                    i++;
                    try {
                        newExpense.setAmount(new BigDecimal(data[i]));
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
                default -> throw new ExpenseErrorException("Invalid parameters.");
            }
        }
        newExpense.setDate(LocalDateTime.now());
        newExpense.setId(fileService.getAvailableId());

        fileService.addExpense(newExpense);
    }


}
