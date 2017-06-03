package model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import util.MyPopupMenu;
import util.MyPopupTriggerListener;
import util.MyActionListener;

public class Grid {
	static Grid grid = null;

	static Node[][] matrix;
	static int columns;
	static int lines;

	static JFrame main_frame;
	static JButton[][] nodes;

	static Node source;
	static Node actual;
	static Node destiny;

	static Color actualColor = new Color(155, 205, 255);
	static Color visitedColor = new Color(205, 230, 255);
	static Color noOptionsColor = new Color(255, 178, 102);

	static Color sourceColor = new Color(204, 153, 255);
	static Color destinyColor = new Color(255, 205, 230);

	static Color validColor = new Color(155, 255, 155);
	static Color invalidColor = new Color(255, 155,155);

	static long delay = 5;
	public static int bombPower = 1;

	static State state = new State();

	final JFileChooser fc = new JFileChooser("C:\\");

	public static Grid getSharedInstance() {
		if (grid == null) {
			@SuppressWarnings("resource")
			Scanner entrada = new Scanner(System.in);
			System.out.print("Digite a altura do Grid: ");
			int lines = entrada.nextInt();
			System.out.print("Digite a larguta do Grid: ");
			int columns = entrada.nextInt();
			grid = new Grid(lines, columns);
			return grid;
		} else {
			return grid;
		}
	}

	public Grid(int lines, int columns) {
		matrix = new Node[lines][columns];
		Grid.columns = columns;
		Grid.lines = lines;

		for(int i = 0; i < lines; i++) {
			for(int j = 0; j < columns; j++){
				matrix[i][j] = new Node(i,j, columns);
			}
		}
	}

	public void happyPath() throws InterruptedException {

	}

	public Point getBestDistance() { //Pega o ponto com a melhor distância //>> COMPLEXIDADE: O(61)
		if (actual.decisions == null) { 																						// COMPLEXIDADE: 1
			actual.decisions = calcDistances(); 																				// COMPLEXIDADE: O(22)
		}

		if (actual.decisions.size() > 0) { 																						// COMPLEXIDADE: 1
			Point toReturn; 																									// COMPLEXIDADE: 1
								// Se tem apenas 1 decisao possivel 						// COMPLEXIDADE: 1
			toReturn = actual.decisions.get(0); 																			// COMPLEXIDADE: 1

			actual.decisions.remove(toReturn); 						// Remove a decisao tomada da lista de decisoes possiveis	// COMPLEXIDADE: O(4), pois decisions tem size máximo igual a  4
			return toReturn; 										// COMPLEXIDADE: 1
		}
		return new Point();											// COMPLEXIDADE: 1
	}



	public Point getLower(ArrayList<Point> points) {
		Point toReturn;									// COMPLEXIDADE: 1
		if (points.size() > 0) {						// COMPLEXIDADE: 1
			Point p = points.get(0);					// COMPLEXIDADE: 1
			toReturn = p;								// COMPLEXIDADE: 1
			double lower = matrix[p.i][p.j].distance;	// COMPLEXIDADE: 1
			for(int x = 1; x < points.size(); x++) {	// COMPLEXIDADE: O(3), uma vez que points tem size 4 no max, e o laço itera a partir da posicao 1
				p = points.get(x);						// COMPLEXIDADE: 1
				if(matrix[p.i][p.j].distance < lower){	// COMPLEXIDADE: 1
					toReturn = p;						// COMPLEXIDADE: 1
					lower = matrix[p.i][p.j].distance;	// COMPLEXIDADE: 1
				}
			}
			return toReturn;							// COMPLEXIDADE: 1
		}
		return null;									// COMPLEXIDADE: 1
	}

	public ArrayList<Point> calcDistances(){
		return new ArrayList<Point>();
	}

	public boolean wasVisited(Point p) {
		return (matrix[p.i][p.j].visited);
	}

	public boolean isAdjacent(int id1, int id2) {
		return true;
	}

	public static void main(String args[]) throws InterruptedException {
		Grid.getSharedInstance();
	}
}
