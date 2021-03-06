/**
 *
 */
package roadgraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import geography.GeographicPoint;
import geography.RoadSegment;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team
 *
 *         A class which represents a graph of geographic locations Nodes in the
 *         graph are intersections of multiple roads. Edges are the roads.
 *
 */
public class MapGraph {

	// Maintain both nodes and edges as you will need to
	// be able to look up nodes by lat/lon or by roads
	// that contain those nodes.
	private HashMap<GeographicPoint, MapNode> pointNodeMap;
	private HashSet<MapEdge> edges;
	
	//map road type to speed limit. use trip duration other than distance 
	//to choice the shortest way. 
	private final double roadTypeSpeedLimitMapping(final String roadType){
		double speedLimit = 0;
		switch(roadType){
		case "city street":
			speedLimit = 80.0;//here unit is km/h
			break;
		case "residential":
			speedLimit = 60.0;
			break;
		case "connector":
			speedLimit = 40.0;
			break;
		default:;
		}
		return speedLimit;
	}
	/**
	 * Create a new empty MapGraph
	 *
	 */
	//get logger
	private static final Logger myGraphLogger = Logger.getLogger((MapGraph.class.getPackage().getName()));
	public MapGraph(){
		pointNodeMap = new HashMap<GeographicPoint, MapNode>();
		edges = new HashSet<MapEdge>();
		//add a file handler to logger
		String userHome = System.getProperty("user.home");
		File logDir = new File(userHome + "/javalogs");
		if(!logDir.exists()){
			logDir.mkdirs();
		}
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("%h/javalogs/graph0.log", 0, 3, true);
			myGraphLogger.addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the number of vertices (road intersections) in the graph
	 * 
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices() {
		return pointNodeMap.values().size();
	}

	/**
	 * Get the number of road segments in the graph
	 * 
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges() {
		return edges.size();
	}

	// For us in DEBUGGING. Print the Nodes in the graph
	public void printNodes() {
		System.out.println("****PRINTING NODES ********");
		System.out.println("There are " + getNumVertices() + " Nodes: \n");
		for (GeographicPoint pt : pointNodeMap.keySet()) {
			MapNode n = pointNodeMap.get(pt);
			System.out.println(n);
		}
	}

	// For us in DEBUGGING. Print the Edges in the graph
	public void printEdges() {
		System.out.println("******PRINTING EDGES******");
		System.out.println("There are " + getNumEdges() + " Edges:\n");
		for (MapEdge e : edges) {
			System.out.println(e);
		}

	}

	/**
	 * Add a node corresponding to an intersection
	 *
	 * @param latitude
	 *            The latitude of the location
	 * @param longitude
	 *            The longitude of the location
	 */
	public void addVertex(double latitude, double longitude) {
		GeographicPoint pt = new GeographicPoint(latitude, longitude);
		this.addVertex(pt);
	}

	/**
	 * Add a node corresponding to an intersection at a Geographic Point
	 *
	 * @param location
	 *            The location of the intersection
	 */
	public void addVertex(GeographicPoint location) {
		MapNode n = pointNodeMap.get(location);
		if (n == null) {
			n = new MapNode(location);
			pointNodeMap.put(location, n);
		} else {
			System.out.println("Warning: Node at location " + location + " already exists in the graph.");
		}

	}

	/**
	 * Add an edge representing a segment of a road. Precondition: The
	 * corresponding Nodes must have already been added to the graph.
	 * 
	 * @param roadName
	 *            The name of the road
	 * @param roadType
	 *            The type of the road
	 */
	public void addEdge(double lat1, double lon1, double lat2, double lon2, String roadName, String roadType) {
		// Find the two Nodes associated with this edge.
		GeographicPoint pt1 = new GeographicPoint(lat1, lon1);
		GeographicPoint pt2 = new GeographicPoint(lat2, lon2);

		MapNode n1 = pointNodeMap.get(pt1);
		MapNode n2 = pointNodeMap.get(pt2);

		// check nodes are valid
		if (n1 == null)
			throw new NullPointerException("addEdge: pt1:" + pt1 + "is not in graph");
		if (n2 == null)
			throw new NullPointerException("addEdge: pt2:" + pt2 + "is not in graph");

		addEdge(n1, n2, roadName, roadType, MapEdge.DEFAULT_LENGTH);

	}

	public void addEdge(GeographicPoint pt1, GeographicPoint pt2, String roadName, String roadType) {

		MapNode n1 = pointNodeMap.get(pt1);
		MapNode n2 = pointNodeMap.get(pt2);

		// check nodes are valid
		if (n1 == null)
			throw new NullPointerException("addEdge: pt1:" + pt1 + "is not in graph");
		if (n2 == null)
			throw new NullPointerException("addEdge: pt2:" + pt2 + "is not in graph");

		addEdge(n1, n2, roadName, roadType, MapEdge.DEFAULT_LENGTH);
	}

	public void addEdge(GeographicPoint pt1, GeographicPoint pt2, String roadName, String roadType, double length) {
		MapNode n1 = pointNodeMap.get(pt1);
		MapNode n2 = pointNodeMap.get(pt2);

		// check nodes are valid
		if (n1 == null)
			throw new NullPointerException("addEdge: pt1:" + pt1 + "is not in graph");
		if (n2 == null)
			throw new NullPointerException("addEdge: pt2:" + pt2 + "is not in graph");

		addEdge(n1, n2, roadName, roadType, length);
	}

	/** Given a point, return if there is a corresponding MapNode **/
	public boolean isNode(GeographicPoint point) {
		return pointNodeMap.containsKey(point);
	}

	// Add an edge when you already know the nodes involved in the edge
	private void addEdge(MapNode n1, MapNode n2, String roadName, String roadType, double length) {
		MapEdge edge = new MapEdge(roadName, roadType, n1, n2, length);
		edges.add(edge);
		n1.addEdge(edge);
	}

	/** Returns the nodes in terms of their geographic locations */
	public Collection<GeographicPoint> getVertices() {
		return pointNodeMap.keySet();
	}

	// get a set of neighbor nodes from a mapnode
	private Set<MapNode> getNeighbors(MapNode node) {
		return node.getNeighbors();
	}

	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		Consumer<GeographicPoint> temp = (x) -> {
			System.out.println("visiting:");
			System.out.println(x.x + "," + x.y);
		};
		return bfs(start, goal, temp);
	}

	/**
	 * Find the path from start to goal using Breadth First Search
	 *
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		// Setup - check validity of inputs
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		// setup to begin BFS
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		Queue<MapNode> toExplore = new LinkedList<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		toExplore.add(startNode);
		MapNode next = null;
		visited.add(startNode);
		
		while (!toExplore.isEmpty()) {
			next = toExplore.remove();

			// hook for visualization
			nodeSearched.accept(next.getLocation());
			if (next.equals(endNode))
				break;
			Set<MapNode> neighbors = getNeighbors(next);
			for (MapNode neighbor : neighbors) {
				if (!visited.contains(neighbor)) {
					visited.add(neighbor);
					parentMap.put(neighbor, next);
					toExplore.add(neighbor);
				}
			}
		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}

		// Reconstruct the parent path
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);

		return path;
	}

	/**
	 * Reconstruct a path from start to goal using the parentMap
	 *
	 * @param parentMap
	 *            the HashNode map of children and their parents
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */

	private List<GeographicPoint> reconstructPath(HashMap<MapNode, MapNode> parentMap, MapNode start, MapNode goal) {
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		MapNode current = goal;

		while (!current.equals(start)) {
			path.addFirst(current.getLocation());
			current = parentMap.get(current);
		}

		// add start
		path.addFirst(start.getLocation());
		return path;
	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
		Consumer<GeographicPoint> temp = (x) -> {
			System.out.println("visiting:");
			System.out.println(x.x + "," + x.y);

		};
		return dijkstra(start, goal, temp);
	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @param nodeSearched
	 *            A hook for visualization. See assignment instructions for how
	 *            to use it.
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		//print the size of this graph
		System.out.println("this graph contains " + this.getNumVertices() + " vertices, "
				+ this.getNumEdges() + " edges.");
		// TODO: Implement this method in WEEK 3

		// Hook for visualization. See writeup.
		// nodeSearched.accept(next.getLocation());

		// return null;
		// Setup - check validity of inputs
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		// setup to begin Dijkstra
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		Queue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		//initialize to positive infinity for all vertices
		for(MapNode tmp : this.pointNodeMap.values()){
			tmp.setActualDistance(Double.POSITIVE_INFINITY);
			tmp.setDistance(tmp.getActualDistance());
		}
		// startNode with 0 actual distance.
		startNode.setActualDistance(0);
		startNode.setDistance(startNode.getActualDistance());
		toExplore.add(startNode);
		MapNode next = null;
		
		//for advanced testing
		int countExploredNodes = 0;
		//for repeated dequed nodes
		int repeatedDequedNodes = 0;
		
		while (!toExplore.isEmpty()) {
			System.out.println("there exists " + toExplore.size() + " vertices in the queue.");
			next = toExplore.remove();
			if(visited.add(next) == false){
				repeatedDequedNodes++;
			}
			countExploredNodes++;
//			System.out.println("get shortest way for node: " + next.toString());
			// hook for visualization
			nodeSearched.accept(next.getLocation());
			System.out.println("distance: " + next.getDistance());
			if (next.equals(endNode))
				break;
			Set<MapNode> neighbors = getNeighbors(next);
			for (MapNode neighbor : neighbors) {
				if (!visited.contains(neighbor)) {
					//visited.add(neighbor); //bug fixed, it is feasible for BFS since the later node enqueues, the more hops it takes.
					// update actual distance from the start node to the
					// neighbor node.
					for (MapEdge tmp : next.getEdges()) {
						if (tmp.getEndNode().equals(neighbor)) {
							//judge the distance from the start node to the neighbor node.
							//if it is shorter, then replace the relation in parent-map.
							if(next.getActualDistance() + tmp.getLength() < neighbor.getDistance()){
//								System.out.println("shorter way to the neighbor node: " + next.toString() + neighbor.toString());
								parentMap.put(neighbor, next);
								neighbor.setActualDistance(next.getActualDistance() + tmp.getLength());
								neighbor.setDistance(neighbor.getActualDistance());
								toExplore.add(neighbor);
							}
						}
					}
				}
			}
		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}

		// Reconstruct the parent path
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);
		
		System.out.println("there still remains " + toExplore.size() + " vertices in queue.");
		System.out.println("Explored Nodes number: " + countExploredNodes);
		
		System.out.println("\n\nthe effectiveness of space(|queue|/|V|): " + (100.0 * toExplore.size()/(double)this.getNumVertices()) + "%"
				+ "\nthe effectiveness of time(|visited nodes|/|path of nodes|): " + (countExploredNodes/(double)path.size()));
		System.out.println("the overheads caused by auto-incremented queue(|repeated visited nodes|/|visited nodes|): " + (100.0*repeatedDequedNodes/(double)countExploredNodes)
				+ "%\n\n");
		
		DecimalFormat percentFormat = new DecimalFormat("#0.00");
		myGraphLogger.log(Level.INFO, this.getNumVertices() + " vertices, "
				+ this.getNumEdges() + " edges\n"
				+ "the effectiveness of space(1 - |the residual of queue|/|V|): " + percentFormat.format(100.0 *(1- toExplore.size()/(double)this.getNumVertices())) + "%\n"
				+ "the effectiveness of time(|path of nodes|/|visited nodes|): " + percentFormat.format(100.0 *path.size()/(double)countExploredNodes) + "%\n"
				+ "the overheads caused by auto-incremented queue(|repeated visited nodes|/|visited nodes|): " + percentFormat.format(100.0*repeatedDequedNodes/(double)countExploredNodes) + "%");
		return path;

	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal, String transTool) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
		Consumer<GeographicPoint> temp = (x) -> {
			System.out.println("visiting:");
			System.out.println(x.x + "," + x.y);

		};
		return dijkstra(start, goal, temp, transTool);
	}

	/**
	 * Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @param nodeSearched
	 *            A hook for visualization. See assignment instructions for how
	 *            to use it.
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched, String transTool) {
		// TODO: Implement this method in WEEK 3

		// Hook for visualization. See writeup.
		// nodeSearched.accept(next.getLocation());

		// return null;
		// Setup - check validity of inputs
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		// setup to begin Dijkstra
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		Queue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		//initialize to positive infinity for all vertices
		for(MapNode tmp : this.pointNodeMap.values()){
			tmp.setActualDistance(Double.POSITIVE_INFINITY);
			tmp.setDistance(tmp.getActualDistance());
		}
		// startNode with 0 actual distance.
		startNode.setActualDistance(0);
		startNode.setDistance(startNode.getActualDistance());
		toExplore.add(startNode);
		MapNode next = null;
		
		//for advanced testing
		int countExploredNodes = 0;
		
		while (!toExplore.isEmpty()) {
			next = toExplore.remove();
			visited.add(next);
			countExploredNodes++;
			// hook for visualization
			nodeSearched.accept(next.getLocation());
			System.out.println("distance: " + next.getDistance());
			if (next.equals(endNode))
				break;
			Set<MapNode> neighbors = getNeighbors(next);
			for (MapNode neighbor : neighbors) {
				if (!visited.contains(neighbor)) {
					//visited.add(neighbor); //bug fixed, it is feasible for BFS since the later node enqueues, the more hops it takes.
					// update actual distance from the start node to the
					// neighbor node.
					for (MapEdge tmp : next.getEdges()) {
						if (tmp.getEndNode().equals(neighbor)) {
							//judge the distance from the start node to the neighbor node.
							//if it is shorter, then replace the relation in parent-map.
							
							//choose way's weight in accordance with the transportation tools.
							double roadWeight = getRoadWeight(tmp, transTool);
							System.out.println("roadWeight:" + " " + roadWeight);
							if(next.getActualDistance() + roadWeight < neighbor.getDistance()){
								System.out.println("shorter way to the neighbor node: " + next.toString() + neighbor.toString() + "\nthe edge:" + tmp.toString());
								parentMap.put(neighbor, next);
								//neighbor.setActualDistance(next.getActualDistance() + tmp.getLength());
								//convert length to time.
		/*						double roadDuration = tmp.getLength()/this.roadTypeSpeedLimitMapping(tmp.getRoadType());
								System.out.println("roadDuration:" + " " + roadDuration);
		*/						
								neighbor.setActualDistance(next.getActualDistance() + roadWeight);
								neighbor.setDistance(neighbor.getActualDistance());
								toExplore.add(neighbor);
							}
						}
					}
				}
			}
		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}

		// Reconstruct the parent path
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);
		
		System.out.println("Explored Nodes number: " + countExploredNodes);
		return path;

	}

	private double getRoadWeight(MapEdge edge, String transTool) {
		// TODO Auto-generated method stub
		double roadWeight = Double.POSITIVE_INFINITY;
		switch(transTool){
		case "bus":
		case "car":
			roadWeight = edge.getLength()/roadTypeSpeedLimitMapping(edge.getRoadType());
			break;
		case "walk":
			roadWeight = edge.getLength()/7.2;//walk speed at 7.2km/h.
		case "flight":
			roadWeight = edge.getLength()/900;//flight speed at 900km/h.
		default:;
		}
		return roadWeight;
	}
	/**
	 * Find the path from start to goal using A-Star search
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		Consumer<GeographicPoint> temp = (x) -> {
		};
		return aStarSearch(start, goal, temp);
	}

	/**
	 * Find the path from start to goal using A-Star search
	 * 
	 * @param start
	 *            The starting location
	 * @param goal
	 *            The goal location
	 * @param nodeSearched
	 *            A hook for visualization. See assignment instructions for how
	 *            to use it.
	 * @return The list of intersections that form the shortest path from start
	 *         to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal,
			Consumer<GeographicPoint> nodeSearched) {
		//print the size of this graph
		System.out.println("this graph contains " + this.getNumVertices() + " vertices, "
				+ this.getNumEdges() + " edges.");
		// TODO: Implement this method in WEEK 3

		// Hook for visualization. See writeup.
		// nodeSearched.accept(next.getLocation());

		//return null;
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null) {
			System.err.println("Start node " + start + " does not exist");
			return null;
		}
		if (endNode == null) {
			System.err.println("End node " + goal + " does not exist");
			return null;
		}

		// setup to begin BFS
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		Queue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		//initialize to positive infinity for all vertices
		for(MapNode tmp : this.pointNodeMap.values()){
			tmp.setActualDistance(Double.POSITIVE_INFINITY);
			tmp.setDistance(tmp.getActualDistance());
		}
		// startNode with 0 actual distance.
		startNode.setActualDistance(0);
		startNode.setDistance(startNode.getActualDistance());
		toExplore.add(startNode);
		MapNode next = null;
		//for advanced testing
		int countExploredNodes = 0;
		//for repeated dequed nodes
		int repeatedDequedNodes = 0;

		while (!toExplore.isEmpty()) {
			next = toExplore.remove();
//			visited.add(next);
			if(visited.add(next) == false){
				repeatedDequedNodes++;
			}		
			countExploredNodes++;
			// hook for visualization
			nodeSearched.accept(next.getLocation());
			System.out.println("distance: " + next.getDistance());
			if (next.equals(endNode))
				break;
			Set<MapNode> neighbors = getNeighbors(next);
			for (MapNode neighbor : neighbors) {
				
				if (!visited.contains(neighbor)) {
					/*
					parentMap.put(neighbor, next);
					// update actual distance from the start node to the
					// neighbor node.
					for (MapEdge tmp : next.getEdges()) {
						if(next.getActualDistance() + tmp.getLength() < neighbor.getDistance()){
							System.out.println("shorter way to the neighbor node: " + next.toString() + neighbor.toString());
							parentMap.put(neighbor, next);
							neighbor.setActualDistance(next.getActualDistance() + tmp.getLength());
							neighbor.setDistance(neighbor.getActualDistance() + goal.distance(neighbor.getLocation()));
							toExplore.add(neighbor);
						}
					}
					*/
				}
				
					for (MapEdge tmp : next.getEdges()) {
						if (tmp.getEndNode().equals(neighbor)) {
							//judge the distance from the start node to the neighbor node.
							//if it is shorter, then replace the relation in parent-map.
							//bug fixed:一条路径到该节点所花实际代价比当前已知代价更大，这并不是一条更好的路径
//							if(next.getDistance() + tmp.getLength() < neighbor.getDistance()){
							if(next.getActualDistance() + tmp.getLength() < neighbor.getActualDistance()){
//								System.out.println("shorter way to the neighbor node: " + next.toString() + neighbor.toString());
								parentMap.put(neighbor, next);
								neighbor.setActualDistance(next.getActualDistance() + tmp.getLength());
								neighbor.setDistance(neighbor.getActualDistance() + goal.distance(neighbor.getLocation()));
								toExplore.add(neighbor);
							}
						}
					}
			}

		}
		if (!next.equals(endNode)) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}

		// Reconstruct the parent path
		List<GeographicPoint> path = reconstructPath(parentMap, startNode, endNode);
		
		System.out.println("there still remains " + toExplore.size() + " vertices in queue.");
		System.out.println("Explored Nodes number: " + countExploredNodes);
		
		System.out.println("\n\nthe effectiveness of space(|queue|/|V|): " + (100.0 * toExplore.size()/(double)this.getNumVertices()) + "%"
				+ "\nthe effectiveness of time(|visited nodes|/|path of nodes|): " + (countExploredNodes/(double)path.size()));
		System.out.println("the overheads increased by auto-incremented queue(|repeated visited nodes|/|visited nodes|): " + (100.0*repeatedDequedNodes/(double)countExploredNodes)
				+ "%\n\n");
		
		DecimalFormat percentFormat = new DecimalFormat("#0.00");
		myGraphLogger.log(Level.INFO, this.getNumVertices() + " vertices, "
				+ this.getNumEdges() + " edges\n"
				+ "the effectiveness of space(1 - |the residual of queue|/|V|): " + percentFormat.format(100.0 *(1- toExplore.size()/(double)this.getNumVertices())) + "%\n"
				+ "the effectiveness of time(|path of nodes|/|visited nodes|): " + percentFormat.format(100.0 *path.size()/(double)countExploredNodes) + "%\n"
				+ "the overheads caused by auto-incremented queue(|repeated visited nodes|/|visited nodes|): " + percentFormat.format(100.0*repeatedDequedNodes/(double)countExploredNodes) + "%");
		return path;

	}

	// main method for testing
	public static void main(String[] args) {
		/*
		 * Basic testing System.out.print("Making a new map..."); MapGraph
		 * theMap = new MapGraph(); System.out.print(
		 * "DONE. \nLoading the map...");
		 * GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		 * System.out.println("DONE.");
		 */

		// more advanced testing
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");

		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");

		System.out.println("Num nodes: " + theMap.getNumVertices());
		System.out.println("Num edges: " + theMap.getNumEdges());

		List<GeographicPoint> route = theMap.bfs(new GeographicPoint(1.0, 1.0), new GeographicPoint(8.0, -1.0));

		System.out.println(route);

		route = theMap.dijkstra(new GeographicPoint(7.0, 3.0), new GeographicPoint(4.0, -1.0), "bus");
		route = theMap.dijkstra(new GeographicPoint(7.0, 3.0), new GeographicPoint(4.0, -1.0), "walk");

		System.out.println(route);

		/*
		 * // Use this code in Week 3 End of Week Quiz MapGraph theMap = new
		 * MapGraph(); System.out.print("DONE. \nLoading the map...");
		 * GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		 * System.out.println("DONE.");
		 * 
		 * GeographicPoint start = new GeographicPoint(32.868629, -117.215393);
		 * GeographicPoint end = new GeographicPoint(32.868629, -117.215393);
		 * 
		 * List<GeographicPoint> route = theMap.dijkstra(start,end);
		 * List<GeographicPoint> route2 = theMap.aStarSearch(start,end);
		 */
/*		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);

		List<GeographicPoint> route = theMap.dijkstra(start, end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start, end);
*/
	}

}
