package mo.zain.smartfarmer.ui;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.controle.CommentAdapter;
import mo.zain.smartfarmer.controle.PostAdapter;
import mo.zain.smartfarmer.model.Comment;
import mo.zain.smartfarmer.model.Post;

public class CommentFragment extends Fragment {

    String myUid,myEmail,myName,myDp
            ,postID,pLike,hisDp,hisName,pcommentCount;
    private ImageView postImage;
    CircleImageView userPost;
    TextView namePost,title,description,plikes,time;
    Button love,share;
    //
    EditText comment;
    ImageView send;
    FirebaseFirestore db ;
    ProgressDialog progressDialog;
    private DatabaseReference CommentRef;
    List<Comment>commentList=new ArrayList<>();
    private DatabaseReference likesRef;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    CommentAdapter commentAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_comment, container, false);
        likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postID=getArguments().getString("PostId");
        postImage=view.findViewById(R.id.Image);
        userPost=view.findViewById(R.id.postProfile);
        namePost=view.findViewById(R.id.postName);
        title=view.findViewById(R.id.title);
        description=view.findViewById(R.id.description);
        plikes=view.findViewById(R.id.loveCount);
        love=view.findViewById(R.id.buttonlove);
        share=view.findViewById(R.id.buttonShare);
        comment=view.findViewById(R.id.editTextTextPersonName);
        send=view.findViewById(R.id.circleImageView);
        time=view.findViewById(R.id.timeTxt);
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        loadPostInfo(postID);
        checkUserStatus();
        loadUserInfo();
        loadComments(postID);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        setLikes(postID);
        return view;
    }

    private void loadComments(String postID) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference applicationsRef = rootRef.collection("Posts");
        DocumentReference applicationIdRef = applicationsRef.document(postID);
        applicationIdRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> commentList1 = (List<Map<String, Object>>) document.get("comments");
                    commentList.clear();
                    for (int i=0;i<commentList1.size();i++)
                    {
                        Comment comment=new Comment();
                        for (Map.Entry mapElement : commentList1.get(i).entrySet()) {
                            String key = (String)mapElement.getKey();
                            String value = ((String) mapElement.getValue());
                            if (key.equals("commenttxt"))
                            {
                                comment.setCommenttxt(value);
                            }
                            else if(key.equals("userUid"))
                            {
                                comment.setUserUid(value);
                            }
                            else if(key.equals("userEmail"))
                            {
                                comment.setUserEmail(value);
                            }
                            else if(key.equals("userName"))
                            {
                                comment.setUserName(value);
                            }
                            else if(key.equals("myDp"))
                            {
                                comment.setMyDp(value);
                            }else if (key.equals("time"))
                            {
                                comment.setTime(value);
                            }

                        }
                        commentList.add(comment);
                    }
                   commentAdapter = new CommentAdapter(getActivity(), commentList);
                    commentAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(commentAdapter);

                }
            }
        });

    }




    private void postComment()
    {
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Adding Comment..");
        progressDialog.show();

        String commentStr=comment.getText().toString();
        if (TextUtils.isEmpty(commentStr))
        {
            comment.setError("No Comment Entered");
            progressDialog.dismiss();
            return;
        }
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy hh:mm a");
        String currentTime=formatter.format(date);
        Comment comment1=new Comment(myUid,myEmail,myName,myDp,commentStr,currentTime);
        FirebaseFirestore rootReff=FirebaseFirestore.getInstance();
        CollectionReference app=rootReff.collection("Posts");
        DocumentReference documentReference=app.document(postID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            documentReference.update( "comments", FieldValue.arrayUnion(comment1));
                            comment.setText("");
                            updateCommentCount();
                            loadComments(postID);
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                FancyToast.makeText(getContext(),""+e.getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                progressDialog.dismiss();
            }
        });

    }


    private void setLikes(String postId) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(myUid))
                {
                    love.setTextColor(Color.RED);
                    love.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.read_love_icon, //left
                            0, //top
                            0, //right
                            0 //bottom
                    );
                }else {
                    love.setTextColor(Color.BLACK);
                    love.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.love_like_heart_icon, //left
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


    private void updateCommentCount() {
       // loadComments(postID);
        loadPostInfo(postID);
        DocumentReference noteRef =
                db.collection("Posts").document(postID);
            noteRef.update("commentCount",(""+(Integer.parseInt(pcommentCount)+1)));
        FancyToast.makeText(getContext(),"You Comment Successfuly !",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
    }
    private void loadUserInfo()
    {
        Query query= FirebaseDatabase.getInstance().getReference("User");
        query.orderByChild("id").equalTo(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    myName=""+ds.child("UserName").getValue();
                    myDp=""+ds.child("imageURL").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void loadPostInfo(String Id)
    {
        DocumentReference docRef = db.collection("Posts").document(Id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Post post=document.toObject(Post.class);
                        title.setText(post.getTitle());
                        description.setText(post.getDescription());
                        pcommentCount=post.getCommentCount();

                        namePost.setText(post.getName());
                        time.setText(post.getTime());
                        if (!post.getUserImage().equals(""))
                        Glide.with(getContext())
                                .load(post.getUserImage())
                                .into(userPost);
                        else
                            Glide.with(getContext())
                            .load(R.drawable.ic_profile)
                            .into(userPost);
                        plikes.setText(post.getLoveCount()+" Love");

                        try {
                            Glide.with(getContext())
                                    .load(post.getImage())
                                    .into(postImage);
                        } catch (Exception ex) { }
                        if (!post.getImage().equals("noImage")) {
                            postImage.setVisibility(View.VISIBLE);
                        } else {
                            postImage.setVisibility(View.GONE);
                            try {
                                Glide.with(getContext())
                                        .load(post.getImage())
                                        .into(postImage);
                            } catch (Exception ex) { }
                        }

                    } else {

                    }
                } else {
                    FancyToast.makeText(getContext(),task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();

                }
            }
        });

    }
    private void checkUserStatus()
    {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            myEmail=user.getEmail();
            myUid=user.getUid();
        }else {
            getActivity().finish();
        }
    }
}