package com.tickefy.tickefy.service;


import com.tickefy.tickefy.inputStream.MultipartInputStreamFileResource;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();

            } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.NOT_FOUND) {


                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("identified", false);
                errorResponse.put("message", "Face not recognized.");
                return errorResponse;
            } else {
                throw new Exception("Unexpected error from facial recognition service: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new Exception("Error calling facial recognition service: " + e.getMessage(), e);
        }
    }
}
