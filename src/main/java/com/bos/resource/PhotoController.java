package com.bos.resource;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PhotoController {

    @GetMapping("/photos")
    public List<Photo> photos() {
        Photo photo1 = getPhoto("1", "Photo 1 Title", "Photo is nice", "user1");
        Photo photo2 = getPhoto("2", "Photo 2 Title", "Photo is good", "user2");

        return List.of(photo1, photo2);
    }

    @GetMapping("/remotePhotos")
    public List<Photo> remotePhotos() {
        Photo photo1 = getPhoto("Remote 1", "Remote Photo 1 Title", "Remote Photo is nice", "Remote user1");
        Photo photo2 = getPhoto("Remote 2", "Remote Photo 2 Title", "Remote Photo is good", "Remote user2");

        return List.of(photo1, photo2);
    }

    private Photo getPhoto(String photoId, String photoTitle, String photoDescription, String user) {
        return Photo.builder()
                .photoId(photoId)
                .photoTitle(photoTitle)
                .photoDescription(photoDescription)
                .userId(user)
                .build();
    }

    @GetMapping("/token-expire")
    public Map<String, Object> tokenExpire() {

        Map<String, Object> result = new HashMap<>();
        result.put("error", new OAuth2Error("invalid token...:(", "token is expired..!", null));

        return result;
    }
}
