package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    @JsonIgnore
    public String[] getExpenseStringArr(){
        return this.toString().split(",");
    }

    @Override
    public String toString(){
        return String.format("%s,%s,%s,%s,%s", id, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                description, amount, category);
    }
}
