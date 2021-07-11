package mo.zain.smartfarmer.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import mo.zain.smartfarmer.Data.PlantsClient;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Plant;


public class PlantsViewModel extends ViewModel {
    public MutableLiveData<List<Plant>> plantsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<String> Plants = new MutableLiveData<>();


    public void getPlants() {
        Observable<List<Plant>> observable = PlantsClient.getINSTANCE().getPlants()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observer<List<Plant>> observer = new Observer<List<Plant>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i("Hussein","first");
            }

            @Override
            public void onNext(@NonNull List<Plant> Plants) {

                //view.findViewById(R.id.shimmer).setVisibility(View.GONE);
                Log.i("Hussein","next");
                plantsMutableLiveData.postValue(Plants);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i("Hussein",e.getMessage());
            }

            @Override
            public void onComplete() {
                //shimmerFrameLayout.setVisibility(View.INVISIBLE);
                Log.i("Hussein","complete");
            }
        };
        observable.subscribe(observer);
    }
}
