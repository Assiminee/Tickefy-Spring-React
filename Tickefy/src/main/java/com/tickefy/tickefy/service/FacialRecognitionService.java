package com.tickefy.tickefy.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface FacialRecognitionService {

    public Map<String, Object> identifyClient(MultipartFile facePhoto) throws Exception;
}
