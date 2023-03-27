import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class Battleship3D {
    // main
    public static void main(String[] args) {
        // general variables
        String[][][] board;
        HashMap<String, Integer> ships = new HashMap<String, Integer>();
        Scanner s = new Scanner(System.in);

        // welcome
        System.out.println("Welcome to Battleship3D!");
        System.out.println("------------------------");

        // set up 3d board size
        System.out.print("Give the uniform size of your 3D board as a whole number: ");
        int size = s.nextInt();

        System.out.println("Creating 3D board of size " + size + "x" + size + "x" + size + "...");
        board = initGameArray(size);
        System.out.println("3D board initialized");
        System.out.println();

        // set up ship data
        System.out.println("Initializing ships...");
        initShipHashMap(ships);

        // set up ship placements
        for (String ship : ships.keySet()) { // placing should occur in here...
            System.out.println("Select a fulcrum point (<x><y><z>) to place your " + ship + " at.");
            System.out.print("Choose the x value: ");
            int x = s.nextInt() - 1;
            System.out.print("Choose the y value: ");
            int y = s.nextInt() - 1;
            System.out.print("Choose the z value: ");
            int z = s.nextInt() - 1;

            System.out.println();
            System.out.println("Evaluating positions...");
            HashMap<String, Boolean> validDirections = evalPlacementPos(board, size, x, y, z, ships.get(ship));

            while (!(checkHashMapValsTrue(validDirections))) {
                continue;
            }

            System.out.println("Possible directions for " + "(" + (x+1) +","+ (y+1) +","+ (z+1) +")" + ": ");
            for (String key: validDirections.keySet()) {
                System.out.println(key + ": " + validDirections.get(key));
            }
            System.out.println();
            
            String direction = null;
            boolean validDirectionInput = false;
            while (!(validDirectionInput)) {
                System.out.print("Which direction would you like to place the ship?: ");
                direction = s.nextLine();
                validDirectionInput = existsInHashMap(validDirections, direction);
            }

            ArrayList<int[]> updatedPositions = placeShip(board, ship, x, y, z, ships.get(ship), direction);
            for (int[] coordinate : updatedPositions) {
                System.out.println("Ship placed at coordinates: (" + (coordinate[0] + 1) + ", " + (coordinate[1] + 1) 
                                   + ", " +(coordinate[2] + 1) + ")");
            }
            System.out.println();

            printCube(board);
        }

        s.close();
    }

    // set 3d board size
    public static String[][][] initGameArray(int size) {
        return new String[size][size][size];
    }

    // set ships to be placed
    public static void initShipHashMap(HashMap<String, Integer> ships) {
        ships.put("Aircraft Carrier", 5);
        ships.put("Battleship", 4);
        ships.put("Destroyer", 3);
        ships.put("Submarine", 3);
        ships.put("Patrol Boat", 2);
        ships.put("John", 1);
    }

    // place the ships on the 3d board
    public static ArrayList<int[]> placeShip(String[][][] board, String ship, int centerX, int centerY, int centerZ, int length, String direction) {
        ArrayList<int[]> updatedCoordinates = new ArrayList<>();
        // set center coordinate to 1
        board[centerX][centerY][centerZ] = String.valueOf(ship.charAt(0));
        updatedCoordinates.add(new int[]{centerX, centerY, centerZ});
        // complete the rest
        switch(direction.toLowerCase()) {
            case "left":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY][centerZ - i] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX, centerY, centerZ - i});
                }
                break;
            case "right":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY][centerZ + i] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX, centerY, centerZ + i});
                }
                break;
            case "front":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY + i][centerZ] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX, centerY + i, centerZ});
                }
                break;
            case "back":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY - i][centerZ] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX, centerY - i, centerZ});
                }
                break;
            case "up":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX - i][centerY][centerZ] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX - i, centerY, centerZ});
                }
                break;
            case "down":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX + i][centerY][centerZ] = String.valueOf(ship.charAt(0));
                    updatedCoordinates.add(new int[]{centerX + i, centerY, centerZ}); 
                }
                break;
        }
        return updatedCoordinates;
    }    

    public static HashMap<String, Boolean> evalPlacementPos(String[][][] board, int size, int centerX, int centerY, int centerZ, int radius) {
        // array for validating the unobstructed directions from fulcrum point
        HashMap<String, Boolean> validDirections = new HashMap<String, Boolean>();
        validDirections.put("Up", true);
        validDirections.put("Down", true);
        validDirections.put("Left", true);
        validDirections.put("Right", true);
        validDirections.put("Front", true);
        validDirections.put("Back", true);

        // iterate through each index around the point of interest
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    // check if the point is within the desired range and in one of the valid directions
                    boolean directionInRange = (x == centerX && y == centerY) || (x == centerX && z == centerZ)
                            || (y == centerY && z == centerZ);
                    int distance = Math.abs(x - centerX) + Math.abs(y - centerY) + Math.abs(z - centerZ);
                    if (directionInRange && distance >= 1 && distance <= radius * 2) { // only consider points within range
                        // check if the current index is within bounds
                        String direction = getDirection(centerX, centerY, centerZ, x,y,z);

                        // if a direction has a point that is NOT valid, change the validDirections boolean array
                        if (!(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size)) {
                            validDirections.put(direction, false);
                        }
                        System.out.println("(" + (x+1)+","+(y+1)+","+(z+1)+")-"+direction+")");
                        //System.out.println("(" + (x+1)+","+(y+1)+","+(z+1)+")-"+direction+"-"+(isValid?"valid":"invalid"));
                    }
                }
            }
        }
        return validDirections;
    }

    // return the directions
    public static String getDirection(int centerX, int centerY, int centerZ, int x, int y, int z) {
        if (centerX == x && centerY == y && centerZ == z) {
            return "Center";
        } else if (x > centerX) {
            return "Right";
        } else if (x < centerX) {
            return "Left";
        } else if (y > centerY) {
            return "Up";
        } else if (y < centerY) { 
            return "Down";
        } else if (z > centerZ) {
            return "Behind";
        } else if (z < centerZ) {
            return "Front";
        }
        return null;
    }    

    public static boolean existsInHashMap(HashMap<String, Boolean> validDirections, String direction) {
        for (String key : validDirections.keySet()) {
            if (key.toLowerCase().equals(direction.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static void printCube(String[][][] arr3d) {
        for (int i = 0; i < arr3d.length; i++) {
            for (int j = 0; j < arr3d[i].length; j++) {
                for (int k = 0; k < arr3d[i][j].length; k++) {
                    System.out.print(arr3d[i][j][k] + " ");
                }
                System.out.println(); 
            }
            System.out.println();
        }
    }

    public static boolean checkHashMapValsTrue(HashMap<String, Boolean> validDirections) {
        for (boolean b: validDirections.values()) {
            if (b) {
                return true;
            }
        }
        return false;
    }
}