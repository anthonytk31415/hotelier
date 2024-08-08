package com.tpd.staybooking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

/*part of a project that uses the Spring Data Elasticsearch library to interact with an Elasticsearch database.
Elasticsearch is a search and analytics engine often used for indexing and querying large volumes of data.
Use Elasticsearch because it has geo-indexing, which makes it convenient to index locations.

Below code imports various annotations and classes from the Spring Data Elasticsearch library.
These annotations are used to provide metadata about how the class fields should be mapped to the Elasticsearch index.
*/

/* @Document(indexName = "loc") specifies that instances of this class should be stored in an Elasticsearch index named "loc".
In Elasticsearch, an index is similar to a table in SQL databases.
In simple terms, you create a search space and put all the content into that search space. It’s similar to a table in SQL. Here, the table is called an index. Each element in the index's search space is a document (like rows of data in a database, but not listed row by row like in a database since it's NoSQL).

*/
@Document(indexName = "loc")
public class Location {

    @Id // id field is the identifier for the document in the Elasticsearch index.
    @Field(type = FieldType.Long) // the id field should be stored as a Long type in the Elasticsearch index
    private Long id;

    @GeoPointField
    private GeoPoint geoPoint; // The Geopoint class has two fields—latitude and longitude.

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}
