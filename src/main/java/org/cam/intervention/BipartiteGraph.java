package org.cam.intervention;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class BipartiteGraph {

    BipartiteGraph(){

    }

    private static Graph<String, DefaultEdge> create() {
        // Create a bipartite graph using GraphTypeBuilder
        Graph<String, DefaultEdge> bipartiteGraph = GraphTypeBuilder
                .undirected()
                .allowingMultipleEdges(false)
                .allowingSelfLoops(false)
                .vertexClass(String.class)
                .edgeClass(DefaultEdge.class)
                .buildGraph();

        // Add vertices to the two partitions (A and B)
        String[] partitionA = {"A1", "A2", "A3"};
        String[] partitionB = {"B1", "B2", "B3"};


        for (String vertex : partitionA) {
            bipartiteGraph.addVertex(vertex);
        }

        for (String vertex : partitionB) {
            bipartiteGraph.addVertex(vertex);
        }

        // Add edges between vertices in different partitions
        for (String vertexA : partitionA) {
            for (String vertexB : partitionB) {
                bipartiteGraph.addEdge(vertexA, vertexB);
            }
        }
        return bipartiteGraph;
    }
}
