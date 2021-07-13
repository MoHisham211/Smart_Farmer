package mo.zain.smartfarmer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.adpter.CompanyListAdapter;
import mo.zain.smartfarmer.model.CompanyModel;


public class CompanyFragment extends Fragment {

    List<CompanyModel> companyModels;
    CompanyListAdapter adapter;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String name,email,phone,imageProfile;
    ShapeableImageView profileImage;
    TextView profileName;
    DatabaseReference df;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_company, container, false);
        firebaseAuth=FirebaseAuth.getInstance();
        profileImage=view.findViewById(R.id.profileImage);
        profileName=view.findViewById(R.id.profileName);
        recyclerView=view.findViewById(R.id.chat_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        companyModels =new ArrayList<>();
        getAllCompanys();
        loadInfo();
        return view;
    }
    private void loadInfo()
    {
        df= FirebaseDatabase.getInstance()
                .getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Query query=df;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map) snapshot.getValue();

                profileName.setText(map.get("UserName"));
                try {
                    if (!map.get("imageURL").equals(""))
                        Glide.with(getContext()).load(map.get("imageURL")).into(profileImage);
                    else
                        Glide.with(getContext()).load(R.drawable.ic_profile).into(profileImage);
                }catch (Exception e)
                {

                }

                email=map.get("Email");
                name=map.get("UserName");
                phone=map.get("Mobile");
                imageProfile=map.get("imageURL");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(getContext(),error.getMessage(),FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            }
        });

    }

    private void getAllCompanys() {
        final FirebaseUser firebaseUser=
                FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance()
                .getReference("Company");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyModels.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    CompanyModel companyModel =ds.getValue(CompanyModel.class);
                    assert companyModel !=null;
                    assert firebaseUser !=null;
                   // Toast.makeText(getContext(), ""+companyModel.getId(), Toast.LENGTH_SHORT).show();
                    if(!companyModel.getId().equals(firebaseUser.getUid()))
                    {
                        companyModels.add(companyModel);

                    }

                }
                adapter =new CompanyListAdapter(getContext(), companyModels);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}