package codefiles;

import java.util.HashMap;
/**
 * A database that contains the ApiObjects that are created
 * @author Viktor Polak, Tor Stenfeldt
 * @version 1.0
 */
public class Database {
    private HashMap<String, ApiObject> database;

    public Database() {
        this.database = new HashMap<>();
    }

    public void putObject(ApiObject object) {
        this.database.putIfAbsent(object.getId(), object);
    }

    public ApiObject getObject(String key) {
        return this.database.get(key);
    }

    public ApiObject[] getObjects() {
        return this.database.values().toArray(new ApiObject[database.size()]);
    }

    public void setData(ApiObject[] data) {
        clear();
        for (int i=0; i<data.length; i++) {
            this.database.put(data[i].getId(), data[i]);
        }
    }

    public int size() {
        return this.database.size();
    }

    public void clear() {
        this.database.clear();
    }

}