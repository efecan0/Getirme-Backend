package com.example.getirme.service.impl;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static com.example.getirme.exception.MessageType.BAD_REQUEST;

@Service
public class OpenStreetMapService {

    private static final Double EARTH_RADIUS = 6371D;
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q={address}&format=json";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private final Map<String, String[]> coordinateCache = new HashMap<>();

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    String[] getCoordinates(String address) {
        if (coordinateCache.containsKey(address)) {
            return coordinateCache.get(address);
        }

        String url = NOMINATIM_URL.replace("{address}", address.replace(" ", "+"));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.isArray() && root.size() > 0) {
                String lat = root.get(0).get("lat").asText();
                String lon = root.get(0).get("lon").asText();
                String[] coords = new String[]{lat, lon};
                coordinateCache.put(address, coords);
                return coords;
            }
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(BAD_REQUEST, "Address parsing error: " + e.getMessage()));
        }

        throw new BaseException(new ErrorMessage(BAD_REQUEST, "Wrong Address Format"));
    }

    public Double calculateDistance(String address1, String address2) {
        if (address1.equalsIgnoreCase(address2)) {
            return 0.0;
        }

        String[] coords1 = getCoordinates(address1);
        String[] coords2 = getCoordinates(address2);

        Double lat1 = Double.parseDouble(coords1[0]);
        Double lon1 = Double.parseDouble(coords1[1]);
        Double lat2 = Double.parseDouble(coords2[0]);
        Double lon2 = Double.parseDouble(coords2[1]);

        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLon = Math.toRadians(lon2 - lon1);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.ceil(EARTH_RADIUS * c);
    }
}
