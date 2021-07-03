package mo.zain.smartfarmer.controle;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private Activity mCtx;
    private List<Post> postList=new ArrayList<>();
    boolean flag=false;
    private String userId;
    private ProgressDialog pd;
    private DatabaseReference likesRef;
    private FirebaseFirestore db ;
    View v;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;
    EditText titleEt,descriptionEt;
    RoundedImageView imagePost;
    private static final int IMAGE_REQUEST = 1;
    Uri imageuri;
    public static String urlImage;
    private StorageReference fileRef;
    private StorageReference imageReference;
    public PostAdapter(Activity mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        pd=new ProgressDialog(mCtx);
        pd.setMessage("Deleting.........");
        likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        db = FirebaseFirestore.getInstance();
        imageReference = FirebaseStorage.getInstance().getReference();
        fileRef = null;
        //realtime --->Comment

    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_item, parent, false);
        return new PostViewHolder(v);

    }



    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.PostViewHolder holder, int position) {

        Post post = postList.get(position);
        String imag=postList.get(position).getImage();





        if (!post.getUserImage().equals(""))
            Glide.with(mCtx)
                    .load(post.getUserImage())
                    .into(holder.postProfile);
        else
            Glide.with(mCtx)
                    .load(R.drawable.ic_profile)
                    .into(holder.postProfile);

        Glide.with(mCtx)
                .load(imag)
                .into(holder.imagePost);
        holder.postName
                .setText(post.getName());

        holder.title
                    .setText(post.getTitle());
        holder.des
                .setText(post.getDescription());
        holder.loveCount
                .setText(post.getLoveCount()+" Love");

        DocumentReference noteRef =
                db.collection("Posts").document(post.getPostId());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mCtx, "Test", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("PostId", post.getPostId());
                Navigation.findNavController(v).navigate(R.id.commentFragment,bundle);
            }
        });

        setLikes(holder,post.getPostId());
        try {
            Glide.with(mCtx)
                    .load(imag)
                    .into(holder.imagePost);
        } catch (Exception ex) {

        }
        if (!imag.equals("noImage")) {
            //hide imageView
            holder.imagePost.setVisibility(View.VISIBLE);
        } else {
            //post
            holder.imagePost.setVisibility(View.GONE);
            try {
                Glide.with(mCtx)
                        .load(post.getImage())
                        .into(holder.imagePost);
            } catch (Exception ex) {

            }
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.edit,post.getUid()
                        ,userId,post.getPostId(),post.getImage()
                        ,post.getTitle(),post.getDescription()
                        ,post.getLoveCount(),post.getEmail(),post.getName()
                        ,post.getPhone(),post.getUserImage());
            }
        });


        holder.love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getUid().equals(userId)) {
                    int pLike=Integer.parseInt(post.getLoveCount());
                    flag =true;
                    likesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (flag)
                            {
                                if (snapshot.child(post.getPostId()).hasChild(userId))
                                {
                                    //
                                    noteRef.update("loveCount",(""+(pLike-1)));
                                    likesRef.child(post.getPostId()).child(userId).removeValue();
                                    flag=false;
                                }
                                else
                                {
                                    noteRef.update("loveCount",(""+(pLike+1)));
                                    likesRef.child(post.getPostId()).child(userId).setValue("Liked");
                                    flag=false;
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }

    private void setLikes(PostViewHolder holder, String postId) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(userId))
                {
                    holder.love.setTextColor(Color.RED);
                    holder.love.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.read_love_icon, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                }else {
                    holder.love.setTextColor(Color.BLACK);
                        holder.love.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.heart_love_icon, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    void showMoreOptions(ImageView edit,String userID,String myid
            ,String postId,String postImage,String title,String des,
                         String loveCount,String email, String name, String phone, String userImage)
    {
        PopupMenu popupMenu=new PopupMenu(mCtx,edit, Gravity.END);


        if (userID.equals(myid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE,2,0,"View Detail");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();


                    if (id==0){
                        delete(postId,postImage);
                    }
                    if (id==1)
                    {
                        EditAll(title,des,postImage,postId,loveCount,email,name,phone,userImage,userID);
                    }
                    if (id==2)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("PostId", postId);
                        Navigation.findNavController(v).navigate(R.id.commentFragment,bundle);
                    }



                return false;
            }
        });
        popupMenu.show();
    }

    private void EditAll(String title, String des, String postImage,String postId, String loveCount, String email, String name, String phone, String userImage,String userId) {

        openBottomSheet(title,des,postImage,postId,loveCount,email,name,phone,userImage,userId);
    }

    void delete(String postId,String image){
        if (image.equals("noImage")){
                deleteWithoutImage(postId);
        }else {
            deleteWithImage(postId,image);
        }
    }
    void deleteWithoutImage(String postId)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef =
                db.collection("Posts").
                        document(postId);
        noteRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            FancyToast.makeText(mCtx,"Deleted Successfuly", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                        else{
                            pd.dismiss();
                            FancyToast.makeText(mCtx,""+task.getException().getMessage(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                    }
                });
    }
    void deleteWithImage(String postId,String image)
    {

        StorageReference reference= FirebaseStorage.getInstance().getReferenceFromUrl(image);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference noteRef =
                                db.collection("Posts").
                                        document(postId);
                        noteRef.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    pd.dismiss();
                                    FancyToast.makeText(mCtx,"Deleted Successfuly", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                }
                                else{
                                    pd.dismiss();
                                    FancyToast.makeText(mCtx,""+task.getException().getMessage(), FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                pd.dismiss();
                FancyToast.makeText(mCtx,"Failed : "+e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        TextView title,des,loveCount,postName;
        ImageView imagePost,edit;
        Button love,comment,share;
        CircleImageView postProfile;
        public PostViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            des=itemView.findViewById(R.id.description);
            imagePost=itemView.findViewById(R.id.Image);
            love=itemView.findViewById(R.id.buttonlove);
            comment=itemView.findViewById(R.id.buttoncomment);
            share=itemView.findViewById(R.id.buttonShare);
            loveCount=itemView.findViewById(R.id.loveCount);
            edit=itemView.findViewById(R.id.editPost);
            postProfile=itemView.findViewById(R.id.postProfile);
            postName=itemView.findViewById(R.id.postName);

        }
    }
    private void openBottomSheet(String title, String des, String postImage,String PostId, String loveCount, String email, String name, String phone, String userImage,String userId)
    {
        //urlImage="";
        bottomSheetDialog=new BottomSheetDialog(
                mCtx,R.style.BottomSheetDialog
        );
        bottomSheetView=LayoutInflater.from(mCtx)
                .inflate(R.layout.layout_bottom_sheet,(LinearLayout)v
                        .findViewById(R.id.bottomSheetContainer));
        titleEt=bottomSheetView.findViewById(R.id.titlePost);
        descriptionEt=bottomSheetView.findViewById(R.id.desPost);
        imagePost=bottomSheetView.findViewById(R.id.imagePost);
        titleEt.setText(title);
        descriptionEt.setText(des);
        Glide.with(mCtx)
                .load(postImage)
                .into(imagePost);
        bottomSheetView.findViewById(R.id.publishBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(titleEt.getText().toString(),descriptionEt.getText().toString()
                        ,postImage,PostId,loveCount,email,name,phone,userImage,userId);

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
    private void uploadFile(String title, String des, String postImage,String postId, String loveCount, String email, String name, String phone, String userImage,String userId) {

        pd.setTitle("Wait for the data to be modified");
        pd.setMessage("waiting..");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        if (imageuri != null) {
            String randomKey = UUID.randomUUID().toString();
            fileRef = imageReference.child("posts/" + randomKey);
            fileRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final String name = taskSnapshot.getMetadata().getName();
                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage= uri.toString();
                                    uploadPost(urlImage,postId,postImage,title,des,loveCount,email,name,phone,userImage,userId);

                                }
                            });
                            pd.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            FancyToast.makeText(mCtx,""+exception.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //meal=new Meal(meal.getCat(),meal.getMealName(),meal.getPrice(),meal.getMealImage(),meal.getDescription(),meal.getCity());

            Post post=new Post();
            post.setTitle(title);
            post.setDescription(des);
            post.setImage(postImage);
            post.setLoveCount(loveCount);
            post.setEmail(email);
            post.setName(name);
            post.setPhone(phone);
            post.setUserImage(userImage);
            post.setUid(userId);

            DocumentReference noteRef =
                    db.collection("Posts").
                            document(postId);
            noteRef.set(post)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pd.dismiss();
                        //Toast.makeText(UpdateCategory.this, "Done Edit Name", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        pd.dismiss();
                        FancyToast.makeText(mCtx,""+task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();

                    }
                }
            });
        }
        bottomSheetDialog.dismiss();
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mCtx.startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                IMAGE_REQUEST);
    }

    private void uploadPost(String imgURL,String postId,String postImage,String title,String desc, String loveCount, String email, String name, String phone, String userImage,String userId)
    {

        FirebaseFirestore db =
                FirebaseFirestore.getInstance();

        DocumentReference noteRef =
                db.collection("Posts").
                        document(postId);

        noteRef.set(getPost(postId,imgURL,postImage,title,desc,loveCount,email,name,phone,userImage,userId)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(UpdateCategory.this, "Done", Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(mCtx,"Done! " , FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }
                else{

                }
            }
        });

    }
    private Post getPost(String pId,String imgURL,String postImage,String title,String desc, String loveCount, String email, String name, String phone, String userImage,String userId) {
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(postImage);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                //Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                //Log.d(TAG, "onFailure: did not delete file");
            }
        });
        Post post=new Post();
        post.setTitle(title);
        post.setDescription(desc);
        post.setImage(postImage);
        post.setPostId(pId);
        post.setLoveCount(loveCount);
        post.setEmail(email);
        post.setName(name);
        post.setPhone(phone);
        post.setUserImage(userImage);
        post.setUid(userId);
        return post;
    }
}
