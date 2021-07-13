package mo.zain.smartfarmer.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.authentication.IntroActivity;


public class ProfileFragment extends Fragment {

    CircleImageView circleImageView;
    ImageView right,close,back,logout;
    TextView edit;
    DatabaseReference df;
    Button editBtn;
    EditText userNameEt,emailEt,mobileEt;
    String email,name,phone,imageProfile;
    FloatingActionButton imageEdit;
    private static final int IMAGE_REQUEST = 1;
    Uri imageuri;
    private StorageReference imageReference;
    private StorageReference fileRef;
    public static String urlImage;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        imageReference = FirebaseStorage.getInstance().getReference();
        fileRef = null;
//        findNavController(fragment).navigate(
//                SignInFragmentDirections.actionSignInFragmentToUserNameFragment());

        circleImageView=view.findViewById(R.id.ic_profile);
        right=view.findViewById(R.id.right);
        close=view.findViewById(R.id.descard);
        logout=view.findViewById(R.id.logout);
        edit=view.findViewById(R.id.edit);
        editBtn=view.findViewById(R.id.editBtn);
        userNameEt=view.findViewById(R.id.userNameEt);
        emailEt=view.findViewById(R.id.emailEt);
        mobileEt=view.findViewById(R.id.mobileEt);
        imageEdit=view.findViewById(R.id.imageEdit);
        back=view.findViewById(R.id.back);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogFun();
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                right.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                logout.setVisibility(View.GONE);
                edit.setText("Edit Profile");
            }
        });

        Fragment fragment = new ProfileFragment();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_homeFragment);
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                updateProfile();
                logout.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                edit.setText("My Profile");
                userNameEt.setFreezesText(true);
                mobileEt.setFreezesText(true);
                emailEt.setFreezesText(true);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout.setVisibility(View.VISIBLE);
                right.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
                edit.setText("My Profile");
            }
        });
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

                userNameEt.setText(map.get("UserName"));
                emailEt.setText(map.get("Email"));
                mobileEt.setText(map.get("Mobile"));
                try {
                    if (!map.get("imageURL").equals(""))
                        Glide.with(getContext()).
                                load(map.get("imageURL")).into(circleImageView);
                    else
                        Glide.with(getContext()).load(R.drawable.ic_profile).into(circleImageView);
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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && data != null)
        {
            imageuri = data.getData();
            circleImageView.setImageURI(imageuri);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void LogFun()
    {
        FirebaseAuth.getInstance().signOut();
        getActivity().startActivity(new Intent(getContext(), IntroActivity.class));
        getActivity().finish();
    }

    private void updateProfile()
    {
        if (imageuri != null&& !userNameEt.getText().toString().isEmpty()&&
                !mobileEt.getText().toString().isEmpty()&&
                !emailEt.getText().toString().isEmpty()) {
            String randomKey = UUID.randomUUID().toString();
            fileRef = imageReference.child("profile/" + randomKey);
            fileRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final String name = taskSnapshot.getMetadata().getName();
                            Task<Uri> result = taskSnapshot.getMetadata().getReference()
                                    .getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage= uri.toString();
                                    uploadNewProfile(urlImage);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //progressDialog.dismiss();
                            FancyToast.makeText(getContext(),""+exception.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    });
        }
        else{
            if (userNameEt.getText().toString().isEmpty())
            {
                userNameEt.setError("You Must Enter Your Name");
            }else if (mobileEt.getText().toString().isEmpty())
            {
                mobileEt.setError("You Must Enter Your Phoone");
            }else if (emailEt.getText().toString().isEmpty())
            {
                emailEt.setError("You Must Enter Your Email");
            }else {
                databaseReference=FirebaseDatabase.getInstance()
                        .getReference("User")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                HashMap<String,String> map=new HashMap<>();
                map.put("imageURL",imageProfile);
                map.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("UserName",userNameEt.getText().toString());
                map.put("Email",emailEt.getText().toString());
                map.put("Mobile",mobileEt.getText().toString());
                databaseReference.setValue(map);
                progressBar.setVisibility(View.GONE);
                FancyToast.makeText(getContext(),"You Update Your information Successfully",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();

            }


    }
    }

    private void uploadNewProfile(String urlImage) {
        databaseReference=FirebaseDatabase.getInstance()
                .getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,String> map=new HashMap<>();
        map.put("imageURL",urlImage);
        map.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("UserName",userNameEt.getText().toString());
        map.put("Email",emailEt.getText().toString());
        map.put("Mobile",mobileEt.getText().toString());
        databaseReference.setValue(map);
        progressBar.setVisibility(View.GONE);
        //pd.dismiss();
    }
    public static void popBackStack(FragmentManager manager){
        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}