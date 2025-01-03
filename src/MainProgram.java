import java.io.*;
import java.util.*;

public class MainProgram {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Ask for the .txt file directory path
        System.out.println("Enter the path to the .txt file (quotes will be ignored):");
        String filePath = scanner.nextLine().replace("\"", ""); // Remove quotes
        File logFile = new File(filePath);

        // Step 2: Check if the file exists, create it if not
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                System.out.println("File created at: " + filePath);
            } catch (IOException e) {
                System.err.println("Error creating file. Please check the path and try again.");
                return;
            }
        }

        // Step 3: Read existing log entries
        Map<String, WorkLogEntry> logEntries;
        try {
            logEntries = WorkLogger.readLogFile(logFile);
        } catch (IOException e) {
            System.err.println("Error reading the log file: " + e.getMessage());
            return;
        }

        // Step 4: Display the menu
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Log Work");
            System.out.println("2. Show Total");
            System.out.println("3. Total Earned");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
        
            switch (choice) {
                case "1":
                    logWork(scanner, logFile, logEntries);
                    break;
                case "2":
                    showTotal(logEntries);
                    break;
                case "3":
                    showTotalEarned(logEntries);
                    break;
                case "4":
                    System.out.println("Exiting the program.");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }}
        

    private static void logWork(Scanner scanner, File logFile, Map<String, WorkLogEntry> logEntries) {
        
        String date;

        while (true) {
            System.out.print("Enter the date (MM/DD/YYYY): ");
            date = scanner.nextLine();
        if (date.matches("^(0[1-9]|1[0-2])/([0-2][0-9]|3[01])/\\d{4}$")) {
            break; // Exit loop if valid
        } else {
            System.out.println("Invalid date format. Please enter the date as MM/DD/YYYY.");
        }
        }

        if (logEntries.containsKey(date)) {
            System.out.println("Date " + date + " found. Do you want to update the entry? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                updateLog(scanner, logEntries, date);
                try {
                    WorkLogger.writeLogFile(logFile, logEntries);
                    System.out.println("Work log updated successfully!");
                } catch (IOException e) {
                    System.err.println("Error writing to the log file: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Date " + date + " not found. Do you want to create a new entry? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                createNewLogEntry(scanner, logEntries, date);
                try {
                    WorkLogger.writeLogFile(logFile, logEntries);
                    System.out.println("New work log created successfully!");
                } catch (IOException e) {
                    System.err.println("Error writing to the log file: " + e.getMessage());
                }
            }
        }
    }
    
    private static void updateLog(Scanner scanner, Map<String, WorkLogEntry> logEntries, String date) {
        WorkLogEntry entry = logEntries.get(date);
        while (true) {
            System.out.print("Enter the job type (SOSI or Lion Bridge): ");
            String jobType = scanner.nextLine().toLowerCase();
        
            if (jobType.contains("sosi")) {
                while (true) {
                    System.out.print("Enter additional SOSI worked hours (decimal allowed): ");
                    try {
                        double hours = Double.parseDouble(scanner.nextLine());
                        entry.addSosiHours(hours);
                        break; // Exit the numeric input loop
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid decimal number for hours.");
                    }
                }
                break; // Exit the job type loop after successful input
            } else if (jobType.contains("lion") || jobType.contains("bridge")) {
                while (true) {
                    System.out.print("Enter additional Lion Bridge worked minutes (decimal allowed): ");
                    try {
                        double minutes = Double.parseDouble(scanner.nextLine());
                        entry.addLionBridgeMinutes(minutes);
                        break; // Exit the numeric input loop
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid decimal number for minutes.");
                    }
                }
                break; // Exit the job type loop after successful input
            } else {
                System.out.println("Invalid job type. Please enter 'SOSI' or 'Lion Bridge'.");
            }
        }
    }
        

    private static void createNewLogEntry(Scanner scanner, Map<String, WorkLogEntry> logEntries, String date) {
        WorkLogEntry entry = new WorkLogEntry(date);
    
        while (true) {
            System.out.print("Enter the job type (SOSI or Lion Bridge): ");
            String jobType = scanner.nextLine().toLowerCase();
    
            if (jobType.contains("sosi")) {
                while (true) {
                    System.out.print("Enter SOSI worked hours (decimal allowed): ");
                    try {
                        double hours = Double.parseDouble(scanner.nextLine());
                        entry.addSosiHours(hours);
                        break; // Exit the numeric input loop
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid decimal number for hours.");
                    }
                }
                break; // Exit the job type loop after processing
            } else if (jobType.contains("lion") || jobType.contains("bridge")) {
                while (true) {
                    System.out.print("Enter Lion Bridge worked minutes (decimal allowed): ");
                    try {
                        double minutes = Double.parseDouble(scanner.nextLine());
                        entry.addLionBridgeMinutes(minutes);
                        break; // Exit the numeric input loop
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid decimal number for minutes.");
                    }
                }
                break; // Exit the job type loop after processing
            } else {
                System.out.println("Invalid job type. Please enter 'SOSI' or 'Lion Bridge'.");
            }
        }
    
        logEntries.put(date, entry); // Add the new log entry to the map
    }
    

    private static void showTotal(Map<String, WorkLogEntry> logEntries) {
        double totalSosiHours = 0;
        double totalLionBridgeMinutes = 0;

        for (WorkLogEntry entry : logEntries.values()) {
            totalSosiHours += entry.getSosiHours();
            totalLionBridgeMinutes += entry.getLionBridgeMinutes();
        }

        double totalLionBridgeHours = totalLionBridgeMinutes / 60;
        double overallTotalHours = totalSosiHours + totalLionBridgeHours;

        System.out.println("\nTotal Work Summary:");
        System.out.println("Total SOSI Hours: " + String.format("%.4f", totalSosiHours));
        System.out.println("Total Lion Bridge Minutes: " +  String.format("%.4f",totalLionBridgeMinutes));
        System.out.println("Total Lion Bridge Hours: " +  String.format("%.4f", totalLionBridgeHours));
        System.out.println("Overall Total Hours: " +  String.format("%.4f", overallTotalHours));
    }

    private static void showTotalEarned(Map<String, WorkLogEntry> logEntries) {
        double totalSosiIncome = 0;
        double totalLionBridgeIncome = 0;
    
        // Calculate the income
        for (WorkLogEntry entry : logEntries.values()) {
            totalSosiIncome += entry.getSosiHours() * 46.97;
            totalLionBridgeIncome += entry.getLionBridgeMinutes() * 0.65;
        }
    
        double overallTotalIncome = totalSosiIncome + totalLionBridgeIncome;
    
        // Display the income summary
        System.out.println("\nTotal Income Summary:");
        System.out.println("Total SOSI Income: $" + String.format("%.2f", totalSosiIncome));
        System.out.println("Total Lion Bridge Income: $" + String.format("%.2f", totalLionBridgeIncome));
        System.out.println("Overall Total Income: $" + String.format("%.2f", overallTotalIncome));
    }
    
}
