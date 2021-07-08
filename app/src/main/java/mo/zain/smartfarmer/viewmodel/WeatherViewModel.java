package mo.zain.smartfarmer.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import mo.zain.smartfarmer.Data.WeatherClient;
import mo.zain.smartfarmer.model.weather.WeatherModel;

public class WeatherViewModel extends ViewModel {
    public MutableLiveData<WeatherModel> weatherMutableLiveData = new MutableLiveData<>();

    public void getDetialsWeather(String lat, String lon, String appID) {
        Observable<WeatherModel> observable = WeatherClient.getINSTANCE().getDetialsWeather(lat,lon,appID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<WeatherModel> observer = new Observer<WeatherModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i("Hussein","first");
            }

            @Override
            public void onNext(@NonNull WeatherModel weatherModel) {
                Log.i("Hussein","next");
                weatherMutableLiveData.postValue(weatherModel);
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
