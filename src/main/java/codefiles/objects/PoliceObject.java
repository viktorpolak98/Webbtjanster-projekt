package codefiles.objects;

/**
 * An object representing police reports.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 2.1.0
 */
public class PoliceObject {
    private String id;
    private String date;
    private String name; 
    private String summary;
    private String url;
    private String type;
    private String location;

    public PoliceObject(String id, String date, String name, String summary, String url, String type, String location) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.summary = summary;
        this.url = url;
        this.type = type;
        this.location = location;
    }

    public String getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }

    public String getName() {
        return this.name;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getUrl() {
        return this.url;
    }

    public String getType() {
        return this.type;
    }

    public String getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "ApiObject{" +
                "id='" + id + '\'' +
                ", datetime='" + date + '\'' +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}