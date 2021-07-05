package mo.zain.smartfarmer.ui.read;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import mo.zain.smartfarmer.R;


public class ChooseReadFragment extends Fragment {



    CardView cardViewPlants,cardViewFruits,cardViewVegetables;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_read, container, false);

        cardViewPlants = view.findViewById(R.id.choose_plants);
        cardViewFruits = view.findViewById(R.id.choose_furits);
        cardViewVegetables = view.findViewById(R.id.choose_vegtabiles);

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
        return view;
    }

}