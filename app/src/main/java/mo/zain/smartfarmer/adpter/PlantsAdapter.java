package mo.zain.smartfarmer.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Plant;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantViewHolder> {
    private List<Plant> plantsList = new ArrayList<>();
    private MySetOnClickListener listener;
    private Context context;

    public PlantsAdapter(MySetOnClickListener listener, Context context) {
        this.listener = listener;
        this.context  = context;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
         holder.namePalnt.setText(plantsList.get(position).getPlantName());
         Glide.with(context).load(plantsList.get(position).getPlantImg()).into(holder.imgPlant);
    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    public void setList(List<Plant> plantsList) {
        this.plantsList = plantsList;
        notifyDataSetChanged();
    }

    public class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView namePalnt;
        ImageView imgPlant;
        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            namePalnt = itemView.findViewById(R.id.name_plant);
            imgPlant = itemView.findViewById(R.id.img_plant);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.getPostion(getAdapterPosition(),itemView);
                }
            });
        }
    }



    public interface MySetOnClickListener{
        void getPostion(int postion,View view);
    }
}