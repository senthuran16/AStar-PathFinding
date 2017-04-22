
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Senthuran Ambalavanar w1608452 2015215
 *
 */
class Cell {

    int x, y; // coordinates
    double gCost = 0; // G : distance from starting point
    double heuristicCost = 0; // H : distance to end point (approximate)
    double finalCost = 0; // F = G + H
    Cell parent; // cell, visited before the current cell

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + this.x + ", " + this.y + "]";
    }
}

public class PathFinding {

    // distance metrics
    // m : manhattan, e : euclidean, c : chebyshev
    static char metrics = 'm';

    // grid size
    static int n = 5;

    // probability : 0 - no blocks. Increase towards 1
    static double p = 0.4;

    // travelling costs
    public static int v_h_cost = 1;
    public static double diagonalCost = 2;

    //Blocked cells are just null Cell values in grid
    static Cell[][] grid = new Cell[n][n]; // available cells
    static boolean[][] matrix = new boolean[n][n]; // blocked status of cells

    // contains cells that were travelled from
    static ArrayList<Integer[]> path = new ArrayList<Integer[]>();

    // stores visitable (unvisited) cells
    static PriorityQueue<Cell> open;

    // 'visited' status of cells
    static boolean closed[][];

    // start & end coordinates
    static int startX, startY;
    static int endX, endY;

    /*
    for time calculations
    -----------------
    time1 = calculate path
    time2 = show path & grid in CLI
    time3 = show path & grid in GUI
     */
    static long time1_t1, time1_t2, time2_t1, time2_t2, time3_t1, time3_t2;

    public static void setBlocked(int x, int y) {
        grid[x][y] = null;
    }

    public static void setStartCell(int x, int y) {
        startX = x;
        startY = y;
    }

    public static void setEndCell(int x, int y) {
        endX = x;
        endY = y;
    }

    /**
     * check and update G & F costs when new F cost is less than old F cost
     *
     * @param current : current cell
     * @param temp : temporary cell (next cell to move)
     * @param cost : g cost for the temp cell ( g of current + moving cost )
     */
    static void updateCost(Cell current, Cell temp, double cost) {

        // if temp cell is blocked or visited through
        if (temp == null || closed[temp.x][temp.y]) {
            // no need to look for this cell to move
            return;
        }

        // temporary F value for the temp node
        double tempFinalCost = cost + temp.heuristicCost; // temp(F = G + H)

        // open status of temp cell
        boolean inOpen = open.contains(temp);

        // if temp cell is not calculated yet
        // or new f cost is less than old f cost
        if (!inOpen || tempFinalCost < temp.finalCost) {
            // update the new F & G costs for the temp cell
            temp.finalCost = tempFinalCost;
            temp.gCost = cost;

            // current cell is the parent for this temp cell
            temp.parent = current;

            if (!inOpen) {
                // open this cell
                open.add(temp);
            }
        }
    }

    /**
     * travels through the grid starts with starting point as the current cell
     * continues until current cell reaches the ending point
     */
    public static void travel() {

        //add the start location to open list.
        open.add(grid[startX][startY]);

        // to store current cell
        Cell current;

        // iterate until no more open blocks in queue or
        // end cell has reached
        while (true) {

            // remove the chosen item from priority queue
            // and assign as current cell
            current = open.poll();

            // no more cells available to travel
            if (current == null) {
                // exit from loop
                break;
            }

            // this cell is no more visitable
            closed[current.x][current.y] = true;

            /* end cell is closed in above line,
             since 'path found' is determined if end cell is closed */
            
            // current cell is the end cell
            // no need to look more
            if (current.equals(grid[endX][endY])) {
                return;
            }

            // update costs of connected cells
            // each connected cells are replaced in this
            Cell t;

            if (current.x - 1 >= 0) {

                // left cell
                t = grid[current.x - 1][current.y];
                updateCost(current, t, current.gCost + v_h_cost);

                if (current.y - 1 >= 0) {

                    if (metrics != 'm') {
                        // left bottom cell (not for manhattan)
                        t = grid[current.x - 1][current.y - 1];
                        updateCost(current, t, current.gCost + diagonalCost);
                    }

                }

                if (current.y + 1 < grid[0].length) {

                    if (metrics != 'm') {
                        // left top cell (not for manhattan)
                        t = grid[current.x - 1][current.y + 1];
                        updateCost(current, t, current.gCost + diagonalCost);
                    }
                }
            }

            if (current.y - 1 >= 0) {

                // bottom cell
                t = grid[current.x][current.y - 1];
                updateCost(current, t, current.gCost + v_h_cost);
            }

            if (current.y + 1 < grid[0].length) {

                // top cell
                t = grid[current.x][current.y + 1];
                updateCost(current, t, current.gCost + v_h_cost);
            }

            if (current.x + 1 < grid.length) {

                // right cell
                t = grid[current.x + 1][current.y];
                updateCost(current, t, current.gCost + v_h_cost);

                if (current.y - 1 >= 0) {

                    if (metrics != 'm') {
                        // right bottom cell (not for manhattan)
                        t = grid[current.x + 1][current.y - 1];
                        updateCost(current, t, current.gCost + diagonalCost);
                    }
                }

                if (current.y + 1 < grid[0].length) {

                    if (metrics != 'm') {
                        // top right cell (not for manhattan)
                        t = grid[current.x + 1][current.y + 1];
                        updateCost(current, t, current.gCost + diagonalCost);
                    }
                }
            }

            // while loop continues
        }
    }

    /**
     * sets heuristic values sets starting point, ending point, blocks, g & f
     * for starting point runs the path find by triggering travel() method
     * displays the path & grid in CLI
     *
     * @param n : grid size
     * @param sx : start location x
     * @param sy : start location y
     * @param ex : end location x
     * @param ey : end location y
     * @param blocked : int array of {blocked cell x,y coordinates as int array}
     */
    public static void run(int n, int sx, int sy, int ex, int ey, int[][] blocked) {

        // nxn grid
        grid = new Cell[n][n];

        // to be blocked at end
        //matrix = new boolean[n][n];
        // no cells are visited at beginning
        closed = new boolean[n][n];

        // priority queue to contain open cells
        // cell with least finalCost to be chosen as head
        open = new PriorityQueue<>(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {

                Cell c1 = (Cell) o1;
                Cell c2 = (Cell) o2;

                if (c1.finalCost < c2.finalCost) {
                    return -1; // this matters for priority
                } else if (c1.finalCost > c2.finalCost) {
                    return 1;
                } else {
                    return 0; // equal fCosts : then first in first out
                }
            }
        });

        // calculate path - time start
        time1_t1 = System.nanoTime();

        // set start position x,y static variables
        setStartCell(sx, sy);

        // set end position x,y static variables
        setEndCell(ex, ey);

        // calculate & assign heuristic costs
        for (int x = 0; x < n; x++) { // x
            for (int y = 0; y < n; y++) { // y

                // create a cell object in the grid with coordinates
                grid[x][y] = new Cell(x, y);

                // calculate & assign heuristic cost for cell object
                if (metrics == 'm') {

                    // manhattan
                    grid[x][y].heuristicCost = Math.abs(x - endX) + Math.abs(y - endY);

                } else if (metrics == 'e') {

                    // euclidean
                    grid[x][y].heuristicCost = Math.sqrt(Math.pow((x - endX), 2) + Math.pow((y - endY), 2));

                } else {

                    // chebyshev
                    grid[x][y].heuristicCost = Math.max(Math.abs(x - endX), Math.abs(y - endY));

                }
            }
        }

        // start location's g cost begins from 0
        grid[sx][sy].gCost = 0;
        grid[sx][sy].finalCost = grid[sx][sy].gCost + grid[sx][sy].heuristicCost; // F = G + H

        // create blocks in the grid        
        // @param : {X,Y} coordinates to block
        for (int i = 0; i < blocked.length; i++) {
            // for each cell to be blocked

            // block cell : indexed [x][y]
            setBlocked(blocked[i][0], blocked[i][1]);

            // block in matrix (to draw grid)
            //[blocked[i][0]][blocked[i][1]] = true;
        }

        // travel from starting point to ending point
        travel();

        // trace path ///////////////////////////////////////////////////////////
        if (closed[endX][endY]) {

            // start from end point
            Cell current = grid[endX][endY];
            path.add(new Integer[]{Integer.valueOf(current.x), Integer.valueOf(current.y)});

            // iterate until the super parent (starting cell) is found
            while (current.parent != null) {
                // add parent of the current node
                path.add(new Integer[]{Integer.valueOf(current.parent.x), Integer.valueOf(current.parent.y)});

                // ready for next parent finding
                current = current.parent;
            }

            // calculate path - time end
            time1_t2 = System.nanoTime();

            System.out.println("");
            System.out.println("Path found!");
            System.out.println("");

        } else {

            // end point has not reached
            // path stopped in middle
            // calculate path - time end
            time1_t2 = System.nanoTime();

            System.out.println("");
            System.out.println("No possible path!");
            System.out.println("");
        }
        ////////////////////////////////////////////////////////////////////////

        // display grid & path in CLI - time start
        time2_t1 = System.nanoTime();

        // display grid ////////////////////////////////////////////////////////
        System.out.println("Grid: ");
        for (int x = 0; x < n; x++) { // x
            for (int y = 0; y < n; y++) { // y

                boolean cont = false; // "in path arraylist" status

                for (Integer[] iarr : path) {

                    if (iarr[0] == x && iarr[1] == y) {
                        cont = true;
                    }

                }

                if (x == sx && y == sy) {
                    System.out.print("S  "); // start point
                } else if (x == ex && y == ey) {
                    System.out.print("E  ");  // end point
                } else if (cont) {
                    System.out.print("X  "); // travelled through
                } else if (grid[x][y] != null) {
                    System.out.printf("%-3d", 0); // normal open cell
                } else {
                    System.out.print("1  "); // blocked
                }

            }
            System.out.println();

        }
        System.out.println();
        ////////////////////////////////////////////////////////////////////////

        // display grid & path in CLI - time end
        time2_t2 = System.nanoTime();

    }

    /**
     * draw the N-by-N boolean matrix to standard draw
     *
     * @param a : boolean matrix
     * @param which : boolean status of open cells
     */
    public static void show(boolean[][] a, boolean which) {
        int N = a.length;
        StdDraw.setXscale(-1, N);;
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (a[i][j] == which) {
                    StdDraw.square(j, N - i - 1, .5);
                } else {
                    StdDraw.filledSquare(j, N - i - 1, .5);
                }
            }
        }
    }

    /**
     * draw the N-by-N boolean matrix to standard draw, including the points A
     * (x1, y1) and B (x2,y2) to be marked by dots
     *
     * @param a : boolean matrix
     * @param which : boolean status of open cells
     * @param x1 : starting point x
     * @param y1 : starting point y
     * @param x2 : ending point x
     * @param y2 : ending point y
     * @param path : traveled cells' ArrayList
     */
    public static void showPath(boolean[][] a, boolean which, int x1, int y1, int x2, int y2, ArrayList<Integer[]> path) {
        int N = a.length;
        StdDraw.setXscale(-1, N);
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                // check whether the cell is a member of path
                boolean cont = false;

                for (Integer[] iarr : path) {

                    if (iarr[0] == i && iarr[1] == j) {
                        cont = true;
                    }

                }

                if (cont) {

                    // cell is included in path
                    if (metrics != 'e') {
                        // not euclidean

                        // cell is in path
                        StdDraw.setPenColor(StdDraw.GREEN);
                        StdDraw.filledSquare(j, N - i - 1, .5);
                    }

                }

                if (a[i][j] == which) {

                    // unblocked cell
                    if (i == x1 && j == y1) {

                        // start point
                        StdDraw.setPenRadius(0.002);
                        StdDraw.setPenColor(StdDraw.BLUE);
                        StdDraw.filledCircle(j, N - i - 1, .3);
                    } else if (i == x2 && j == y2) {

                        // end point
                        StdDraw.setPenRadius(0.002);
                        StdDraw.setPenColor(StdDraw.RED);
                        StdDraw.filledCircle(j, N - i - 1, .3);
                    } else {

                        // open cell
                        StdDraw.setPenRadius(0.002);
                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.square(j, N - i - 1, .5);
                    }
                } else {

                    // blocked cell
                    StdDraw.setPenRadius(0.002);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledSquare(j, N - i - 1, .5);
                }
            }
        }

        // show red line
        for (int i = 1; i < path.size(); i++) {

            // euclidean path
            StdDraw.setPenRadius(0.008);
            StdDraw.setPenColor(StdDraw.RED);
            // y, n-1-x , y, n-1-x
            StdDraw.line(path.get(i - 1)[1], n - 1 - path.get(i - 1)[0], path.get(i)[1], n - 1 - path.get(i)[0]);

        }

    }

    /**
     * returns a random N x N boolean matrix where each entry is true with
     * probability p
     *
     * @param N : matrix size
     * @param p : probability
     * @return
     */
    public static boolean[][] random(int N, double p) {
        boolean[][] a = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                a[i][j] = StdRandom.bernoulli(p);
            }
        }
        return a;
    }

    public static void main(String[] args) throws Exception {

        try {

            System.out.println("PATH FINDING ON SQUARE GRID");
            System.out.println("=============");
            System.out.println("[m] Manhattan");
            System.out.println("[e] Euclidean");
            System.out.println("[c] Chebyshev");

            Scanner sc1 = new Scanner(System.in);
            System.out.println("");
            System.out.println("Select your choice : ");
            metrics = sc1.nextLine().charAt(0);

            switch (metrics) {
                case 'm':
                    // manhattan
                    diagonalCost = 2; // cost for diagonal : anyhow no use
                    v_h_cost = 1; // cost for vertical & horizontal
                    break;

                case 'e':
                    // euclidean
                    diagonalCost = Math.sqrt(2.0); // cost for diagonal
                    v_h_cost = 1; // cost for vertical & horizontal
                    break;
                case 'c':
                    // chebyshev
                    diagonalCost = 1; // cost for diagonal
                    v_h_cost = 1; // cost for vertical & horizontal
                    break;
                default:
                    // manhattan
                    System.out.println("");
                    System.out.println("Invalid choice. Going for Manhattan metrics");
                    System.out.println("");

                    metrics = 'm';
            }

            System.out.println("\nEnter Grid Size : ");
            n = sc1.nextInt();

            System.out.println("\nEnter Probability for blocks (0 - No blocks. Increase towards 1) : ");
            p = sc1.nextDouble();

            // generate random blocks : boolean[][]          
            matrix = random(n, p);

            // show grid with blocks in StdDraw
            show(matrix, false);

            Scanner sc = new Scanner(System.in);
            int sx, sy, ex, ey;
            System.out.println("Enter Starting point (x y) : ");
            sy = sc.nextInt();
            sx = sc.nextInt();
            System.out.println("Enter Ending point (x y) : ");
            ey = sc.nextInt();
            ex = sc.nextInt();

            // get blocked cells x,y coordinates ///////////////////////////////
            // put blocked cells to arraylist
            ArrayList<int[]> aMatrix = new ArrayList<int[]>();

            // convert arraylist to array
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {

                    // if blocked cell
                    if (matrix[i][j]) {

                        // add blocked cell's coordinates as int[]
                        aMatrix.add(new int[]{i, j});
                    }
                }
            }

            int[][] intMatrix = new int[aMatrix.size()][2];

            int counter = 0;

            for (int[] ia : aMatrix) {
                intMatrix[counter][0] = ia[0]; // x coordinate
                intMatrix[counter][1] = ia[1]; // y coordinate
                counter++;
            }

            ////////////////////////////////////////////////////////////////////
            // find path after blocking randomly got cells
            run(n, sx, sy, ex, ey, intMatrix);

            // display grid & path in StdDraw - time start
            time3_t1 = System.nanoTime();

            // show grid with path & start,end points
            showPath(matrix, false, sx, sy, ex, ey, path);

            // display grid & path in StdDraw - time end
            time3_t2 = System.nanoTime();

            if (metrics == 'e') {
                System.out.println("TOTAL COST : " + grid[ex][ey].finalCost);
            } else {
                System.out.println("TOTAL COST : " + grid[ex][ey].finalCost);
            }

            // output elapsed times
            System.out.println("");
            System.out.println("ELAPSED TIMES");
            System.out.println("=============");
            System.out.println("Find Path              : " + ((time1_t2 - time1_t1) / 1000000.0) + " milliseconds");
            System.out.println("Display Grid (CLI)     : " + ((time2_t2 - time2_t1) / 1000000.0) + " milliseconds");
            System.out.println("Display Grid (StdDraw) : " + ((time3_t2 - time3_t1) / 1000000.0) + " milliseconds");
            System.out.println("");

        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(null, "Can't travel from a blocked cell!", "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Something unexpectedly happened!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}
