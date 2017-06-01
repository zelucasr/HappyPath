package model;

public class Point { //Classe para representar um ponto na matriz
	public int i;	//� utilizada para guardar as poss�veis decis�es a serem tomadas.
	public int j;
	
	public Point(){
		this.i = -1;
		this.j = -1;
	}
	
	public Point(int i, int j){
		this.i = i;
		this.j = j;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}
	
	
}
