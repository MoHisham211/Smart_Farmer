package mo.zain.smartfarmer.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import mo.zain.smartfarmer.Data.PlantsClient;
import mo.zain.smartfarmer.model.Plant;
import mo.zain.smartfarmer.model.PlantPrice;

public class PriceViewModel extends ViewModel {
    public MutableLiveData<List<PlantPrice>> plantsPriceMutableLiveData = new MutableLiveData<>();

    public void getPlantsPrice() {
        Observable<List<PlantPrice>> observable = PlantsClient.getINSTANCE().getPlantPrice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<List<PlantPrice>> observer = new Observer<List<PlantPrice>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i("Hussein","first");
            }

            @Override
            public void onNext(@NonNull List<PlantPrice> plantPrices) {
                Log.i("Hussein","next");
                plantsPriceMutableLiveData.postValue(plantPrices);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i("Hussein",e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Hussein","complete");
            }
        };
        observable.subscribe(observer);
    }
}
