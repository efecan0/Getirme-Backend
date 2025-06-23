package com.example.getirme.service;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public interface IRoutingService {
    GraphPath<Long, DefaultWeightedEdge> route(double slat, double slon, double dlat, double dlon);
    List<double[]> toLatLng(GraphPath<Long, DefaultWeightedEdge> p);
}