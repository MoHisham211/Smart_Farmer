package mo.zain.smartfarmer.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

import mo.zain.smartfarmer.CompanyActivity;
import mo.zain.smartfarmer.MainActivity;
import mo.zain.smartfarmer.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextInputEditText email, password;
    String txtEmail, txtPassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView forgetPass, toSignUp;
    private AlertDialog dialogForgetPassword;
    ProgressDialog mDialog;
    private ImageView facebook, google;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private String WHO_REGISTER = "User";
    private TextInputLayout textInputLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputLayout1=findViewById(R.id.textInputLayout1);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btnLogin);
        forgetPass = findViewById(R.id.forget);
        facebook = findViewById(R.id.imageFacebook);
        google = findViewById(R.id.imageGoogle);
        toSignUp = findViewById(R.id.toSignUp);
        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(LoginActivity.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait while we check your credentials");
        progressDialog.setCanceledOnTouchOutside(false);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

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
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));

            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UpdateUI(user);
                } else {
                    UpdateUI(null);
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    firebaseAuth.signOut();
                }
            }
        };
        //
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetDialog();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail = email.getText().toString();
                txtPassword = password.getText().toString();
                if (txtEmail.equals("")) {
                    email.setError("Email is Required.");
                    email.setFocusable(true);
                } else if (txtPassword.equals("")) {
                    password.setError("Password is Required.");
                    password.setFocusable(true);
                } else {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (WHO_REGISTER.equals("User")) {
                                            getAndStoreToken();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            finish();
                                        } else if (WHO_REGISTER.equals("Company")) {
                                            getAndStoreToken();
                                            Intent intent = new Intent(getApplicationContext(), CompanyActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            progressDialog.dismiss();
                                            finish();
                                        } else {
                                            progressDialog.show();
                                            FancyToast.makeText(LoginActivity.this, "Sign Up Failed !!", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                DatabaseReference reference;
                                assert user != null;
                                getAndStoreToken();
                                String email = user.getEmail();
                                String userid = user.getUid();
                                String userName = user.getDisplayName();
                                String url = user.getPhotoUrl().toString();
                                reference = FirebaseDatabase.getInstance()
                                        .getReference("User")
                                        .child(userid);//.child(userid)
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("Email", email);
                                hashMap.put("UserName", userName);
                                hashMap.put("onlineStatus","online");
                                hashMap.put("Mobile", "");
                                hashMap.put("id", userid);
                                hashMap.put("imageURL", url);
                                reference.setValue(hashMap);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //progressDialog.dismiss();
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //progressDialog.dismiss();
                                finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w("TAG", "signInWithCredential:failure", task.getException());
                            FancyToast.makeText(LoginActivity.this, "Failed..." + task.getException().getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FancyToast.makeText(LoginActivity.this, "" + e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });

    }

    private void handleFacebookToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    UpdateUI(user);
                } else {
                    FancyToast.makeText(LoginActivity.this, "" + task.getException().getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    //UpdateUI(null);
                }
            }
        });
    }

    private void UpdateUI(FirebaseUser user) {
        if (user != null) {
            DatabaseReference reference;
            String userid = user.getUid();

            reference = FirebaseDatabase.getInstance()
                    .getReference("User")
                    .child(userid);//.child(userid)
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Email", "");
            hashMap.put("UserName", user.getDisplayName());
            hashMap.put("id", userid);
            hashMap.put("onlineStatus","online");
            hashMap.put("Mobile", "");
            hashMap.put("imageURL", user.getPhotoUrl().toString());
            reference.setValue(hashMap);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void showForgetDialog() {
        if (dialogForgetPassword == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_forget_password,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogForgetPassword = builder.create();
            if (dialogForgetPassword.getWindow() != null) {
                dialogForgetPassword.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputLink = view.findViewById(R.id.layout);
            inputLink.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputLink.getText().toString().trim().equals("")) {
                        FancyToast.makeText(LoginActivity.this, "Enter Your Email", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(inputLink.getText().toString().trim()).matches()) {
                        FancyToast.makeText(LoginActivity.this, "Incorrect Email", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    } else {
                        beginRecovery(inputLink.getText().toString());
                        dialogForgetPassword.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogForgetPassword.dismiss();
                }
            });
        }
        dialogForgetPassword.show();
    }

    private void beginRecovery(String email) {
        mDialog.setMessage("Sending Email");
        mDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FancyToast.makeText(LoginActivity.this, "Send Email Done!!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            mDialog.dismiss();
                        } else {
                            FancyToast.makeText(LoginActivity.this, "Password setting failed, please try again", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            mDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                FancyToast.makeText(LoginActivity.this, "" + e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.user:
                if (checked) {

                    WHO_REGISTER = "User";
                }
                return;
            case R.id.company:
                if (checked) {

                    WHO_REGISTER = "Company";
                }
                return;
        }
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