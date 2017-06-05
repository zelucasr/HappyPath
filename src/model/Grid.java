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
		paintGrid();
	}

	public void happyPath() throws InterruptedException {
		if (destiny == null || source == null) {
			System.out.println("Defina um nó Source e um nó Destiny.");
		} else {
			saveState();
			//long tempoInicial = System.nanoTime();
			// ALGORITMO
			//>> COMPLEXIDADE PARTE 1: O(268n) > O(n)
			//>> COMPLEXIDADE PARTE 2: O(4n) + Omega((9/8)(n^2 - 2n))
			//>> COMPLEXCIDADE TOTAL: O(272n) + Omega((9/8)(n^2 - 2n))

			ArrayList<Integer> resultado = new ArrayList<Integer>();	// COMPLEXIDADE: 1
			actual = source;											// COMPLEXIDADE: 1
			resultado.add(actual.id);									// COMPLEXIDADE: 1
			// PARTE 1 //>> COMPLEXIDADE: 268n
			while (actual.id != destiny.id) {							// COMPLEXIDADE: O(4n)
				// SEM INTERFACE
//				Point next = getBestDistanceSemInterface(); 			// COMPLEXIDADE: O(61)
//				if (next.i != -1) {										// COMPLEXIDADE: 1
//					actual.visited = true;								// COMPLEXIDADE: 1
//					if (actual.decisions.size() == 0) {					// COMPLEXIDADE: 1
//						matrix[actual.i][actual.j].setValid(false); 	// COMPLEXIDADE: 1
//					}
//					actual = matrix[next.i][next.j];					// COMPLEXIDADE: 1
//					resultado.add(actual.id);							// COMPLEXIDADE: 1
//				} else {
//					resultado.add(-1);									// COMPLEXIDADE: 1
//					System.out.println("Não achou caminho.");			// Interface, não entra na conta.
//					break;
//				}

				// COM INTERFACE
				Point next = getBestDistance();
				if (next.i != -1) {
					setVisited(actual);
					if (actual.decisions.size() == 0) {
						setInvalid(actual.i, actual.j);
					}
					actual = matrix[next.i][next.j];
					resultado.add(actual.id);
					setActual(actual.id);
					Thread.sleep(delay);
				} else {
					resultado.add(-1);
					System.out.println("Não achou caminho.");
					break;
				}
			}

			// PARTE 2 //>> COMPLEXIDADE: O(4n) + Omega((9/8)(n^2 - 2n)), depois de algumas simplificações
			/*
			 * Como descrito no relatório, o pior caso para essa parte do algoritmo é quando não há atalhos no caminho gerado,
			 * assim o caminho nunca será encurtado nos laços mais internos, e os laços mais externos serão executados a quantidade
			 * máxima de vezes. Nesse caso, o tamanho do caminho gerado é um pouco maior que n/2. Por isso iremos usar n/2 o tamanho
			 * do caminho como n/2 e a notação Omega, uma vez que sabemos que o caminho será um pouco maior que isso.
			 */
			if(resultado.get(resultado.size()-1) != -1) {						// COMPLEXIDADE: O(1)
				for (int i = 0; i < resultado.size(); i++) {					// COMPLEXIDADE: 0 <= i <= length
					for (int j = resultado.size()-1; j > i+1; j--) {			// COMPLEXIDADE: i < j < length
						if (resultado.get(i) == resultado.get(j)) {				// COMPLEXIDADE: 1
							for (int k = j; k > i; k--) {
								resultado.remove(k);
							}
							break;
						}

						if (isAdjacent(resultado.get(i), resultado.get(j))) {	// COMPLEXIDADE: 8
							for (int k = j-1; k > i; k--) {
								resultado.remove(k);
							}
							break;
						}
					}
				}
			}
			//long tempoFinal = System.nanoTime();

			int[] toPrint = new int[resultado.size()+2];
			toPrint[0] = source.id;
			toPrint[1] = destiny.id;
			for (int i = 0; i < resultado.size(); i++) {
				toPrint[i+2] = resultado.get(i);
			}
			printResult(toPrint);
			//System.out.println("" + (tempoFinal-tempoInicial));

			//System.out.println("LOOPS: " + loops + "RESULTADO SIZE: " + resultado.size());

//			for (int i = 0; i < matrix.length; i++) {
//				for (int j = 0; j < matrix[i].length; j++) {
//					if (matrix[i][j].decisions != null) {
//						System.out.print("0" + matrix[i][j].decisions.size() + " ");
//					} else {
//						System.out.print("-1");
//					}
//				}
//				System.out.println("");
//			}
//			System.out.println("");
		}
	}

	public Point getBestDistance() { //Pega o ponto com a melhor distância //>> COMPLEXIDADE: O(61)
		if (actual.decisions == null) { 																						// COMPLEXIDADE: 1
			actual.decisions = calcDistances(); 																				// COMPLEXIDADE: O(22)
		}

		if (actual.decisions.size() > 0) { 																						// COMPLEXIDADE: 1
			Point toReturn; 																									// COMPLEXIDADE: 1

			if (actual.decisions.size() == 1) { 					// Se tem apenas 1 decisao possivel 						// COMPLEXIDADE: 1
				toReturn = actual.decisions.get(0); 																			// COMPLEXIDADE: 1
			} else if (actual.decisions.size() == 2) { 				// Se tem 2 decisoes possiveis 								// COMPLEXIDADE: 1
				toReturn = getBestOfTwo(actual.decisions);																		// COMPLEXIDADE: O(24)
			} else if (actual.decisions.size() == 3) { 				// Se ter 3 decisoes possiveis 								// COMPLEXIDADE: 1
				toReturn = getBestOfThree(actual.decisions); 																	// COMPLEXIDADE: O(26)
			} else {												// Se tem 4 decisoes possiveis (que eh o maximo)
				toReturn = getBestOfFour(actual.decisions); 																	// COMPLEXIDADE: O(28)
			}

			actual.decisions.remove(toReturn); 						// Remove a decisao tomada da lista de decisoes possiveis	// COMPLEXIDADE: O(4), pois decisions tem size máximo igual a  4
			return toReturn; 										// COMPLEXIDADE: 1
		}
		return new Point();											// COMPLEXIDADE: 1
	}

	public Point getBestDistanceSemInterface() { //Pega o ponto com a melhor distância //>> COMPLEXIDADE: 61
		if (actual.decisions == null) { 																						// COMPLEXIDADE: 1
			actual.decisions = calcDistances(); 																				// COMPLEXIDADE: O(22)
		}

		if (actual.decisions.size() > 0) { 																						// COMPLEXIDADE: 1
			Point toReturn; 																									// COMPLEXIDADE: 1

			if (actual.decisions.size() == 1) { 					// Se tem apenas 1 decisao possivel							// COMPLEXIDADE: 1
				toReturn = actual.decisions.get(0); 																			// COMPLEXIDADE: 1
			} else if (actual.decisions.size() == 2) { 				// Se tem 2 decisoes possiveis 								// COMPLEXIDADE: 1
				toReturn = getBestOfTwo(actual.decisions); 																		// COMPLEXIDADE: O(24)
			} else if (actual.decisions.size() == 3) { 				// Se ter 3 decisoes possiveis								// COMPLEXIDADE: 1
				toReturn = getBestOfThree(actual.decisions); 																	// COMPLEXIDADE: O(26)
			} else {												// Se tem 4 decisoes possiveis (que eh o maximo)
				toReturn = getBestOfFour(actual.decisions); 																	// COMPLEXIDADE: O(28)
			}

			actual.decisions.remove(toReturn); 						// Remove a decisao tomada da lista de decisoes possiveis	// COMPLEXIDADE: O(4), pois decisions tem size máximo igual a  4
			return toReturn; 																									// COMPLEXIDADE: 1
		}
		return new Point();																										// COMPLEXIDADE: 1
	}

	public Point getBestOfTwo(ArrayList<Point> points) { //>> COMPLEXIDADE: O(24)
		Point toReturn; 												// COMPLEXIDADE: 1
		if (!wasVisited(points.get(0)) && !wasVisited(points.get(1))) {	// COMPLEXIDADE: 2
			toReturn = getLower(points); 								// COMPLEXIDADE: 18
		} else if (!wasVisited(points.get(0))) { 						// COMPLEXIDADE: 1
			toReturn = points.get(0); 									// COMPLEXIDADE: 1
		} else if (!wasVisited(points.get(1))) { 						// COMPLEXIDADE: 1
			toReturn = points.get(1); 									// COMPLEXIDADE: 1
		} else {
			toReturn = getLower(points); 								// COMPLEXIDADE: 18
		}
		return toReturn; 												// COMPLEXIDADE: 1
	}

	public Point getBestOfThree(ArrayList<Point> points) { //>> COMPLEXIDADE: 26
		Point toReturn; 																				// COMPLEXIDADE: 1
		if (!wasVisited(points.get(0)) && !wasVisited(points.get(1)) && !wasVisited(points.get(2))) {	// COMPLEXIDADE: 3
			toReturn = getLower(points); 																// COMPLEXIDADE: 18
		} else if (wasVisited(points.get(0))) { 														// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>();												// COMPLEXIDADE: 1
			aux.add(points.get(1)); 																	// COMPLEXIDADE: 1
			aux.add(points.get(2)); 																	// COMPLEXIDADE: 1
			toReturn = getBestOfTwo(aux); 																// COMPLEXIDADE: 24
		} else if (wasVisited(points.get(1))) { 														// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>(); 												// COMPLEXIDADE: 1
			aux.add(points.get(0)); 																	// COMPLEXIDADE: 1
			aux.add(points.get(2)); 																	// COMPLEXIDADE: 1
			toReturn = getBestOfTwo(aux);  																// COMPLEXIDADE: 24
		} else if (wasVisited(points.get(2))) { 														// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>();												// COMPLEXIDADE: 1
			aux.add(points.get(0)); 																	// COMPLEXIDADE: 1
			aux.add(points.get(1)); 																	// COMPLEXIDADE: 1
			toReturn = getBestOfTwo(aux); 																// COMPLEXIDADE: 24
		} else {
			toReturn = getLower(points); 																// COMPLEXIDADE: 18
		}
		return toReturn; 																				// COMPLEXIDADE: 1
	}

	public Point getBestOfFour(ArrayList<Point> points) { //>> COMPLEXIDADE: 28
		Point toReturn; 																											// COMPLEXIDADE: 1
		if (!wasVisited(points.get(0)) && !wasVisited(points.get(1)) && !wasVisited(points.get(2)) && !wasVisited(points.get(3))) { // COMPLEXIDADE: 4
			toReturn = getLower(points); 																							// COMPLEXIDADE: 18
		} else if (wasVisited(points.get(0))) { 																					// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>(); 																			// COMPLEXIDADE: 1
			aux.add(points.get(1)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(2)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(3)); 																								// COMPLEXIDADE: 1
			toReturn = getBestOfThree(aux); 																						// COMPLEXIDADE: 26
		} else if (wasVisited(points.get(1))) { 																					// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>(); 																			// COMPLEXIDADE: 1
			aux.add(points.get(0)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(2)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(3)); 																								// COMPLEXIDADE: 1
			toReturn = getBestOfThree(aux); 																						// COMPLEXIDADE: 26
		} else if (wasVisited(points.get(2))) { 																					// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>(); 																			// COMPLEXIDADE: 1
			aux.add(points.get(0)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(1)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(3)); 																								// COMPLEXIDADE: 1
			toReturn = getBestOfThree(aux); 																						// COMPLEXIDADE: 26
		} else if (wasVisited(points.get(3))) { 																					// COMPLEXIDADE: 1
			ArrayList<Point> aux = new ArrayList<Point>(); 																			// COMPLEXIDADE: 1
			aux.add(points.get(0)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(1)); 																								// COMPLEXIDADE: 1
			aux.add(points.get(2)); 																								// COMPLEXIDADE: 1
			toReturn = getBestOfThree(aux); 																						// COMPLEXIDADE: 26
		} else {
			toReturn = getLower(points); 																							// COMPLEXIDADE: 18
		}
		return toReturn;																											// COMPLEXIDADE: 1
	}

	public Point getLower(ArrayList<Point> points) { //>> COMPLEXIDADE: O(1)
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

	public ArrayList<Point> calcDistances(){	//>> COMPLEXIDADE: 22
		ArrayList<Point> decisions = new ArrayList<Point>(); 																					// COMPLEXIDADE: 1
		// LEFT
		if(actual.j!=0 && matrix[actual.i][actual.j-1].valid){ // Se não estiver na esquerda e o da esquerda for valido 						// COMPLEXIDADE: 1
			if (matrix[actual.i][actual.j-1].distance == -1) { 																					// COMPLEXIDADE: 1
				matrix[actual.i][actual.j-1].distance = Math.sqrt(Math.pow((destiny.i)-(actual.i), 2) + Math.pow((destiny.j)-(actual.j-1), 2)); // COMPLEXIDADE: 1
			}
			Point p = new Point(actual.i, actual.j-1); 																							// COMPLEXIDADE: 1
			decisions.add(p); 																													// COMPLEXIDADE: 1
		}
		// UP
		if(actual.i!=0 && matrix[actual.i-1][actual.j].valid){ //Se não estiver em cima e o de cima for valido 									// COMPLEXIDADE: 1
			if (matrix[actual.i-1][actual.j].distance == -1) { 																					// COMPLEXIDADE: 1
				matrix[actual.i-1][actual.j].distance = Math.sqrt(Math.pow((destiny.i)-(actual.i-1), 2) + Math.pow((destiny.j)-(actual.j), 2)); // COMPLEXIDADE: 1
			}
			Point p = new Point(actual.i-1, actual.j); 																							// COMPLEXIDADE: 1
			decisions.add(p); 																													// COMPLEXIDADE: 1
		}
		// RIGHT
		if(actual.j!=columns-1 && matrix[actual.i][actual.j+1].valid){ //Se não estiver na direita e o da direita for valido					// COMPLEXIDADE: 1
			if (matrix[actual.i][actual.j+1].distance == -1) { 																					// COMPLEXIDADE: 1
				matrix[actual.i][actual.j+1].distance = Math.sqrt(Math.pow((destiny.i)-(actual.i), 2) + Math.pow((destiny.j)-(actual.j+1), 2)); // COMPLEXIDADE: 1
			}
			Point p = new Point(actual.i, actual.j+1); 																							// COMPLEXIDADE: 1
			decisions.add(p); 																													// COMPLEXIDADE: 1
		}
		// DOWN
		if(actual.i!=lines-1 && matrix[actual.i+1][actual.j].valid){ //Se não estiver em baixo e o de baixo for válido 							// COMPLEXIDADE: 1
			if (matrix[actual.i+1][actual.j].distance == -1) { 																					// COMPLEXIDADE: 1
				matrix[actual.i+1][actual.j].distance = Math.sqrt(Math.pow((destiny.i)-(actual.i+1), 2) + Math.pow((destiny.j)-(actual.j), 2)); // COMPLEXIDADE: 1
			}
			Point p = new Point(actual.i+1, actual.j); 																							// COMPLEXIDADE: 1
			decisions.add(p); 																													// COMPLEXIDADE: 1
		}
		return decisions; 																														// COMPLEXIDADE: 1
	}

	public boolean wasVisited(Point p) { //>> COMPLEXIDADE: 1
		return (matrix[p.i][p.j].visited); // COMPLEXIDADE: 1
	}

	public boolean isAdjacent(int id1, int id2) { //>> COMPLEXIDADE: 1
		return ((id1/columns == id2/columns) && (id1%columns == (id2%columns)+1)) ||
			((id1/columns == id2/columns) && (id1%columns == (id2%columns)-1)) ||
			((id1/columns == (id2/columns)-1) && (id1%columns == id2%columns)) ||
			((id1/columns == (id2/columns)+1) && (id1%columns == id2%columns));	// COMPLEXIDADE: 1
	}

	public void printResult(int [] resultado) {
		System.out.print("\n" + resultado[0] + " to " + resultado[1] + ": ");
		for (int i = 2; i < resultado.length; i++) {
			System.out.print(resultado[i] + " ");
		}
	}

	/*
	 * GUI
	 */

	/*
	 * Actual and Visisted
	 */

	public void setActual(int id) {					//>> COMPLEXIDADE: 1
		actual = matrix[id/columns][id%columns]; 	// COMPLEXIDADE: 1
		nodes[id/columns][id%columns].setBackground(actualColor);
		nodes[id/columns][id%columns].repaint();
	}

	public void setVisited(Node node) { //>> COMPLEXIDADE: 2
		node.visited = true;
		nodes[node.i][node.j].setBackground(visitedColor); 	// COMPLEXIDADE: 1
		nodes[node.i][node.j].repaint();					// COMPLEXIDADE: 1
	}

	/*
	 * Source and Destiny
	 */

	public static void setSource(int id) {
		if (source == null) {
			source = matrix[id/columns][id%columns];
			nodes[id/columns][id%columns].setBackground(sourceColor);
			nodes[id/columns][id%columns].repaint();
		} else {
			nodes[source.id/columns][source.id%columns].setBackground(validColor);
			matrix[source.id/columns][source.id%columns].setValid(true);
			source = matrix[id/columns][id%columns];
			nodes[id/columns][id%columns].setBackground(sourceColor);
			nodes[id/columns][id%columns].repaint();
		}
	}

	public static void setSource(int i, int j) {
		setSource(i*lines+j);
	}

	public static void setDestiny(int id) {
		if (destiny == null) {
			destiny = matrix[id/columns][id%columns];
			nodes[id/columns][id%columns].setBackground(destinyColor);
			nodes[id/columns][id%columns].repaint();
		} else {
			nodes[destiny.id/columns][destiny.id%columns].setBackground(validColor);
			matrix[destiny.id/columns][destiny.id%columns].setValid(true);
			destiny = matrix[id/columns][id%columns];
			nodes[id/columns][id%columns].setBackground(destinyColor);
			nodes[id/columns][id%columns].repaint();
		}
	}

	public static void setDestiny(int i, int j) {
		setDestiny(i*lines+j);
	}

	/*
	 * Valid and Invalid
	 */

	public static void setInvalid(int i, int j) { 		//>> COMPLEXIDADE: 1
		matrix[i][j].setValid(false); 					// COMPLEXIDADE: 1
		nodes[i][j].setBackground(invalidColor);
		nodes[i][j].repaint();
	}

	public void setValid(int i, int j) {
		matrix[i][j].setValid(true);
		nodes[i][j].setBackground(validColor);
		nodes[i][j].repaint();
	}

	public void injectFaultListByNode(int[] routers){
		for(int i = 0; i < routers.length; i++){
			if(matrix[(routers[i]/columns)][(routers[i]%columns)].isValid()){
				matrix[(routers[i]/columns)][(routers[i]%columns)].setValid(false);
				nodes[(routers[i]/columns)][(routers[i]%columns)].setBackground(invalidColor);
				nodes[(routers[i]/columns)][(routers[i]%columns)].repaint();
			}
		}
	}

	public void paintGrid(){
		if (main_frame != null) {
			main_frame.dispose();
		}

		int nodeSize = 32;
		int gap = 5;
		main_frame = new JFrame("Unhappy Path");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.setLayout(null);
		main_frame.setSize((columns*nodeSize)+((columns+2)*gap) > 1050 ? (columns*nodeSize)+((columns+2)*gap) : 1050, 30+30+(lines*nodeSize)+((lines+2)*gap));
		main_frame.setLocationRelativeTo(null);
		main_frame.setResizable(false);
		nodes = new JButton[lines][columns];
		for(int i = 0; i < lines; i++){
			for(int j = 0; j < columns; j++){
				nodes[i][j] = new JButton("" + (matrix[i][j].getId())); // set text
				nodes[i][j].setBorder(null);
				if(matrix[i][j].isValid()){
					nodes[i][j].setBackground(validColor);
				}
				else{
					nodes[i][j].setBackground(invalidColor);
				}
				nodes[i][j].setBounds(gap+(j*(nodeSize+gap)),30+gap+(i*(nodeSize+gap)),nodeSize,nodeSize);
				nodes[i][j].addMouseListener(new MyPopupTriggerListener(new MyPopupMenu(i, j)));
				nodes[i][j].addActionListener(new MyActionListener(i, j) {
					@Override
					public void actionPerformed(ActionEvent e) {
						toogleValid(i, j);
					}
				});
				nodes[i][j].setVisible(true);
				main_frame.add(nodes[i][j]);
			}
		}

		JButton runBtn = new JButton("Run");
		runBtn.setBounds(5, 5, 150, 25);
		runBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		runBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							happyPath();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				});
				t.start();
			}
		});
		main_frame.add(runBtn);

		JButton resetBtn = new JButton("Reset");
		resetBtn.setBounds(160, 5, 150, 25);
		resetBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						reset();
					}
				});
				t.start();
			}
		});
		main_frame.add(resetBtn);

		JTextField delayTxtFld = new JTextField();
		delayTxtFld.setBounds(315, 5, 50, 25);
		main_frame.add(delayTxtFld);

		JButton setDelayBtn = new JButton("> Set Delay");
		setDelayBtn.setBounds(370, 5, 150, 25);
		setDelayBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setDelayBtn.addActionListener(new MyActionListener(delayTxtFld, 0) {
			public void actionPerformed(ActionEvent e) {
				Grid.delay = Integer.parseInt(this.delay.getText());
			}
		});
		main_frame.add(setDelayBtn);

		JTextField bombPowerTxtFld = new JTextField();
		bombPowerTxtFld.setBounds(525, 5, 50, 25);
		main_frame.add(bombPowerTxtFld);

		JButton setBombPowerBtn = new JButton("> Set Bomb Power");
		setBombPowerBtn.setBounds(580, 5, 150, 25);
		setBombPowerBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBombPowerBtn.addActionListener(new MyActionListener(bombPowerTxtFld, 1) {
			public void actionPerformed(ActionEvent e) {
				Grid.bombPower = Integer.parseInt(this.bombPower.getText());
			}
		});
		main_frame.add(setBombPowerBtn);

		JButton openStateBtn = new JButton("Open State");
		openStateBtn.setBounds(735, 5, 150, 25);
		openStateBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		openStateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(Grid.main_frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						@SuppressWarnings("resource")
						Scanner scan = new Scanner(file);
						int lines = scan.nextInt();
						int columns = scan.nextInt();
						Grid.grid = new Grid(lines, columns);
						setSource(scan.nextInt());
						setDestiny(scan.nextInt());
						ArrayList<Integer> invalidNodesIds = new ArrayList<Integer>();
						while(scan.hasNextInt()) {
							invalidNodesIds.add(scan.nextInt());
						}
						int[] invalidNodes = new int[invalidNodesIds.size()];
						for (int i = 0; i < invalidNodesIds.size(); i++) {
							invalidNodes[i] = invalidNodesIds.get(i);
						}
						injectFaultListByNode(invalidNodes);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		main_frame.add(openStateBtn);

		JButton saveStateBtn = new JButton("Save State");
		saveStateBtn.setBounds(890, 5, 150, 25);
		saveStateBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		saveStateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Grid.source != null && Grid.destiny != null) {
					int returnVal = fc.showSaveDialog(Grid.main_frame);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						try {
							FileWriter fileWriter = new FileWriter(file);
							fileWriter.write("" + Grid.lines + " " + Grid.columns + "\n");
							fileWriter.write("" + Grid.source.id + " " + Grid.destiny.id + "\n");
							for (int i = 0; i < matrix.length; i++) {
								for (int j = 0; j < matrix[i].length; j++) {
									if (!matrix[i][j].valid) {
										fileWriter.write("" + matrix[i][j].id + " ");
									}
								}
							}
							fileWriter.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else {
					System.out.println("Defina um nó Source e um nó Destiny.");
				}
			}
		});
		main_frame.add(saveStateBtn);

		main_frame.setVisible(true);
	}

	public static void dropBomb(int i, int j, int power) {
		setInvalid(i, j);
		nodes[i][j].setBackground(invalidColor);

		if (power > 0) {
			if (j-1 >= 0) {
				dropBomb(i, j-1, power-1);
			}
			if (i-1 >= 0) {
				dropBomb(i-1, j, power-1);
			}
			if (j+1 <= columns-1) {
				dropBomb(i, j+1, power-1);
			}
			if (i+1 <= lines-1) {
				dropBomb(i+1, j, power-1);
			}

		}
	}

	public void toogleValid(int i, int j) {
		if (nodes[i][j].getBackground() == validColor) {
			setInvalid(i, j);
		} else if (nodes[i][j].getBackground() == invalidColor) {
			setValid(i, j);
		}
	}

	public void reset() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				setValid(i, j);
				matrix[i][j].decisions = null;
				matrix[i][j].distance = -1;
				matrix[i][j].visited = false;
			}
		}
		if (state.source != null) {
			setSource(state.source);
		}
		if (state.destiny != null) {
			setDestiny(state.destiny);
		}
		if (state.invalidNodes != null) {
			injectFaultListByNode(state.invalidNodes);
		}
	}

	public void saveState() {
		ArrayList<Integer> invalidNodesIds = new ArrayList<Integer>();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (!matrix[i][j].valid) {
					invalidNodesIds.add(matrix[i][j].id);
				}
			}
		}

		int[] invalidNodes = new int[invalidNodesIds.size()];
		for (int i = 0; i < invalidNodesIds.size(); i++) {
			invalidNodes[i] = invalidNodesIds.get(i);
		}

		state = new State(source.id, destiny.id, invalidNodes);
	}

	public static void main(String args[]) throws InterruptedException {
		Grid.getSharedInstance();
	}
}
