package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage; 

public class ManhattanARA extends Application {
	//Size of each tile in GridPane Visualization
	private final int tileSize = 12;
	@Override
	public void start(Stage primaryStage) {
		//GridPane Visualization colors////
		Image obs = createImage(Color.BLACK);
		Image emp = createImage(Color.WHITE);
		Image pat = createImage(Color.BLUE);
		Image sta = createImage(Color.GREEN);
		Image fin = createImage(Color.RED);
		//Prompt user for gridsize and obstacle density
		Scanner input = new Scanner(System.in);
		System.out.println("Please enter the length 'n' of the 'n x n' enviromment you would like to test (ex. '100' will create a 100 x 100 environment) :");
		int n = input.nextInt(); //size of n x n grid
		int size = n * n; //number of nodes
		System.out.println("Please enter the percent of obstacles you would like to fill the environment with (ex. '10' will create an environment with 10% of the nodes being randomly designated as obstacles) :");
		double obstaclePercent = input.nextInt(); //percentage of obstacles
		input.close();
		int numOfObstacles = (int)(size*(obstaclePercent/100)); //number of obstacles = number of nodes * % of obstacles (represents 10% as 10/100 = .1, .1 * 100 = 10/100 nodes as obstacles)
		//Initialize Timing variables
		long initialTime;
		long finalTime;
		long responseTime;
		// Initialize n x n grid
		Node[][] e = new Node[n][n];
		///////////////////////////////////////////////////// Create Coordinates for each Node
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				e[i][j] = new Node((new Coordinates(i, j)), 0);
			}
		} 
		Set<Point> pointSet = new HashSet<Point>(); //////////////////////////////////////////// HashSet to include the start, finish, and Obstacle
		Node start = e[(int)(Math.random() * n)][(int)(Math.random() * n)]; //randomly initialize start node
		Point sPoint = new Point();
		sPoint.x = start.xCoord;
		sPoint.y = start.yCoord;
		pointSet.add(sPoint); ////////////////// adds start node to the HashSet
		Node finish = e[(int)(Math.random() * n)][(int)(Math.random() * n)]; //randomly initialize goal node
		Point fPoint = new Point();
		fPoint.x = finish.xCoord;
		fPoint.y = finish.yCoord;
		pointSet.add(fPoint); //////////////// adds finish node to the HashSet
		while (pointSet.size() < 2) { ///////////////////////////////////////////////////////// ensures goal node initializes at different location than start (only runs if Goal was not added to HashSet)
			finish = e[(int)(Math.random() * n)][(int)(Math.random() * n)]; //initialize goal node
			fPoint = new Point();
			fPoint.x = finish.xCoord;
			fPoint.y = finish.yCoord;
			pointSet.add(fPoint); //if goal node is unique than start node, add it to the set so while loop stops
		}
		start.hValue = finish.xCoord - start.xCoord + finish.yCoord - start.yCoord; //initialize start hValue
		finish.hValue = 0; //initialize finish hValue
		double G = Double.POSITIVE_INFINITY; //cost of best solution (initialized to infinity until a solution is found)
		// Update hValue for each Node (distance to Finish)
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				////// Manhattan node.hValue = x.goal - x.node + y.goal - y.node
				e[i][j].hValue = Math.abs(finish.xCoord - e[i][j].xCoord) + Math.abs(finish.yCoord - e[i][j].yCoord);
			}
		}
		/////////////////////////////////////////////////////////////////////////randomized test obstacles
		int range = n;
		for (int q = 0; q < numOfObstacles; q++) {
			int xOb = (int)(Math.random() * range);
			int yOb = (int)(Math.random() * range);
			Point oPoint = new Point();
			oPoint.x = xOb;
			oPoint.y = yOb;
			q--;
			if (!(pointSet.contains(oPoint))) {
				pointSet.add(oPoint);
				e[xOb][yOb].hValue = 9999;
				q++;
			}
		}
		// Build Edges for each Node
		for (int i = 0; i < n; i++) { // Outer for-loop	
			for (int j = 0; j < n; j++) { // Inner for-loop
				//Top Row 
				if (i == 0) {
					if (j == 0) { e[i][j].edges = new ArrayList<Edge>(); // Top row, Left Column
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
					}
					} if (j == n-1) { e[i][j].edges = new ArrayList<Edge>(); // Top row, Right Column
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					} else if (j > 0 && j < n-1) { e[i][j].edges = new ArrayList<Edge>(); // Top Row, Inner Columns
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					}
				} //Bottom Row
				if (i == n-1) {
					if (j == 0) { e[i][j].edges = new ArrayList<Edge>(); // Bottom Row, Left Column
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
					}
					} if (j == n-1) { e[i][j].edges = new ArrayList<Edge>(); //  Bottom Row, Right Column
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					} else if (j > 0 && j < n-1) { e[i][j].edges = new ArrayList<Edge>(); // Bottom Row, Inner Columns
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					}
				} //Left Column 
				if (j == 0) {
					if (i == 0) { e[i][j].edges = new ArrayList<Edge>(); // Left Column, Top Row
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
					}
					} if (i == n-1) { e[i][j].edges = new ArrayList<Edge>(); // Left Column, Bottom Row
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
					}
					} else if (i > 0 && i < n-1) { e[i][j].edges = new ArrayList<Edge>(); // Left Column, Inner Rows
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
					}
					}
				} //Right Column 
				if (j == n-1) {
					if (i == 0) { e[i][j].edges = new ArrayList<Edge>(); // Right Column, Top Row
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					} if (i == n-1) { e[i][j].edges = new ArrayList<Edge>(); // Right Column, Bottom Row
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					} else if (i > 0 && i < n-1) { e[i][j].edges = new ArrayList<Edge>(); // Right Column, Inner Rows
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
					}
				} else if (i > 0 && i < n-1 && j > 0 && j < n-1) { //Inner Rows, Inner Columns
					e[i][j].edges = new ArrayList<Edge>(); 
					if (!(e[i][j].hValue == 9999)) {
						e[i][j].edges.add(new Edge(e[i - 1][j])); //up
						e[i][j].edges.add(new Edge(e[i][j + 1])); //right
						e[i][j].edges.add(new Edge(e[i + 1][j])); //down
						e[i][j].edges.add(new Edge(e[i][j - 1])); //left
					}
				}
			} // Inner for-loop
		} // Outer for-loop	
		// 2D Array Visualization
		String startt = "S";
		String finishh = "G";
		String pathway = "X";
		String empty = "E";
		String obstacle = "O";
		//Initialize every value in the finalArray to "E"
		String[][] finalArray = new String[n][n]; 
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				finalArray[i][j] = empty;
			}
		}

		//represent obstacles in finalArray to "O"
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				////// For each Node in Array, If hValue == 9999, then the array visual will represent that as an Obstacle
				if (e[i][j].hValue == 9999) {
					finalArray[i][j] = obstacle;
				}
			}
		}
		//Run Algorithm 3 & 4 with inputs: start/goal/w/wDecrement/G////////////////////////////
		initialTime = System.currentTimeMillis();
		List<Node> Solution = ARA(start, finish, .7, .1, G);
		finalTime = System.currentTimeMillis();
		responseTime = finalTime - initialTime;
		if (Solution.isEmpty()) {
			System.out.println("No path available from S to G. Obstacles block all possible paths.");
		}
		System.out.print("Path: ");
		System.out.println(Solution);
		//For each Node from the Solution Path, set the coordinates in the finalArray visualization to "X"
		for (Node q: Solution) {
			int xx = q.xCoord;
			int yy = q.yCoord;
			finalArray[xx][yy] = pathway;
		}
		//Initialize Start and Finish value in finalArray visualization
		finalArray[start.xCoord][start.yCoord] = startt;
		finalArray[finish.xCoord][finish.yCoord] = finishh;
		//Print the finalArray visualization as a 2D Matrix (now using JavaFX instead)
//		for (String[] row : finalArray) {
//			System.out.println(Arrays.toString(row));
//		}
		System.out.println("Algorithm runtime (ms): " + responseTime);
		System.out.println("Path Length: " + Solution.size());
		//Use finalArray to build the colors for the GridPane array
		Image[][] grid = new Image[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (finalArray[i][j] == "S") {
					grid[i][j] = sta;
				} else if (finalArray[i][j] == "G") {
					grid[i][j] = fin;
				} else if (finalArray[i][j] == "X") {
					grid[i][j] = pat;
				} else if (finalArray[i][j] == "E") {
					grid[i][j] = emp;
				} else if (finalArray[i][j] == "O") {
					grid[i][j] = obs;
				}
			}
		}
		////GridPane visualization Code////////////////////////////////
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setHgap(1);
		gridPane.setVgap(1);
		gridPane.setStyle("-fx-background-color: grey;");
		for (int y = 0 ; y < grid.length ; y++) {
			for (int x = 0 ; x < grid[y].length ; x++) {
				ImageView imageView = new ImageView(grid[y][x]);
				imageView.setFitWidth(tileSize);
				imageView.setFitHeight(tileSize);
				gridPane.add(imageView, x, y);
			}
		}
		ScrollPane sp = new ScrollPane(gridPane);
		//default stage size
		primaryStage.setWidth(1200);
		primaryStage.setHeight(700);
		Scene scene = new Scene(sp);
		primaryStage.setScene(scene);
		primaryStage.show();
		////End of GridPane visualization Code/////////////////////////
	}
	private Image createImage(Color color) {
		WritableImage image = new WritableImage(1, 1);
		image.getPixelWriter().setColor(0, 0, color);
		return image ;
	}
	
	public static void main(String[] args) {
		//Launches Visual
		launch(args);
	} // End of Main method

	//Algorithm 3//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static List<Node> ARA(Node s, Node f, double w, double wDecrement, double G) {
		PriorityQueue<Node> frontier = new PriorityQueue<Node>(new Comparator<Node>() {
			//compare method defined by whichever node's f_value is smaller
			public int compare(Node i, Node j) {
				if(i.fValue > j.fValue) {return 1;}
				else if (i.fValue < j.fValue) {return -1;}
				else{return 0;}
			} //end of Compare method override///////////////////
		}); // end of Priority Queue Comparator
		s.gValue = 0; //gValue at start node = 0
		List<Node> newSolution = new ArrayList<Node>();
		List<Node> incumbent = new ArrayList<Node>();
		List<Node> closed = new ArrayList<Node>();
		frontier.add(s); // Add start node to OPEN list
		while (!frontier.isEmpty()) {
			newSolution = improveSolution(frontier, w, G, f, s, closed);
			//If newSolution = 'none', then just return incumbent there was no better solution
			if (!(newSolution == null)) {
				G = newSolution.size() - 1;
				incumbent = newSolution;
			} //else, it contains none
			else {
				return incumbent;
			}
			w = w - wDecrement; //update value of w
			//---------------------------update keys for each node in OPEN
			for (Node k : frontier) {
				k.fValue = k.gValue + (w*k.hValue);
			}

			//---------------------------prune nodes g(.) + h(.) >= G
			while (!frontier.isEmpty()) {
				if (frontier.peek().gValue + frontier.peek().hValue >= G) {
					//pop the top off frontier
					frontier.remove();
				}
			}
		} // End of While-loop
		return incumbent;
	} // end of ARA Method

	//Algorithm 4////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static List<Node> improveSolution(PriorityQueue<Node> open, double w, double G, Node f, Node s, List<Node> closed) {
		closed.add(s);
		while (!open.isEmpty()) {
			Node current = open.poll(); //pop current node off frontier
			current.fValue = current.gValue + w*current.hValue;
			if (G <= (current.gValue + (w * current.hValue))) { // if G < weighted current.fValue of g(n) + w*h(n)
				return null; // return None, G best path is already is proven to be w-admissible
			}
			//no path found due to obstacles
			if ((current.edges).isEmpty()) {
				break;
			}
			for (Edge z : current.edges) { //For each successor of current node
				if (!open.contains(z.target) || current.gValue + 1 < z.target.gValue) { // If frontier doesn't contain n' || g(n) + 1 < g(n')
					//z.target.gValue = current.gValue + 1;  //update g(n')
					if (current.gValue + 1 + z.target.hValue < G) { //if g(n') + h(n') < G
						if (z.target == f) { //if n' is the goal node,
							z.target.parent = current;
							z.target.gValue = current.gValue + 1;
							z.target.fValue = z.target.gValue + (w*z.target.hValue);
							G = z.target.gValue; //formalize best path cost to G
							return path(s, f); //-----------------------------------------return best path to the goal
						} 
						if (closed.contains(z.target)) { // if n' is already in open, then update its position
							continue;
						}
						else if (!closed.contains(z.target)) { // otherwise, update n' keys and add n' to open
							z.target.parent = current;
							z.target.gValue = current.gValue + 1;
							z.target.fValue = z.target.gValue + (w*z.target.hValue);
							open.add(z.target);
							closed.add(z.target);
						}
					}
				}
			} // End of for-each loop
		} // End of while-loop
		return null; // return None
	} // End of improveSolution method

	//Path method//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static List<Node> path(Node start, Node goal) {
		List<Node> path = new ArrayList<Node>();
		//Initializing noPath for when no path from start to finish can be found
		List<Node> noPath = new ArrayList<Node>();
		Node none = new Node(new Coordinates(999, 999), 999); //none
		noPath.add(none);
		//loop adds finish to path, then sets finish = finish.parent, all the way to start
		path.add(goal);
		Node parent = goal.parent;
		while (parent != start) {
			path.add(parent);
			parent = parent.parent;
		}
		path.add(parent);
		//reverses order
		Collections.reverse(path);
		//If no path from start to finish can be found, then returns noPath
		if (!(path.contains(start))) {
			return noPath;
		}
		//Otherwise, return path
		return path;
	}
} // Main class
