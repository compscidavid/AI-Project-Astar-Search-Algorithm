package application;

import java.util.ArrayList;

public class Node {
	public String coordinates;
	public Coordinates location;
	public int xCoord;
	public int yCoord;
	public int gValue;
	public int hValue;
	public double fValue;
	public ArrayList<Edge> edges;
	//public Edge[] neighbors;
	public Node parent;
	
	public Node(Coordinates location, int hValue) {
		this.location = location;
		this.hValue = hValue;
		this.xCoord = location.x;
		this.yCoord = location.y;
		this.coordinates = Integer.toString(xCoord) + "," + Integer.toString(yCoord);
	}
	
	public String toString() {
		this.coordinates = Integer.toString(xCoord) + "," + Integer.toString(yCoord);
		return coordinates;
	}
}
