package com.tpd.staybooking.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.tpd.staybooking.exception.GeoCodingException;
import com.tpd.staybooking.exception.InvalidStayAddressException;
import com.tpd.staybooking.model.Location;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;

// This class is responsible for interacting with the Google Maps Geocoding API to retrieve geographic coordinates (latitude and longitude) for a given address.
@Service
public class GeoCodingService {

    private final GeoApiContext context;

    public GeoCodingService(GeoApiContext context) {
        this.context = context;
    }

    public Location getLatLng(Long id, String address) {
        try {
            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0]; // The meaning of await()
                                                                                        // execution is that [0] means
                                                                                        // to get the highest matching
                                                                                        // result.
            if (result.partialMatch) { // Only perform exact match. Partial match will still be invalid."
                throw new InvalidStayAddressException("Failed to find stay address");
            }
            return new Location(id, new GeoPoint(result.geometry.location.lat, result.geometry.location.lng)); // return
                                                                                                               // 一个
                                                                                                               // Location
                                                                                                               // Object.
                                                                                                               // This
                                                                                                               // object
                                                                                                               // exists
                                                                                                               // in the
                                                                                                               // Elasticsearch
                                                                                                               // database.
        } catch (IOException | ApiException | InterruptedException e) {
            e.printStackTrace();
            throw new GeoCodingException("Failed to encode stay address");
        }
    }
}
