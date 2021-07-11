package mo.zain.smartfarmer.ui.read;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Plant;


public class DetialsPlantFragment extends Fragment {

    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView descPlant,catogryPlant,diseases,youtube,source;
    ImageView imgPlant;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detials_plant, container, false);


        Plant plant = (Plant)getArguments().get("EXTRA_DATA_PLANT");


        collapsingToolbarLayout = view.findViewById(R.id.name_plant_detials);
        imgPlant = view.findViewById(R.id.img_plant_detials);
        catogryPlant = view.findViewById(R.id.category_plant_detials);
        descPlant = view.findViewById(R.id.desc_plant_detials);
        diseases=view.findViewById(R.id.desc_plant_diseases);
        youtube=view.findViewById(R.id.youtube);
        source=view.findViewById(R.id.source);


        collapsingToolbarLayout.setTitle(plant.getPlantName());
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        Glide.with(getContext()).load(plant.getPlantImg()).into(imgPlant);
        catogryPlant.setText(plant.getPlantCategory());
        diseases.setText(plant.getPlantDiseases());
        descPlant.setText(plant.getPlantDesc());
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tostart = new Intent(Intent.ACTION_VIEW);
                tostart.setData(Uri.parse(plant.getPlantVideo()));
                startActivity(tostart);
            }
        });
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(plant.getPlantSource()));
                startActivity(i);
            }
        });



        return view;
    }
}