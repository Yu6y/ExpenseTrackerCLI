package org.example.controller;

import org.example.exception.ExpenseErrorException;
import org.example.service.ExpenseService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    public void handleCommand(String[] data) {
        String command = data[0];
        String params[] = Arrays.copyOfRange(data, 1, data.length);

        try {
            switch (command) {
                case "add":
                    if (isValidAddParams(params))
                        expenseService.addExpense(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "list":
                    if (isValidListParams(params))
                        expenseService.listExpenses(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "update":
                    if (isValidUpdateParams(params))
                        expenseService.updateExpense(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "delete":
                    if (isValidDeleteParams(params))
                        expenseService.deleteExpense(params[1]);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "summary":
                    if (isValidSummaryParams(params))
                        expenseService.showSummary(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "export":
                    expenseService.exportToCSV();
                    break;
                case "setLimit":
                    if (isValidSetLimitParams(params))
                        expenseService.setLimit(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "removeLimit":
                    if (isValidRemoveLimitParams(params))
                        expenseService.removeLimit(params);
                    else
                        System.out.println("Invalid parameters!");
                    break;
                case "showLimits":
                    expenseService.showLimits();
                    break;
                case "help":
                    printHelp();
                    break;
                default:
                    System.out.println("Command not found. Use 'help' parameter to list available commands.");
            }
        } catch (ExpenseErrorException e) {
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
                case("--date"):
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

    private void printHelp(){
        System.out.println("Usage: ExpenseTracker command [params]");
        System.out.println("Available commands:");
        System.out.println("--> add --description 'description' --amount amount [--category category --date 'yyyy-MM-dd HH:mm']. " +
                "Available categories: food, entertainment, bills, unforeseen, other");
        System.out.println("--> list [--category category]");
        System.out.println("--> update --id id [--description description] [--amount amount] [--category category]" +
                "[--date yyyy-MM-dd]. At least one param is required");
        System.out.println("--> delete --id id.");
        System.out.println("--> summary [--month month] [--category category]. If the --month flag is used, " +
                "the program returns the summary for the specified month of the current year.");
        System.out.println("--> export. Export data to CSV file.");
        System.out.println("--> setLimit --month numOfMonth limit");
        System.out.println("--> removeLimit [--month numOfMonth");
        System.out.println("--> showLimits");
    }

    private boolean isValidAddParams(String[] params) {
        List<String> paramList = List.of(params);
        return params.length > 0 && params.length % 2 == 0 && checkParams(params) &&
                paramList.contains("--description") && paramList.contains("--amount");
    }

    private boolean isValidListParams(String[] params) {
        return params.length == 0 || (params.length == 2 && List.of(params).contains("--category"));
    }

    private boolean isValidUpdateParams(String[] params) {
        return params.length >= 4 && List.of(params).contains("--id") && checkParams(params);
    }

    private boolean isValidDeleteParams(String[] params) {
        return params.length == 2 && List.of(params).contains("--id");
    }

    private boolean isValidSummaryParams(String[] params) {
        List<String> paramList = List.of(params);
        return params.length == 0 || (params.length == 2 && (paramList.contains("--month") ||
                paramList.contains("--category")));
    }

    private boolean isValidSetLimitParams(String[] params) {
        return params.length == 3 && "--month".equals(params[0]);
    }

    private boolean isValidRemoveLimitParams(String[] params) {
        return params.length == 0 || (params.length == 2 && List.of(params).contains("--month"));
    }

}