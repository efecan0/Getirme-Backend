package com.example.getirme.service.impl;

import com.example.getirme.exception.BaseException;
import com.example.getirme.exception.ErrorMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.getirme.exception.MessageType.BAD_REQUEST;

@Service
public class OpenStreetMapService {

    private static final Double EARTH_RADIUS = 6371D; // Kilometre
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q={address}&format=json";

    private String[] getCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String url = NOMINATIM_URL.replace("{address}", address.replace(" ", "+"));
        String response = restTemplate.getForObject(url, String.class);

        // JSON Parsing (Basitleştirilmiş)
        // Örnek için ilk sonucu döndürüyoruz:
        if (response != null && response.contains("\"lat\"")) {
            String lat = response.split("\"lat\":\"")[1].split("\"")[0];
            String lon = response.split("\"lon\":\"")[1].split("\"")[0];
            return new String[]{lat, lon};
        }

        throw new BaseException(new ErrorMessage(BAD_REQUEST , "Wrong Address Format"));
    }


    public Double calculateDistance(String address1 , String address2) {
        Double lat1 , lon1 , lat2 , lon2;
        String[] coords1 = getCoordinates(address1);
        String[] coords2 = getCoordinates(address2);
        lat1 = Double.parseDouble(coords1[0]);
        lon1 = Double.parseDouble(coords1[1]);
        lat2 = Double.parseDouble(coords2[0]);
        lon2 = Double.parseDouble(coords2[1]);
        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLon = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        System.out.println("lat1 : " + lat1 + " lon1 : " + lon1 + " lat2 : " + lat2 + " lon2 : " + lon2);
        return Math.ceil(EARTH_RADIUS * c);
    }


}