package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRouteController;
import com.example.getirme.controller.IUserController;
import com.example.getirme.service.IRoutingService;
import com.example.getirme.service.IUserService;
import com.example.getirme.service.impl.RoutingServiceImpl;
import lombok.RequiredArgsConstructor;

import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RouteControllerImpl extends BaseController implements IRouteController {

    @Autowired
    IRoutingService routing;

    @GetMapping("/route")
    @Override
    public ResponseEntity<RootEntity<Map<String, Object>>>  route(@RequestParam double slat, @RequestParam double slon,
                                     @RequestParam double dlat, @RequestParam double dlon) {

        GraphPath<Long, DefaultWeightedEdge> p = routing.route(slat, slon, dlat, dlon);
        Map<String, Object> body = Map.of(
                "distance", p.getWeight(),     // metre
                "points",   routing.toLatLng(p) // [[lat,lon], …]
        );

        return ok(body);   // BaseController.ok(...)
    }
}
