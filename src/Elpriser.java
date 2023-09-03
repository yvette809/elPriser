import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Elpriser {

        private static int[] electricityPrices = new int[24];

        public static void main(String[] args) {

            while (true) {

                menuDisplay();
                System.out.println("Enter your Choice: ");
                Scanner s = new Scanner(System.in);
                //int userChoice = s.nextInt();
                String userChoice = s.next();
                switch (userChoice.toLowerCase()) {

                    case "1":
                        inputElectricityPrices(s);
                        break;
                    case "2":
                        calcMinMaxAvgPrice();
                        break;
                    case "3":
                        sortElectricityUsage();
                        break;
                    case "4":
                        findBestChargingTime();
                        break;
                    case "5":
                        readPricesFromFile();
                        break;
                    case "e":
                    case "E":
                        System.out.println("Exiting program");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a valid choice");

                }


            }


        }


        public static void menuDisplay() {
            System.out.println("Elpriser");
            System.out.println("========");
            System.out.println("1. " + "Inmatning");
            System.out.println("2. " + "Min, Max och Medel");
            System.out.println("3. " + "Sortera");
            System.out.println("4. " + "Bästa Laddningstid (4h)");
            System.out.println("e. " + "Avsluta");

        }

        private static void inputElectricityPrices(Scanner scanner) {
            System.out.println("Enter electricity prices for each interval (in öre per kWh):");
            for (int i = 0; i < 24; i++) {
                int startHour = i;
                int endHour = (i + 1) % 24; // Calculate the ending hour (wraps around to 0 for 23-00 interval)
                System.out.print("Price for interval " + startHour + "-" + endHour + ": ");
                try {
                    int priceInCents = scanner.nextInt();
                    electricityPrices[i] = priceInCents;

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.next(); // Clear the invalid input
                    i--; // Retry for the same interval
                }
            }
            System.out.println("Electricity prices have been updated.");
            // Display the entire array
            System.out.println("Electricity prices for each interval: " + Arrays.toString(electricityPrices));
        }

        private static void calcMinMaxAvgPrice() {
            System.out.println("pries " + electricityPrices[0]);
            int minPrice = electricityPrices[0];
            int maxPrice = electricityPrices[0];
            int minHour = 0;
            int maxHour = 0;
            int sum = 0;

            for (int i = 1; i < 24; i++) {

                int price = electricityPrices[i];
                if (price < minPrice) {
                    minPrice = price;
                    minHour = i;
                }
                if (price > maxPrice) {
                    maxPrice = price;
                    maxHour = i;
                }

                sum += price;

            }
            int averagePrice = sum / 24;

            System.out.println("Lowest price: " + minPrice + " öre during hour " + minHour + "-" + ((minHour + 1) % 24));
            System.out.println("Highest price: " + maxPrice + " öre during hour " + maxHour + "-" + ((maxHour + 1) % 24));
            System.out.println("Daily average price: " + averagePrice + " öre/kwh");
        }


        public static void sortElectricityUsage() {
            // Create a copy of the electricityPrices array to sort
            int[] sortedPrices = Arrays.copyOf(electricityPrices, 24);

            // Sort the copy of prices in ascending order
            Arrays.sort(sortedPrices);

            System.out.println("Sorted electricity prices: " + sortedPrices);

            for (int i = 0; i < 24; i++) {
                int price = sortedPrices[i];
                int hour = findHourByPrice(price);

                System.out.println(String.format("%02d-%02d %d öre", hour, (hour + 1) % 24, price));
            }
        }

        public static int findHourByPrice(int price) {
            // Find and return the hour for the given price in the original electricityPrices array
            for (int i = 0; i < 24; i++) {
                if (electricityPrices[i] == price) {
                    return i;
                }
            }
            return -1; // Return -1 if the price is not found (shouldn't happen)
        }

        public static void findBestChargingTime() {
            int minTotalPrice = Integer.MAX_VALUE; // Initialize the minimum total price to the maximum possible value
            int bestStartTime = 0; // Initialize the best start time
            double averagePrice = 0; // Initialize the average price

            for (int startHour = 0; startHour < 24; startHour++) {
                int totalPrice = 0;

                for (int i = 0; i < 4; i++) {
                    int hour = (startHour + i) % 24; // Calculate the current hour in a cyclic manner

                    totalPrice += electricityPrices[hour];
                }

                if (totalPrice < minTotalPrice) {
                    minTotalPrice = totalPrice;
                    bestStartTime = startHour;
                }
            }

            // Calculate the average price for the 4 cheapest consecutive hours
            for (int i = 0; i < 4; i++) {
                int hour = (bestStartTime + i) % 24;
                averagePrice += electricityPrices[hour];
            }
            averagePrice /= 4.0; // Calculate the average

            System.out.println("Best time to start charging for 4 hours is from " + bestStartTime + "-" + ((bestStartTime + 3) % 24));
            System.out.println("Lowest total price for 4 hours: " + minTotalPrice + " cents");
            System.out.println("Average price during these 4 hours: " + averagePrice + " cents");
        }



        public static void readPricesFromFile() {
            File file = new File("prices.csv");

            if (!file.exists()) {
                System.out.println("The file 'prices.csv' does not exist in the current working directory.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String currentLine = br.readLine();
                int lineNumber = 0;
                int[] newPrices = new int[24];

                while (currentLine != null) {
                    lineNumber++;
                    String[] parts = currentLine.split(",");

                    if (parts.length != 24) {
                        System.out.println("Error reading line " + lineNumber + " from file. Incorrect number of prices.");
                        return;
                    }

                    for (int i = 0; i < 24; i++) {
                        try {
                            newPrices[i] = Integer.parseInt(parts[i].trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Error reading line " + lineNumber + " from file. Invalid price format.");
                            return;
                        }
                    }
                }

                // If all lines were read successfully, update the electricityPrices array
                electricityPrices = newPrices;
                System.out.println("Electricity prices have been updated from file.");
                // Print the prices from the CSV file
                System.out.println("Electricity prices from 'prices.csv':");
                for (int i = 0; i < 24; i++) {
                    System.out.println("Hour " + i + ": " + electricityPrices[i] + " öre");
                }
            } catch (IOException e) {
                System.out.println("Error reading from the file: " + e.getMessage());
            }
        }











}
