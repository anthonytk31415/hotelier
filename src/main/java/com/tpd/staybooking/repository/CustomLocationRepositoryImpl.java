package com.tpd.staybooking.repository;

import com.tpd.staybooking.model.Location;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

/*
This code defines a custom implementation of a repository interface named CustomLocationRepository for performing
geographical distance-based searches on the Elasticsearch index containing Location data(This search is beyond the standard methods pro). The implementation uses
the Spring Data Elasticsearch framework for querying the Elasticsearch database.*/

public class CustomLocationRepositoryImpl implements CustomLocationRepository {

    private static final String DEFAULT_DISTANCE = "50";
    private final ElasticsearchOperations elasticsearchOperations; // this class object interacts with the Elasticsearch
                                                                   // database

    public CustomLocationRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public List<Long> searchByDistance(double lat, double lon, String distance) {
        if (distance == null || distance.isEmpty()) {
            distance = DEFAULT_DISTANCE;
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withFilter(
                new GeoDistanceQueryBuilder("geoPoint").point(lat, lon).distance(distance, DistanceUnit.KILOMETERS));

        SearchHits<Location> searchResult = elasticsearchOperations.search(queryBuilder.build(), Location.class);
        return searchResult.getSearchHits().stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());
        /*
         * // The above method does not need to create a new ArrayList. Instead, create
         * a stream from searchResult and then map each element in the stream
         * individually.
         * List<Long> locationIDs = new ArrayList<>();
         * for (SearchHit<Location> hit : searchResult.getSearchHits()) {
         * LocationIDs.add(hit.getContent().getId());
         * }
         * return locationIDs；
         */
    }
}