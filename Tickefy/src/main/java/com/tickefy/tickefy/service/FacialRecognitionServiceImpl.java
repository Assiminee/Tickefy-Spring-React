package com.tickefy.tickefy.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickefy.tickefy.inputStream.MultipartInputStreamFileResource;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacialRecognitionServiceImpl implements FacialRecognitionService {


    @Override
    public Map<String, Object> identifyClient(MultipartFile facePhoto) throws Exception {
        try {
            String url = "http://python-app:8000/api/v1/users/identify";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(facePhoto.getInputStream(), facePhoto.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

                return response.getBody();

        } catch (HttpClientErrorException e) {
            try {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("identified", false);
                errorResponse.put("message", "Face not recognized.");
                errorResponse.put("status", e.getStatusCode().value()); // include status for use in controller
                return errorResponse;
            } catch (Exception parseException) {
                return Map.of(
                        "is_image_valid", false,
                        "message", "Unknown error occurred while assessing image",
                        "status", 500
                );
            }
        } catch (Exception e) {
            return Map.of(
                    "is_image_valid", false,
                    "message", "Internal error: " + e.getMessage(),
                    "status", 500
            );
        }
    }
}
