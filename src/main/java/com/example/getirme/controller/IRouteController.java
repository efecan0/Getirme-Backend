package com.example.getirme.controller;

import com.example.getirme.model.RootEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

public interface IRouteController {
    ResponseEntity<RootEntity<Map<String, Object>>> route(@RequestParam double slat, @RequestParam double slon,
                                                          @RequestParam double dlat, @RequestParam double dlon);
}
