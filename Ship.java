import java.util.ArrayList;

public class Ship {
    private String symbol;
    private String name;
    private int length;
    private ArrayList<int[]> coordinates;
    private Boolean sunk;

    public Ship(String name, int length) {
        this.symbol = String.valueOf(name.charAt(0));
        this.name = name;
        this.length = length;
        this.coordinates = new ArrayList<int[]>();
        this.sunk = false;
    }

    // Getter method for the 'name' field
    public String getSymbol() {
        return symbol;
    }

    // Getter method for the 'name' field
    public String getName() {
        return name;
    }

    // Setter method for the 'name' field
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for the 'length' field
    public int getLength() {
        return length;
    }

    // Getter method for the 'coordinates' field
    public ArrayList<int[]> getCoordinates() {
        return coordinates;
    }

    // Setter method for the 'coordinates' field
    public void setCoordinates(ArrayList<int[]> coordinates) {
        this.coordinates = coordinates;
    }

    // Getter method for the 'sunk' field
    public Boolean isSunk() {
        return sunk;
    }

    // Setter method for the 'sunk' field
    public void setSunk(Boolean sunk) {
        this.sunk = sunk;
    }
}
