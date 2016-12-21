package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Niclas Johansson
 */
public class TrafikLabConnector {

    //Adresses to api
    private final String apiKeyStationsearch = "4431b4f8-6444-496c-903b-58c319190f96";
    private final String apiKeyDepartures = "575664d7-da0f-4e76-88b9-55b42f29c19a";
    private final String stationURL = "https://api.resrobot.se/v2/location.name?key=%s&format=json&input=%s";
    //key, id
    private final String departuresURL = "https://api.resrobot.se/v2/departureBoard?key=%s&maxJourneys=20&format=json&id=%s";
    //key, id, date(yyyy-mm-dd), time (hh:mm)
    private final String departuresURLSpecTime = "https://api.resrobot.se/v2/departureBoard?key=%s&maxJourneys=20&format=json&id=%s&date=%s&time=%s";

    /**
     * Retrive all station based on a search. Returns a Hashmap where 
     * key=stationName and value=idNumber. Returns null if error occured.
     *
     * @param searchString
     * @return Map<String,String>
     */
    public Map<String, String> getStations(String searchString) {
        String adress = String.format(stationURL, apiKeyStationsearch, searchString);
        JSONObject jo = getJsonFromApi(adress);
        if (jo == null) {
            return null;
        }
        JSONArray ja = (JSONArray) jo.get("StopLocation");
        Map<String, String> result = new HashMap();
        for (int i = 0; i < ja.size(); i++) {
            JSONObject JoTemp = (JSONObject) ja.get(i);
            result.put(JoTemp.get("name").toString(), JoTemp.get("id").toString());
        }
        System.out.println(Arrays.asList(result));
        return result;
    }

    /**
     * 
     * @param stationId
     * @param date
     * @return
     */
    public Departure[] getDepartures(String stationId, String date, String time) {
        //Base url on if time and date was entered
        String adress = "";
        if (date == null || time == null) {
            adress = String.format(departuresURL, apiKeyDepartures, stationId);
        } else {
            adress = String.format(departuresURLSpecTime, apiKeyDepartures, stationId, date, time);
        }
        //Process response
        JSONObject entireResponse = getJsonFromApi(adress);
        JSONArray jsonArrOfDep = (JSONArray) entireResponse.get("Departure");
        //Check the amount of departures and create correctly size array
        int amountOfDep = jsonArrOfDep.size();
        Departure[] result = new Departure[amountOfDep];
        //Create new departure object for every departure in JSON array
        for (int i = 0; i < amountOfDep; i++) {
            JSONObject departureJson = (JSONObject) jsonArrOfDep.get(i);
            Departure dep = new Departure();
            dep.date = departureJson.get("date").toString();
            dep.direction = departureJson.get("direction").toString();
            dep.from = departureJson.get("stop").toString();
            //TODO
            dep.id = departureJson.get("").toString();
            //----
            dep.name = departureJson.get("name").toString();
            JSONObject stopsObject = (JSONObject)departureJson.get("Stops");
            JSONArray stopsArray = (JSONArray)stopsObject.get("Stop");
            dep.stops = new String[amountOfDep];
            for(int j = 0; j < amountOfDep; j++){
                JSONObject stopJs = (JSONObject) stopsArray.get(i); 
                String name = stopJs.get("name").toString();
                dep.stops[i] = stopsArray.get(i).toString();
            }
            dep.time = departureJson.get("time").toString();
            String transportCategory = departureJson.get("transportCategory").toString();
            dep.transsportType = transportCategory.charAt(0);
            result[i] = dep;
        }
        return result;
    }

    private JSONObject getJsonFromApi(String adress) {
        JSONObject jo = null;
        try {
            URL url = new URL(adress);
            InputStream in = url.openStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));
            String line;
            String result = "";
            while ((line = bin.readLine()) != null) {
                result = result + line;
                result = result + "\n";
            }
            JSONParser parser = new JSONParser();
            jo = (JSONObject) parser.parse(result);
        } catch (MalformedURLException ex) {
            return null;
        } catch (IOException | ParseException ex) {
            return null;
        }
        return jo;
    }
}
