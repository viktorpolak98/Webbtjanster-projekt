package codefiles;

import codefiles.objects.PoliceObject;
import codefiles.objects.TwitterObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

/**
 * A class enabling websites to access the database.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 1.0
 */
public class ApiRunner {
	private Database storage;
	//private final Twitter twitter = new TwitterFactory().getInstance();
	private final Twitter twitter = TwitterFactory.getSingleton();
	private final AccessToken accessToken = new AccessToken("1339912172923727873-XklaSMP6xQJC9AfIMXyMk2Tg3S56kc", "36TPy4D7TbvjhIi2BIqQsaEfbObeqZCHG9Jj2sZFuhkAW");

	public ApiRunner() {
		port(4000);
		staticFiles.location("/public");
		twitter.setOAuthConsumer("aesqAiPjoaUmxhHYq6gbTkqyN", "eddjO1013o25Bufn8u4wMligT1eMHJGYH1A9r3hmfBZeMfdiXj");
		twitter.setOAuthAccessToken(accessToken);

		try {
			this.storage = new Database();
			initRoutes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the different routes, enabling access to the database.
	 * the get token requires no values and will return the data stored in the database.
	 * the put token requires a collection of police events from their API and stores it in the database.
	 */
	public void initRoutes() {
		options("/*",
				(request, response) -> {
					String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");

					if (accessControlRequestHeaders != null) {
						response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
					}

					String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

					if (accessControlRequestMethod != null) {
						response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
					}

					return "OK";
				}
		);

		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

		//get("/events?type=:searchTerm&startDate=:startDate&endDate=:endDate", (req, res) -> {
		get("/events/:searchTerm/:startDate/:endDate", (req, res) -> {
			res.header("Content-Type", "application/json");
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);

			String searchTerm = req.params(":searchTerm");
			String startDate = req.params(":startDate");
			String endDate = req.params(":endDate");

			populateDataFromApi();
			PoliceObject[] resources = this.storage.getPolice(searchTerm, startDate, endDate);
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i = 0; i < resources.length; i++) {
				JsonObject event = new JsonObject();
				event.addProperty("id", resources[i].getId());

				String localDateTime = resources[i].getDatetime().substring(0, 19);
				localDateTime = localDateTime.replace(' ', 'T');
				event.addProperty("datetime", localDateTime);

				String name = resources[i].getName();
				event.addProperty("name", name);

				String summary = resources[i].getSummary();
				event.addProperty("summary", summary);

				String url = resources[i].getUrl();
				event.addProperty("url", url);

				String type = resources[i].getType();
				event.addProperty("type", type);

				String coordinates = resources[i].getLocation();
				event.addProperty("location", coordinates);

				sb.append(event.toString());

				if (i < resources.length - 1) {
					sb.append(",");
				}
			}
			sb.append("]");

			return sb.toString();
		});

		put("/", (req, res) -> {
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);

			Gson gson = new Gson();
			String request = req.body();

            request = request.substring(request.lastIndexOf("['"));
            request = request.substring(0, request.lastIndexOf("']"));
			String[] requests = request.split("', '");
            PoliceObject[] events = new PoliceObject[requests.length];

            for (int i=0; i<events.length; i++) {
                events[i] = gson.fromJson(requests[i], PoliceObject.class);
            }

            this.storage.setPolice(events);
			return "";
		});

		get("/tweets/:x/:y/:date", (req, res) -> {
			res.type("application/json");
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);
			String x = req.params(":x");
			String y = req.params(":y");
			String coordinates = x + "," + y;

			String startDate = req.params(":date");

			if (startDate.charAt(12) == ':') {
				System.out.println("Error located. Date size: " + startDate.length());
				startDate = startDate.split("T")[0] + "T0" + startDate.split("T")[1];
			}

			String endDate = "";

			LocalDateTime formattedStartDate = LocalDateTime.parse(startDate);
			//LocalDateTime formattedEndDate = formattedStartDate.plusMinutes(30);
			LocalDateTime formattedEndDate = formattedStartDate.plusDays(1);

			startDate = formattedStartDate.toString();
			startDate += ":00";
			startDate = startDate.replace('T', ' ');

			endDate = formattedEndDate.toString();
			endDate += ":00";
			endDate = endDate.replace('T', ' ');

			String[] splitFullLocation = coordinates.split(",");
			String latitude = splitFullLocation[splitFullLocation.length-2];
			String longitude = splitFullLocation[splitFullLocation.length-1];

			populateTwitterData(latitude, longitude, startDate, endDate);

			TwitterObject[] resources = this.storage.getTwitter();
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i = 0; i < resources.length; i++){
				JsonObject event = new JsonObject();

				event.addProperty("id", resources[i].getId());
				event.addProperty("text", resources[i].getText());
				event.addProperty("location", resources[i].getLocation());
				event.addProperty("datetime", resources[i].getDatetime());
				event.addProperty("user", resources[i].getUser());

				if (i < resources.length - 1){
					sb.append(",");
				}
			}
			sb.append("]");

			return sb.toString();
		});
	}

	/**
	 * This method aquires data from the police-api as a string
	 * After the raw data is aquired it splits each seprate object in to an array
	 * After the objects have been divided it adds each parts values in to an two dimensional array
	 *
	 * @return a two dimensional array containing each entry and their values.
	 */
	public String[][] SplitDataFromApi() {
		HttpResponse<JsonNode> response = null;
		String body;
		String[] entries;
		String[][] data;

		try {
			response = Unirest.get(
					"https://polisen.se/api/events"
			).queryString(
					"format", "json"
			).asJson();

		} catch (UnirestException e) {
			e.printStackTrace();
		}

		if (response == null) {
			return null;
		}

		body = response.getBody().toString();
		body = body.substring(1, body.length() - 2);
		entries = body.split("},\\{");

		data = new String[entries.length][8];

		for (int i = 0; i < entries.length; i++) {
			String[] values = new String[8];
			for (int j = 0; j < 7; j++) {
				int breakpoint = entries[i].indexOf(",\"") + 2;
				values[j] = entries[i].substring(0, breakpoint);
				entries[i] = entries[i].substring(breakpoint);
			}
			values[7] = entries[i];

			//summary
			if (values[0].length()-3 >= 0) {
				values[0] = values[0].substring(values[0].indexOf(":\"") + 2, values[0].length() -3 );
			} else {
				values[0] = "";
			}

			//DateTime
			if (values[1].length()-3 >= 0){
				values[1] = values[1].substring(values[1].indexOf(":\"") + 2, values[1].length() - 3);
			} else {
				values[1] = "";
			}

			//name
			if (values[2].length()-3 >= 0){
				values[2] = values[2].substring(values[2].indexOf(":\"") + 2, values[2].length() - 3);
			} else {
				values[2] = "";
			}

			//location name
			if (values[3].length()-3 >= 0){
				values[3] = values[3].substring(values[3].indexOf(":\"") + 2, values[3].length() - 3);
			} else {
				values[3] = "";
			}

			//location cords
			if (values[4].length()-4 >= 0){
				values[4] = values[4].substring(values[4].indexOf(":\"") + 2, values[4].length() - 4);
			} else {
				values[4] = "";
			}

			//id
			if (values[5].length()-2 >= 0){
				values[5] = values[5].substring(values[5].indexOf(":") + 1, values[5].length() - 2);
			} else {
				values[5] = "";
			}

			//type
			if (values[6].length()-3 >= 0){
				values[6] = values[6].substring(values[6].indexOf(":\"") + 2, values[6].length() - 3);
			} else {
				values[6] = "";
			}

			//url
			if (values[7].length()-1 >= 0){
				values[7] = values[7].substring(6, values[7].length() - 1);
			} else {
				values[7] = "";
			}

			for (int j = 0; j < values.length; j++) {
				data[i][j] = values[j];
			}
		}

		return data;
	}

	/**
	 * Creates objects from the data received from the police API and stores them in the database.
	 */
	public void populateDataFromApi() {
		String[][] data = SplitDataFromApi();

		for (int i = 0; i < data.length; i++) {
	 		PoliceObject apiObject = new PoliceObject(data[i][5], data[i][1], data[i][2], data[i][0], data[i][7], data[i][6], data[i][4]);
			storage.putPolice(apiObject);
		}
	}

	public boolean populateTwitterData(String lat, String lon, String from, String until) {
		QueryResult result;

		double latitude = Double.parseDouble(lat);
		double longitude = Double.parseDouble(lon);

		from = from.replace(' ', ':');
		until = until.replace(' ', ':');

		System.out.println("Lat: " + latitude);
		System.out.println("Long: " + longitude);
		System.out.println("From: " + from);
		System.out.println("Until: " + until);

		try {
			System.out.println("Searching...");
			Query query = new Query();
			//query.setGeoCode((new GeoLocation(latitude, longitude)), 1.0, "km");
			query.geoCode(new GeoLocation(latitude, longitude), 10.0, "km");
			query.setSince(from);
			query.setUntil(until);
			query.setCount(100);

			result = twitter.search(query);
			System.out.println("Number of tweets: " + result.getCount());
			System.out.println("Number of returnable tweets: " + result.getTweets().size());
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
			return false;
		}

		List<Status> tweets = new ArrayList<>(result.getTweets());
		TwitterObject[] tweetArray = new TwitterObject[tweets.size()];

		for (int i=0; i<tweetArray.length; i++) {
			Status tweet = tweets.get(i);

			String id = "" + tweet.getId();
			String text = tweet.getText();
			System.out.println(text);

			String location = tweet.getGeoLocation().toString();
			String datetime = tweet.getCreatedAt().toString().substring(0, 10);
			String user = tweet.getUser().getName();

			TwitterObject twitterObject = new TwitterObject(id, text, location, datetime, user);
			tweetArray[i] = twitterObject;
		}

		this.storage.setTwitter(tweetArray);


		return true;
	}

	public static void main(String[] args) {
		ApiRunner server = new ApiRunner();
	}
}