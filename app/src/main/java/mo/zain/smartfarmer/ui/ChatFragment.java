package mo.zain.smartfarmer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import mo.zain.smartfarmer.R;
import mo.zain.smartfarmer.adpter.ChatAdapter;
import mo.zain.smartfarmer.model.ChatModel;
import mo.zain.smartfarmer.model.Plant;


public class ChatFragment extends Fragment {

    ShapeableImageView profile;
    TextView Name,status;
    DatabaseReference df;
    String id,hisImage;
    EditText editTextTextPersonName;
    RecyclerView recyclerView;
    List<ChatModel> chatModels=new ArrayList<>();
    ChatAdapter adapterChat;
    ImageView back,circleImageView;
    private static final String AUTH_KEY = "key=AAAAlhHeGaw:APA91bEPgudJTgV0dPB8bEZmVcXdvhbEtH5d0KBlY0pDsJ1rm2Ue-nl1SkgnWgKeFt0SIzLY6zLpGXvXsXx2lpsQ5QAlg6kCUsOqhgxYDF-OXgSfRevcXUFsiY44gJiuasJZnh4n--3s";
    String token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        id = getArguments().get("MyId").toString();
        recyclerView=view.findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        status=view.findViewById(R.id.status);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile=view.findViewById(R.id.profileImage);
        Name=view.findViewById(R.id.profileName);
        editTextTextPersonName=view.findViewById(R.id.editTextTextPersonName);
        back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_chatFragment_to_companyFragment);

            }
        });
        circleImageView=view.findViewById(R.id.circleImageView);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextTextPersonName.getText().equals(""))
                {
                    sendMessage(editTextTextPersonName.getText().toString());
                    editTextTextPersonName.setText("");
                }else
                {
                    FancyToast.makeText(getContext(),"Enter Your Messsage",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }

            }
        });
        loadInfo();
        readMessages();
        return view;
    }
    private void sendMessage(final String message) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        hashMap.put("receiver",id);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        hashMap.put("receiverToken",token);

        databaseReference.child("Chats").push()
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushNotification("tokens",message,token);
                    }
                }).start();
            }
        });

        String msg=message;

    }

    private void loadInfo()
    {
        df= FirebaseDatabase.getInstance()
                .getReference("Company")
                .child(id);
        Query query=df;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> map = (Map) snapshot.getValue();

                Name.setText(map.get("companyname"));
                if (!map.get("imageURL").equals(""))
                    try {
                        Glide.with(getContext()).load(map.get("imageURL")).into(profile);

                    }catch (Exception e)
                    {

                    }
                else
                    try {
                        Glide.with(getContext()).load(R.drawable.ic_profile).into(profile);

                    }catch (Exception e)
                    {

                    }

                hisImage=map.get("imageURL") ;
                status.setText(map.get("onlineStatus"));
                getAndStoreToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FancyToast.makeText(getContext(),error.getMessage(),FancyToast.LENGTH_LONG, FancyToast.ERROR,false).show();
            }
        });

    }
    private void readMessages() {
        DatabaseReference dbRef=FirebaseDatabase.getInstance()
                .getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModels.clear();
                String myId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ChatModel chat=ds.getValue(ChatModel.class);
                    if ((chat.getReceiver()
                            .equals(myId) && chat.getSender().equals(id) )||
                            (chat.getReceiver().equals(id)&& chat.getSender().equals(myId))) {

                        chatModels.add(chat);

                    }
                    adapterChat=new ChatAdapter(getContext(),chatModels,hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkOnlineStatus(String status)
    {
        DatabaseReference dbRef=FirebaseDatabase.getInstance()
                .getReference("User").child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        checkOnlineStatus("offline");
    }
    private void pushNotification(String type,String msg,String tokenAgain) {

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", msg);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");
            jNotification.put("icon", R.drawable.ic_plant_login);

            jData.put("picture", R.drawable.ic_plant_login);

            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put(tokenAgain);
                    //ja.put("eeLl305CQtmpp_7sWccgTy:APA91bHekOjRFRg14jQZdvs2liGFfX1_1HbWNpdz1rz51fgEwUhtEtlTb-nAG3GXxYXexib-iHytGpoVv8GXKsesz1KTjS_9yGpC-mpvZNeaxIxVkjDXoYf5-jG8Hev8_fUhASgybJ6M");
                    jPayload.put("registration_ids", ja);
                    break;
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("A7A",resp+" A7a "+AUTH_KEY);
                    // mTextView.setText(resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
    private void getAndStoreToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w("TAGFCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        token = task.getResult();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.
                                        getInstance().getReference("Tokens")
                                        .child(id)
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    token= String.valueOf(task.getResult().getValue());
                                    //Toast.makeText(getContext(), ""+token, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

}