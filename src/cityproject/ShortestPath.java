package cityproject;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 * The ShortestPath class implements Dijkstra's algorithm to find the shortest path from a list
 * of nodes in a graph. 
 * 
 * @author Luis Ortiz
 */
public class ShortestPath {
 
    /**
     * This method implements Dijkstra's algorithm to find the shortest path from the source
     * city to the destination city. It starts by finding the shortest distance from the source
     * city to every other city in the cities array. Once the single source to every destination
     * problem is solved, then the shortest path from the source city to the destination city is
     * said to be solved.
     * 
     * @param cities        The list of cities that makes up the nodes in the graph
     * @param cityCount     The number of cities in the cities array
     * @param links         The list of all edges between every city in the cities array
     * @param linkCount     The number of links in the links array
     * @param src           The source city to start the shortest path from
     * @param dest          The destination city in the shortest path
     * @return              A Stack of city nodes that makes up the shortest path from
     *                      the source city to the destination city.
     */
    public static Stack<City> dijkstrasAlgorithm(City[] cities, int cityCount, Edge[] links,
                                                 int linkCount, City src, City dest) {
        // Set best distance of every city in the cities array
        // the highest possible integer value.
        for (int i = 0; i < cityCount; i++) {
            cities[i].setBestDistance(Integer.MAX_VALUE);
        }// end for
        
        // Grab the source city in accordance with the algorithm and
        // set the best distance to zero and the immediate predecessor to null.
        City currentCity = src;
        currentCity.setBestDistance(0);
        currentCity.setImmediatePredecessor(null);
        Edge link = null;
        
        // Create a list of all of the cities that have not been visited.
        LinkedList<City> unvisitedCities = (LinkedList<City>) unvisitedNodes(cities, cityCount);
        while(unvisitedCities.size() > 0) {
            // Get a LinkedList containing all of the adjacent nodes to the current city.
            AdjacencyNode adjNode = currentCity.getAdjacencyListHead();
            
            // For each node adjacent to the current city, determine if there is a new
            // shortest path to this node from the current city.
            for (; adjNode != null; adjNode = adjNode.next) {
                link = findLink(links, linkCount, currentCity, adjNode.getCity());
                // If the distance from currentCity to adjNode is shorter than the adjNode's current best distance,
                // then update the best distance and immediate predecessor of adjNode to include currentCity.
                if ((link.getLength() + link.getSource().getBestDistance()) < adjNode.getCity().getBestDistance()) {
                    adjNode.getCity().setBestDistance(link.getLength() + link.getSource().getBestDistance());
                    adjNode.getCity().setImmediatePredecessor(currentCity);
                }// end if
            }// end for
            
            // Set currentCity to visited after visiting all adjacent nodes
            currentCity.setVisited(true);
            System.out.println("Removing current city: " + currentCity.getName() + " size now: " + unvisitedCities.size());
            // Remove currentCity from the listed of unvisited cities
            unvisitedCities.remove(currentCity);
            
            // Find the next unvisited city with the smallest best distance.
            if (unvisitedCities.size() != 0) {
                currentCity = bestDistanceNode(unvisitedCities);
                System.out.println("Current city is now " + currentCity.getName());
            }// end if
        }// end while
        
        // Return a stack containing all the cities that make up the shortest path
        return getShortestPath(dest);
    }// end dijkstrasAlgorithm(...)
    
    /**
     * This method creates a LinkedList containing all of the cities in
     * the cities array.
     * 
     * @param cities        The cities array containing every city
     * @param cityCount     The number of cities in the cities array
     * @return              A LinkedList containing all of the cities in the cities array
     */
    public static List unvisitedNodes(City[] cities, int cityCount) {
        LinkedList<City> list = new LinkedList<>();
        for(int i = 0; i < cityCount; i++) {
            list.add(cities[i]);
        }// end for
        
        return list;
    }// end unvisitedNodes(City[] cities, int cityCount)
    
    /**
     * This method searches for an Edge containing source city src and destination city dest.
     * If no such link exists then null is returned.
     * 
     * @param links         The links array containing all of the edges in the graph
     * @param linkCount     The number of edges in the links array
     * @param src           The source city in the edge
     * @param dest          The destination city in the edge
     * @return              An edge containing the source city and destination city,
     *                      else return null.
     */
    public static Edge findLink(Edge[] links, int linkCount, City src, City dest) {
        Edge link = null;
        for (int i = 0; i < linkCount; i++) {
            if (links[i].getSource() == src && links[i].getDestination() == dest) {
                link = links[i];
                break;
            }// end if
        }// end for
        return link;
    }// end findLink(Edge[] links, int linkCount, City src, City dest)
    
    /**
     * This method returns the city with the smallest best distance value from the list
     * of unvisited cities.
     * 
     * @param unvisitedCities   A list containing all of the unvisited cities
     * @return                  The city containing the smallest best distance in
     *                          the unvisited cities list.
     */
    public static City bestDistanceNode(LinkedList<City> unvisitedCities) {
        City currentCity;
        City bestDistanceCity = unvisitedCities.getFirst();
        // Search each city in the list for the best distance node
        for (int i = 0; i < unvisitedCities.size(); i++) {
            currentCity = unvisitedCities.get(i);
            
            if (currentCity.getBestDistance() < bestDistanceCity.getBestDistance()) {
                bestDistanceCity = currentCity;
            }// end if
        }// end for
        
        return bestDistanceCity;
    }// end bestDistanceNode(LinkedList<City> unvisitedCities)
    
    /**
     * This method returns a stack of City objects that makes up the shortest
     * path from a source city to the destination city. This method takes the
     * destination city and creates the shortest path by going through the immediate
     * predecessor of each city in the path until we eventually reach the source city.
     * 
     * @param dest  The destination city in the path
     * @return      A stack of City objects that makes up the shortest path.
     */
    public static Stack<City> getShortestPath(City dest) {
        Stack<City> path = new Stack<>();
        City currentCity = dest;
        // Add each city to the path stack until the source city is reached.
        // It is known that the source city is reached once a city object contains
        // an immediate predecessor property that is null.
        while (currentCity != null) {
            path.push(currentCity);
            currentCity = currentCity.getImmediatePredecessor();
        }// end while
        
        return path;
    }// end getShortestPath
    
}// end class ShortestPath