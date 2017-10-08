package roadgraph;

import geography.GeographicPoint;

public class Edge {
	private GeographicPoint destination;
	private String roadName;
	private String roadType;
	private double length;
	
	public Edge(){
		
	}
	
	public Edge(GeographicPoint _destination, String _roadName, String _roadType, double _length){
		this.destination = _destination;
		this.roadName = _roadName;
		this.roadType = _roadType;
		this.length = _length;
	}

	public GeographicPoint getDestination() {
		return destination;
	}

	public void setDestination(GeographicPoint destination) {
		this.destination = destination;
	}

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	public String getRoadType() {
		return roadType;
	}

	public void setRoadType(String roadType) {
		this.roadType = roadType;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
	
	
}
