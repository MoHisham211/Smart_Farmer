package mo.zain.smartfarmer.adpter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.CompanyModel;
import mo.zain.smartfarmer.model.UserModel;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    Context context;
    List<UserModel> userModels =new ArrayList<>();

    public UserListAdapter(Context context, List<UserModel> userModels) {
        this.context = context;
        this.userModels = userModels;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.chat_items,parent,false);

        return new UserListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserListAdapter.MyViewHolder holder, int position) {

        UserModel userModel = userModels.get(position);
        holder.compamyName.setText(userModel.getUserName());
        if (!userModel.getImageURL().equals(""))
            Glide.with(context)
                    .load(userModel.getImageURL())
                    .into(holder.companyImage);
        else
            Glide.with(context)
                    .load(R.drawable.ic_profile)
                    .into(holder.companyImage);

        String CompanyId= userModel.getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("MyId",CompanyId);
                Navigation.findNavController(v).navigate(R.id.chatToUserFragment,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView companyImage;
        TextView compamyName;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            companyImage=itemView.findViewById(R.id.companyImage);
            compamyName=itemView.findViewById(R.id.CompanyName);

        }
    }
}
