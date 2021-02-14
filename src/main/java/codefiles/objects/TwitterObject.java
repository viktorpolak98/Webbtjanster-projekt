package codefiles.objects;

/**
 * An object representing tweets.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 2.1.0
 */
public class TwitterObject {
    private String id;
    private String text;
    private String location;
    private String date;
    private String user;

    public TwitterObject(String id, String text, String location, String date, String user) {
        this.id = id;
        this.text = text;
        this.location = location;
        this.date = date;
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

    public String getDate() {
        return date;
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
                ", datetime='" + date + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
