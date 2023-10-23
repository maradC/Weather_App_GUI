import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//Retrieve weather data from API - this backend logic will fetch the latest weather
//data from external API and return it. the gui will
//display the data to the user

public class WeatherApp{

    public static JSONObject getWeatherData (String locationName){
        //get location coordinates using the geolocation Api
        JSONArray locationData = getLocationData(locationName);
        //extract latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Bulid Api request URL with location coordinates

        String urlString = " https://api.open-meteo.com/v1/forecast?latitude="+
                latitude+"&longitude="+longitude+"&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FNew_York";

        try{
            //call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200- means that the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Couldnt Connect to API ");
                return null;
            }
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read and store into String BUlider
                resultJson.append(scanner.nextLine());

            }
            scanner.close();
            conn.disconnect();

            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly  = (JSONObject) resultJsonObj.get("hourly");

            //we want to get the current hour's data
            //so we need to get the index of your current hour

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity data
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity =  (long) relativeHumidity.get(index);

            //get windsped
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("Weather condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            return weatherData;


        }catch(Exception e ){
            e.printStackTrace();
        }
        return null;
    }
    //retrives geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName){
        //replace any whitespace in location name to + to adhere to the API's request format
        locationName = locationName.replaceAll(" ", "+");

        //Bulid API url with location peremeter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";

        try{
            //Call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means good
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API ");
            }else{
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and stores the resulting data into our string bulider
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                //Disconnects from API
                conn.disconnect();

                //parse  the JSON into the JSON Obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonOBj = (JSONObject) parser.parse (String.valueOf(resultJson));

                //get the list of location data the API generated from the location name
                 JSONArray locationData = (JSONArray) resultsJsonOBj.get("results");
                return locationData;
            }

        }catch (Exception e){
            e.printStackTrace();;
        }
        return null;
    }
    private static HttpURLConnection fetchApiResponse (String urlString){
        try{
            //attempt to create connection
            URL url =  new URL (urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;
        }catch(IOException e ){
            e.printStackTrace();
        }

        //could not make connection
        return null;

    }
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time and see which one matches our current time
        for(int i =0 ; i < timeList.size() ; i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return the index
                return i;

            }
        }
        return 0;
    }
    public static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date to be "2023-10-22T00:00"(this is how we read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");

        //Format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    private static String convertWeatherCode(long weathercode){
        String weatherCondtiton = " ";
        if(weathercode==0L){
            //Clear
        weatherCondtiton = "Clear";
        } else if (weathercode > 0L && weathercode <=3L ) {
            weatherCondtiton = "Cloudy";
        }else if ((weathercode >= 51L && weathercode <=67)||(weathercode>=80L && weathercode <=99L)){
            weatherCondtiton = "Rain";
        } else if (weathercode >= 71L && weathercode <=77L ) {
            weatherCondtiton = "Snow";

        }
        return weatherCondtiton;
    }
}
