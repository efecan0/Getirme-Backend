package com.example.getirme.controller.impl;

import com.example.getirme.controller.IRouteController;
import com.example.getirme.controller.IUserController;
import com.example.getirme.dto.RouteRequest;
import com.example.getirme.service.IRoutingService;
import com.example.getirme.service.IUserService;
import com.example.getirme.service.impl.RoutingServiceImpl;
import lombok.RequiredArgsConstructor;

import com.example.getirme.model.RootEntity;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/route", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<RootEntity<Map<String, Object>>> route(@RequestBody RouteRequest body) {
        GraphPath<Long, DefaultWeightedEdge> p = routing.route(body.getAddress1(), body.getAddress2());
        Map<String, Object> response = Map.of(
                "distance", p.getWeight(),      // metre
                "points",   routing.toLatLng(p) // [[lat,lon], …]
        );
        return ok(response);   // BaseController.ok(...)
    }


}
