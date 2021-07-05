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

public class VegetablesViewModel extends ViewModel {
    public MutableLiveData<List<Plant>> vegetablesMutableLiveData = new MutableLiveData<>();
    MutableLiveData<String> Vegetables = new MutableLiveData<>();

    public void getVegetables() {
        Observable<List<Plant>> observable = PlantsClient.getINSTANCE().getVegetables()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<List<Plant>> observer = new Observer<List<Plant>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i("Hussein","first");
            }

            @Override
            public void onNext(@NonNull List<Plant> Vegetables) {
                Log.i("Hussein","next");
                vegetablesMutableLiveData.postValue(Vegetables);
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
