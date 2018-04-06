package com.example.shreyas.missingpersons;

/**
 * Created by shreyas on 23/3/18.
 */

public class ImageItem {

    private String imageUrl;
    private String imageId;
    private String imageDescription;

    private String id;
    private String gender;
    private String age;
    private String location;

    public ImageItem(String imageUrl, String imageId, String imageDescription, String id, String gender, String age, String location) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;
        this.imageDescription = imageDescription;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.location = location;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "id='" + id + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
