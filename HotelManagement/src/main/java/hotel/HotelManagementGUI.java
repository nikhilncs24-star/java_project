package hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HotelManagementGUI {
    private JFrame frame;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, emailField, roomField, checkInField, checkOutField, searchField, discountField;
    private JButton bookButton, cancelButton, orderFoodButton, viewButton, availableRoomsButton;
    private JButton historyButton, billButton, reportButton, availableButton;
    private JButton checkInButton, checkOutButton, markCleanButton;
    private JButton pickCheckInButton, pickCheckOutButton;
    private JTextArea notificationArea;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Customer> allCustomers = new ArrayList<>();
    private ArrayList<Food> foodMenu = new ArrayList<>();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private double taxRate = 10.0; // 10% tax
    private String currentSearchQuery = "";

    public HotelManagementGUI() {
        if (!login()) return;
        initializeRooms();
        initializeFoodMenu();
        initializeGUI();
        frame.setVisible(true);
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

    private boolean login() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        int option = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if ("admin".equals(user) && "password".equals(pass)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials!");
                return login();
            }
        }
        return false;
    }

    private void initializeGUI() {
        frame = new JFrame("Hotel Management System");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        nameField = new JTextField(10);
        phoneField = new JTextField(10);
        emailField = new JTextField(15);
        roomField = new JTextField(5);
        checkInField = new JTextField(10);
        checkOutField = new JTextField(10);
        searchField = new JTextField(15);
        discountField = new JTextField(5);

        bookButton = new JButton("Reserve Room");
        cancelButton = new JButton("Cancel Reservation");
        checkInButton = new JButton("Check-in");
        checkOutButton = new JButton("Check-out");
        orderFoodButton = new JButton("Order Food");
        viewButton = new JButton("View Rooms");
        availableRoomsButton = new JButton("Available Rooms");
        historyButton = new JButton("Booking History");
        billButton = new JButton("Generate Bill");
        reportButton = new JButton("Reports");
        availableButton = new JButton("Mark Available");
        markCleanButton = new JButton("Mark Clean");
        pickCheckInButton = new JButton("Pick");
        pickCheckOutButton = new JButton("Pick");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Room Number:"));
        inputPanel.add(roomField);
        inputPanel.add(new JLabel("Check-in (yyyy-MM-dd):"));
        inputPanel.add(checkInField);
        inputPanel.add(pickCheckInButton);
        inputPanel.add(new JLabel("Check-out (yyyy-MM-dd):"));
        inputPanel.add(checkOutField);
        inputPanel.add(pickCheckOutButton);
        inputPanel.add(new JLabel("Discount ($):"));
        inputPanel.add(discountField);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search Customer:"));
        searchPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 7, 5, 5));
        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(orderFoodButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(availableRoomsButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(billButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(availableButton);
        buttonPanel.add(markCleanButton);

        topPanel.add(inputPanel);
        topPanel.add(searchPanel);
        topPanel.add(buttonPanel);

        frame.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Customer", "Phone", "Email", "Room", "Status", "Check-in", "Check-out", "Room Bill", "Food Bill", "Tax", "Discount", "Grand Total"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingTable = new JTable(tableModel);
        bookingTable.setDefaultRenderer(Object.class, new HighlightRenderer());
        frame.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Notifications:"), BorderLayout.NORTH);
        notificationArea = new JTextArea(5, 50);
        notificationArea.setEditable(false);
        bottomPanel.add(new JScrollPane(notificationArea), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        viewButton.addActionListener(e -> viewRooms());
        availableRoomsButton.addActionListener(e -> viewAvailableRooms());
        bookButton.addActionListener(e -> reserveRoom());
        cancelButton.addActionListener(e -> cancelReservation());
        checkInButton.addActionListener(e -> checkInGuest());
        checkOutButton.addActionListener(e -> checkOutGuest());
        orderFoodButton.addActionListener(e -> orderFoodWithQuantity());
        historyButton.addActionListener(e -> viewBookingHistory());
        billButton.addActionListener(e -> generateInvoice());
        reportButton.addActionListener(e -> generateReport());
        pickCheckInButton.addActionListener(e -> pickDate(checkInField));
        pickCheckOutButton.addActionListener(e -> pickDate(checkOutField));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterCustomers(); }
            public void removeUpdate(DocumentEvent e) { filterCustomers(); }
            public void insertUpdate(DocumentEvent e) { filterCustomers(); }
        });
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

    private void reserveRoom() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        int roomNumber;
        LocalDate checkIn, checkOut;
        try {
            roomNumber = Integer.parseInt(roomField.getText());
            checkIn = LocalDate.parse(checkInField.getText(), dateFormatter);
            checkOut = LocalDate.parse(checkOutField.getText(), dateFormatter);
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) throw new DateTimeParseException("Invalid dates", "", 0);
        } catch (NumberFormatException | DateTimeParseException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input! Use yyyy-MM-dd for dates.");
            return;
        }
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (room == null || !room.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Room not available!");
            return;
        }
        room.reserveRoom();
        customers.add(new Customer(name, phone, email, roomNumber, checkIn, checkOut));
        updateBookingTable();
        updateNotifications();
        JOptionPane.showMessageDialog(frame, "Room reserved successfully!");
    }

    private void cancelReservation() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber && cs.getStatus() == Customer.Status.RESERVED).findFirst().orElse(null);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c != null && room != null) {
            customers.remove(c);
            allCustomers.add(c);
            room.setAvailable();
            updateBookingTable();
            updateNotifications();
            JOptionPane.showMessageDialog(frame, "Reservation canceled!");
        } else {
            JOptionPane.showMessageDialog(frame, "No active reservation for this room!");
        }
    }

    private void checkInGuest() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber && cs.getStatus() == Customer.Status.RESERVED).findFirst().orElse(null);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c != null && room != null && room.isReserved()) {
            c.setStatus(Customer.Status.CHECKED_IN);
            room.checkIn();
            updateBookingTable();
            updateNotifications();
            JOptionPane.showMessageDialog(frame, "Guest checked in!");
        } else {
            JOptionPane.showMessageDialog(frame, "No reservation to check-in!");
        }
    }

    private void checkOutGuest() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber && cs.getStatus() == Customer.Status.CHECKED_IN).findFirst().orElse(null);
        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c != null && room != null && room.isOccupied()) {
            c.setStatus(Customer.Status.CHECKED_OUT);
            room.checkOut();
            customers.remove(c);
            allCustomers.add(c);
            updateBookingTable();
            updateNotifications();
            JOptionPane.showMessageDialog(frame, "Guest checked out!");
        } else {
            JOptionPane.showMessageDialog(frame, "No checked-in guest for this room!");
        }
    }

    private void orderFoodWithQuantity() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Customer customer = customers.stream().filter(c -> c.getRoomNumber() == roomNumber && c.getStatus() == Customer.Status.CHECKED_IN).findFirst().orElse(null);
        if (customer == null) { JOptionPane.showMessageDialog(frame, "Guest not checked in!"); return; }

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
                double discount = 0;
                try { discount = Double.parseDouble(discountField.getText()); } catch (Exception e) {}
                tableModel.addRow(new Object[]{
                        c.getName(),
                        c.getPhone(),
                        c.getEmail(),
                        c.getRoomNumber(),
                        c.getStatus(),
                        c.getCheckInDate(),
                        c.getCheckOutDate(),
                        c.getRoomBill(room.getPricePerDay()),
                        c.getFoodBill(),
                        c.getTax(taxRate),
                        discount,
                        c.getGrandTotal(room.getPricePerDay(), taxRate, discount)
                });
            }
        }
    }

    private void viewBookingHistory() {
        StringBuilder sb = new StringBuilder("=== All Bookings ===\n");
        sb.append("Current Bookings:\n");
        if (customers.isEmpty()) {
            sb.append("No current bookings.\n");
        } else {
            for (Customer c : customers) sb.append(c).append("\n");
        }
        sb.append("\nPast Bookings:\n");
        if (allCustomers.isEmpty()) {
            sb.append("No past bookings.\n");
        } else {
            for (Customer c : allCustomers) sb.append(c).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString());
    }

    private void generateInvoice() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame,"Invalid room number!"); return; }

        Customer c = customers.stream().filter(cs -> cs.getRoomNumber() == roomNumber).findFirst().orElse(null);
        Room r = rooms.stream().filter(ro -> ro.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (c == null || r == null) { JOptionPane.showMessageDialog(frame,"Room not booked!"); return; }

        double discount = 0;
        try { discount = Double.parseDouble(discountField.getText()); } catch (Exception e) {}

        StringBuilder invoice = new StringBuilder("Invoice for " + c.getName() + " (" + c.getPhone() + ")\nEmail: " + c.getEmail() +
                "\nRoom: " + r.getRoomNumber() + "\nStatus: " + c.getStatus() +
                "\nCheck-in: " + c.getCheckInDate() + "\nCheck-out: " + c.getCheckOutDate() +
                "\nDays: " + c.getStayDays() + "\nRoom Bill: $" + c.getRoomBill(r.getPricePerDay()) +
                "\nFood Bill: $" + c.getFoodBill() + "\nSubtotal: $" + c.getSubtotal(r.getPricePerDay()) +
                "\nTax (" + taxRate + "%): $" + c.getTax(taxRate) + "\nDiscount: $" + discount +
                "\nGrand Total: $" + c.getGrandTotal(r.getPricePerDay(), taxRate, discount));

        JOptionPane.showMessageDialog(frame, invoice.toString());

        // Save to file
        try (FileWriter fw = new FileWriter("Invoice_Room" + r.getRoomNumber() + ".txt")) {
            fw.write(invoice.toString());
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void generateReport() {
        double totalRevenue = 0, roomRevenue = 0, foodRevenue = 0;
        int totalBookings = allCustomers.size() + customers.size();
        int totalRooms = rooms.size();
        long occupiedRooms = rooms.stream().filter(r -> !r.isAvailable()).count();
        double occupancyRate = (double) occupiedRooms / totalRooms * 100;

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
                "\nFood Revenue: $" + foodRevenue +
                "\nOccupancy Rate: " + String.format("%.2f", occupancyRate) + "%";

        JOptionPane.showMessageDialog(frame, report);
    }

    private void filterCustomers() {
        String query = searchField.getText().trim().toLowerCase();
        currentSearchQuery = query;
        updateBookingTable();
    }

    private void setRoomMaintenance() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (room == null) {
            JOptionPane.showMessageDialog(frame, "Room not found!");
            return;
        }
        if (!room.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Room is currently in use!");
            return;
        }
        room.setMaintenance();
        JOptionPane.showMessageDialog(frame, "Room set to maintenance!");
    }

    private void markRoomAvailable() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (room == null) {
            JOptionPane.showMessageDialog(frame, "Room not found!");
            return;
        }
        room.setAvailable();
        JOptionPane.showMessageDialog(frame, "Room marked as available!");
    }

    private void markRoomClean() {
        int roomNumber;
        try { roomNumber = Integer.parseInt(roomField.getText()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(frame, "Invalid room number!"); return; }

        Room room = rooms.stream().filter(r -> r.getRoomNumber() == roomNumber).findFirst().orElse(null);
        if (room == null) {
            JOptionPane.showMessageDialog(frame, "Room not found!");
            return;
        }
        if (room.getStatus() == Room.Status.DIRTY) {
            room.setAvailable();
            JOptionPane.showMessageDialog(frame, "Room marked as clean and available!");
        } else {
            JOptionPane.showMessageDialog(frame, "Room is not dirty!");
        }
    }

    private void pickDate(JTextField field) {
        LocalDate current = LocalDate.now();
        try {
            current = LocalDate.parse(field.getText(), dateFormatter);
        } catch (Exception e) {
            // Use today if invalid
        }

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(current.getYear(), 2020, 2030, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(current.getMonthValue(), 1, 12, 1));
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(current.getDayOfMonth(), 1, 31, 1));

        panel.add(new JLabel("Year:"));
        panel.add(yearSpinner);
        panel.add(new JLabel("Month:"));
        panel.add(monthSpinner);
        panel.add(new JLabel("Day:"));
        panel.add(daySpinner);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Select Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int year = (Integer) yearSpinner.getValue();
            int month = (Integer) monthSpinner.getValue();
            int day = (Integer) daySpinner.getValue();
            try {
                LocalDate selected = LocalDate.of(year, month, day);
                field.setText(selected.format(dateFormatter));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid date selected.");
            }
        }
    }

    private void updateNotifications() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        StringBuilder sb = new StringBuilder();
        for (Customer c : customers) {
            if (c.getCheckOutDate().isEqual(today)) {
                sb.append("Check-out today: ").append(c.getName()).append(" (Room ").append(c.getRoomNumber()).append(")\n");
            } else if (c.getCheckOutDate().isEqual(tomorrow)) {
                sb.append("Check-out tomorrow: ").append(c.getName()).append(" (Room ").append(c.getRoomNumber()).append(")\n");
            }
        }
        if (sb.length() == 0) sb.append("No upcoming check-outs.");
        notificationArea.setText(sb.toString());
    }

    private class HighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else if (!currentSearchQuery.isEmpty()) {
                boolean matches = false;
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object val = table.getValueAt(row, col);
                    if (val != null && val.toString().toLowerCase().contains(currentSearchQuery)) {
                        matches = true;
                        break;
                    }
                }
                if (matches) {
                    setBackground(Color.YELLOW);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(HotelManagementGUI::new); }
}


