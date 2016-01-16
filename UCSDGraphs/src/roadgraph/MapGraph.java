/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	//TODO: Add your member variables here in WEEK 2
	
	//data structure
/*	//use Set to maintain the vertices.
	private Set<GeographicPoint> vertices;
*/	
	//the graph is sparse so i take adjacency list as the structure.
	//private Map<GeographicPoint, ArrayList<GeographicPoint>> adjListMap;
	
	//MapGraph manages nodes and node can maintain edges.
	private Set<Node> nodes;
	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		// TODO: Implement in this constructor in WEEK 2
		//this.vertices = new HashSet<GeographicPoint>();
		//this.adjListMap = new HashMap<GeographicPoint, ArrayList<GeographicPoint>>();
		this.nodes = new HashSet<Node>();
		
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		//TODO: Implement this method in WEEK 2
		//return 0;
		//return this.adjListMap.size();
		return this.nodes.size();
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		//TODO: Implement this method in WEEK 2
		//return null;
/*		Set<GeographicPoint> vertices = new HashSet<GeographicPoint>(this.adjListMap.keySet());
		return vertices;
*/		
		Set<GeographicPoint> vertices = new HashSet<GeographicPoint>();
		for(Node tmp: this.nodes){
			vertices.add(tmp.getLocation());
		}
		return vertices;
		
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		//TODO: Implement this method in WEEK 2
		//return 0;
		int totalEdges = 0;
		Iterator<Node> iter = this.nodes.iterator();
		while(iter.hasNext()){
			totalEdges += iter.next().getEdges().size();
		}
		return totalEdges;
	}

	
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		// TODO: Implement this method in WEEK 2
		//focus on functionality, not service logic and no need to consider exception, 
		//e.g. the location to be added whether exists in Set already. 
		//But there is a return statement, so take care of that.
		//return this.vertices.add(location);
/*		if(this.adjListMap.containsKey(location))
			return false;
		else{
			this.adjListMap.put(location, new ArrayList<GeographicPoint>());
			return true;
		}
*/	
		Node addingNode = new Node(location);
		if(this.nodes.contains(addingNode))
			return false;
		else{
			return this.nodes.add(addingNode);
		}
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {

		//TODO: Implement this method in WEEK 2
		//there is some other information supplied like roadName etc, so i need to reconstruct my map structure.
		//create Edge object and Node object.
		Edge edge = new Edge(to, roadName, roadType, length);
		Iterator<Node> iter = this.nodes.iterator();
		while(iter.hasNext()){
			Node tmp = iter.next();//reference or copy?
			if(tmp.getLocation().equals(from)){
				tmp.getEdges().add(edge);
				break;
			}
		}
	}
	

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		System.out.println("start:" + start.toString());
		System.out.println("goal:" + goal.toString());

		// TODO: Implement this method in WEEK 2
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		//return null;
		//1. consumer use method. 2. BFS with queue. 3. record path within map 4. extract path from map and print path with consumer.
		List<GeographicPoint> path = new ArrayList<GeographicPoint>();
		Queue<GeographicPoint> que = new LinkedList<GeographicPoint>();
		//hop indicates that if we want to reach the destination how many steps do we need.
		Map<GeographicPoint, Integer> parentMap = new HashMap<GeographicPoint, Integer>();
//		return null;
		
		//searching
		//the start location enqueues.
		que.offer(start);
		//initial hop number.
		parentMap.put(start, 0);
		System.out.println("que:" + que.peek().toString());
		System.out.println("parentMap:" + parentMap.values().toString());
		//return null;
		
		while(que.isEmpty() == false){
			GeographicPoint head = que.remove();
			nodeSearched.accept(head);
			//visit
			System.out.println("visiting:\n" + head.toString());
			
			if(head.equals(goal)){
				break;
			}
			//find the node associated with this point.
			Iterator<Node> iter = this.nodes.iterator();
			while(iter.hasNext()){
				Node tmp = iter.next();//reference at element in iterator. 
				if(tmp.getLocation().equals(head)){
					for(Edge edgeTmp: tmp.getEdges()){
						//put inherent relation into map.
						//if the next hop already has existed in parentMap, we just ignore it.
						if(parentMap.containsKey(edgeTmp.getDestination()))
								continue;
						int parentHop = parentMap.get(head);
						parentMap.put(edgeTmp.getDestination(), parentHop+1);
						//neighbors enqueue.
						que.offer(edgeTmp.getDestination());
					}
					break;
				}
			}
		}
		//extracting
		path.add(goal);
		if(parentMap.get(goal) == null){
			System.out.println("can not find a path between start and goal.");
			return null;
		}
		int shortestHop = parentMap.get(goal);
		Node childNode = new Node(goal);
		int tmpHop = shortestHop;
		while(tmpHop > 0){
			for(GeographicPoint point: parentMap.keySet()){
				Node potentialNode = new Node(point);
				//find the node associated with the point in MapGraph.nodes.
				//find the node associated with this point.
				Iterator<Node> iter = this.nodes.iterator();
				while(iter.hasNext()){
					Node tmp = iter.next();//reference at element in iterator. 
					if(tmp.getLocation().equals(point)){
						potentialNode = tmp;
						break;
					}
				}
				if(parentMap.get(point)==(tmpHop-1) && potentialNode.isParentOf(childNode)){
					path.add(point);
					tmpHop--;
					childNode = new Node(point);
					break;
				}
					
			}
		}
		//reverse the order.
		for(int i = 0; i < path.size()/2; i++){
			GeographicPoint temp = path.get(i);
			path.set(i, path.get(path.size()-1-i));
			path.set(path.size()-1-i, temp);
		}
		return path;
		
	}
	

	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 3

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		return null;
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 3
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		return null;
	}

	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
		
		// You can use this method for testing.  
		List<GeographicPoint> path = theMap.bfs(new GeographicPoint(5.0, 1.0), new GeographicPoint(5.0, 1));
		System.out.println(path.toString());
		
		/* Use this code in Week 3 End of Week Quiz
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		*/
		
	}

}
