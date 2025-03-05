package org.example.model;

public enum ExpenseCategory {
    FOOD("food"),
    ENT("entertainment"),
    BILLS("bills"),
    UNF("unforeseen"),
    OTHER("other");


    private final String category;

    ExpenseCategory(String category){
        this.category = category;
    }

    public String getValue(){
        return category;
    }

    public static ExpenseCategory getCategory(String cat){
        for(ExpenseCategory categoryI: ExpenseCategory.values())
            if(categoryI.getValue().equals(cat))
                return categoryI;

        return null;
    }
}
