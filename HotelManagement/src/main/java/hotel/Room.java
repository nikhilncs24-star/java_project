package hotel;

public class Room {
    private int roomNumber;
    private String type;
    private double pricePerDay;
    private boolean available;

    public Room(int roomNumber, String type, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public boolean isAvailable() {
        return available;
    }

    public void bookRoom() {
        available = false;
    }

    public void cancelBooking() {
        available = true;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - $" + pricePerDay + "/day" +
                " | " + (available ? "Available" : "Booked");
    }
}
