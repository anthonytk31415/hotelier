package com.tpd.staybooking.service;

import com.tpd.staybooking.exception.StayDeleteException;
import com.tpd.staybooking.exception.StayNotExistException;
import com.tpd.staybooking.model.*;
import com.tpd.staybooking.repository.LocationRepository;
import com.tpd.staybooking.repository.ReservationRepository;
import com.tpd.staybooking.repository.StayRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*this StayService class acts as an intermediary between the controller and the repository,
providing methods to perform various operations related to stays, including listing, adding, and deleting stays.
The class handles database interactions, business logic, and exception handling related to these operations.*/
@Service // This annotation indicates that the class is a Spring service component.
         // Services are used to encapsulate business logic and operations in a
         // structured manner.
public class StayService {

    private final ImageStorageService imageStorageService;
    private final StayRepository stayRepository;
    private final GeoCodingService geoCodingService;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;

    public StayService(ImageStorageService imageStorageService, StayRepository stayRepository,
            GeoCodingService geoCodingService, LocationRepository locationRepository,
            ReservationRepository reservationRepository) {
        this.imageStorageService = imageStorageService;
        this.stayRepository = stayRepository;
        this.geoCodingService = geoCodingService;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
    }

    /*
     * This method retrieves a list of stays associated with a specific user (host)
     * based on the provided username.
     * It uses the injected stayRepository to query the database. The method is
     * useful for fetching stays that belong
     * to a particular user for displaying or processing purposes.
     */
    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
        // this method is querying the repository to find stays where the host matches
        // the specified user.
        // returns the result of a query performed on a stayRepository.
    }

    /*
     * This method retrieves a specific stay by its ID and verifies that the stay is
     * associated with the provided host username.
     * If the stay does not exist or is not associated with the host, a
     * StayNotExistException is thrown.
     */
    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    /*
     * This method adds a new stay to the system. It accepts a Stay object and an
     * array of MultipartFile images.
     * It processes the images, saves them using the imageStorageService, and
     * associates them with the stay as StayImage objects.
     * It then saves the stay using the stayRepository. Additionally, it uses the
     * geoCodingService to retrieve and
     * save the location coordinates associated with the stay's address.
     */
    // Why not store the image directly in the database, but instead store it in a
    // bucket first and then in the database? SQL is not suitable for storing binary
    // or large data. Search efficiency would decrease, and performance would be
    // reduced. If a column is an image, it takes up a lot of memory. For any
    // content that does not require indexing, we use Amazon S3 or Google Cloud
    // Storage to store it.
    @Transactional
    public void add(Stay stay, MultipartFile[] images) {
        List<StayImage> stayImages = Arrays.stream(images) // First convert the list into a Java Stream.
                .filter(image -> !image.isEmpty()) // Corner case - if the image is empty, just filter it out and do
                                                   // nothing.
                .parallel() // Create multiple threads to upload simultaneously.
                .map(imageStorageService::save) // For each incoming image, save it each time. This can also be written
                                                // as .map(image -> imageStorageService.save(image));
                .map(mediaLink -> new StayImage(mediaLink, stay)) // After saving each file, there will be a media link
                                                                  // (assuming URL is a field).
                .collect(Collectors.toList()); // Put the media links into a list.
        /*
         * It has the same meaning as the lambda expression above. The only difference
         * is that the for loop below does not include parallel().
         * List<String> mediaLinks2 = new ArrayList<>();
         * for (MultipartFile image : images) {
         * mediaLinks2.add(imageStorageService.save(image));
         * }
         * However, the frequent API approach above reads very smoothly and is easy to
         * understand.
         */
        // When saving, you need to store the photo, the stay, and also save the
        // geolocation. You need to add @Transactional because there are multiple write
        // operations.
        stay.setImages(stayImages);
        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress()); //
        locationRepository.save(location); // Connect to the index in Elasticsearch.
    }

    /*
     * This method is used to delete a stay. It verifies that the stay with the
     * provided ID exists and is associated with
     * the provided host username. It also checks if there are active reservations
     * associated with the stay.
     * If there are, a StayDeleteException is thrown. If all checks pass, the stay
     * is deleted using the stayRepository.
     */
    public void delete(Long stayId, String username) throws StayNotExistException, StayDeleteException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }

        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && !reservations.isEmpty()) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
        stayRepository.deleteById(stayId);
    }
}
