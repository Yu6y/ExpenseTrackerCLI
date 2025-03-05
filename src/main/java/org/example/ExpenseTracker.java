package org.example;

import org.example.controller.CommandHandler;

public class ExpenseTracker {
    public static void main(String[] args) {
        if(args.length == 0)
            System.out.println("Command not found. Use 'help' parameter to list available params.");
        else
            new CommandHandler().handleCommand(args);
    }
}
