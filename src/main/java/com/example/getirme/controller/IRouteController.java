package com.example.getirme.controller;

import com.example.getirme.controller.impl.RouteControllerImpl;
import com.example.getirme.dto.RouteRequest;
import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

public interface IRouteController {
    ResponseEntity<RootEntity<Map<String, Object>>> route(@RequestBody RouteRequest body);
}