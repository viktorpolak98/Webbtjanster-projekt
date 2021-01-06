import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static spark.Spark.*;

public class APIRunner {
	private Database storage;

	public APIRunner() {
		port(5000);

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

	public static void main(String[] args) {
		APIRunner server = new APIRunner();
	}
}
