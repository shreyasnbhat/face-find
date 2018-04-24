package com.example.shreyas.missingpersons;

/**
 * Created by shreyas on 17/3/18.
 */

public class Constants {
    public static String SERVER_IP = "192.168.43.41";
    public static String PORT = "5000";
    public static String ROOT = "http://" + SERVER_IP + ":" + PORT + "/";
    public static String IMAGE_REQUEST = ROOT + "static/img";
    public static String API = ROOT + "api/";
    public static String ADD_USER = API + "users/add";
    public static String DELETE_USER = API + "users/delete";
    public static String AUTHENTICATE = API + "authenticate";
    public static String UPLOAD_IMAGE = API + "users/upload";
    public static String MATCH = API + "match";
    public static String GET_IMAGES = API + "images";
}
