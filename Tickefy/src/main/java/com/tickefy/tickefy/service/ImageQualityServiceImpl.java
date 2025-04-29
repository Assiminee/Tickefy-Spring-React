package com.tickefy.tickefy.service;


import com.tickefy.tickefy.inputStream.MultipartInputStreamFileResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageQualityServiceImpl implements ImageQualityService {

    @Override
    public Map<String, Object> assessImageQuality(UUID userId, MultipartFile imageFile) throws Exception {
        try {
            String url = "http://python-app:8000/api/v1/users/" + userId + "/assess_image_quality";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(imageFile.getInputStream(), imageFile.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            return response.getBody(); // Return full body with both keys
        } catch (Exception e) {
            throw new Exception("Error while calling image quality API: " + e.getMessage(), e);
        }
    }


}
