package com.videostore.videostore.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.videostore.videostore.application.exception.ImageUploadException;
import com.videostore.videostore.application.port.out.ImageStoragePort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class CloudinaryImageStorageAdapter implements ImageStoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryImageStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(byte[] file, String filename) {
        try {
            String publicId = "movies/" + UUID.randomUUID();

            Map<?, ?> result = cloudinary.uploader().upload(
                    file,
                    Map.of(
                            "public_id", publicId,
                            "resource_type", "image"
                    )
            );

            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }
}
