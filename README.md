# Hotelier

## Summary

Hotelier is an online stay rental application developed using Spring Boot, offering streamlined housing rental management services and features including uploads, deletions, searches, and reservations. 

This app is for two types of customers: 
1) Hotel stay managers (hoteliers), who want to offer their hotel rental spot to potential customers
2) Stay seekers, who want to search, reserve, and stay at a hotel rental spot. 

Features of the app: 
- The app uses Hibernate, Google Cloud Storage, and ElasticSearch for efficient geohashing-based data storage and retrieval. 
- Hotelier also implements advanced security measures with token-based authentication and role-based access control. 
- 2-sided transactions from hotel managers and guests. 

## Running Hotelier
Download the application to your machine. 

Update the items in `java/com/tpd/staybooking/resources` folder to match your settings for databases, instances, etc. 

Run the `StaybookingApplication.java` file. 

## A Tour of the API Services

POST `/authenticate/guest`

Authenticate the user as a guest

POST `/authenticate/host`

Authenticate the user as a host

GET `/reservations`

Get a list by guest of all the reservations. 

POST `/reservations`

Add a reservation to the guest's reservations. 

DELETE `/reservations/{reservationId}`

Delete the reservation by reservationId

GET `/search`

Return a list of stays based on various parameters: guest number, check-in date, checkout date, location in lat, long, and distance.  

GET `/stays`

Return a list of stays by the authenticated user. 

GET `/stays/{stayId}`

Retrieve specific stay information by stayId. 

POST `/stays`

Add a stay for users to use. 

DELETE `/stays/{stayId}`

Remove a stay from the stay inventory. 

GET `/stays/reservations/{stayId}`

Get all reservations for a specific stayId. 

## Authentication
The AuthenticationService class is responsible for authenticating users and generating JWT tokens upon successful authentication. 
User Flow: 
- Users need to be registered 
- Then, authentication can happen, handled by the RegisterService. 

Below is a detailed breakdown of how it performs authentication:

### Authentication Process: 
- The authenticate method takes a User object and a UserRole enum as parameters.
- It attempts to authenticate the user using the AuthenticationManager. This is done by creating an UsernamePasswordAuthenticationToken with the username and password from the User object.
- The authenticationManager.authenticate method performs the actual authentication. If the credentials are invalid or the user does not exist, an AuthenticationException will be thrown.

### Role Validation:

After successful authentication, the method checks if the authenticated user has the required role.

It verifies this by checking if the authorities (roles) of the Authentication object contain the required SimpleGrantedAuthority for the specified role.

If the user does not have the required role or the Authentication object is null or not authenticated, it throws the UserNotExistException.

### Token Generation:
If the authentication is successful and the role check passes, the method generates a JWT token using the JwtUtil class.

The token is created with the authenticated user's username.

Finally, the method returns a Token object containing the generated JWT token.


## Geocoding Service 
What does it do: 
- convert geographic coordinates (lat, long) using the Google Maps Geocoding API, to a Location object.
Why use this: 
- clean interface for converting addresses to geographic coordinates
- out of the box error handling


## Security 
What we use: 
- CORS filters and JWT filters to ensure requests are protected and authenticated, 
- See the filters section for more detail. 

# Construction Background and Considerations

## Why ElasticSearch for our data store: Geohashing 
Mainly we use ElasticSearch for geohashing with Google Maps for its spatial indexing and fast querying capabilities. Driven by: 
- Geohashing Efficiency: allows spatial data to be grouped into areas for faster searching. We'll use this to search in an area for our hotel stays. 
- Efficient Geospatial Queries: e.g distance queries (e.g., finding all points within a radius) and bounding box queries (e.g., finding points within a rectangular area). 
- Scalability: we can handle large-scale datasets.
- Real-time Search: can be used interactive maps (e.g. pan or zoom in, return points of interest)

## We use Hibernate for ORM using ElasticSearch
Key features include:
- ORM: Hibernate maps Java classes to database tables and Java object properties to columns. 
- Automatic SQL Generation
- HQL (Hibernate Query Language): works with Java objects rather than database tables. 
- Caching: has built-in support for first-level and second-level caching. 
- Transaction Management
- Lazy Loading
- Database Independence: abstracts away the specific SQL dialects of different databases. 

## Geospatial Use Cases: 
- search by proximity 
- map based listings
- distance filter based search 
- route based stays (TBD)
- price based stays in a location (TBD)
- local area recommendations (TBD)