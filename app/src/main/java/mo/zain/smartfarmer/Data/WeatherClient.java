package mo.zain.smartfarmer.Data;


import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import mo.zain.smartfarmer.model.weather.WeatherModel;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherClient {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private WeatherInterface weatherInterface;
    private static WeatherClient INSTANCE;

    public WeatherClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        weatherInterface = retrofit.create(WeatherInterface.class);
    }

    public static WeatherClient getINSTANCE() {
        if (null == INSTANCE) {
            INSTANCE = new WeatherClient();
        }
        return INSTANCE;
    }

    public Observable<WeatherModel> getDetialsWeather(String lat, String lon, String appID) {
        return weatherInterface.getDetialsWeather(lat,lon,appID);
    }

}