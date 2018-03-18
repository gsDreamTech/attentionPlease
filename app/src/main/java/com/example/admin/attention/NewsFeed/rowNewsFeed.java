package com.example.admin.attention.NewsFeed;

/**
 * Created by ADMIN on 1/3/2018.
 */

public class rowNewsFeed {
   public String title;
    public String one_line_desc;
    public String detail_desc;
    public String ccode;
    //public String topics;
    public String links;
    public String thumb_image;
    public String image;
    public rowNewsFeed(){}
    public rowNewsFeed(String title, String one_line_desc, String detail_desc, String ccode,  String links, String thumb_image, String image) {
        this.title = title;
        this.one_line_desc = one_line_desc;
        this.detail_desc = detail_desc;
        this.ccode = ccode;
        //this.topics = topics;
        this.links = links;
        this.thumb_image = thumb_image;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getTitle() {
        return title;
    }

    public String getOne_line_desc() {
        return one_line_desc;
    }

    public String getDetail_desc() {
        return detail_desc;
    }

    public String getCcode() {
        return ccode;
    }


    public String getLinks() {
        return links;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOne_line_desc(String one_line_desc) {
        this.one_line_desc = one_line_desc;
    }

    public void setDetail_desc(String detail_desc) {
        this.detail_desc = detail_desc;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }


    public void setLinks(String links) {
        this.links = links;
    }
}
