package mo.zain.smartfarmer.controle;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private Context mCtx;
    private List<Post> postList=new ArrayList<>();
    boolean flag=false;
    String userId;
    ProgressDialog pd;
    public PostAdapter(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
        userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        pd=new ProgressDialog(mCtx);
        pd.setMessage("Deleting.........");
    }

    @NonNull
    @NotNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_item, parent, false);
        return new PostViewHolder(v);

    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull PostAdapter.PostViewHolder holder, int position) {

        Post post = postList.get(position);
        String imag=postList.get(position).getImage();

        holder.title
                    .setText(post.getTitle());
        holder.des
                .setText(post.getDescription());
        holder.loveCount
                .setText("Love"+post.getLoveCount());

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

        if (post.getUid().equals(userId))
        {
            holder.edit
                    .setVisibility(View.VISIBLE);
        }else
        {
            holder.edit
                    .setVisibility(View.GONE);
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.edit,post.getUid(),userId,post.getPostId(),post.getImage(),post.getTitle(),post.getDescription());
            }
        });
        holder.love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getUid().equals(userId)) {
                    if (flag == false) {
                        flag = true;
                        holder.love.setTextColor(Color.RED);
                        holder.love.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.read_love_icon, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                    } else {
                        flag = false;
                        holder.love.setTextColor(Color.BLACK);
                        holder.love.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.heart_love_icon, //left
                                0, //top
                                0, //right
                                0 //bottom
                        );
                    }

                }
            }
        });

    }
    void showMoreOptions(ImageView edit,String userID,String myid,String postId,String postImage,String title,String des)
    {
        PopupMenu popupMenu=new PopupMenu(mCtx,edit, Gravity.END);

        popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
        popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if (id==0){
                    delete(postId,postImage);
                }
                if (id==1)
                {
                    EditAll(title,des,postImage);
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void EditAll(String title, String des, String postImage) {

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

        TextView title,des,loveCount;
        ImageView imagePost,edit;
        Button love,comment,share;
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
        }
    }
}
