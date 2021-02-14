package codefiles;

import codefiles.objects.PoliceObject;
import codefiles.objects.TwitterObject;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

/**
 * The API of the database, enabling sites to access its data using the specified routes.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 2.1.0
 */
public class ApiRunner {
	private Database storage;
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
	 * Routes:
	 * options("/*")
	 *   sets up CORS.
	 * get("/events/:searchTerm/:startDate/:endDate)
	 *   populates the database with the data from the Police database then
	 *   returns all events matching the specified search term, start- & end date.
	 * get("/tweets/:x/:y/:date")
	 *   populates the database with the data from the Twitter database then
	 *   returns all tweets matching the specified time and area.
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

		get("/events/:searchTerm/:startDate/:endDate", (req, res) -> {
			res.header("Content-Type", "application/json");
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);

			String searchTerm = req.params(":searchTerm");
			String startDate = req.params(":startDate");
			String endDate = req.params(":endDate");

			populatePoliceData();
			PoliceObject[] resources = this.storage.getPolice(searchTerm, startDate, endDate);
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i = 0; i < resources.length; i++) {
				JsonObject event = new JsonObject();
				event.addProperty("id", resources[i].getId());

				String localDateTime = resources[i].getDate();
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

		get("/tweets/:x/:y/:date", (req, res) -> {
			res.type("application/json");
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);
			String x = req.params(":x");
			String y = req.params(":y");
			String coordinates = x + "," + y;

			String[] splitFullLocation = coordinates.split(",");
			String latitude = splitFullLocation[splitFullLocation.length-2];
			String longitude = splitFullLocation[splitFullLocation.length-1];

			String startDate = req.params(":date");
			String endDate;

			LocalDate formattedStartDate = LocalDate.parse(startDate);
			LocalDate formattedEndDate = formattedStartDate.plusDays(2);

			startDate = formattedStartDate.toString();
			endDate = formattedEndDate.toString();

			populateTwitterData(latitude, longitude, startDate, endDate);

			TwitterObject[] tweets = this.storage.getTwitter();
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i = 0; i < tweets.length; i++){
				JsonObject event = new JsonObject();

				event.addProperty("id", tweets[i].getId());
				event.addProperty("text", tweets[i].getText());
				event.addProperty("location", tweets[i].getLocation());
				event.addProperty("datetime", tweets[i].getDate());
				event.addProperty("user", tweets[i].getUser());
				sb.append(event.toString());

				if (i < tweets.length - 1){
					sb.append(",");
				}
			}
			sb.append("]");

			return sb.toString();
		});
	}

	/**
	 * This method acquires data from using the Police API, returned as a string.
	 * After the raw data has been acquired it is split into separate events and values
	 * and put into a two-dimensional array, representing events and their attributes.
	 * @return a two dimensional String array containing each entry and their values.
	 */
	public String[][] policeApiInterpreter() {
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
				int breakPoint = values[1].indexOf(":\"");
				values[1] = values[1].substring(breakPoint +2, breakPoint + 12);
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
	 * Takes the objects generated by the policeApiInterpreter method and populates the database.
	 */
	public void populatePoliceData() {
		String[][] data = policeApiInterpreter();
		PoliceObject[] events = new PoliceObject[data.length];

		for (int i = 0; i < data.length; i++) {
	 		PoliceObject apiObject = new PoliceObject(data[i][5], data[i][1], data[i][2], data[i][0], data[i][7], data[i][6], data[i][4]);
			events[i] = apiObject;
		}

		this.storage.setPolice(events);
	}

	/**
	 * Queries the Twitter database for tweets matching the specified arguments then
	 * sends this off to be put into the database.
	 * @param lat a String representing the latitude of the requested tweets.
	 * @param lon a String representing the longitude of the requested tweets.
	 * @param from a String representing when, back in time to start querying.
	 * @param until a String representing how far back in time it is to query.
	 */
	public void populateTwitterData(String lat, String lon, String from, String until) {
		QueryResult result;

		double latitude = Double.parseDouble(lat);
		double longitude = Double.parseDouble(lon);

		from = from.split(" ")[0];
		until = until.split(" ")[0];

		System.out.println("Lat: " + latitude);
		System.out.println("Long: " + longitude);
		System.out.println("From: " + from);
		System.out.println("Until: " + until);

		try {
			System.out.println("Searching...");
			Query query = new Query();
			query.geoCode(new GeoLocation(latitude, longitude), 3.0, "km");
			query.setSince(from);
			query.setUntil(until);
			query.setCount(15);

			result = twitter.search(query);
			System.out.println("Number of tweets: " + result.getCount());
			System.out.println("Number of returnable tweets: " + result.getTweets().size());
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			return;
		}

		System.out.println();
		populateTwitterMap(result, latitude, longitude);
	}

	/**
	 * Refractors the supplied data from the twitter database then stores it in the database.
	 * @param result the QueryResult containing the tweets matching the query.
	 * @param latitude a double representing the latitude of the search query,
	 *                    in case the tweet has no GeoLocation data.
	 * @param longitude a double representing the longitude of the search query,
	 *                     in case the tweet has no GeoLocation data.
	 */
	private void populateTwitterMap(QueryResult result, double latitude, double longitude) {
		List<Status> tweets = new ArrayList<>(result.getTweets());
		TwitterObject[] tweetArray = new TwitterObject[tweets.size()];

		for (int i=0; i<tweetArray.length; i++) {
			Status tweet = tweets.get(i);

			String id = "" + tweet.getId();
			String text = tweet.getText();

			String location;
			if (tweet.getGeoLocation() == null) {
				location = latitude + "," + longitude;
			} else {
				location = tweet.getGeoLocation().getLatitude() + "," + tweet.getGeoLocation().getLongitude();
			}

			String datetime = tweet.getCreatedAt().toString().substring(0, 10);
			String user = tweet.getUser().getName();

			TwitterObject twitterObject = new TwitterObject(id, text, location, datetime, user);
			tweetArray[i] = twitterObject;
		}

		this.storage.setTwitter(tweetArray);
	}

	public static void main(String[] args) {
		ApiRunner server = new ApiRunner();
	}
}