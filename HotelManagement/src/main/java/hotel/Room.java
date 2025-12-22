package hotel;

public class Room {
    public enum Status {AVAILABLE, BOOKED, CLEANING, MAINTENANCE}

    private int roomNumber;
    private String type;
    private double pricePerDay;
    private Status status;

    public Room(int roomNumber, String type, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.status = Status.AVAILABLE;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPricePerDay() { return pricePerDay; }
    public Status getStatus() { return status; }

    public boolean isAvailable() { return status == Status.AVAILABLE; }
    public void bookRoom() { status = Status.BOOKED; }
    public void checkOut() { status = Status.CLEANING; }
    public void setMaintenance() { status = Status.MAINTENANCE; }
    public void setAvailable() { status = Status.AVAILABLE; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - $" + pricePerDay + "/day | " + status;
    }
}
