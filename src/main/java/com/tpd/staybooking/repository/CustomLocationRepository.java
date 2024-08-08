package com.tpd.staybooking.repository;

import java.util.List;

// Creating this CustomLocationRepository is to use the search API. How to implement the search API? You need to do it yourself. You need to write a class to implement this method.
public interface CustomLocationRepository {

    List<Long> searchByDistance(double lat, double lon, String distance);
}
