import java.util.*;

public class HashTableFundamentals {

    class ParkingSpot {
        String licensePlate;
        long entryTime;
        boolean occupied;

        ParkingSpot() {
            occupied = false;
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int occupiedSpots;
    private int totalProbes;

    public HashTableFundamentals(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    // park vehicle using linear probing
    public void parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedSpots++;
        totalProbes += probes;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // exit vehicle and compute fee
    public void exitVehicle(String plate) {
        int index = hash(plate);

        while (table[index].occupied) {
            if (table[index].licensePlate.equals(plate)) {

                long durationMillis = System.currentTimeMillis() - table[index].entryTime;
                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = Math.max(2.0, hours * 5); // $5 per hour, min $2

                table[index].occupied = false;
                occupiedSpots--;

                System.out.printf("Spot #%d freed. Duration: %.2f hrs, Fee: $%.2f\n",
                        index, hours, fee);
                return;
            }
            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    // find nearest available spot from entrance (spot 0)
    public int nearestAvailableSpot() {
        for (int i = 0; i < capacity; i++) {
            if (!table[i].occupied) return i;
        }
        return -1;
    }

    // statistics
    public void getStatistics() {
        double occupancy = (occupiedSpots * 100.0) / capacity;
        double avgProbes = occupiedSpots == 0 ? 0 : (double) totalProbes / occupiedSpots;

        System.out.println("Occupancy: " + occupancy + "%");
        System.out.println("Average Probes: " + avgProbes);
    }

    // demo
    public static void main(String[] args) throws InterruptedException {

        HashTableFundamentals parking = new HashTableFundamentals(10);

        parking.parkVehicle("ABC1234");
        parking.parkVehicle("XYZ9999");
        parking.parkVehicle("CAR5678");

        Thread.sleep(2000);

        parking.exitVehicle("ABC1234");

        System.out.println("Nearest free spot: " + parking.nearestAvailableSpot());

        parking.getStatistics();
    }
}