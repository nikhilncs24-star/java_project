package hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class HotelManagementGUI {
    private JFrame frame;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, roomField, daysField;
    private JButton bookButton, cancelButton, orderFoodButton, viewButton, bookingStatusButton, availableRoomsButton;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Food> foodMenu = new ArrayList<>();

    public HotelManagementGUI() {
        initializeRooms();
        initializeFoodMenu();
        initializeGUI();
    }

    // Initialize rooms
    private void initializeRooms() {
        rooms.add(new Room(101, "Single", 50.0));
        rooms.add(new Room(102, "Single", 50.0));
        rooms.add(new Room(201, "Double", 80.0));
        rooms.add(new Room(202, "Double", 80.0));
        rooms.add(new Room(301, "Suite", 150.0));
    }

    // Initialize food menu
// Initialize Indian Food Menu
private void initializeFoodMenu() {
    foodMenu.add(new Food("Paneer Butter Masala", 7.0));
    foodMenu.add(new Food("Butter Chicken", 8.5));
    foodMenu.add(new Food("Dal Makhani", 6.0));
    foodMenu.add(new Food("Veg Biryani", 7.0));
    foodMenu.add(new Food("Chicken Biryani", 8.0));
    foodMenu.add(new Food("Naan", 1.5));
    foodMenu.add(new Food("Roti", 1.0));
    foodMenu.add(new Food("Samosa", 2.0));
    foodMenu.add(new Food("Gulab Jamun", 3.0));
    foodMenu.add(new Food("Masala Chai", 1.5));
}


    // Initialize GUI
    private void initializeGUI() {
        frame = new JFrame("Hotel Management System");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top panel for inputs and buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        nameField = new JTextField(10);
        roomField = new JTextField(5);
        daysField = new JTextField(5);

        topPanel.add(new JLabel("Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Room Number:"));
        topPanel.add(roomField);
        topPanel.add(new JLabel("No. of Days:"));
        topPanel.add(daysField);

        bookButton = new JButton("Book Room");
        cancelButton = new JButton("Cancel Booking");
        orderFoodButton = new JButton("Order Food");
        viewButton = new JButton("View Rooms");
        bookingStatusButton = new JButton("View Bookings");
        availableRoomsButton = new JButton("Show Available Rooms");

        topPanel.add(bookButton);
        topPanel.add(cancelButton);
        topPanel.add(orderFoodButton);
        topPanel.add(viewButton);
        topPanel.add(bookingStatusButton);
        topPanel.add(availableRoomsButton);

        frame.add(topPanel, BorderLayout.NORTH);

        // JTable for bookings
        String[] columns = {"Customer", "Room", "Days", "Room Bill", "Food Bill", "Grand Total"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        frame.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        // Button actions
        viewButton.addActionListener(e -> viewRooms());
        bookButton.addActionListener(e -> bookRoom());
        cancelButton.addActionListener(e -> cancelBooking());
        orderFoodButton.addActionListener(e -> orderFoodWithQuantity());
        bookingStatusButton.addActionListener(e -> viewBookings());
        availableRoomsButton.addActionListener(e -> viewAvailableRooms());

        frame.setVisible(true);
    }

    // View all rooms
    private void viewRooms() {
        StringBuilder sb = new StringBuilder("All Rooms:\n");
        for (Room room : rooms) {
            sb.append(room.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "All Rooms", JOptionPane.INFORMATION_MESSAGE);
    }

    // View available rooms
    private void viewAvailableRooms() {
        StringBuilder sb = new StringBuilder("Available Rooms:\n");
        boolean anyAvailable = false;
        for (Room room : rooms) {
            if (room.isAvailable()) {
                sb.append(room.toString()).append("\n");
                anyAvailable = true;
            }
        }
        if (!anyAvailable) {
            sb.append("No rooms are currently available!");
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "Available Rooms", JOptionPane.INFORMATION_MESSAGE);
    }

    // Book a room
    private void bookRoom() {
        String name = nameField.getText().trim();
        int roomNumber, stayDays;

        try {
            roomNumber = Integer.parseInt(roomField.getText());
            stayDays = Integer.parseInt(daysField.getText());
            if (stayDays <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number or days!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room selectedRoom = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(frame, "Room does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!selectedRoom.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Room is already booked!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            selectedRoom.bookRoom();
            customers.add(new Customer(name, roomNumber, stayDays));
            updateBookingTable();
            JOptionPane.showMessageDialog(frame, "Room booked successfully for " + name, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Cancel booking
    private void cancelBooking() {
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = customers.stream().filter(c -> c.getRoomNumber() == roomNumber).findFirst().orElse(null);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);

        if (customer != null && room != null) {
            customers.remove(customer);
            room.cancelBooking();
            updateBookingTable();
            JOptionPane.showMessageDialog(frame, "Booking cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No booking found for this room.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Order food with quantity
    private void orderFoodWithQuantity() {
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid room number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = customers.stream().filter(c -> c.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (customer == null) {
            JOptionPane.showMessageDialog(frame, "No booking found for this room!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(foodMenu.size(), 3));
        JCheckBox[] checkBoxes = new JCheckBox[foodMenu.size()];
        JSpinner[] quantitySpinners = new JSpinner[foodMenu.size()];

        for (int i = 0; i < foodMenu.size(); i++) {
            Food f = foodMenu.get(i);
            checkBoxes[i] = new JCheckBox(f.getName() + " ($" + f.getPrice() + ")");
            quantitySpinners[i] = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
            panel.add(checkBoxes[i]);
            panel.add(new JLabel("Qty:"));
            panel.add(quantitySpinners[i]);
        }

        int option = JOptionPane.showConfirmDialog(frame, panel, "Select Food Items", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (int i = 0; i < foodMenu.size(); i++) {
                if (checkBoxes[i].isSelected()) {
                    int qty = (Integer) quantitySpinners[i].getValue();
                    for (int q = 0; q < qty; q++) {
                        customer.addFood(foodMenu.get(i));
                    }
                }
            }
            updateBookingTable();
            JOptionPane.showMessageDialog(frame, "Food items added for " + customer.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // View bookings
    private void viewBookings() {
        updateBookingTable();
        JOptionPane.showMessageDialog(frame, "Bookings updated in the table.", "View Bookings", JOptionPane.INFORMATION_MESSAGE);
    }

    // Update JTable
    private void updateBookingTable() {
        tableModel.setRowCount(0);
        for (Customer customer : customers) {
            Room room = rooms.stream().filter(r -> r.getRoomNumber() == customer.getRoomNumber()).findFirst().orElse(null);
            if (room != null) {
                double roomBill = customer.getRoomBill(room.getPricePerDay());
                double foodBill = customer.getFoodBill();
                double grandTotal = customer.getGrandTotal(room.getPricePerDay());
                tableModel.addRow(new Object[]{
                        customer.getName(),
                        customer.getRoomNumber(),
                        customer.getStayDays(),
                        roomBill,
                        foodBill,
                        grandTotal
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelManagementGUI::new);
    }
}
