public class ApiObject {
    private String id;
    private String datetime;
    private String name; 
    private String summary;
    private String url;
    private String type;
    private String location;
}

public ApiObject(String id, String datetime, String name, String summary, String url, String type, String location) {
    this.id = id;
    this.datetime = datetime;
    this.name = name;
    this.summary = summary;
    this.url = url;
    this.type = type;
    this.location = location;
}

public String getId() {
    return this.id;
}

public String getDatetime() {
    return this.datetime;
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
