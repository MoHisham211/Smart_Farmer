package mo.zain.smartfarmer.Data;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import mo.zain.smartfarmer.model.Plant;
import retrofit2.http.GET;

public interface PlantsInterface {
    @GET("Plants.json")
    Observable<List<Plant>> getPlants();

    @GET("Fruits.json")
    Observable<List<Plant>> getFruits();

    @GET("Vegetables.json")
    Observable<List<Plant>> getVegetables();

}
