package mo.zain.smartfarmer.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.controle.PostAdapter;
import mo.zain.smartfarmer.model.Comment;
import mo.zain.smartfarmer.model.Post;


public class HomeFragment extends Fragment {

    CircleImageView profileImage,addPost;
    TextView profileName;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference df;
    private static final int IMAGE_REQUEST = 1;
    Uri imageuri;
    ProgressDialog progressDialog;
    public static String urlImage;
    View bottomSheetView;
    EditText title,description;
    RoundedImageView imagePost;
    private StorageReference imageReference;
    private StorageReference fileRef;
    FirebaseFirestore db ;
    BottomSheetDialog bottomSheetDialog;
    List<Post> lists=new ArrayList<>();
    PostAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    String name,email,phone,imageProfile;
    LinearLayout toProfile;
    List<Comment> comments=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        profileImage=view.findViewById(R.id.profileImage);
        profileName=view.findViewById(R.id.profileName);
        addPost=view.findViewById(R.id.addPost);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        imageReference = FirebaseStorage.getInstance().getReference();
        fileRef = null;
        db = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Publish Post");
        progressDialog.setMessage("Please be patient");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        adapter = new PostAdapter(getActivity(), lists);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        toProfile=view.findViewById(R.id.toProfile);
        toProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.profileFragment);
            }
        });
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
        loadInfo();
        loadPosts();
        return view;
    }

    private void openBottomSheet()
    {
        urlImage="";
        bottomSheetDialog=new BottomSheetDialog(
                getContext(),R.style.BottomSheetDialog
        );
        bottomSheetView=LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet,(LinearLayout)
                        getView().findViewById(R.id.bottomSheetContainer));
        title=bottomSheetView.findViewById(R.id.titlePost);
        description=bottomSheetView.findViewById(R.id.desPost);
        imagePost=bottomSheetView.findViewById(R.id.imagePost);
        bottomSheetView.findViewById(R.id.publishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        bottomSheetView.findViewById(R.id.imagePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void loadInfo()
    {
        df= FirebaseDatabase.getInstance()
                .getReference("User")
                .child(firebaseUser.getUid());
        Query query=df;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map) snapshot.getValue();

                profileName.setText(map.get("UserName"));
                if (!map.get("imageURL").equals(""))
                Glide.with(getContext()).load(map.get("imageURL")).into(profileImage);
                else
                Glide.with(getContext()).load(R.drawable.ic_profile).into(profileImage);
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
            imagePost.setImageURI(imageuri);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void uploadFile() {

        progressDialog.show();
        if (imageuri != null&&!title.getText().toString().isEmpty()) {
            String randomKey = UUID.randomUUID().toString();
            fileRef = imageReference.child("posts/" + randomKey);
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
                                    uploadPost(urlImage);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(),""+exception.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    });
        } else if (!title.getText().toString().isEmpty()){
            Post post=
                    new Post(title.getText().toString()
                            ,description.getText().toString()
                            ,"noImage",firebaseUser.getUid(),"0",email,name,phone,imageProfile,"0",comments);
            db.collection("Posts").
                    add(post)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            loadPosts();
                            progressDialog.dismiss();
                            title.setText("");
                            description.setText("");
                            urlImage="";
                            FancyToast.makeText(getContext(),"Success",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                            bottomSheetDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(),"Failed",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    });
        }else
        {
            title.setError("You Must Enter Title");
            progressDialog.dismiss();
        }
    }
    private void uploadPost(String imgURL)
    {
        db.collection("Posts").
                add(getPost(imgURL))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        loadPosts();
                        progressDialog.dismiss();
                        title.setText("");
                        description.setText("");
                        urlImage="";
                        FancyToast.makeText(getContext(),"Success",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                        bottomSheetDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        FancyToast.makeText(getContext(),"Failed",FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    }
                });
    }
    private Post getPost(String imgURL) {
        String titleT=title.getText().toString();
        String desT=description.getText().toString();
        String postPhoto=imgURL;
        return new Post(titleT,desT,postPhoto,firebaseUser.getUid(),"0",email,name,phone,imageProfile,"0",comments);
    }
    private void loadPosts() {
        db.collection("Posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        lists.clear();
                        if (value != null && !value.isEmpty()) {
                            for (QueryDocumentSnapshot doc : value) {
                                Post post=doc.toObject(Post.class);
                                post.setPostId(doc.getId());
                                DocumentReference noteRef =
                                        db.collection("Posts").document(post.getPostId());
                                noteRef.update("postId",doc.getId());
                                lists.add(post);
                            }
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}