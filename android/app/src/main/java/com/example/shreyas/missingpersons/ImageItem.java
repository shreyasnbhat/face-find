package com.example.shreyas.missingpersons;

/**
 * Created by shreyas on 23/3/18.
 */

public class ImageItem {

    private String imageUrl;
    private String imageId;
    private String imageDescription;

    public ImageItem(String imageUrl, String imageId) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;

    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }
}
