package codefiles;

import codefiles.objects.PoliceObject;
import codefiles.objects.TwitterObject;

import java.time.LocalDate;
import java.util.*;

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

    public PoliceObject[] getPolice(String searchTerm, String startDate, String endDate) {
        LinkedList<PoliceObject> list = new LinkedList<>();

        boolean hasType = (!searchTerm.equals("*"));
        boolean hasStart = (!startDate.equals("*"));
        boolean hasEnd = (!endDate.equals("*"));
        boolean hasDate = (hasStart && hasEnd);

        if (hasType && hasDate) {
            LocalDate formattedStart = LocalDate.parse(startDate);
            LocalDate formattedEnd = LocalDate.parse(endDate);

            this.policeDB.values().forEach(e -> {
                if (e.getType().equals(searchTerm)) {
                    if (checkDate(e, formattedStart, formattedEnd)) {
                        list.add(e);
                    }
                }
            });
        } else if (hasType) {
            this.policeDB.values().forEach(e -> {
                if (e.getType().equals(searchTerm)) {
                    list.add(e);
                }
            });
        } else if (hasDate) {
            LocalDate formattedStart = LocalDate.parse(startDate);
            LocalDate formattedEnd = LocalDate.parse(endDate);

            this.policeDB.values().forEach(e -> {
                if (checkDate(e, formattedStart, formattedEnd)) {
                    list.add(e);
                }
            });
        } else {
            list.addAll(this.policeDB.values());
        }

        PoliceObject[] array = new PoliceObject[list.size()];
        for (int i=0; i<list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }

    private boolean checkDate(PoliceObject po, LocalDate start, LocalDate end) {
        if (po.getDatetime().length() > 10) {
            LocalDate formattedEvent = LocalDate.parse(po.getDatetime().substring(0, 10));
            boolean isAfter = formattedEvent.isAfter(start) || formattedEvent.isEqual(start);
            boolean isBefore = formattedEvent.isBefore(end) || formattedEvent.isEqual(end);
            return (isAfter && isBefore);
        }

        return false;
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
}