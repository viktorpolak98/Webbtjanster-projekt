package codefiles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.text.NumberFormat;
import java.util.List;

import static spark.Spark.*;

/**
 * A class enabling websites to access the database.
 * @author Viktor Polak, Tor Stenfeldt
 * @version 1.0
 */
public class APIRunner {
	private Database storage;
	private final Twitter twitter = new TwitterFactory().getInstance();
	private final AccessToken accessToken = new AccessToken("1339912172923727873-XklaSMP6xQJC9AfIMXyMk2Tg3S56kc", "36TPy4D7TbvjhIi2BIqQsaEfbObeqZCHG9Jj2sZFuhkAW");



	public APIRunner() {
		port(3000);
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

		get("/", (req, res) -> {
			res.header("Content-Type", "application/json");
			res.header(
					"Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept"
			);

			populateDataFromApi();
			ApiObject[] resources = this.storage.getObjects();

			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i = 0; i < resources.length; i++) {
				JsonObject event = new JsonObject();
				event.addProperty("id", resources[i].getId());
				event.addProperty("datetime", resources[i].getDatetime());
				event.addProperty("name", resources[i].getName());
				event.addProperty("summary", resources[i].getSummary());
				event.addProperty("url", resources[i].getUrl());
				event.addProperty("type", resources[i].getType());
				event.addProperty("location", resources[i].getLocation());
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
			ApiObject[] events = new ApiObject[requests.length];

			for (int i = 0; i < events.length; i++) {
				events[i] = gson.fromJson(requests[i], ApiObject.class);
			}

			this.storage.setData(events);

			return "";
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
		System.out.println("Split");
		HttpResponse<JsonNode> response = null;
		String body;
		String[] entries;
		String[][] data;

		try {
			response = Unirest.get(
					"https://polisen.se/api/events"
			).queryString(
					"fromat", "json"
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
			if (values[0].length()-3 >= 0){
				values[0] = values[0].substring(values[0].indexOf(":\"") + 2, values[0].length() - 3);
				System.out.println(values[0]);
			} else {
				values[0] = "";
			}

			//DateTime
			if (values[1].length()-3 >= 0){
				values[1] = values[1].substring(values[1].indexOf(":\"") + 2, values[1].length() - 3);
				System.out.println(values[1]);
			} else {
				values[1] = "";
			}

			//name
			if (values[2].length()-3 >= 0){
				values[2] = values[2].substring(values[2].indexOf(":\"") + 2, values[2].length() - 3);
				System.out.println(values[2]);
			} else {
				values[2] = "";
			}

			//location name
			if (values[3].length()-3 >= 0){
				values[3] = values[3].substring(values[3].indexOf(":\"") + 2, values[3].length() - 3);
				System.out.println(values[3]);
			} else {
				values[3] = "";
			}

			//location cords
			if (values[4].length()-4 >= 0){
				values[4] = values[4].substring(values[4].indexOf(":\"") + 2, values[4].length() - 4);
				System.out.println(values[4]);
			} else {
				values[4] = "";
			}

			//id
			if (values[5].length()-2 >= 0){
				values[5] = values[5].substring(values[5].indexOf(":") + 1, values[5].length() - 2);
				System.out.println(values[5]);
			} else {
				values[5] = "";
			}

			//type
			if (values[6].length()-3 >= 0){
				values[6] = values[6].substring(values[6].indexOf(":\"") + 2, values[6].length() - 3);
				System.out.println(values[6]);
			} else {
				values[6] = "";
			}

			//url
			if (values[7].length()-1 >= 0){
				values[7] = values[7].substring(6, values[7].length() - 1);
				System.out.println(values[7]);
			} else {
				values[7] = "";
			}

			for (int j = 0; j < values.length; j++) {
				data[i][j] = values[j];
			}
		}

		return data;
	}


		/*
			String fullLocation = values[4];
			String[] splitFullLocation = fullLocation.split(",");
			String latitude = splitFullLocation[splitFullLocation.length-2];
			String longitude = splitFullLocation[splitFullLocation.length-1];
			populateTwitterData(latitude, longitude);
		 */

	/**
	 * Creates objects from the data received from the police API and stores them in the database.
	 */
	public void populateDataFromApi() {
		String[][] data = SplitDataFromApi();

		for (int i = 0; i < data.length; i++) {
			ApiObject apiObject = new ApiObject(data[i][5], data[i][1], data[i][2], data[i][0], data[i][7], data[i][6], data[i][4]);
			storage.putObject(apiObject);
		}
	}

	public void populateTwitterData(String lat, String lon, String from, String until){

			double latitude = Double.parseDouble(lat);
			double longitude = Double.parseDouble(lon);
			try {
				Query query = new Query();
				query.geoCode(new GeoLocation(latitude, longitude), 3.0, "km");
				query.setSince(from);
				query.setUntil(until);
				QueryResult result;
				System.out.println("Searching...");

				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {

//				System.out.println(tweet.getUser());
					System.out.println(tweet.getText());
//				System.out.println(tweet.getId());
//				System.out.println(tweet.getPlace());
					System.out.println(tweet.getCreatedAt() + "\n");
					//TODO Add twitterData to database
				/*
				database.add(tweet.getText());


				 */
				}

			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: " + te.getMessage());
				System.exit(-1);
			}

	}

	/*private static class Main{
		public static void main(String[] args) {
			final Twitter twitter = new TwitterFactory().getInstance();
			final AccessToken accessToken = new AccessToken("1339912172923727873-XklaSMP6xQJC9AfIMXyMk2Tg3S56kc", "36TPy4D7TbvjhIi2BIqQsaEfbObeqZCHG9Jj2sZFuhkAW");
			twitter.setOAuthConsumer("aesqAiPjoaUmxhHYq6gbTkqyN", "eddjO1013o25Bufn8u4wMligT1eMHJGYH1A9r3hmfBZeMfdiXj");
			twitter.setOAuthAccessToken(accessToken);

			String date = "2021-02-10:07:08:36";
			String date2 = "2021-02-12:07:08:36";
			try {
				Query query = new Query();
				query.geoCode(new GeoLocation(65.316698, 21.480036), 1.0, "km").setSince(date);
				query.until(date2);
				QueryResult result;
				System.out.println("Searching...");

				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {

				System.out.println(tweet.getUser());
					System.out.println(tweet.getText());
				System.out.println(tweet.getId());
				System.out.println(tweet.getPlace());
					System.out.println(tweet.getCreatedAt() + "\n");
					//TODO Add twitterData to database
				/*
				database.add(tweet.getText());


				 */

/*					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} */
/*				}

			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: " + te.getMessage());
				System.exit(-1);
			}


		}
	} */

	public static void main(String[] args) throws TwitterException {
		APIRunner server = new APIRunner();
	}
}