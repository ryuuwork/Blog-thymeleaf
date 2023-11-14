package com.tuananhdo.utils;

public class PhotoPathUtil {
    public static String getPhotoPath(Long id, String photos) {
        if (id == null || photos == null || photos.isEmpty()) {
            return "/common/assets/images/products/s1.jpg";
        }
        return "/user-photos/" + id + "/" + photos;
    }
}
