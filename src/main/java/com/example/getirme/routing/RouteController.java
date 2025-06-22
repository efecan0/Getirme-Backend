package com.example.getirme.routing;

import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/route")
public class RouteController {

    private final RoutingService routing;

    @GetMapping
    public Map<String, Object> route(@RequestParam double slat, @RequestParam double slon,
                                     @RequestParam double dlat, @RequestParam double dlon) {

        GraphPath<Long, DefaultWeightedEdge> p = routing.route(slat, slon, dlat, dlon);
        return Map.of(
                "distance", p.getWeight(),          // metre
                "points", routing.toLatLng(p)       // [[lat,lon], …]
        );
    }
}
