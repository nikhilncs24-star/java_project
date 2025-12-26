package hotel;

public class Room {
    public enum Status {AVAILABLE, RESERVED, OCCUPIED, CLEANING, MAINTENANCE, DIRTY}

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
    public boolean isReserved() { return status == Status.RESERVED; }
    public boolean isOccupied() { return status == Status.OCCUPIED; }

    public void reserveRoom() { status = Status.RESERVED; }
    public void checkIn() { status = Status.OCCUPIED; }
    public void checkOut() { status = Status.DIRTY; }
    public void setCleaning() { status = Status.CLEANING; }
    public void setMaintenance() { status = Status.MAINTENANCE; }
    public void setAvailable() { status = Status.AVAILABLE; }
    public void markDirty() { status = Status.DIRTY; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ") - $" + pricePerDay + "/day | " + status;
    }
}
