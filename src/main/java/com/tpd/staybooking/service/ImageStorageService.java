package com.tpd.staybooking.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.tpd.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${gcs.bucket}") // This annotation is used to inject the value of the gcs.bucket property from
                            // the application.properties file. It represents the name of the Google Cloud
                            // Storage bucket where the images will be uploaded.
    private String bucketName; // In the entire project, the bucket name will definitely not change, and if the
                               // bucket...
                               // If the name changes, the consequences could be severe. The preferred approach
                               // is to make this a final field and create it in the constructor.

    private final Storage storage; // an instance of the Storage class, which provides methods to interact with
                                   // Google Cloud Storage.

    public ImageStorageService(Storage storage) {
        this.storage = storage;
    }

    public String save(MultipartFile file) throws GCSUploadException { // Images can be sent from the frontend as
                                                                       // multiple files, so use multipart file.
        String filename = UUID.randomUUID().toString(); // UUID
        BlobInfo blobInfo = null; // BlobInfo file
        try {
            blobInfo = storage.createFrom( // This is the file upload functionality. This block of code creates a new
                                           // BlobInfo instance and uploads it.
                                           // the image file to the GCS bucket. The BlobInfo defines metadata for the
                                           // uploaded object, such as its filename, content type, and access control.
                    BlobInfo // Below are the file attributes definition - here, build...
                             // Using a pattern is very convenient. It allows you to define the required
                             // fields and set others to default values without mentioning them, resulting in
                             // cleaner code."
                            .newBuilder(bucketName, filename)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))) // This
                                                                                                                    // grants
                                                                                                                    // public
                                                                                                                    // read
                                                                                                                    // access
                                                                                                                    // to
                                                                                                                    // the
                                                                                                                    // uploaded
                                                                                                                    // image,
                                                                                                                    // allowing
                                                                                                                    // anyone
                                                                                                                    // to
                                                                                                                    // view
                                                                                                                    // it
                                                                                                                    // without
                                                                                                                    // needing
                                                                                                                    // authentication.
                            .build(),
                    file.getInputStream()); // Convert the file into a binary data stream before uploading it.
        } catch (IOException exception) {
            throw new GCSUploadException("Failed to upload file to GCS");
        }

        return blobInfo.getMediaLink(); // After the upload is successful, the method returns the media link URL of the
                                        // uploaded image
    }
}

// The above operations involve recording the media link after uploading.
// The next step is to inject imageStorage into the image service. When we
// finish uploading a stay, we will retrieve all images, upload them to Google
// Cloud, and store the image URLs in the database.