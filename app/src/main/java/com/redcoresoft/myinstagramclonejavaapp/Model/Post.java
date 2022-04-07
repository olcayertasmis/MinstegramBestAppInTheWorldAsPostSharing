package com.redcoresoft.myinstagramclonejavaapp.Model;

public class Post {

    public String email;
    public String downloadUrl;
    public String comment;
    public String location;

    public Post(String email, String downloadUrl, String comment,String location) {
        this.email = email;
        this.downloadUrl = downloadUrl;
        this.comment = comment;
        this.location = location;
    }
}
