package codefiles;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;

import static spark.Spark.*;

public class APIRunner {
	private Database storage;

	public APIRunner() {
		port(5000);
		populateDataFromApi();

		try {
			this.storage = new Database();
			initRoutes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

            ApiObject[] resources = this.storage.getObjects();
			StringBuilder sb = new StringBuilder();

			sb.append("[");
			for (int i=0; i<resources.length; i++) {
				JsonObject event = new JsonObject();
				event.addProperty("id",  resources[i].getId());
                event.addProperty("datetime", resources[i].getDatetime());
				event.addProperty("name", resources[i].getName());
                event.addProperty("summary", resources[i].getSummary());
                event.addProperty("url", resources[i].getUrl());
                event.addProperty("type", resources[i].getType());
                event.addProperty("location", resources[i].getLocation());
				sb.append(event.toString());

				if (i<resources.length-1) {
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

            for (int i=0; i<events.length; i++) {
                events[i] = gson.fromJson(requests[i], ApiObject.class);
            }

            this.storage.setData(events);

			return "";
		});
	}

	public void populateDataFromApi(){
		HttpResponse<JsonNode> response;

		try{
			response = Unirest.get("https://polisen.se/api/events")
					.queryString("fromat", "json")
					.asJson();

			System.out.println("Response from polisen");
			System.out.println(response.getBody());
			System.out.println();

			String string = response.getBody().toString();

            String[] data = string.split("\",\"|" +
                    "\\},\\{|" +
                    "\\},|" +
					", \\{");
//            String[] data = string.split("summary|" +
//                    "datetime|" +
//                    ",\"name|" +
//                    "location|" +
//                    "id|" +
//                    "type|" +
//                    "url");
//			String[] data = string.split("\",\"|" +
//					"\\},\\{");



			System.out.println(string + " String");
//            System.out.println(Arrays.toString(data));
			String summary;
			String date;
			String name;
			String location;
			String id;
			String type;
			String url;


			for (int i = 0; i < data.length; i++){
				System.out.println(data[i]);
				System.out.println();
//                summary = data[i];
//                date = data[i+1];
//                name = data[i+2];
//                location = data[i+3];
//                id = data[i+4];
//                type = data[i+5];
//                url = data[i+6];
//
//                ApiObject apiObject = new ApiObject(id, date, name, summary, url, type, location);
//                System.out.println(apiObject.toString());
			}

//            System.out.println(data[5]);

//            for (int i = 0; i < 500; i++){
//
//            }



//            Database storage = new Database();
//            storage.putObject();

		} catch (UnirestException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		APIRunner server = new APIRunner();
	}
}
