package codefiles.objects;

/**
 * An object representing tweets.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 1.0
 */
public class TwitterObject {
    private String id;
    private String text;
    private String location;
    private String datetime;
    private String user;

    public TwitterObject(String id, String text, String location, String datetime, String user) {
        this.id = id;
        this.text = text;
        this.location = location;
        this.datetime = datetime;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getLocation() {
        return location;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "TwitterObject{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", location='" + location + '\'' +
                ", datetime='" + datetime + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
