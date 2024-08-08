# Hotelier

## Summary

Hotelier is an online stay rental application developed using React and Spring Boot, offering streamlined housing rental management services and features including uploads, deletions, searches, and reservations. 

The app uses Hibernate, Google Cloud Storage, and ElasticSearch for efficient data storage and retrieval. 

Hotelier also implements advanced security measures with token-based authentication and role-based access control. 

We leverage Elasticsearch for both its powerful data retrieval and analysis capabilities as well as its geo-indexing features, which makes it convenient to index locations. for the search functions. 

## Running the app
Download the application to your machine. 
Update the items in `java/com/tpd/staybooking/resources folder` to match your settings for databases, instances, etc. 
Run the StaybookingApplication.java file. 

## API Routes

POST /authenticate/guest
Authenticate the user as a guest

POST /authenticate/host
Authenticate the user as a host

GET /reservations
Get a list by guest of all the reservations. 

POST /reservations
Add a reservation to the guest's reservations. 

DELETE /reservations/{reservationId}
Delete the reservation by reservationId

GET /search
Return a list of stays based on various parameters: guest number, check-in date, checkout date, location in lat, long, and distance.  

GET /stays
Return a list of stays by the authenticated user. 

GET /stays/{stayId}
Retrieve specific stay information by stayId. 

POST /stays
Add a stay for users to use. 

DELETE /stays/{stayId}
Remove a stay from the stay inventory. 

GET /stays/reservations/{stayId}
Get all reservations for a specific stayId. 

## Authentication
The AuthenticationService class is responsible for authenticating users and generating JWT tokens upon successful authentication. 

Users need to be registered before authentication can happen, handled by the RegisterService. 

Below is a detailed breakdown of how it performs authentication:

### Authentication Process: 
The authenticate method takes a User object and a UserRole enum as parameters.

It attempts to authenticate the user using the AuthenticationManager. This is done by creating an UsernamePasswordAuthenticationToken with the username and password from the User object.

The authenticationManager.authenticate method performs the actual authentication. If the credentials are invalid or the user does not exist, an AuthenticationException will be thrown.

### Role Validation:

After successful authentication, the method checks if the authenticated user has the required role.

It verifies this by checking if the authorities (roles) of the Authentication object contain the required SimpleGrantedAuthority for the specified role.

If the user does not have the required role or the Authentication object is null or not authenticated, it throws the UserNotExistException.

### Token Generation:
If the authentication is successful and the role check passes, the method generates a JWT token using the JwtUtil class.

The token is created with the authenticated user's username.

Finally, the method returns a Token object containing the generated JWT token.


## Geocoding Service 
The GeoCodingService class is responsible for converting an address into geographic coordinates (latitude and longitude) using the Google Maps Geocoding API. It then creates and returns a Location object that includes these coordinates.

We use this class to provide a clean interface for converting addresses to geographic coordinates, with appropriate error handling for common issues encountered when working with external APIs.


## Security 
The app uses CORS filters and JWT filters to ensure requests are protected and authenticated, respectively. See the filters section for more detail. 


