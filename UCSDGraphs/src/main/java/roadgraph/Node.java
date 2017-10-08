package roadgraph;

import java.util.ArrayList;

import geography.GeographicPoint;

public class Node {
	private GeographicPoint location;
	private ArrayList<Edge> edges;
	
	
	public Node() {
		super();
	}

	

	public Node(GeographicPoint location) {
		super();
		this.location = location;
		this.edges = new ArrayList<Edge>();
	}





	public Node(GeographicPoint location, ArrayList<Edge> edges) {
		super();
		this.location = location;
		this.edges = edges;
	}



	public GeographicPoint getLocation() {
		return location;
	}



	public void setLocation(GeographicPoint location) {
		this.location = location;
	}



	public ArrayList<Edge> getEdges() {
		return edges;
	}



	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}



	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		//return this.location.equals(((Node)obj).getLocation());
		return obj==null ? this.location==null : this.location.equals(((Node)obj).getLocation());
	}



	public boolean isParentOf(Node childNode) {
		// TODO Auto-generated method stub
		System.out.println(this.location.toString() + " next hop are:");
		for(Edge tmp: this.edges){
			System.out.println(tmp.getDestination().toString());
			if(tmp.getDestination().equals(childNode.getLocation()))
				return true;
		}
		return false;
		
	}



	
}
