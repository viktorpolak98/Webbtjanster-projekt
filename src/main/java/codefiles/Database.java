package codefiles;

import codefiles.objects.PoliceObject;
import codefiles.objects.TwitterObject;

import java.time.LocalDate;
import java.util.*;

/**
 * The database containing police and Twitter objects.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 2.1.0
 */
public class Database {
    private HashMap<String, PoliceObject> policeDB;
    private HashMap<String, TwitterObject> twitterDB;

    public Database() {
        this.policeDB = new HashMap<>();
        this.twitterDB = new HashMap<>();
    }

    /**
     * Gets the events from the police database given the specified parameters.
     * @param type a String representing the type of police event.
     * @param startDate a String representing the start date from where to query.
     * @param endDate a String representing the end date when to stop querying.
     * @return an array of PoliceObjects matching the supplied attributes.
     */
    public PoliceObject[] getPolice(String type, String startDate, String endDate) {
        LinkedList<PoliceObject> list = new LinkedList<>();

        boolean hasType = (!type.equals("*"));
        boolean hasStart = (!startDate.equals("*"));
        boolean hasEnd = (!endDate.equals("*"));
        boolean hasDate = (hasStart && hasEnd);

        if (hasType && hasDate) {
            LocalDate formattedStart = LocalDate.parse(startDate);
            LocalDate formattedEnd = LocalDate.parse(endDate);

            this.policeDB.values().forEach(e -> {
                if (e.getType().equals(type)) {
                    if (checkDate(e, formattedStart, formattedEnd)) {
                        list.add(e);
                    }
                }
            });
        } else if (hasType) {
            this.policeDB.values().forEach(e -> {
                if (e.getType().equals(type)) {
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

    /**
     * Checks if the reported time of the supplied police object lies between the
     * two supplied dates or not.
     * @param po the PoliceObject which to check.
     * @param start a String representing the beginning of the timeline.
     * @param end a String representing the end of the timeline.
     * @return a boolean declaring whether the police event happened during the supplied timeline.
     */
    private boolean checkDate(PoliceObject po, LocalDate start, LocalDate end) {
        LocalDate formattedEvent = LocalDate.parse(po.getDate());
        boolean isAfter = formattedEvent.isAfter(start) || formattedEvent.isEqual(start);
        boolean isBefore = formattedEvent.isBefore(end) || formattedEvent.isEqual(end);
        return (isAfter && isBefore);
    }

    /**
     * Sets the data in the hashmap containing police events to the specified attribute.
     * As the Swedish Police appearently reports events with empty values from time to time,
     * these will be checked and potentially ignored while going into the hashmap.
     * @param data an array of PoliceObjects which is to be stored.
     */
    public void setPolice(PoliceObject[] data) {
        clearPolice();

        for (PoliceObject event : data) {
            boolean hasID = !event.getId().isEmpty();
            boolean hasDateTime = !event.getDate().isEmpty();
            boolean hasName = !event.getName().isEmpty();
            boolean hasSummary = !event.getSummary().isEmpty();
            boolean hasUrl = !event.getUrl().isEmpty();
            boolean hasType = !event.getType().isEmpty();
            boolean hasLocation = !event.getLocation().isEmpty();

            if (hasID && hasDateTime && hasName && hasSummary && hasUrl && hasType && hasLocation) {
                this.policeDB.put(event.getId(), event);
            }
        }
    }

    public void clearPolice() {
        this.policeDB.clear();
    }

    public TwitterObject[] getTwitter() {
        return this.twitterDB.values().toArray(new TwitterObject[twitterDB.size()]);
    }

    /**
     * Overwrites the data in the hashmap containing TwitterObjects to the given attribute.
     * @param data an array of TwitterObjects to be put in the database.
     */
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