package org.example.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExpenseData {
    private List<Expense> expensesList;
    private BigDecimal[] monthLimit;

    public ExpenseData(){
        this.expensesList = new ArrayList<>();
        this.monthLimit = new BigDecimal[12];
        for(int i = 0; i < 12; i++){
            monthLimit[i] = BigDecimal.ZERO;
        }

    }

    public List<Expense> getExpensesList(){
        return expensesList;
    }

    public void addToExpenseToList(Expense expense){
        expensesList.add(expense);
    }

    public void updateExpenseToList(int index, Expense expense){
        expensesList.set(index, expense);
    }

    public void removeExpense(Expense expense){
        expensesList.remove(expense);
    }

    public BigDecimal[] getMonthLimit(){
        return monthLimit;
    }

    public void setMonthLimit(int index, BigDecimal monthLimit){
        this.monthLimit[index - 1] = monthLimit;
    }
}
