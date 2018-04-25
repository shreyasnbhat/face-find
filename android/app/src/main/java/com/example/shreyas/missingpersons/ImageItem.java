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
    private String latitude;
    private String longitude;
    private String phone;

    public ImageItem(String imageUrl, String imageId, String imageDescription, String id, String gender, String age, String latitude, String longitude, String phone) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;
        this.imageDescription = imageDescription;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
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



    @Override
    public String toString() {
        return "Name:     \t" + id + '\n' +
                "Gender:  \t\t" + gender + '\n' +
                "Age:     \t\t\t" + age + '\n' +
                "Latitude:\t\t" + latitude + '\n'+
                "Longitude:" + longitude + '\n'+
                "Contact No:\t"+ phone + '\n';
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }

    public String getPhone() {
        return phone;
    }
}
