package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME, transactions);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        showTitle();

        while (running) {
            System.out.println("  \u001B[32m[D] Add Deposit       \u001B[32m[P] Make Payment");
            System.out.println("  \u001B[32m[L] View Ledger\u001B[0m        \u001B[31mX) Exit\u001B[0m");
            System.out.print("\n  What can I do for you today? > ");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    //Creates new file if one doesn't yet exist
    public static void loadTransactions(String fileName, ArrayList<Transaction> transactions) {

        try {
            File file = new File(fileName);

            if (file.createNewFile()) {
                System.out.println("Transaction file created successfully!");
            }

        } catch (IOException e) {
            System.out.println("Error creating transaction file: " + e.getMessage());

        }

        //Reads csv File line by line till empty, and breaks each part into there respected data types
        //Calls contructor Transactions and creats object with all 5 values and puts into arraylist
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                String description = parts[2];
                String vendor = parts[3];
                double price = Double.parseDouble(parts[4]);

                Transaction payment = new Transaction(date, time, description, vendor, price);

                transactions.add(payment);
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }



    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */
    //Prompts user for information
    private static void addDeposit(Scanner scanner) {
        while (true) {
            try {
          System.out.println("What's todays date (yyyy-MM-dd)");
          LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FMT);
          showProgress("ADD DEPOSIT", 1);
          System.out.println("What time is it? (HH:mm:ss)");
          LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FMT);
          showProgress("ADD DEPOSIT", 2);
          System.out.println("What is the reason for this transaction?");
          String description = scanner.nextLine();
          showProgress("ADD DEPOSIT", 3);
          System.out.println("What is your name?");
          String vendor = scanner.nextLine();
          showProgress("ADD DEPOSIT", 4);
          System.out.println("How much would you like to Deposit");
          double price = scanner.nextDouble();
          showProgress("ADD DEPOSIT", 5);
          scanner.nextLine();

          //Creates a New object whilst also appending it to the csv file

        if (price > 0) {
            Transaction deposit = new Transaction(date, time, description, vendor, price
            );
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {

                writer.newLine();
                writer.write(deposit.toString());
                writer.newLine();

            } catch (IOException e) {
                System.out.println("Error saving deposit: " + e.getMessage());
            }

            //Adds New object to Arraylist
            transactions.add(deposit);
            System.out.println(price + "$ Has Been Depsited to your account!");
            showProgress("ADD DEPOSIT", 6);

            break; // exits while loup
        } else {
            System.out.println("Payment must be greater than 0");
        }
    } catch (DateTimeParseException e) {
        System.out.println("Incorrect date or time. AGAIN.");

    } catch (InputMismatchException e) {
        System.out.println("Invalid amount. Enter a number.");
        scanner.nextLine();
    }
}
    }
    //Promts for user information
    private static void addPayment(Scanner scanner) {
        while (true) {
            try {
                System.out.println("What's todays date (yyyy-MM-dd)");
                LocalDate date = LocalDate.parse(scanner.nextLine(), DATE_FMT);
                showProgress("ADD PAYMENT", 1);
                System.out.println("What time is it? (HH:mm:ss)");
                LocalTime time = LocalTime.parse(scanner.nextLine(), TIME_FMT);
                showProgress("ADD PAYMENT", 2);
                System.out.println("What is the reason for this transaction?");
                String description = scanner.nextLine();
                showProgress("ADD PAYMENT", 3);
                System.out.println("What is your name?");
                String vendor = scanner.nextLine();
                showProgress("ADD PAYMENT", 4);
                System.out.println("How much Will you be paying today?");
                double price = scanner.nextDouble();
                showProgress("ADD PAYMENT", 5);
                scanner.nextLine();

                //Creates a New object whilst also appending it to the csv file
                //Turn positve price enterted to Negative

                if (price > 0) {
                    price = -price;
                    Transaction payment = new Transaction(date, time, description, vendor, price
                    );
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                        writer.newLine();
                        writer.write(payment.toString());
                        writer.newLine();

                    } catch (IOException e) {
                        System.out.println("Error saving Payment: " + e.getMessage());
                        return;
                    }

                    //Adds New object to Arraylist
                    transactions.add(payment);
                    System.out.println(price + "$ Was payed Today!");
                    showProgress("ADD PAYMENT", 6);

                    break;
                } else {
                    System.out.println("Payment must be greater than 0");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Incorrect date or time. AGAIN.");
                return;
            } catch (InputMismatchException e) {
                System.out.println("Invalid amount. Enter a number.");
                scanner.nextLine();
            }
        }
    }
    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        //Compares the Transactions by Date then Time if nescarry, then reverses list
        transactions.sort(

                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    //Displays Full transactions List
    private static void displayLedger() {
        //Takes Transaction class so it spefices the type/ object
        for (Transaction transaction : transactions) {
            System.out.println(transaction);

        }
    }

    private static void displayDeposits() {

        /*for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getAmount() > 0) {
                System.out.println(transactions.get(i));*/
        for (Transaction deposit : transactions) {
            if (deposit.getAmount() > 0) {
                System.out.println(deposit);
            }
        }
    }

    private static void displayPayments() {

        /*for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getAmount() < 0) {
                System.out.println(transactions.get(i));*/
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println(transaction);
            }
        }
            }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            //Sort by date and time
            transactions.sort(
                    Comparator.comparing(Transaction::getDate)
                            .thenComparing(Transaction::getTime)
                            .reversed()
            );

            switch (input) {
                case "1" -> {
                    //Month To DATE
                    LocalDate end = LocalDate.now(); // End of Date is "Now"
                    LocalDate start = end.withDayOfMonth(1); // Starts with first day of current month
                    filterTransactionsByDate(start, end);//Grabs everything in the range
                }
                case "2" -> {
                    //Previous Month
                    LocalDate today = LocalDate.now();
                    LocalDate previousMonth = today.minusMonths(1);//Goes backwards exactly 1 month
                    LocalDate start = previousMonth.withDayOfMonth(1);//Now start that month on the 1st
                    LocalDate end = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth());//Takes full length of that previous month
                    filterTransactionsByDate(start, end);
                }
                case "3" -> {
                    //Year to Date
                    LocalDate end = LocalDate.now();
                    LocalDate start = end.withDayOfYear(1);
                    filterTransactionsByDate(start, end);
                }
                case "4" -> {
                    //Previous Year
                    LocalDate previousYear = LocalDate.now().minusYears(1);
                    LocalDate start = previousYear.withDayOfYear(1);
                    LocalDate end = previousYear.withDayOfYear(previousYear.lengthOfYear());
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.println("Name of Vendor: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }
    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    //Grabs paramenters for start and end date selected and prints everyting inbetween those 2 dates
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {

        //Grabs each transaction individaully by date
        /*for (int i = 0; i < transactions.size(); i++) {
            LocalDate transactionDate = transactions.get(i).getDate();*/
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = (transaction.getDate());

            if (!transactionDate.isBefore(start) && !transactionDate.isAfter(end)) {
                System.out.println(transaction);
            }
        }
    }

    //Compares Entered vendor to vendors on transactions list
    private static void filterTransactionsByVendor(String vendor) {

        boolean vendorFound = false;

      //  for (int i = 0; i < transactions.size(); i++) {
        for (Transaction transaction : transactions) {
            if (vendor.equalsIgnoreCase(transaction.getVendor())) {
                System.out.println(transaction);
                vendorFound = true;
            }
        }
            if (!vendorFound){
                System.out.println("No Vendors by that name.");
            }
        }



    private static void customSearch(Scanner scanner) {

        boolean running = true;
        while (running) {
            System.out.println("Search by:");
            System.out.println("1) Date Range");
            System.out.println("2) Description");
            System.out.println("3) Vendor");
            System.out.println("4) Exact amount");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "1" -> {
                    System.out.println("Enter start date: (yyyy-MM-dd)");
                    LocalDate start = LocalDate.parse(scanner.nextLine(), DATE_FMT);
                    System.out.println("Enter end date:  (yyyy-MM-dd)");
                    LocalDate end = LocalDate.parse(scanner.nextLine(), DATE_FMT);


                    if (start.isAfter(end)) {
                        System.out.println("Start date cannot be after end date.");
                    } else {
                        filterTransactionsByDate(start, end);
                    }
                }
                case "2" -> {
                    System.out.println("Enter payment description");
                    String description = scanner.nextLine();
                    filterTransactionsByDescription(description);
                }
                case "3" -> {
                    System.out.println("Name of Vendor: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "4" -> {
                    try {
                        System.out.println("Enter Exact Amount: ");
                        double amount = scanner.nextDouble();
                        filterTransactionsByAmount(amount);
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid amount. Please enter a number.");
                    }
                    scanner.nextLine();
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid option");

            }
        }
    }

    //Compares Entered desription to descriptions on transactions list
    private static void filterTransactionsByDescription(String description) {

        boolean descriptionFound = false;

        for (Transaction transaction : transactions) {
            //if (description.equalsIgnoreCase(transaction.getDescription()))
            //Allows transaction to display andthing that contains what was entered by the user
                if (transaction.getDescription().toLowerCase().contains(description.toLowerCase())) {
                System.out.println(transaction);
                descriptionFound = true;
            }
        }
        if (!descriptionFound){
            System.out.println("No Descriptions by that name.");
        }
    }
    //Compares entered amount to amount on transaction list
    private static void filterTransactionsByAmount(double amount) {

        boolean amountFound = false;

        for (Transaction transaction : transactions) {
            if (amount == transaction.getAmount()) {
                System.out.println(transaction);
                amountFound = true;
            }
        }
        if (!amountFound){
            System.out.println("No transactions contain that amount.");
        }
    }

    private static void showTitle() {
        System.out.print("\u001B[36m");//Color blue

        System.out.println("""
            
            #      #####  ####    ####  #####  ####       #      #####  #   #  #   #
            #      #      #   #  #      #      #   #      #        #    ##  #  #  #
            #      ####   #   #  #  ##  ####   ####       #        #    # # #  ###
            #      #      #   #  #   #  #      #  #       #        #    #  ##  #  #
            #####  #####  ####    ###   #####  #   #      #####  #####  #   #  #   #
            
                                  YOUR MONEY, IS OUR PRIORITY.
            
            =========================================================================
            """);

        System.out.print("\u001B[0m");
    }
    private static void showProgress(String title, int completedSteps) {
        // Order Steps completed in
        String[] steps = {"Date", "Time", "Description", "Vendor", "Amount", "Saved"};

        System.out.println("|----------------------------|");
        System.out.printf("| %-26s |%n", title);
        //-26s Gives a min width so the box is always same length

        for (int i = 0; i < steps.length; i++) {
            if (i < completedSteps) { // Whatever number is when method called will stop there and only mark those as completed
                System.out.print("\u001B[32m");
                System.out.printf("| [X] %-22s |%n", steps[i]);
                System.out.print("\u001B[0m");
            } else {
                System.out.printf("| [ ] %-22s |%n", steps[i]); //Keeps blank since not filled yet
            }
        }
        System.out.println("+----------------------------+");
    }
}
