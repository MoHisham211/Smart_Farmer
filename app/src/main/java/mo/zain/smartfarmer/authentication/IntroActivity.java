package mo.zain.smartfarmer.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Arrays;
import java.util.HashMap;

import mo.zain.smartfarmer.MainActivity;
import mo.zain.smartfarmer.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.jetbrains.annotations.NotNull;


public class IntroActivity extends AppCompatActivity {

    private Button signIn,signUp;
    private ImageView facebook,google;
    private static final int RC_SIGN_IN = 100;
    ProgressDialog mDialog;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseAuth Fauth;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager=CallbackManager.Factory.create();
        initialization();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // not called
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // not called
                //Log.d("fb_login_sdk", "callback cancel");
            }

            @Override
            public void onError(FacebookException e) {
                // not called
                //Log.d("fb_login_sdk", "callback onError");
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("fb_login_sdk", "click");
//                List<String> perm = new ArrayList<String>();
                LoginManager.getInstance().logInWithReadPermissions(IntroActivity.this, Arrays.asList("email", "public_profile"));

            }
        });
        action();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null)
                {
                    UpdateUI(user);
                } else
                {
                    UpdateUI(null);
                }
            }
        };
        accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken==null)
                {
                    firebaseAuth.signOut();
                }
            }
        };
    }
    private void initialization(){
        signIn=findViewById(R.id.btnSignIn);
        signUp=findViewById(R.id.btnSignUp);
        facebook=findViewById(R.id.imageFacebook);
        google=findViewById(R.id.imageGoogle);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        mDialog = new ProgressDialog(IntroActivity.this);
        Fauth = FirebaseAuth.getInstance();
    }
    private void action(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                DatabaseReference reference;
                                assert user != null;
                                String email=user.getEmail();
                                String userid=user.getUid();
                                String userName=user.getDisplayName();
                                String url=user.getPhotoUrl().toString();
                                reference= FirebaseDatabase.getInstance()
                                        .getReference("User")
                                        .child(userid);//.child(userid)
                                HashMap<String,String> hashMap=new HashMap<>();
                                hashMap.put("Email",email);
                                hashMap.put("UserName",userName);
                                hashMap.put("Mobile","");
                                hashMap.put("id",userid);
                                hashMap.put("imageURL",url);
                                reference.setValue(hashMap);
                                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //progressDialog.dismiss();
                                finish();
                            }else {
                                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //progressDialog.dismiss();
                                finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w("TAG", "signInWithCredential:failure", task.getException());
                            FancyToast.makeText(IntroActivity.this,"Failed..."+task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FancyToast.makeText(IntroActivity.this,""+e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
            }
        });

    }


    @Override
    protected void onStart() {

        super.onStart();
        if (firebaseUser!=null)
        {
            //firebaseAuth.addAuthStateListener(authStateListener);
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        }

    }
    private void handleFacebookToken(AccessToken token)
    {

        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    UpdateUI(user);
                }else {
                    FancyToast.makeText(IntroActivity.this,""+task.getException().getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    //UpdateUI(null);
                }
            }
        });
    }
    private void UpdateUI(FirebaseUser user)
    {
        if (user !=null)
        {
            DatabaseReference reference;
            String userid=user.getUid();

            reference= FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(userid);//.child(userid)
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("Email","");
            hashMap.put("UserName",user.getDisplayName());
            hashMap.put("id",userid);
            hashMap.put("Mobile","");
            hashMap.put("imageURL",user.getPhotoUrl().toString());
            reference.setValue(hashMap);
            startActivity(new Intent(IntroActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}