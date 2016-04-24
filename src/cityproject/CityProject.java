/* CityProject.java  -- project class file with the project's main method 
 *
 *  * The CityProject software package creates a graph of cities in the Unitied 
 * States with links between the cities. Each city is a vertex in the graph.
 * Each link between cities is an edge in the graph. The data for the cities and
 * links are read into arrays from CSV data files, which should be in the 
 * project folder. (CSV files, can be created, read, and edited using MS Excel.)
 *
 * The package's main class is the CityProject class. Other classes include:
 * 
 *   * AjacencyNode - a node for a linked list of Cities.  Each City has a list 
 *     of adjacnt cities, created from the links data file. Each list node has 
 *     a destination City, distance data, and a pointer to the next node. 
 *
 *   * City extends Vertex - Each City is a Vertex with added properties.  
 *     Each City has a unique name, and X and Y coordinates for location on a 
 *     1500 by 900 JPanel.
 *
 *   * Edge - an edge in a graph, with a source, destination, and length.
 *   
 *   * CityMap extends JPanel - a map of the graph on a 1500 by 900 GUI JPanel.
 *
 *   * Vertex - each Vertex in a graph.
 * 
 * The main method in the CityProject class calls methods to reads City and Edge 
 * data from data files into arrays, set up the adjacency list in each instance 
 * of City, print a list of Vertex cities and their Edges, then draw a map of the graph.
 *
 * created for use by students in CSCI 211 at Community Colle of Philadelphia
 * copyright 2014 by C. herbert.  last edited Nov. 23, 2014 by C. Herbert
 */

package cityproject;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class CityProject {

    // main metod for the project
    public static void main(String[] args) {

        City[] cities = new City[200]; //array of cities (Vertices) max = 200
        for (int i = 0; i < cities.length; i++) {
            cities[i] = new City();
        }// end for

        Edge[] links = new Edge[2000];// array of links  (Edges)  max = 2000
        for (int i = 0; i < links.length; i++) {
            links[i] = new Edge();
        }// end for

        int cityCount; //    actual number of cities
        int linkCount; //    actual number of links

        // load cities into an array from a datafile
        cityCount = readCities(cities);

        // load links into an array from a datafile
        linkCount = readLinks(links, cities);

        // create the adjacency list for each city based on the link array
        createAdjacencyLists(cityCount, cities, linkCount, links);

        // print adjacency lists for all cities
        //PrintAdjacencyLists(cityCount, cities);

        // instatiate a new scrollable map of the cities and links
        DrawScrollableMap(cityCount, cities, linkCount, links);
        
        // Prompt the user for the source city
        String src = JOptionPane.showInputDialog("Enter the name of a source city in format (city_name state_initials):" +
                                                 "\nExample: Philadelphia PA");
        // Check if the city is in the list of cities
        boolean valid = validCity(cities, cityCount, src);
        if (!valid) {
            // The user did not enter a valid city, terminate the program
            System.out.println("The source city you entered is not in the list of cities.");
            JOptionPane.showMessageDialog(null, "The source city " + src + " is not in the list of cities.",
                    "Jaws Warning", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }// end if
        
        // Prompt the user for the destination city
        String dest = JOptionPane.showInputDialog("Enter the name of a destination city in format (city_name state_initials):" + 
                                                  "\nExample: Philadelphia PA");
        // Check if the city is in the list of cities
        valid = validCity(cities, cityCount, dest);
        if (!valid) {
            // The user did not enter a valid city, terminate the program
            System.out.println("The destination city you entered is not in the list of cities.");
            JOptionPane.showMessageDialog(null, "The destination city " + dest + " is not in the list of cities.",
                    "Jaws Warning", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }// end if
        
        
        City sourceCity = findCity(cities, src);
        City destinationCity = findCity(cities, dest);
        
        // Use dijkstra's algorithm to find the shortest path from the source city to the destination city
        Stack<City> path = ShortestPath.dijkstrasAlgorithm(cities, cityCount, links, linkCount, sourceCity, destinationCity);
        
        // The shortestPath string will contain every city and distance that makes up
        // the shortest path from the source to destination.
        String shortestPath = "Shortest path\nFrom: " + src + "\nTo: " + dest;
        shortestPath += "\n\nNumber of cities to traverse: " + (path.size() - 1) + "\n";
        int count = 0; // the number of cities in the shortest path.
        
        // Remove each node from the stack and add the city and distance to the
        // shortest path string.
        while (!path.empty()) {
            count++;
            City currentCity = path.pop();
            shortestPath += "\n" + count + ".) " + currentCity.getName() + " - distance: " + currentCity.getBestDistance() + " miles";
        }// end while
        
        // Print the shortest path from source to destination
        JOptionPane.showMessageDialog(null, shortestPath, "Shortest Path", JOptionPane.PLAIN_MESSAGE);
        
    } // end main
    //************************************************************************
    
    
    /*************************************************************************** 
    * readCities() reads city data into an array from a data file.  The data   
    * file should be a CSV file with the city name and coordinates. City names 
    * should be unique. The coordinates are x and y coordinates for drawing on 
    * a 1500 by 900 JPanel  or JPanel. Each City will be a vertex in a graph of 
    * the cities. The array reference is a parameter. The methods returns the 
    * number of array elements used (the number of cities).                              
    ***************************************************************************/ 
    static int readCities(City[] cities) {

        int count = 0; // number of cities[] elements with data

        String[][] cityData = new String[123][3]; // holds data from the city file
        String delimiter = ",";                   // the delimiter in a csv file
        String line;                              // a String to hold each line from the file

        String fileName = "cities.csv";           // the file to be opened  

        try {
            // Create a Scanner to read the input from a file
            Scanner infile;
            infile = new Scanner(new File(fileName));

            /* This while loop reads lines of text into an array. it uses a Scanner class 
             * boolean function hasNextLine() to see if there is another line in the file.
             */
            while (infile.hasNextLine()) {
                // read the line 
                line = infile.nextLine();

                // split the line into separate objects and store them in a row in the array
                cityData[count] = line.split(delimiter);

                // read data from the 2D array into an array of City objects
                cities[count].setName(cityData[count][0]);
                cities[count].setX(Integer.parseInt(cityData[count][1]));
                cities[count].setY(Integer.parseInt(cityData[count][2]));

                count++;
            }// end while

            infile.close();

        } catch (FileNotFoundException e) {
            // error message dialog box with custom title and the error icon
            JOptionPane.showMessageDialog(null, "File I/O error:" + fileName, "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;

    } // end loadCities() ******************************************************


    
    /*************************************************************************** 
    * readLinks() reads link data into an array from a data file.  The data 
    * file should be a CSV file with source city name, destination city name, 
    * and length of the link on each line. The source and destination names 
    * should match names in the cities data file and array. 
    * The array reference is a parameter. The methods returns the number of 
    * array elements used (the number of links).
    ***************************************************************************/ 

    static int readLinks(Edge[] links, City[] cities) {
        int count = 0; // number of links[] elements with data

        String[][] CityLinkArray = new String[695][3]; // holds data from the link file
        String delimiter = ",";                       // the delimiter in a csv file
        String line;				      // a String to hold each line from the file

        String fileName = "links.csv";                // the file to be opened  

        try {
            // Create a Scanner to read the input from a file
            Scanner infile = new Scanner(new File(fileName));

            /* This while loop reads lines of text into an array. it uses a Scanner class 
             * boolean function hasNextLine() to see if there another line in the file.
             */
            while (infile.hasNextLine()) {
                // read the line 
                line = infile.nextLine();

                // split the line into separate objects and store them in a row in the array
                CityLinkArray[count] = line.split(delimiter);

                // read link data from the 2D array into an array of Edge objects
                // set source to vertex with city name in source column
                links[count].setSource(findCity(cities, CityLinkArray[count][0]));
                // set destination to vertex with city name in destination column
                links[count].setDestination(findCity(cities, CityLinkArray[count][1]));
                //set length to integer valuein length column
                links[count].setLength(Integer.parseInt(CityLinkArray[count][2]));

                count++;

            }// end while

        } catch (FileNotFoundException e) {
            // error message dialog box with custom title and the error icon
            JOptionPane.showMessageDialog(null, "File I/O error:" + fileName, "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return count;
    } // end loadLinks() *******************************************************

    /*************************************************************************** 
    * findCity() returns a reference to the City Object for the City with the 
    * specified name in the specified array .  City names should be unique 
    ***************************************************************************/ 
    static City findCity(City[] cities, String n) {
        int index = 0;  // loop counter
        // go through the cities array until the name is found
        // the name will be in the list

        while (cities[index].getName().compareTo(n) != 0) {

            index++;
        }// end while()
        return cities[index];

    } // end findCity() ********************************************************
    
    /*************************************************************************** 
    * validCity() returns a boolean determining if the String n exists is found
    * in the cities array.
    ***************************************************************************/ 
    static boolean validCity(City[] cities, int cityCount, String n) {
        boolean found = false;  // flag to determine if city exists
        
        // go through the cities array and check if String n
        // exists within the cities array.
        for (int i = 0; i < cityCount; i++) {
            if (cities[i].getName().compareTo(n) == 0) {
                // city has been found
                found = true;
                break;
            }
        }// end for
        return found;

    } // end validCity() ********************************************************
    
    /*************************************************************************** 
    * Create AdjacencyLists() creates an adjacency list for each city. 
    * Each adjacency list is in alphabetical order.
    ***************************************************************************/ 
    static void createAdjacencyLists(int cityCount, City[] cities, int linkCount, Edge[] links) {

        AdjacencyNode temp = new AdjacencyNode();

        // iterate city array
        for (int i = 0; i < cityCount; i++) {

            /* Iterate the link array in reverse order.
               each new link will be placed at the head of the list
               resulting in a list in alphabetical order.*/
            for (int j = linkCount-1; j >= 0; j--) {
 
                /* if the currentl link's source is the current city, then
                   create a node for the link and inseert it into the 
                   adjancency list as the new head of the list. */
                if (links[j].getSource() == cities[i]) {

                    // temporarily store the current value of the list's head
                    temp = cities[i].getAdjacencyListHead();

                    //create a new node
                    AdjacencyNode newNode = new AdjacencyNode();
                    
                    // add city and distance data
                    newNode.setCity(links[j].getDestination());
                    newNode.setDistance(links[j].getLength());
                    
                    // point newNode to the previous list head
                    newNode.setNext(temp);

                    // set the new head of the list to newNode
                    cities[i].setAdjacencyListHead(newNode);

                }  // end if
            } // end for j  (iterate links)
        } // end for i (iterate cities)

    } // end createAdjacencyLists() ********************************************
    
    /*************************************************************************** 
    * PrintAdjacencyLists() print the adjacency list for each city. The set of  
    * lists is alphabetical, as is each list.
    ***************************************************************************/ 
static void PrintAdjacencyLists(int cityCount, City[] cities) {

        System.out.println("List of Edges in the Graph of Cities by Source City");
        // iterate array of cities
        for (int i = 0; i < cityCount; i++) {

            // set current to adjacency list for this city    
            AdjacencyNode current = cities[i].getAdjacencyListHead();

            // print city name
            System.out.println("\nFrom " + cities[i].getName());

            // iterate adjacency list and print each node's data
            while (current != null) {
                System.out.println("\t" + current.toString());
                current = current.getNext();
            } // end while (current != null) 

        }   // end for i 

    } // end PrintAdjacencyLists()**********************************************

    /*************************************************************************** 
    * DrawScrollableMap() creates a frame , then places
    * an instance of CityMap on the frame in a ScrollPane.
    ***************************************************************************/ 
    static void DrawScrollableMap(int cCount, City[] c, int lCount, Edge[] l)  {

        // create a frame for the map
        JFrame mapFrame = new JFrame();
       
        // set the frame's properties
        mapFrame.setTitle("Selected U.S. Cities");
        mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapFrame.setLayout(new BorderLayout());
        mapFrame.setSize(1200, 600);
        
        // create an instance of CityMap
        CityMap map = new CityMap(cCount, c, lCount, l);
        
        // put the map on a ScrollPane in the frame
        mapFrame.add(new JScrollPane(map), BorderLayout.CENTER);
        // show the map
        mapFrame.setVisible(true);
        
    } // end DrawScrollablemap() ***********************************************
    
} // end class cityProject *****************************************************
