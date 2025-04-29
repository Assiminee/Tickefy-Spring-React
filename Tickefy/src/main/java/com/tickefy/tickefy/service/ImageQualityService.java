package com.tickefy.tickefy.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
public interface ImageQualityService {

    public Map<String, Object> assessImageQuality(UUID userId, MultipartFile imageFile) throws Exception;
}
