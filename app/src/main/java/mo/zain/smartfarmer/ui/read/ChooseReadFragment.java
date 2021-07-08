package mo.zain.smartfarmer.ui.read;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.weather.WeatherModel;
import mo.zain.smartfarmer.viewmodel.WeatherViewModel;


public class ChooseReadFragment extends Fragment implements LocationListener {



    CardView cardViewPlants,cardViewFruits,cardViewVegetables;
    TextView city_weather,temp_weather,max_temp,low_temp,dateWeather;
    static String latitude="31.009028",longitude="30.573917";
    final static String appID ="6523ba3cbb4762f5e08a5dc5bf62ad00";
    LocationManager locationManager;
    String permissions[] = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
    final static int _requestCode = 1;
    WeatherViewModel weatherViewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_read, container, false);

        cardViewPlants = view.findViewById(R.id.choose_plants);
        cardViewFruits = view.findViewById(R.id.choose_furits);
        cardViewVegetables = view.findViewById(R.id.choose_vegtabiles);
        city_weather = view.findViewById(R.id.city_weather);
        temp_weather = view.findViewById(R.id.city_temp);
        max_temp = view.findViewById(R.id.high_temp);
        low_temp = view.findViewById(R.id.low_temp);
        dateWeather = view.findViewById(R.id.date_weather);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),permissions,_requestCode);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        //choose type of plants
        //-------------------------------------------------------------
        cardViewPlants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("CHOOSE","Plants");
                Navigation.findNavController(view).navigate(R.id.readPlantsFragment,bundle);
            }
        });

        cardViewFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("CHOOSE","Fruits");
                Navigation.findNavController(view).navigate(R.id.readPlantsFragment,bundle);
            }
        });

        cardViewVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("CHOOSE","Vegetables");
                Navigation.findNavController(view).navigate(R.id.readPlantsFragment,bundle);
            }
        });

        //-------------------------------------------------------------------


        //show data of weather
        //----------------------------------------------------------------

        dateWeather.setText(getData());

        weatherViewModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        weatherViewModel.getDetialsWeather(latitude,longitude,appID);
        weatherViewModel.weatherMutableLiveData.observe(getActivity(), new Observer<WeatherModel>() {
            @Override
            public void onChanged(WeatherModel weatherModel) {

                int num = 0176;
                char degree = (char) num;
                city_weather.setText(weatherModel.getName());
                temp_weather.setText(Math.round(weatherModel.getMain().getTemp()-272.15)+" °C");
                max_temp.setText(Math.round(weatherModel.getMain().getTempMax()-272.15)+" °C");
                low_temp.setText(Math.round(weatherModel.getMain().getTempMin()-272.15)+" °C");

            }
        });

        return view;
    }


    //override all methods of location listener
    //-------------------------------------------------------
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude()+"";
        longitude = location.getLongitude()+"";
        weatherViewModel.getDetialsWeather(latitude,longitude,appID);



    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case _requestCode:
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),permissions,_requestCode);
                }
                else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                }
                break;

        }
    }


    // dispay data
    //----------------------------------------------------------------
    private String getData(){
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}