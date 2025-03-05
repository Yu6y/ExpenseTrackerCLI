package org.example.controller;

import org.example.exception.ExpenseErrorException;
import org.example.service.ExpenseService;

import java.util.Arrays;

public class CommandHandler {
    private ExpenseService expenseService;

    public CommandHandler(){
        try {
            this.expenseService = new ExpenseService();
        }catch(ExpenseErrorException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void handleCommand(String[] data){
        String command = data[0];
        String params[] = Arrays.copyOfRange(data, 1, data.length);
        try {
            switch (command) {
                case ("add"):
                    if ((params.length == 4 || params.length == 6) && checkParams(params))
                        expenseService.addExpense(params);
            }
        }catch(ExpenseErrorException e){
            System.out.println(e.getMessage());
        }
    }

    private boolean checkParams(String[] params){
        for(int i = 0; i < params.length; i+= 2){
            switch(params[i]){
                case("--description"):
                case("--amount"):
                case("--category"):
                case("--id"):
                case("--month"):
                    continue;
                default:
                    return false;
            }
        }

        for(int i = 1; i < params.length; i+= 2){
            if(params[i] == null || params[i].trim().isEmpty())
                return false;
        }

        return true;
    }
}
