package model;

public class State {
	public Integer source;
	public Integer destiny;
	public int[] invalidNodes;
	
	public State(int sourceId, int destinyId, int[] invalidNodesIds) {
		this.source = sourceId;
		this.destiny = destinyId;
		this.invalidNodes = invalidNodesIds;
	}
	
	public State() {
		this.source = null;
		this.destiny = null;
		this.invalidNodes = new int[0];
	}
}
