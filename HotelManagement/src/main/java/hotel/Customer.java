package hotel;

import java.util.ArrayList;

public class Customer {
    private String name;
    private int roomNumber;
    private int stayDays;
    private ArrayList<Food> foodOrders;

    public Customer(String name, int roomNumber, int stayDays) {
        this.name = name;
        this.roomNumber = roomNumber;
        this.stayDays = stayDays;
        this.foodOrders = new ArrayList<>();
    }

    public String getName() { return name; }
    public int getRoomNumber() { return roomNumber; }
    public int getStayDays() { return stayDays; }
    public ArrayList<Food> getFoodOrders() { return foodOrders; }

    public void addFood(Food food) { foodOrders.add(food); }

    public double getFoodBill() {
        double total = 0;
        for (Food f : foodOrders) total += f.getPrice();
        return total;
    }

    public double getRoomBill(double pricePerDay) { return stayDays * pricePerDay; }

    public double getGrandTotal(double pricePerDay) { return getRoomBill(pricePerDay) + getFoodBill(); }

    @Override
    public String toString() {
        return name + " | Room: " + roomNumber + " | Days: " + stayDays + " | Total: $" + getGrandTotal(0);
    }
}
