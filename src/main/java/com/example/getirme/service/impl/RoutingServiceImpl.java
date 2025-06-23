package com.example.getirme.service.impl;

import com.example.getirme.component.GraphLoader;
import com.example.getirme.service.IRoutingService;
import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutingServiceImpl implements IRoutingService {

    private final GraphLoader loader;

    @Autowired
    OpenStreetMapService openStreetMapService;

    @Override
    public GraphPath<Long, DefaultWeightedEdge> route(
            String address1, String address2) {

        String[] coords1 = openStreetMapService.getCoordinates(address1);
        String[] coords2 = openStreetMapService.getCoordinates(address2);

        Double lat1 = Double.parseDouble(coords1[0]);
        Double lon1 = Double.parseDouble(coords1[1]);
        Double lat2 = Double.parseDouble(coords2[0]);
        Double lon2 = Double.parseDouble(coords2[1]);


        long src = loader.nearest(lat1, lon1);
        long dst = loader.nearest(lat2, lon2);

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
