package com.example.getirme.service.impl;

import com.example.getirme.component.GraphLoader;
import com.example.getirme.service.IRoutingService;
import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutingServiceImpl implements IRoutingService {

    private final GraphLoader loader;

    @Override
    public GraphPath<Long, DefaultWeightedEdge> route(
            double slat, double slon, double dlat, double dlon) {

        long src = loader.nearest(slat, slon);
        long dst = loader.nearest(dlat, dlon);

        AStarAdmissibleHeuristic<Long> h = (u, v) -> {
            double[] cu = loader.coords.get(u);
            double[] cv = loader.coords.get(v);
            return GraphLoader.haversine(cu[0], cu[1], cv[0], cv[1]);
        };

        return new AStarShortestPath<>(loader.getGraph(), h).getPath(src, dst);
    }

    /** Lat-lon çiftlerine dönüştür */
    @Override
    public List<double[]> toLatLng(GraphPath<Long, DefaultWeightedEdge> p) {
        return p.getVertexList().stream()
                .map(id -> loader.coords.get(id))
                .toList();
    }
}
