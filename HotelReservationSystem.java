import java.io.*;
import java.util.*;

class Room {                  //ROOM CLASS TO STORE ROOM DETAILS.
    int roomNumber;
    String category;
    double price;
    boolean isAvailable;     

    Room(int roomNumber, String category, double price) {               //Constructor to insert room details
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
        this.isAvailable = true;                                       //check if room is available or not
    }
}

class Booking {                     //Booking class to store booking details.
    static int idCounter = 1460;    //initialize booking ID counter for unique booking IDs.

    int bookingId;
    String customerName;
    int roomNumber;
    String category;
    int nights;
    double totalAmount;
    String status;

    Booking(String name, Room room, int nights) {       //again constructor to insert booking details
        this.bookingId = ++idCounter;
        this.customerName = name;
        this.roomNumber = room.roomNumber;
        this.category = room.category;
        this.nights = nights;
        this.totalAmount = nights * room.price;
        this.status = "Pending";                    //initially pending until payment is confirmed.
    }

    public String toString() {                      //I make function to return booking details in a string format for saving to file.
        return bookingId + "," + customerName + "," + roomNumber + "," +
                category + "," + nights + "," + totalAmount + "," + status;
    }
}

public class HotelReservationSystem {               //Main class for the hotel reservation system.

    static ArrayList<Room> rooms = new ArrayList<>();
    static final String FILE_NAME = "bookings.txt";  

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        initializeRooms();
        loadBookedRooms();  

        while (true) {
            System.out.println("\n========== HOTEL RESERVATION SYSTEM ==========");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewRooms();
                case 2 -> bookRoom(sc);
                case 3 -> cancelBooking(sc);
                case 4 -> viewBookings();
                case 5 -> {
                    System.out.println("Thank you for using the Ishraq Hotel's booking system!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void initializeRooms() {
        rooms.add(new Room(50, "Standard", 1000));
        rooms.add(new Room(55, "Standard", 1000));
        rooms.add(new Room(67, "Deluxe", 2000));
        rooms.add(new Room(89, "Deluxe", 2000));
        rooms.add(new Room(39, "Suite", 3500));
        rooms.add(new Room(39, "Suite", 3500));
        rooms.add(new Room(289, "Suite", 3500));
        rooms.add(new Room(478, "Suite", 3500));
    }

    static void loadBookedRooms() {
        try {                                               //to check if file exists or not, if it exists then read the file and mark the rooms as unavailable based on the booking status.
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));   //read file line by line and split data.
            String line;                                                        //check booking is confirmed or not.

            while ((line = reader.readLine()) != null) {                        //
                String[] data = line.split(",");
                int roomNumber = Integer.parseInt(data[2]);         
                String status = data[6];        

                if (status.equals("Confirmed")) {
                    for (Room r : rooms) {
                        if (r.roomNumber == roomNumber) {
                            r.isAvailable = false;
                        }
                    }
                }
            }

            reader.close();
        } catch (IOException e) {               //if any error occurs while reading the file, print an error message.
            System.out.println("Error loading booked rooms.");
        }
    }

    static void viewRooms() {           //to check available rooms
        System.out.println("\nAvailable Rooms:");
        boolean found = false;

        for (Room r : rooms) {
            if (r.isAvailable) {
                System.out.println("Room " + r.roomNumber + " | " + r.category +
                        " | ₹" + r.price + " per night");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No rooms available.");
        }
    }

    static void bookRoom(Scanner sc) {          //to book rooms

        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        viewRooms();

        System.out.print("Enter room number: ");
        int roomNo = sc.nextInt();

        System.out.print("Enter number of nights: ");
        int nights = sc.nextInt();
        sc.nextLine();

        for (Room r : rooms) {
            if (r.roomNumber == roomNo) {

                if (!r.isAvailable) {
                    System.out.println("❌ Room already booked!");
                    return;
                }

                Booking booking = new Booking(name, r, nights);

                System.out.println("\nTotal Amount: ₹" + booking.totalAmount);

                System.out.print("Has payment been completed? (yes/no): ");
                String paymentResponse = sc.nextLine();

                if (paymentResponse.equalsIgnoreCase("yes")) {      
                    System.out.println("Processing payment...");
                    System.out.println("Payment Successful!");

                    r.isAvailable = false;
                    booking.status = "Confirmed";

                    saveBooking(booking);

                    System.out.println("Booking Confirmed!");
                    System.out.println("Booking ID: " + booking.bookingId);

                } else {
                    booking.status = "Cancelled";
                    System.out.println("Payment not completed. Booking Cancelled.");
                }

                return;
            }
        }

        System.out.println("Invalid Room Number!");
    }

    static void cancelBooking(Scanner sc) {     //function to cancel booking on bookingid.

        System.out.print("Enter Booking ID to cancel: ");
        int id = sc.nextInt();
        sc.nextLine();

        try {
            File inputFile = new File(FILE_NAME);
            if (!inputFile.exists()) {
                System.out.println("No bookings found.");
                return;
            }

            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int bookingId = Integer.parseInt(data[0]);
                int roomNumber = Integer.parseInt(data[2]);

                if (bookingId == id) {
                    found = true;

                    
                    for (Room r : rooms) {
                        if (r.roomNumber == roomNumber) {
                            r.isAvailable = true;
                        }
                    }

                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }

            reader.close();
            writer.close();

            inputFile.delete();
            tempFile.renameTo(inputFile);              

            if (found)
                System.out.println("Booking Cancelled Successfully!");
            else
                System.out.println("Booking ID not found!");

        } catch (IOException e) {
            System.out.println("Error cancelling booking.");
        }
    }

    static void viewBookings() {        //to see bookings

        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                System.out.println("No bookings yet.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            System.out.println("\nAll Bookings:");
            System.out.println("ID, Name, Room, Category, Nights, Amount, Status");

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    static void saveBooking(Booking booking) {  //to save booking details to file

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(booking.toString());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving booking.");
        }
    }

}
