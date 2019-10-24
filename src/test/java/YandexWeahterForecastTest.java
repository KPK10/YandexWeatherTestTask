import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.*;

import java.util.Date;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class YandexWeahterForecastTest {

    private HttpResponse<JsonNode> responseForecast;
    private final String yandexWeatherKey = "533c30f9-4aba-4832-a9d7-213c2540e6b0";
    private final String latMoscow = "55.75396";
    private final String lonMoscow = "37.620393";
    private final String yandexWeatherURL = "https://yandex.ru/pogoda/";

    public YandexWeahterForecastTest() {

    }

    public String getCurrentSeason(int numOfMonth) {
        String seasonName = "";
        switch (numOfMonth) {
            case 1:
            case 2:
            case 12:
                seasonName = "winter";
                break;
            case 3:
            case 4:
            case 5:
                seasonName = "spring";
                break;
            case 6:
            case 7:
            case 8:
                seasonName = "summer";
                break;
            case 9:
            case 10:
            case 11:
                seasonName = "autumn";
                break;
            default:
                seasonName = "unknown";
                break;
        }

        return seasonName;
    }

    public String getMoonTextDescription(int moonCode) {
        String moonText = "";
        switch (moonCode) {
            case 0:
                moonText = "full-moon";
                break;
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
            case 7:
                moonText = "decreasing-moon";
                break;
            case 4:
                moonText = "last-quarter";
                break;
            case 8:
                moonText = "new-moon";
                break;
            case 9:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
                moonText = "growing-moon";
                break;
            case 12:
                moonText = "first-quarter";
                break;
            default:
                moonText = "unknown";
                break;
        }

        return moonText;
    }

    @Before
    public void initTest() {
        try {
            responseForecast = Unirest
                    .get("https://api.weather.yandex.ru/v1/forecast")
                    .header("X-Yandex-API-Key", yandexWeatherKey)
                    .queryString("lat", latMoscow)
                    .queryString("lon", lonMoscow)
                    .queryString("limit", 2)
                    .asJson();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @Test
    public void forecastLatitudeTest() {
        String latRs = responseForecast.getBody().getObject().getJSONObject("info").get("lat").toString();
        assertEquals(latMoscow, latRs);
    }

    @Test
    public void forecastLongitudeTest() {
        String lonRs = responseForecast.getBody().getObject().getJSONObject("info").get("lon").toString();
        assertEquals(lonMoscow, lonRs);
    }

    @Test
    public void forecastOffsetTest() {
        int offsetRs = responseForecast.getBody().getObject().getJSONObject("info").getJSONObject("tzinfo").getInt("offset");
        assertEquals(10800, offsetRs);
    }

    @Test
    public void forecastOffsetFullNameTest() {
        String fullNameRs = responseForecast.getBody().getObject().getJSONObject("info").getJSONObject("tzinfo").getString("name");
        assertEquals("Europe/Moscow", fullNameRs);
    }

    @Test
    public void forecastOffsetShortNameTest() {
        String shortNameRs = responseForecast.getBody().getObject().getJSONObject("info").getJSONObject("tzinfo").getString("abbr");
        assertEquals("MSK", shortNameRs);
    }

    @Test
    public void forecastSummerTimeAttributeTest() {
        boolean attrSummerTimeRs = responseForecast.getBody().getObject().getJSONObject("info").getJSONObject("tzinfo").getBoolean("dst");
        assertEquals(false, attrSummerTimeRs);
    }

    @Test
    public void forecastURLTest() {
        String forecastUrlRs = responseForecast.getBody().getObject().getJSONObject("info").getString("url");
        StringBuilder yandexWeatherMoscowURL = new StringBuilder();
        yandexWeatherMoscowURL.append(yandexWeatherURL);
        yandexWeatherMoscowURL.append("?lat=");
        yandexWeatherMoscowURL.append(latMoscow);
        yandexWeatherMoscowURL.append("&lon=");
        yandexWeatherMoscowURL.append(lonMoscow);
        assertEquals(yandexWeatherMoscowURL.toString(), forecastUrlRs);
    }

    @Test
    public void forecastDurationTest() {
        String responseBodyString = responseForecast.getBody().toString();
        String []dateSplitArray = responseBodyString.split("\"date\"");
        int forecastDaysCount = dateSplitArray.length - 1;
        assertEquals(2, forecastDaysCount);
    }

    @Test
    public void forecastSeasonTest() {
        String seasonRs = responseForecast.getBody().getObject().getJSONObject("fact").getString("season");
        assertEquals(getCurrentSeason(Integer.parseInt(new SimpleDateFormat("MM").format(new Date()))), seasonRs);
    }

    @Test
    public void forecastMoonTextTest() {
        int secondDayMoonCodeRs = responseForecast.getBody().getObject().getJSONArray("forecasts").getJSONObject(1).getInt("moon_code");
        String secondDayMoonTextRs = responseForecast.getBody().getObject().getJSONArray("forecasts").getJSONObject(1).getString("moon_text");
        assertEquals(getMoonTextDescription(secondDayMoonCodeRs), secondDayMoonTextRs);
    }

}
