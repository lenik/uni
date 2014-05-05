package net.bodz.uni.site.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectStat {

    List<?> favorites;
    List<?> comments;
    List<?> downloads;

    public ProjectStat() {
        favorites = new ArrayList<>();
        comments = new ArrayList<>();
        downloads = new ArrayList<>();
    }

    public List<?> getFavorites() {
        return favorites;
    }

    public List<?> getComments() {
        return comments;
    }

    public List<?> getDownloads() {
        return downloads;
    }

}
