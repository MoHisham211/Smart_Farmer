package mo.zain.smartfarmer.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

import mo.zain.smartfarmer.MainActivity;
import mo.zain.smartfarmer.R;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText userName,email,password,phone,city;
    private Button btnRegistration;
    private ProgressDialog progressDialog;
    private TextInputLayout textInputLayout5,textInputLayout1;
    //
    private FirebaseAuth mAuth;
    private String WHO_REGISTER="User";
    private String Email,UserName,Password,Phone,City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        userName=findViewById(R.id.user_name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.passwordRegistration);
        phone=findViewById(R.id.phone);
        city=findViewById(R.id.City);
        textInputLayout5=findViewById(R.id.textInputLayout5);
        textInputLayout1=findViewById(R.id.textInputLayout1);
        btnRegistration=findViewById(R.id.btnRegister);
        //
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Waiting For Sign Up...");

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Email=email.getText().toString().trim();
                 UserName=userName.getText().toString().trim();
                 Password=password.getText().toString().trim();
                 Phone=phone.getText().toString().trim();
                 City=city.getText().toString().trim();


                if (WHO_REGISTER.equals("User"))
                {
                    if (UserName.isEmpty()){
                        userName.setError("Please Enter Your Name");
                        userName.setFocusable(true);
                    } else if (Email.isEmpty()){
                        email.setError("Please Enter Your Email");
                        email.setFocusable(true);
                    }else if (Password.length()>0 && Password.length()<6){
                        password.setError("Password Should be grater than 6 character");
                        password.setFocusable(true);
                    }else if (Password.isEmpty()){
                        password.setError("Please Enter your Password");
                        password.setFocusable(true);
                    }else if (Phone.isEmpty()){
                        phone.setError("Please Enter your Phone Number");
                        phone.setFocusable(true);
                    }else {
                        progressDialog.show();
                        registerNewUser(WHO_REGISTER,Email,Password);
                    }
                }else{
                    if (UserName.isEmpty()){
                        userName.setError("Please Enter Your Company Name");
                        userName.setFocusable(true);
                    } else if (Email.isEmpty()){
                        email.setError("Please Enter Your Email");
                        email.setFocusable(true);
                    }else if (Password.length()>0 && Password.length()<6){
                        password.setError("Password Should be grater than 6 character");
                        password.setFocusable(true);
                    }else if (Password.isEmpty()){
                        password.setError("Please Enter your Password");
                        password.setFocusable(true);
                    }else if (Phone.isEmpty()){
                        phone.setError("Please Enter your Phone Number");
                        phone.setFocusable(true);
                    }else if (City.isEmpty())
                    {
                        city.setError("Please Enter your City");
                        city.setFocusable(true);
                    } else {
                        progressDialog.show();
                        registerNewUser(WHO_REGISTER,Email,Password);
                    }
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.user:
                if (checked)
                {
                    textInputLayout5.setVisibility(View.GONE);
                    textInputLayout1.setHint("UserName");
                    WHO_REGISTER="User";
                }
                return;
            case R.id.company:
                if (checked)
                {
                    textInputLayout5.setVisibility(View.VISIBLE);
                    textInputLayout1.setHint("Company Name");
                    WHO_REGISTER="Company";
                }
                return;
        }
    }
    private void registerNewUser(String who,String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            FancyToast.makeText(RegistrationActivity.this,"Sign Up Failed !!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }else
                        {
                            if (who.equals("User"))
                            {
                                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                                DatabaseReference reference;
                                String userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference(who)
                                        .child(userid);
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("email",Email);
                                hashMap.put("id",userid);
                                hashMap.put("onlineStatus","online");
                                hashMap.put("username",UserName);
                                hashMap.put("phone",Phone);
                                hashMap.put("imageURL","");
                                getAndStoreToken();
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        FancyToast.makeText(RegistrationActivity.this,"Failed To store data !!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    }
                                });

                            }else if (who.equals("Company"))
                            {
                                FirebaseUser firebaseUser=mAuth.getCurrentUser();
                                DatabaseReference reference;
                                String userid=firebaseUser.getUid();
                                reference= FirebaseDatabase.getInstance().getReference(who)
                                        .child(userid);
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("email",Email);
                                hashMap.put("id",userid);
                                hashMap.put("companyname",UserName);
                                hashMap.put("phone",Phone);
                                hashMap.put("onlineStatus","online");
                                hashMap.put("city",City);
                                getAndStoreToken();
                                hashMap.put("imageURL","");
                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        FancyToast.makeText(RegistrationActivity.this,"Failed To store data !!",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                    }
                                });
                            }

                        }
                    }
                });
    }
    private void getAndStoreToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAGFCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("Tokens").child(userId)
                                .setValue(token);
                    }
                });
    }

}