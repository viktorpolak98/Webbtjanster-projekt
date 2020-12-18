public class Database {
    private HashMap<String, ApiObject> database;

    public Database() {
        this.database = new HashMap<>();
    }

    public void putObject(ApiObject object) {
        this.database.putIfAbsent(object);
    }

    public ApiObject getObject(String key) {
        return this.database.get(key);
    }

    public ApliObject[] getObjects() {
        return this.database.toArray();
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