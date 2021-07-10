package mo.zain.smartfarmer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.adpter.PlantPriceAdapter;
import mo.zain.smartfarmer.model.Plant;
import mo.zain.smartfarmer.model.PlantPrice;
import mo.zain.smartfarmer.viewmodel.PriceViewModel;


public class PriceFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        PlantPriceAdapter plantPriceAdapter = new PlantPriceAdapter(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycle_price);
        recyclerView.setAdapter(plantPriceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        PriceViewModel priceViewModel = new ViewModelProvider(getActivity()).get(PriceViewModel.class);
        priceViewModel.getPlantsPrice();

        priceViewModel.plantsPriceMutableLiveData.observe(getActivity(), new Observer<List<PlantPrice>>() {
            @Override
            public void onChanged(List<PlantPrice> plantPrices) {

                plantPriceAdapter.setList(plantPrices);
            }
        });



        return view;
    }
}