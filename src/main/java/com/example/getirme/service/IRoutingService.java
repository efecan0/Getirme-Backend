package com.example.getirme.service;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public interface IRoutingService {
    GraphPath<Long, DefaultWeightedEdge> route(String address1, String address2);
    List<double[]> toLatLng(GraphPath<Long, DefaultWeightedEdge> p);
}