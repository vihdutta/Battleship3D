import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Battleship3D {
    // main
    public static void main(String[] args) {
        // general variables
        String[][][] board;
        String[][][] playerBoard;
        ArrayList<Ship> ships = new ArrayList<Ship>();
        // HashMap<String, Integer> ships = new HashMap<String, Integer>();
        Scanner s = new Scanner(System.in);

        // welcome
        System.out.println("Welcome to Battleship3D!");
        System.out.println("------------------------");

        // set up 3d board size
        System.out.print("Give the uniform size of your 3D board as a whole number: ");
        int size = s.nextInt();

        System.out.println("Creating 3D board of size " + size + "x" + size + "x" + size + "...");
        board = initGameArray(size);
        playerBoard = initGameArray(size);
        System.out.println("3D board initialized");
        System.out.println();

        // set up ship data
        System.out.println("Initializing ships...");
        initShips(ships);

        // set up ship placements
        System.out.println("Do you want ships to be placed by computer? (y/n)");
        s.nextLine();
        String placementInput = s.nextLine();

        boolean autoPlacement = (placementInput.toLowerCase().equals("y")) ? true : false;
        for (Ship ship : ships) {
            boolean possibleToMove = false;
            HashMap<String, Boolean> validDirections = new HashMap<String, Boolean>();

            if (autoPlacement) {
                autoPlacement(board, size, ship, possibleToMove, validDirections);
            } else {
                manualPlacement(board, size, ship, possibleToMove, validDirections, s);
            }
        }

        // ask if they want to print
        System.out.println();
        System.out.println("Play blindly? No board/miss messages; recommended for large autoplay matches. y/n");
        String hideBoardInput = s.nextLine();
        boolean hideBoard = (hideBoardInput.toLowerCase().equals("y")) ? true : false;

        // print the board
        boolean revealLocations = false;
        if (!hideBoard) {
            System.out.println();
            System.out.println("Show locations of the ships? y/n");
            String revealLocationInput = s.nextLine();
            revealLocations = (revealLocationInput.toLowerCase().equals("y")) ? true : false;
        }

        if (!hideBoard) {
            if (revealLocations) {
                printCube(board);
            } else {
                printCube(playerBoard);
            }
        }   

        System.out.println();
        System.out.println("Autoplay? y/n");
        String autoPlayInput = s.nextLine();
        boolean autoPlay = (autoPlayInput.toLowerCase().equals("y")) ? true : false;

        int shots = 0;

        long start = System.currentTimeMillis();
        while (gameAlive(board, ships)) {
            String hitOrMiss = null;
            if (autoPlay) {
                hitOrMiss = autoFire(board, playerBoard, size, ships);
            } else {
                hitOrMiss = fire(board, playerBoard, ships, s);
            }
            shots += 1;

            if (!hideBoard) {
                System.out.println();
                System.out.println("Current board: ");
            }
    
            if (!hideBoard) {
                if (revealLocations) {
                    printCube(board);
                } else {
                    printCube(playerBoard);
                }
            }

            if (hideBoard) {
                if (!hitOrMiss.equals("Miss!")) {
                    if (hitOrMiss.equals("Hit! ")) {
                        System.out.print(hitOrMiss);
                    } else {
                        System.out.println(hitOrMiss);
                    }
                }
            } else {
                System.out.println(hitOrMiss);
            }

        }
        long elapsed = System.currentTimeMillis() - start;
        System.out.println();
        System.out.println("Congrats! You sunk all the ships in " + shots + " shots and " + elapsed / 1000.0 + " seconds!");

        s.close();
    }

    // set 3d board size
    public static String[][][] initGameArray(int size) {
        String[][][] board = new String[size][size][size];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                for (int k = 0; k < board[i][j].length; k++) {
                    board[i][j][k] = "~";
                }
            }
        }
        return board;
    }

    // set ships to be placed
    public static void initShips(ArrayList<Ship> ships) {
        ships.add(new Ship("Aircraft Carrier", 5));
        ships.add(new Ship("Battleship", 4));
        ships.add(new Ship("Destroyer", 3)); //3
        ships.add(new Ship("Submarine", 3)); //3
        ships.add(new Ship("Patrol Boat", 2));
    }

    // place the ships on the 3d board
    public static ArrayList<int[]> placeShip(String[][][] board, Ship ship, int centerX, int centerY, int centerZ,
            int length, String direction) {
        ArrayList<int[]> updatedCoordinates = new ArrayList<>();
        // set center coordinate to 1
        board[centerX][centerY][centerZ] = ship.getSymbol();
        updatedCoordinates.add(new int[] { centerX, centerY, centerZ });
        // complete the rest
        switch (direction.toLowerCase()) {
            case "left":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX][centerY][centerZ - i] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX, centerY, centerZ - i });
                }
                break;
            case "right":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX][centerY][centerZ + i] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX, centerY, centerZ + i });
                }
                break;
            case "front":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX][centerY + i][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX, centerY + i, centerZ });
                }
                break;
            case "back":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX][centerY - i][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX, centerY - i, centerZ });
                }
                break;
            case "up":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX - i][centerY][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX - i, centerY, centerZ });
                }
                break;
            case "down":
                for (int i = 1; i <= length - 1; i++) {
                    board[centerX + i][centerY][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[] { centerX + i, centerY, centerZ });
                }
                break;
        }
        return updatedCoordinates;
    }

    public static HashMap<String, Boolean> evalPlacementPos(String[][][] board, int size, int centerX, int centerY,
            int centerZ, int radius, boolean verbose) {
        // array for validating the unobstructed directions from fulcrum point
        HashMap<String, Boolean> validDirections = new HashMap<String, Boolean>();
        validDirections.put("Up", true);
        validDirections.put("Down", true);
        validDirections.put("Right", true);
        validDirections.put("Left", true);
        validDirections.put("Front", true);
        validDirections.put("Back", true);

        try {
            if (!board[centerX][centerY][centerZ].equals("~")) {
                for (Map.Entry<String, Boolean> entry : validDirections.entrySet()) {
                    entry.setValue(false);
                }
                return validDirections;
            }
        } catch (Exception e) {
            System.out.println("Not a valid position! " + e);
            for (Map.Entry<String, Boolean> entry : validDirections.entrySet()) {
                entry.setValue(false);
            }
            return validDirections;
        }

        // iterate through each index around the point of interest
        for (int z = centerZ - radius; z <= centerZ + radius; z++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    // check if the point is within the desired range and in one of the valid
                    // directions
                    boolean directionInRange = (x == centerX && y == centerY) || (x == centerX && z == centerZ)
                            || (y == centerY && z == centerZ);
                    int distance = Math.abs(x - centerX) + Math.abs(y - centerY) + Math.abs(z - centerZ);
                    if (directionInRange && distance >= 1 && distance <= radius * 2) { // only consider points within
                                                                                       // range
                        // check if the current index is within bounds
                        // String direction = getDirection(centerX, centerY, centerZ, x,y,z);
                        String direction = getDirection(centerZ, centerY, centerX, x, y, z);

                        // if a direction has a point that is NOT valid, change the validDirections
                        // boolean array
                        if (!(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size)) {
                            validDirections.put(direction, false);
                        }
                        try {
                            if (!board[x][y][z].equals("~")) {
                                validDirections.put(direction, false);
                            }
                        } catch (Exception e) {
                            validDirections.put(direction, false);
                        }

                        if (verbose) {
                            System.out.println("(" + (x + 1) + "," + (y + 1) + "," + (z + 1) + ")-" + direction + "");
                        }
                        // System.out.println("(" +
                        // (x+1)+","+(y+1)+","+(z+1)+")-"+direction+"-"+(isValid?"valid":"invalid"));
                    }
                }
            }
        }
        return validDirections;
    }

    public static String autoFire(String[][][] board, String[][][] playerBoard, int size, ArrayList<Ship> ships) {
        boolean notAlreadySelected = true;
        int x = 0;
        int y = 0;
        int z = 0;
        String target = "mr. caudle noo my code isn't working noooo";
        while (notAlreadySelected) {
            x = (int) (Math.random() * size);
            y = (int) (Math.random() * size);
            z = (int) (Math.random() * size);

            target = board[x][y][z];
            if (target.equals("~") || Character.isAlphabetic(target.charAt(0))) {
                // check if the selected coordinates have not already been targeted
                if (!target.equals("*") && !target.equals("X")) {
                    notAlreadySelected = false;
                }
            }
        }

        if (!target.equals("~") && !target.equals("*") && !target.equals("X")) {
            String symbol = board[x][y][z];
            board[x][y][z] = "X"; // hit
            playerBoard[x][y][z] = "X";
            return "Hit! " + sunkOrNot(board, ships, symbol);
        }
        board[x][y][z] = "*";
        playerBoard[x][y][z] = "*";
        return "Miss!";
    }

    public static String fire(String[][][] board, String[][][] playerBoard, ArrayList<Ship> ships, Scanner s) {
        int x = 0;
        int y = 0;
        int z = 0;

        while (true) {
            System.out.println("Select a point (<x><y><z>) to fire at.");
            System.out.print("Choose the x value: ");
            x = s.nextInt() - 1;
            System.out.print("Choose the y value: ");
            y = s.nextInt() - 1;
            System.out.print("Choose the z value: ");
            z = s.nextInt() - 1;
            if (x < 0 || y < 0 || z < 0 || x >= board.length || y >= board[0].length || z >= board[0][0].length) {
                System.out.println("Invalid input, please try again: ");
            } else {
                break;
            }
        }

        String target = board[x][y][z];
        if (!target.equals("~") && !target.equals("*") && !target.equals("X")) {
            String symbol = board[x][y][z];
            board[x][y][z] = "X"; // hit
            playerBoard[x][y][z] = "X";
            return "Hit! " + sunkOrNot(board, ships, symbol);
        }
        board[x][y][z] = "*";
        playerBoard[x][y][z] = "*";
        return "Miss!";
    }

    // check if there's any more of the specific ship left
    public static String sunkOrNot(String[][][] board, ArrayList<Ship> ships, String symbol) {
        // get the name of the ship
        String shipName = "";
        for (Ship ship : ships) {
            if (ship.getSymbol().equals(symbol)) {
                shipName = ship.getName();
            }
        }

        // check if ship symbol still exists
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                for (int z = 0; z < board[0][0].length; z++) {
                    if (board[x][y][z].equals(symbol)) {
                        return ""; // no, ship still alive
                    }
                }
            }
        }

        // if it gets past the loop, it means no more symbol exists of the ship and it has been sunk from the hit!
        return "You sunk the " + shipName + "!";
    }

    // return the directions
    public static String getDirection(int centerZ, int centerY, int centerX, int z, int y, int x) {
        if (centerZ == z && centerY == y && centerX == x) {
            return "Center";
        } else if (x > centerX) {
            return "Right";
        } else if (x < centerX) {
            return "Left";
        } else if (y > centerY) {
            return "Front";
        } else if (y < centerY) {
            return "Back";
        } else if (z > centerZ) {
            return "Down";
        } else if (z < centerZ) {
            return "Up";
        } else {
            return "Unknown";
        }
    }

    public static boolean existsInHashMap(HashMap<String, Boolean> validDirections, String direction) {
        for (String key : validDirections.keySet()) {
            if (key.toLowerCase().equals(direction.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static void printCube(String[][][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                for (int k = 0; k < board[i][j].length; k++) {
                    System.out.print(board[i][j][k] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static boolean checkHashMapValsTrue(HashMap<String, Boolean> validDirections) {
        for (boolean b : validDirections.values()) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    public static void setShipsSunkValues(String[][][] board, ArrayList<Ship> ships) {
        for (Ship ship : ships) {
            boolean isSunk = true; // assume the ship is sunk
            for (int[] coordinate : ship.getCoordinates()) {
                int x = coordinate[0];
                int y = coordinate[1];
                int z = coordinate[2];
                if (!board[x][y][z].equals("X")) {
                    isSunk = false; // if any coordinate is not "X", the ship is not sunk
                    break;
                }
            }
            ship.setSunk(isSunk);
        }
    }

    public static boolean gameAlive(String[][][] board, ArrayList<Ship> ships) {
        setShipsSunkValues(board, ships);
        for (Ship ship : ships) {
            if (ship.isSunk() == false) {
                return true;
            }
        }
        return false;
    }

    public static void autoPlacement(String[][][] board, int size, Ship ship, boolean possibleToMove,
            HashMap<String, Boolean> validDirections) {
        System.out.println("Placing the ships...");
        int x = 0;
        int y = 0;
        int z = 0;
        while (!(possibleToMove)) {
            try {
                x = (int) (Math.random() * size - 1) + 1;
                y = (int) (Math.random() * size - 1) + 1;
                z = (int) (Math.random() * size - 1) + 1;

                validDirections = evalPlacementPos(board, size, x, y, z, ship.getLength() - 1, false);
                possibleToMove = checkHashMapValsTrue(validDirections);
            } catch (Exception e) {
                System.out.println("Direction error...");
            }
        }

        // make an ArrayList that includes only the true values.
        try {
            ArrayList<String> validKeys = new ArrayList<String>();
            for (Map.Entry<String, Boolean> entry : validDirections.entrySet()) {
                if (entry.getValue()) {
                    validKeys.add(entry.getKey());
                }
            }

            // choose a valid direction
            String direction = validKeys.get((int) (Math.random() * validKeys.size()));

            // place the ship
            ArrayList<int[]> updatedPositions = placeShip(board, ship, x, y, z, ship.getLength(), direction);
            ship.setCoordinates(updatedPositions);
        } catch (Exception e) {
            System.out.println("Placement error...");
            autoPlacement(board, size, ship, possibleToMove, validDirections);
        }
    }

    public static void manualPlacement(String[][][] board, int size, Ship ship, boolean possibleToMove,
            HashMap<String, Boolean> validDirections, Scanner s) {
        int x = 0;
        int y = 0;
        int z = 0;
        while (!(possibleToMove)) {
            System.out.println("Select a fulcrum point (<x><y><z>) to place your " + ship.getName() + " at.");
            System.out.print("Choose the x value: ");
            x = s.nextInt() - 1;
            System.out.print("Choose the y value: ");
            y = s.nextInt() - 1;
            System.out.print("Choose the z value: ");
            z = s.nextInt() - 1;

            System.out.println();
            System.out.println("Evaluating positions...");
            validDirections = evalPlacementPos(board, size, x, y, z, ship.getLength() - 1, true);
            System.out.println();
            possibleToMove = checkHashMapValsTrue(validDirections);
        }

        System.out.println("Possible directions for " + "(" + (x + 1) + "," + (y + 1) + "," + (z + 1) + ")" + ": ");
        for (String key : validDirections.keySet()) {
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

        ArrayList<int[]> updatedPositions = placeShip(board, ship, x, y, z, ship.getLength(), direction);
        ship.setCoordinates(updatedPositions);
        for (int[] coordinate : updatedPositions) {
            System.out.println("Ship placed at coordinates: (" + (coordinate[0] + 1) + ", " + (coordinate[1] + 1)
                    + ", " + (coordinate[2] + 1) + ")");
        }
    }
}