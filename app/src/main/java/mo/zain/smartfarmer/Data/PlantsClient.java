package mo.zain.smartfarmer.Data;


import java.util.List;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import mo.zain.smartfarmer.model.Plant;
import mo.zain.smartfarmer.model.PlantPrice;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantsClient {
    private static final String BASE_URL = "https://companycar123.000webhostapp.com/api/";
    private PlantsInterface plantsInterface;
    private static PlantsClient INSTANCE;

    public PlantsClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        plantsInterface = retrofit.create(PlantsInterface.class);
    }

    public static PlantsClient getINSTANCE() {
        if (null == INSTANCE){
            INSTANCE = new PlantsClient();
        }
        return INSTANCE;
    }

    public Observable<List<Plant>> getPlants(){
        return plantsInterface.getPlants();
    }

    public Observable<List<Plant>> getFruits(){
        return plantsInterface.getFruits();
    }

    public Observable<List<Plant>> getVegetables(){
        return plantsInterface.getVegetables();
    }
    public Observable<List<PlantPrice>> getPlantPrice(){
        return plantsInterface.getPlantPrice();
    }

}
