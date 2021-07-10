package mo.zain.smartfarmer.ui.read;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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


public class ChooseReadFragment extends Fragment {



    CardView cardViewPlants,cardViewFruits,cardViewVegetables;
    TextView city_weather,temp_weather,max_temp,low_temp,dateWeather;
    final static String appID ="6523ba3cbb4762f5e08a5dc5bf62ad00";
    WeatherViewModel weatherViewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_read, container, false);

        cardViewPlants = view.findViewById(R.id.choose_plants);
        cardViewFruits = view.findViewById(R.id.choose_furits);
        cardViewVegetables = view.findViewById(R.id.choose_vegtabiles);
        city_weather = view.findViewById(R.id.city_weather);
        temp_weather = view.findViewById(R.id.city_temp);
        max_temp = view.findViewById(R.id.high_temp);
        low_temp = view.findViewById(R.id.low_temp);
        dateWeather = view.findViewById(R.id.date_weather);



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

        SharedPreferences prefs = getActivity().getSharedPreferences("CURRENT_LOCATION", Context.MODE_PRIVATE);
        String latitude = prefs.getString("latitude", "31.0106341");//"No name defined" is the default value.
        String longitude = prefs.getString("longitude", "30.550334"); //0 is the default value.


        dateWeather.setText(getDate());

        weatherViewModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);
        weatherViewModel.getDetialsWeather(latitude,longitude,appID);

        weatherViewModel.weatherMutableLiveData.observe(getActivity(), new Observer<WeatherModel>() {
            @Override
            public void onChanged(WeatherModel weatherModel) {
                city_weather.setText(weatherModel.getName());
                temp_weather.setText(Math.round(weatherModel.getMain().getTemp()-272.15)+" ℃");
                max_temp.setText(Math.round(weatherModel.getMain().getTempMax()-272.15)+" ℃");
                low_temp.setText(Math.round(weatherModel.getMain().getTempMin()-272.15)+" ℃");

            }
        });

        return view;
    }



    // dispay data
    //----------------------------------------------------------------
    private String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}