package model;

import java.util.ArrayList;

public class Node {
	int id;
	boolean valid;
	boolean visited;
	double distance;
	int i, j;
	ArrayList<Point> decisions;
	
	public Node(int line, int column, int total){	
		this.id = line*total+column;
		this.distance = -1;
		this.valid = true;
		this.visited = false;
		
		this.i = line;
		this.j = column;
	}
	
	public int getId() {
		return this.id;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ArrayList<Point> getDecisions() {
		return decisions;
	}

	public void setDecisions(ArrayList<Point> decisions) {
		this.decisions = decisions;
	}
}
