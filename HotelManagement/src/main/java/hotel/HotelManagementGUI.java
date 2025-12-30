package hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Customer {
    public enum Status {RESERVED, CHECKED_IN, CHECKED_OUT}

    private String name;
    private String phone;
    private String email;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Status status;
    private ArrayList<Food> foodOrders;

    public Customer(String name, String phone, String email, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = Status.RESERVED;
        this.foodOrders = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public long getStayDays() { return ChronoUnit.DAYS.between(checkInDate, checkOutDate); }
    public ArrayList<Food> getFoodOrders() { return foodOrders; }

    public void addFood(Food food) { foodOrders.add(food); }

    public double getFoodBill() {
        double total = 0;
        for (Food f : foodOrders) total += f.getPrice();
        return total;
    }

    public double getRoomBill(double pricePerDay) { return getStayDays() * pricePerDay; }

    public double getSubtotal(double pricePerDay) { return getRoomBill(pricePerDay) + getFoodBill(); }

    public double getTax(double taxRate) { return getSubtotal(0) * taxRate / 100; } // Assuming tax on subtotal

    public double getGrandTotal(double pricePerDay, double taxRate, double discount) {
        return getSubtotal(pricePerDay) + getTax(taxRate) - discount;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ") | Room: " + roomNumber + " | Status: " + status +
               " | Check-in: " + checkInDate + " | Check-out: " + checkOutDate + " | Total: $" + getGrandTotal(0, 0, 0);
    }
}
