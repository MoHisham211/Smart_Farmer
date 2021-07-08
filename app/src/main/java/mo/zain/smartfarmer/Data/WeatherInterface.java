package mo.zain.smartfarmer.Data;


import io.reactivex.rxjava3.core.Observable;
import mo.zain.smartfarmer.model.weather.WeatherModel;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface WeatherInterface {

    @GET("weather")
    public Observable<WeatherModel> getDetialsWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appid);

}
