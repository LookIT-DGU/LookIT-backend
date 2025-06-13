package com.dgu.LookIT.fitting.dto.request;

import java.util.HashMap;
import java.util.Map;

public class KlingKolorsTryOnRequest {
    private String personImageUrl;
    private String clothingImageUrl;

    public String getPersonImageUrl() { return personImageUrl; }
    public void setPersonImageUrl(String personImageUrl) { this.personImageUrl = personImageUrl; }

    public String getClothingImageUrl() { return clothingImageUrl; }
    public void setClothingImageUrl(String clothingImageUrl) { this.clothingImageUrl = clothingImageUrl; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("person_image_url", personImageUrl);
        map.put("clothing_image_url", clothingImageUrl);
        return map;
    }
}
