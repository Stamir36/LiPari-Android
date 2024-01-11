package com.unesell.lipari;

public class ImageModel {
    private String imageUrl;
    private String altDescription;

    public ImageModel(String imageUrl, String altDescription) {
        this.imageUrl = imageUrl;
        this.altDescription = altDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAltDescription() {
        return altDescription;
    }
}
