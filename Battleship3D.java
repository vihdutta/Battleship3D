import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class Battleship3D {
    // main
    public static void main(String[] args) {
        // general variables
        String[][][] board;
        ArrayList<Ship> ships = new ArrayList<Ship>();
        //HashMap<String, Integer> ships = new HashMap<String, Integer>();
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
        initShips(ships);

        // set up ship placements
        for (Ship ship : ships) { // placing should occur in here...
            boolean possibleToMove = false;
            
            int x = 0;
            int y = 0;
            int z = 0;
            HashMap<String, Boolean> validDirections = new HashMap<String, Boolean>();
            
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
                validDirections = evalPlacementPos(board, size, x, y, z, ship.getLength()-1);
                System.out.println();
                possibleToMove = checkHashMapValsTrue(validDirections);
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

            ArrayList<int[]> updatedPositions = placeShip(board, ship, x, y, z, ship.getLength(), direction);
            ship.setCoordinates(updatedPositions);
            for (int[] coordinate : updatedPositions) {
                System.out.println("Ship placed at coordinates: (" + (coordinate[0] + 1) + ", " + (coordinate[1] + 1) 
                                   + ", " +(coordinate[2] + 1) + ")");
            }
            System.out.println();

            printCube(board);
        }
        
        // while all the ships do not have an attribute of destroyed... WIP
        System.out.println("Is game alive?: " + gameAlive(board, ships));
        int shots = 0;
        while (gameAlive(board, ships)) {
           System.out.println("Select a point (<x><y><z>) to fire at.");
           System.out.print("Choose the x value: ");
           int x = s.nextInt() - 1;
           System.out.print("Choose the y value: ");
           int y = s.nextInt() - 1;
           System.out.print("Choose the z value: ");
           int z = s.nextInt() - 1;
           
           String hitOrMiss = fire(board, x, y, z);
           shots += 1;
           
           System.out.println();
           System.out.println("Current board: ");
           printCube(board);
           System.out.println(hitOrMiss);
        }
        System.out.println("Congrats! You sunk all the ships in " + shots + " shots!");

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
         ships.add(new Ship("Destroyer", 3));
         ships.add(new Ship("Submarine", 3));
         ships.add(new Ship("Patrol Boat", 2));
         ships.add(new Ship("John", 1));
    }

    // place the ships on the 3d board
    public static ArrayList<int[]> placeShip(String[][][] board, Ship ship, int centerX, int centerY, int centerZ, int length, String direction) {
        ArrayList<int[]> updatedCoordinates = new ArrayList<>();
        // set center coordinate to 1
        board[centerX][centerY][centerZ] = ship.getSymbol();
        updatedCoordinates.add(new int[]{centerX, centerY, centerZ});
        // complete the rest
        switch(direction.toLowerCase()) {
            case "left":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY][centerZ - i] = ship.getSymbol();
                    updatedCoordinates.add(new int[]{centerX, centerY, centerZ - i});
                }
                break;
            case "right":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY][centerZ + i] = ship.getSymbol();
                    updatedCoordinates.add(new int[]{centerX, centerY, centerZ + i});
                }
                break;
            case "front":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY + i][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[]{centerX, centerY + i, centerZ});
                }
                break;
            case "back":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX][centerY - i][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[]{centerX, centerY - i, centerZ});
                }
                break;
            case "up":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX - i][centerY][centerZ] = ship.getSymbol();
                    updatedCoordinates.add(new int[]{centerX - i, centerY, centerZ});
                }
                break;
            case "down":
                for (int i = 1; i <= length-1; i++) {
                    board[centerX + i][centerY][centerZ] = ship.getSymbol();
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
    } catch(Exception e) {
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
                // check if the point is within the desired range and in one of the valid directions
                boolean directionInRange = (x == centerX && y == centerY) || (x == centerX && z == centerZ)
                        || (y == centerY && z == centerZ);
                int distance = Math.abs(x - centerX) + Math.abs(y - centerY) + Math.abs(z - centerZ);
                if (directionInRange && distance >= 1 && distance <= radius * 2) { // only consider points within range
                    // check if the current index is within bounds
                    //String direction = getDirection(centerX, centerY, centerZ, x,y,z);
                    String direction = getDirection(centerZ, centerY, centerX, x,y,z);

                    // if a direction has a point that is NOT valid, change the validDirections boolean array
                    if (!(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size)) {
                        validDirections.put(direction, false);
                    }
                    try {
                        if (!board[x][y][z].equals("~")) {
                            validDirections.put(direction, false);
                        }   
                    } catch(Exception e) {
                        validDirections.put(direction, false);
                    }
                    System.out.println("(" + (x+1)+","+(y+1)+","+(z+1)+")-"+direction+"");
                    //System.out.println("(" + (x+1)+","+(y+1)+","+(z+1)+")-"+direction+"-"+(isValid?"valid":"invalid"));
                }
            }
        }
    }
    return validDirections;
}
    
    public static String fire(String[][][] board, int centerX, int centerY, int centerZ) {
      String target = board[centerX][centerY][centerZ];
      if (!target.equals("~") && !target.equals("*") && !target.equals("X")) {
         board[centerX][centerY][centerZ] = "X"; // hit
         return "Hit!";
      }
      board[centerX][centerY][centerZ] = "*";
      return "Miss!";
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
}