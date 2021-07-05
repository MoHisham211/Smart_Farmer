package mo.zain.smartfarmer.ui.read;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Plant;


public class DetialsPlantFragment extends Fragment {

    TextView namePlant,descPlant;
    ImageView imgPlant;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detials_plant, container, false);

        Plant plant = (Plant)getArguments().get("EXTRA_DATA_PLANT");
        namePlant = view.findViewById(R.id.name_plant_detials);
        descPlant = view.findViewById(R.id.desc_plant_details);
        imgPlant = view.findViewById(R.id.img_plant_detials);
        namePlant.setText(plant.getPlantName());
        descPlant.setText(plant.getPlantDesc());
        Glide.with(getContext()).load(plant.getPlantImg()).into(imgPlant);


        return view;
    }
}