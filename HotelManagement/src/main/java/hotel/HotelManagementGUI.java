package hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HotelManagementGUI {
    private JFrame frame;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, roomField, daysField;
    private JButton bookButton, cancelButton, orderFoodButton, viewButton, availableRoomsButton;
    private JButton historyButton, billButton, reportButton;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Customer> allCustomers = new ArrayList<>();
    private ArrayList<Food> foodMenu = new ArrayList<>();

    public HotelManagementGUI() {
        initializeRooms();
        initializeFoodMenu();
        initializeGUI();
    }

    private void initializeRooms() {
        rooms.add(new Room(101, "Single", 50.0));
        rooms.add(new Room(102, "Single", 50.0));
        rooms.add(new Room(201, "Double", 80.0));
        rooms.add(new Room(202, "Double", 80.0));
        rooms.add(new Room(301, "Suite", 150.0));
    }

    private void initializeFoodMenu() {
        foodMenu.add(new Food("Paneer Butter Masala", 7.0, Food.Category.MAIN));
        foodMenu.add(new Food("Butter Chicken", 8.5, Food.Category.MAIN));
        foodMenu.add(new Food("Dal Makhani", 6.0, Food.Category.MAIN));
        foodMenu.add(new Food("Veg Biryani", 7.0, Food.Category.MAIN));
        foodMenu.add(new Food("Chicken Biryani", 8.0, Food.Category.MAIN));
        foodMenu.add(new Food("Naan", 1.5, Food.Category.STARTER));
        foodMenu.add(new Food("Roti", 1.0, Food.Category.STARTER));
        foodMenu.add(new Food("Samosa", 2.0, Food.Category.STARTER));
        foodMenu.add(new Food("Gulab Jamun", 3.0, Food.Category.DESSERT));
        foodMenu.add(new Food("Masala Chai", 1.5, Food.Category.BEVERAGE));
    }

    private void initializeGUI() {
        frame = new JFrame("Hotel Management System");
        frame.setSize(950, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        nameField = new JTextField(10);
        roomField = new JTextField(5);
        daysField = new JTextField(5);

        topPanel.add(new JLabel("Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Room Number:"));
        topPanel.add(roomField);
        topPanel.add(new JLabel("Days:"));
        topPanel.add(daysField);

        bookButton = new JButton("Book Room");
        cancelButton = new JButton("Cancel Booking");
        orderFoodButton = new JButton("Order Food");
        viewButton = new JButton("View Rooms");
        availableRoomsButton = new JButton("Available Rooms");
        historyButton = new JButton("Booking History");
        billButton = new JButton("Generate Bill");
        reportButton = new JButton("Reports");

        topPanel.add(bookButton);
        topPanel.add(cancelButton);
        topPanel.add(orderFoodButton);
        topPanel.add(viewButton);
        topPanel.add(availableRoomsButton);
        topPanel.add(historyButton);
        topPanel.add(billButton);
        topPanel.add(reportButton);

        frame.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Customer", "Room", "Days", "Room Bill", "Food Bill", "Grand Total"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        frame.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        // Button actions
        viewButton.addActionListener(e -> viewRooms());
        availableRoomsButton.addActionListener(e -> viewAvailableRooms());
        bookButton.addActionListener(e -> bookRoom());
        cancelButton.addActionListener(e -> cancelBooking());
        orderFoodButton.addActionListener(e -> orderFoodWithQuantity());
        historyButton.addActionListener(e -> viewBookingHistory());
        billButton.addActionListener(e -> generateInvoice());
        reportButton.addActionListener(e -> generateReport());

        frame.setVisible(true);
    }

    private void viewRooms() {
        StringBuilder sb = new StringBuilder("All Rooms:\n");
        for (Room room : rooms) sb.append(room).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    private void viewAvailableRooms() {
        StringBuilder sb = new StringBuilder("Available Rooms:\n");
        for (Room room : rooms) if (room.isAvailable()) sb.append(room).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    private void bookRoom() {
        String name = nameField.getText().trim();
        int roomNumber, stayDays;
        try {
            roomNumber = Integer.parseInt(roomField.getText());
            stayDays = Integer.parseInt(daysField.getText());
            if (stayDays <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input!");
            return;
        }
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (room == null || !room.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Room not available!");
            return;
        }
        room.bookRoom();
        customers.add(new Customer(name, roomNumber, stayDays));
        updateBookingTable();
        JOptionPane.showMessageDialog(frame, "Room booked successfully!");
    }

    private void cancelBooking() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber).findFirst().orElse(null);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c != null && room != null) {
            customers.remove(c);
            allCustomers.add(c);
            room.checkOut();
            updateBookingTable();
            JOptionPane.showMessageDialog(frame, "Booking canceled!");
        }
    }

    private void orderFoodWithQuantity() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer customer = customers.stream().filter(c -> c.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (customer == null) { JOptionPane.showMessageDialog(frame, "Room not booked!"); return; }

        JPanel panel = new JPanel(new GridLayout(foodMenu.size(), 3));
        JCheckBox[] checkBoxes = new JCheckBox[foodMenu.size()];
        JSpinner[] quantities = new JSpinner[foodMenu.size()];
        for (int i = 0; i < foodMenu.size(); i++) {
            Food f = foodMenu.get(i);
            checkBoxes[i] = new JCheckBox(f.toString());
            quantities[i] = new JSpinner(new SpinnerNumberModel(1,1,20,1));
            panel.add(checkBoxes[i]);
            panel.add(new JLabel("Qty:"));
            panel.add(quantities[i]);
        }
        int option = JOptionPane.showConfirmDialog(frame, panel, "Select Food Items", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (int i = 0; i < foodMenu.size(); i++) {
                if (checkBoxes[i].isSelected()) {
                    int qty = (Integer) quantities[i].getValue();
                    for (int q = 0; q < qty; q++) customer.addFood(foodMenu.get(i));
                }
            }
            updateBookingTable();
        }
    }

    private void updateBookingTable() {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            Room room = rooms.stream().filter(r -> r.getRoomNumber() == c.getRoomNumber()).findFirst().orElse(null);
            if (room != null) {
                tableModel.addRow(new Object[]{
                        c.getName(),
                        c.getRoomNumber(),
                        c.getStayDays(),
                        c.getRoomBill(room.getPricePerDay()),
                        c.getFoodBill(),
                        c.getGrandTotal(room.getPricePerDay())
                });
            }
        }
    }

    private void viewBookingHistory() {
        StringBuilder sb = new StringBuilder("=== Booking History ===\n");
        for (Customer c : allCustomers) sb.append(c).append("\n");
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    private void generateInvoice() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame,"Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber).findFirst().orElse(null);
        Room r = rooms.stream().filter(ro -> ro.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c == null || r == null) { JOptionPane.showMessageDialog(frame,"Room not booked!"); return; }

        StringBuilder invoice = new StringBuilder("Invoice for " + c.getName() + "\nRoom: " + r.getRoomNumber() +
                "\nDays: " + c.getStayDays() + "\nRoom Bill: $" + c.getRoomBill(r.getPricePerDay()) +
                "\nFood Bill: $" + c.getFoodBill() + "\nGrand Total: $" + c.getGrandTotal(r.getPricePerDay()));

        JOptionPane.showMessageDialog(frame, invoice.toString());

        // Save to file
        try (FileWriter fw = new FileWriter("Invoice_Room" + r.getRoomNumber() + ".txt")) {
            fw.write(invoice.toString());
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void generateReport() {
        double totalRevenue = 0, roomRevenue = 0, foodRevenue = 0;
        int totalBookings = allCustomers.size() + customers.size();

        for (Customer c : customers) {
            Room r = rooms.stream().filter(ro -> ro.getRoomNumber() == c.getRoomNumber()).findFirst().orElse(null);
            if (r != null) {
                roomRevenue += c.getRoomBill(r.getPricePerDay());
                foodRevenue += c.getFoodBill();
            }
        }

        for (Customer c : allCustomers) {
            Room r = rooms.stream().filter(ro -> ro.getRoomNumber() == c.getRoomNumber()).findFirst().orElse(null);
            if (r != null) {
                roomRevenue += c.getRoomBill(r.getPricePerDay());
                foodRevenue += c.getFoodBill();
            }
        }

        totalRevenue = roomRevenue + foodRevenue;
        String report = "=== Hotel Report ===\nTotal Bookings: " + totalBookings +
                "\nTotal Revenue: $" + totalRevenue +
                "\nRoom Revenue: $" + roomRevenue +
                "\nFood Revenue: $" + foodRevenue;

        JOptionPane.showMessageDialog(frame, report);
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(HotelManagementGUI::new); }
}


