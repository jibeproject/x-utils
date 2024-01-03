package org.cam;

import org.jfree.base.modules.SubSystem;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.Id;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.config.ConfigUtils;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.speedy.SpeedyDijkstra;
import org.matsim.core.router.speedy.SpeedyGraph;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.vehicles.Vehicle;
import org.matsim.api.core.v01.network.Node;
import org.jgrapht.alg.shortestpath.SuurballeKDisjointShortestPaths;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.cam.CreateGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.traverse.ClosestFirstIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cam.io.ReadCSV;

public class RunSimulation {
    public static Network matsimNetwork;

    public static void main(String[] args) {
        // Read Manchester travel survey
        /*
        ReadCSV readCSV = new ReadCSV();
        List<String[]> allData = readCSV.readAllDataAtOnce("travelSurvey/tripsWithXY.csv");

        // print Data
        for (String[] row : allData) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

         */

        // Read Matsim network
        CreateGraph createGraph = new CreateGraph();
        createGraph.setLinkToMatsimNetwork("network/toyNetwork.xml");
        createGraph.loadMatsimNetwork();
        matsimNetwork = createGraph.getMatsimNetwork();

        // Create JGraphT-like graph from Matsim network
        Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for(Node node : matsimNetwork.getNodes().values()){
            System.out.println("XX" + node.getId().toString());
            directedGraph.addVertex(node.getId().toString());

        }

        for(Link link : matsimNetwork.getLinks().values()){
            // aadt = (Double) link.getAttributes().getAttribute("aadt");
            // System.out.println("From/To nodes: " + link.getFromNode().getId() + "-" + link.getToNode().getId());
            directedGraph.addEdge(link.getFromNode().getId().toString(), link.getToNode().getId().toString());
        }

        // Test
        boolean edgeExists = directedGraph.containsEdge("15", "1");
        System.out.println("Edge (15, 1) exists: " + edgeExists);

        edgeExists = directedGraph.containsEdge("15", "2");
        System.out.println("Edge (15, 2) exists: " + edgeExists);

        // Print shortest path
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(directedGraph);
        GraphPath<String, DefaultEdge> graphPath = dijkstraShortestPath.getPath("15",  "2");
        //for(Object edge : graphPath.getEdgeList()){
        //    System.out.println(edge);
        //}

        System.out.println(graphPath);
        System.out.println(directedGraph.vertexSet().size());
        System.out.println(directedGraph.edgeSet().size());




        /*


        //
        Double aadt = 0.0;

        // read matsim network with BE indicators - household locations from TRADS - accessibility ??
        for(Link link : network.getLinks().values()){
            aadt = (Double) link.getAttributes().getAttribute("aadt");
            System.out.println("AADT is " + aadt);
            break;
        }

        // Run least cost path
        SpeedyGraph speedyGraph = new SpeedyGraph(network);
        SpeedyDijkstra speedyDijkstra = new SpeedyDijkstra(speedyGraph, null, null);
        Map<Id<Node>, ? extends Node> nodes= network.getNodes();
        //speedyDijkstra.calcLeastCostPath();

         */

        // Create graph object

        Graph<String, DefaultEdge> directedGraph2 = new DefaultDirectedGraph<>(DefaultEdge.class);
        directedGraph2.addVertex("v1");
        directedGraph2.addVertex("v2");
        directedGraph2.addVertex("v3");
        directedGraph2.addVertex("v4");
        directedGraph2.addVertex("v5");
        directedGraph2.addVertex("v6");

        directedGraph2.addEdge("v1", "v2");
        directedGraph2.addEdge("v1", "v3");
        directedGraph2.addEdge("v2", "v4");
        directedGraph2.addEdge("v3", "v5");
        directedGraph2.addEdge("v3", "v4");
        directedGraph2.addEdge("v4", "v6");
        directedGraph2.addEdge("v5", "v6");
        directedGraph2.addEdge("v6", "v5");

        // Run Suurballe algo.
        SuurballeKDisjointShortestPaths suurballeKDisjointShortestPaths = new SuurballeKDisjointShortestPaths(directedGraph2);
        List<GraphPath<String, DefaultEdge>> listPaths = suurballeKDisjointShortestPaths.getPaths("v1", "v6", 10);

        for (GraphPath item : listPaths) {
            //System.out.println(item.getLength());
            for (Object edge : item.getEdgeList()){
                System.out.println(edge);

            }
            System.out.println("==========");

        }


    }
}
