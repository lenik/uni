package net.bodz.uni.autocommit.classifier;

public class StatusEntry {

    StatusType type;
    String path;

    public StatusType getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public void setType(StatusType type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
