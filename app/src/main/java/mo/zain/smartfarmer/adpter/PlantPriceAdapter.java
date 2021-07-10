package mo.zain.smartfarmer.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.PlantPrice;

public class PlantPriceAdapter extends RecyclerView.Adapter<PlantPriceAdapter.PlantPriceViewHolder> {

    List<PlantPrice> plantsPriceList = new ArrayList<>();

    private Context context;

    public PlantPriceAdapter(Context context) {
        this.context  = context;
    }

    @NonNull
    @Override
    public PlantPriceAdapter.PlantPriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlantPriceAdapter.PlantPriceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_price, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlantPriceAdapter.PlantPriceViewHolder holder, int position) {

        holder.plant_name_price.setText(plantsPriceList.get(position).getNamePlant());
        holder.price_plant.setText("Price: "+plantsPriceList.get(position).getPricePlant()+"$");
        Glide.with(context).load(plantsPriceList.get(position).getImgPlant()).into(holder.img_panlt_price);
    }

    @Override
    public int getItemCount() {
        return plantsPriceList.size();
    }

    public void setList(List<PlantPrice> plantsPriceList) {
        this.plantsPriceList = plantsPriceList;
        notifyDataSetChanged();
    }

    public class PlantPriceViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView img_panlt_price;
        TextView plant_name_price;
        Button price_plant;
        public PlantPriceViewHolder(@NonNull View itemView) {
            super(itemView);

            img_panlt_price = itemView.findViewById(R.id.img_plant_price);
            plant_name_price = itemView.findViewById(R.id.name_plant_price);
            price_plant = itemView.findViewById(R.id.price_palnt);

        }
    }



}
