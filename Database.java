public class Database {
    private HashMap<String, ApiObject> database;

    public void Database() {
        this.database = new HashMap<>();
    }

    public void putObject(ApiObject object) {
        this.database.putIfAbsent(object);
    }

    public ApiObject getObject(String key) {
        return this.database.get(key);
    }

    public int size() {
        return this.database.size();
    }

    public void clear() {
        this.database.clear();
    }
}