package codefiles;

import codefiles.objects.PoliceObject;
import codefiles.objects.TwitterObject;

import java.util.HashMap;

/**
 * The database containing police- and Twitter objects.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 1.1
 */
public class Database {
    private HashMap<String, PoliceObject> policeDB;
    private HashMap<String, TwitterObject> twitterDB;

    public Database() {
        this.policeDB = new HashMap<>();
        this.twitterDB = new HashMap<>();
    }

    public void putPolice(PoliceObject object) {
        this.policeDB.putIfAbsent(object.getId(), object);
    }

    public PoliceObject getPolice(String key) {
        return this.policeDB.get(key);
    }

    public PoliceObject[] getPolice() {
        return this.policeDB.values().toArray(new PoliceObject[policeDB.size()]);
    }

    public void setPolice(PoliceObject[] data) {
        clearPolice();
        for (int i=0; i<data.length; i++) {
            this.policeDB.put(data[i].getId(), data[i]);
        }
    }

    public void clearPolice() {
        this.policeDB.clear();
    }

    public int sizePolice() {
        return this.policeDB.size();
    }

    public void putTwitter(TwitterObject object) {
        this.twitterDB.putIfAbsent(object.getId(), object);
    }

    public TwitterObject getTwitter(String key) {
        return this.twitterDB.get(key);
    }

    public TwitterObject[] getTwitter() {
        return this.twitterDB.values().toArray(new TwitterObject[twitterDB.size()]);
    }

    public void setTwitter(TwitterObject[] data) {
        clearTwitter();
        for (int i=0; i<data.length; i++) {
            this.twitterDB.put(data[i].getId(), data[i]);
        }
    }

    public void clearTwitter() {
        this.twitterDB.clear();
    }

    public int sizeTwitter() {
        return this.policeDB.size();
    }
}