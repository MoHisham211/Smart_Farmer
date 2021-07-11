package mo.zain.smartfarmer.ui.read;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.adpter.PlantsAdapter;
import mo.zain.smartfarmer.model.Plant;
import mo.zain.smartfarmer.viewmodel.FruitsViewModel;
import mo.zain.smartfarmer.viewmodel.PlantsViewModel;
import mo.zain.smartfarmer.viewmodel.VegetablesViewModel;


public class ReadPlantsFragment extends Fragment implements PlantsAdapter.MySetOnClickListener {


    public static String keyPlant = "EXTRA_DATA_PLANT";
    List<Plant> plantList = new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout linerShimmer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read_plants, container, false);

        String valueChoose = getArguments().getString("CHOOSE").toString();
        shimmerFrameLayout=view.findViewById(R.id.shimmer);//null null
//        shimmerFrameLayout.setShimmer(shimmerFrameLayout);
        linerShimmer=view.findViewById(R.id.linerShimmer);

        // setup to recycleview to show data about plants
        PlantsAdapter plantsAdapter = new PlantsAdapter(this,getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setAdapter(plantsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));


        // get suitable data
        //------------------------------------------------------------------
        if(valueChoose.equals("Plants")) {
            PlantsViewModel plantsViewModel = new ViewModelProvider(getActivity()).get(PlantsViewModel.class);
            plantsViewModel.getPlants();
            plantsViewModel.plantsMutableLiveData.observe(getActivity(), new Observer<List<Plant>>() {
                @Override
                public void onChanged(List<Plant> plants) {

                    plantList = plants;
                    plantsAdapter.setList(plants);
                    //shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    linerShimmer.setVisibility(View.GONE);

                }
            });
        }
        else if(valueChoose.equals("Fruits")) {
            FruitsViewModel fruitsViewModel = new ViewModelProvider(getActivity()).get(FruitsViewModel.class);
            fruitsViewModel.getFruits();

            fruitsViewModel.fruitsMutableLiveData.observe(getActivity(), new Observer<List<Plant>>() {
                @Override
                public void onChanged(List<Plant> fruits) {

                    plantList = fruits;
                    plantsAdapter.setList(plantList);
                   // shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    linerShimmer.setVisibility(View.GONE);

                }
            });

        }else if(valueChoose.equals("Vegetables")) {
            VegetablesViewModel vegetablesViewModel = new ViewModelProvider(getActivity()).get(VegetablesViewModel.class);
            vegetablesViewModel.getVegetables();

            vegetablesViewModel.vegetablesMutableLiveData.observe(getActivity(), new Observer<List<Plant>>() {
                @Override
                public void onChanged(List<Plant> vegetables) {

                    plantList = vegetables;
                    plantsAdapter.setList(plantList);
                    //shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    linerShimmer.setVisibility(View.GONE);

                }
            });

        }

        //-------------------------------------------------------------------------------



        return view;
    }


    @Override
    public void getPostion(int postion,View view) {
        Plant plant = plantList.get(postion);
        Bundle bundle = new Bundle();
        bundle.putSerializable(keyPlant,plant);
        Navigation.findNavController(view).navigate(R.id.detialsPlantFragment,bundle);

    }




}