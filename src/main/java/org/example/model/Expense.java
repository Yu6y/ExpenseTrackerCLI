package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Expense {
    private int id;
    private LocalDateTime date;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;

    public Expense(){
        this.id = 0;
        this.date = LocalDateTime.now();
        this.description = "";
        this.amount = null;
        this.category = null;
    }

    public Expense(int id, LocalDateTime date, String description, BigDecimal amount, ExpenseCategory category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    @Override
    public String toString(){
        return String.format("%s\t%s\t%s\t$%s\t%s", id, description, date, amount, category);
    }
}
