import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGUI(){
        //set up our Gui and add a title
        super("Weather App");
        //configure gui to end the programs process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //set the size of our gui(in pixels)
        setSize(450,650);

        //load our gui at the center of the screen
        setLocationRelativeTo(null);

        //make our layout manger null to manually position our Componets within the gui
        setLayout(null);

        //Prevent any resize of our gui
        setResizable(false);

        addGUIComponets();

    }
    private void addGUIComponets(){
        //search field
        JTextField searchTextField = new JTextField();

        //set the location and sizeof our componets
        searchTextField.setBounds(15,15,351,45);

        //change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);


        //Weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/weatherapp_images/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450 , 217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText =  new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD,48));

        //Center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //Weather Condition Description
        JLabel weatherConditionDec = new JLabel("Cloudy");
        weatherConditionDec.setBounds(0,405,450,36);
        weatherConditionDec.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDec.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDec);
        
        //Humidity Image
        JLabel HumidityImage = new JLabel(loadImage("src/assets/weatherapp_images/humidity.png"));
        HumidityImage.setBounds(15,500,74,66);
        add(HumidityImage);
        
        //Humidity text
        JLabel HumidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        HumidityText.setBounds(90,500,85,55);
        HumidityText.setFont(new Font("Dialog", Font.BOLD,16));
        add(HumidityText);

        //WindSpeed Image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/weatherapp_images/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74,66);
        add(windSpeedImage);

        //WindSpeed Text
        JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 100%</html>");
        windSpeedText.setBounds(310,500,85,55);
       windSpeedText.setFont(new Font("Dialog", Font.BOLD,16));
        add(windSpeedText);

        //SEARCH BUTTON
        JButton searchButton = new JButton(loadImage("src/assets/weatherapp_images/search.png"));
        //change the cursor to a hand courser over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();
                //validate input from user - remove whitepace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }
                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update Gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //depending on codtion, we will update the weather image that corresponds with the condtion
                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/cloudy.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/weatherapp_images/snow.pngImage"));
                        break;
                }
                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                //update weather condition
                weatherConditionDec.setText(weatherCondition);

                //update humidty text
                long humidity = (long) weatherData.get("humidity");
                HumidityText.setText("<html><b>Humidity<\b>"+ humidity +"%</html>");

                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windpseed<\b>"+ windspeed +"km/h</html>");



            }
        });
        add(searchButton);

    }
    //used to create images in our gui Componets
    private ImageIcon loadImage(String resourcePath){
        try {
            //Read the image file from the path given
           BufferedImage image = ImageIO.read(new File(resourcePath));
            //return an image so that our components  can render it
            return new ImageIcon(image);
        } catch(IOException e ){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
                return null;
    }

}

