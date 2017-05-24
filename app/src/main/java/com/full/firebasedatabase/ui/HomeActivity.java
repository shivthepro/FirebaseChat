package com.full.firebasedatabase.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.full.firebasedatabase.R;
import com.full.firebasedatabase.util.ValidationUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";
    private GoogleApiClient mGoogleApiClient;

    //FB
    private LoginManager mLoginManager;
    private GoogleSignInApi mGoogleSignInApi;

    enum OAuth {
        GOOGLE, FACEBOOK, TWITTER
    }
    public static final String[] mPermissionsArray = {"email","public_profile"};
    public static final List<String> mFbPermissions = Arrays.asList(mPermissionsArray);

    /**
     * Google Sign in request code
     **/
    private static final int G_SIGN_IN = 111;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.btn_login_anonymous).setOnClickListener(this);
        findViewById(R.id.btn_g_signin).setOnClickListener(this);
        findViewById(R.id.btn_fb).setOnClickListener(this);

        // Google Sign in

        GoogleSignInOptions lGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, lGso)
                .build();

        // Facebook Sign in

        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();

        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: ");
                registerAccountInFirebase(loginResult.getAccessToken().getToken(), OAuth.FACEBOOK);
            }

            @Override
            public void onCancel() {
                Toast.makeText(HomeActivity.this, "Login Process cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(HomeActivity.this, "Error Occoured", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: " + e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login_anonymous:
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //TODO: start Activity to send user to next activity
                            Log.d(TAG, "onComplete: " + task.getResult().getUser());
                            startActivity(new Intent(HomeActivity.this, ChatActivity.class));
                        } else {
                            Toast.makeText(HomeActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            // Google Sign in handle
            case R.id.btn_g_signin:
                mGoogleSignInApi = Auth.GoogleSignInApi;
                Intent lGoogleSignInIntent = mGoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(lGoogleSignInIntent, G_SIGN_IN);
                break;

            // FB Sign in
            case R.id.btn_fb:
                mLoginManager.logOut();
                mLoginManager.logInWithReadPermissions(this,mFbPermissions);
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == G_SIGN_IN) {
            GoogleSignInResult lGoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (lGoogleSignInResult.isSuccess()) {

                GoogleSignInAccount lGoogleSignInAccount = lGoogleSignInResult.getSignInAccount();
                Log.d(TAG, "onActivityResult: " + lGoogleSignInAccount.getDisplayName());
                registerAccountInFirebase(lGoogleSignInAccount.getIdToken(), OAuth.GOOGLE);
            }
            Log.d(TAG, "onActivityResult: " + lGoogleSignInResult.isSuccess());
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Registers the account in firebase and logs into the application
     * @param pAccessToken
     * @param pOAuth
     */
    private void registerAccountInFirebase(String pAccessToken, final OAuth pOAuth) {


        AuthCredential lAuthCredential = null;
        switch (pOAuth) {
            case GOOGLE:
                lAuthCredential = GoogleAuthProvider.getCredential(pAccessToken, null);
                break;
            case FACEBOOK:
                lAuthCredential = FacebookAuthProvider.getCredential(pAccessToken);
                break;
        }
        mAuth.signInWithCredential(lAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());

                if (task.isSuccessful()) {
                    startActivity(new Intent(HomeActivity.this, ChatActivity.class));
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    if (pOAuth == OAuth.FACEBOOK)
                        Toast.makeText(HomeActivity.this, "Account already associated with a google account", Toast.LENGTH_SHORT).show();
                    if (pOAuth == OAuth.GOOGLE)
                        Toast.makeText(HomeActivity.this, "Account already associated with a facebook account", Toast.LENGTH_SHORT).show();
                }
                if(mGoogleApiClient.isConnected())
                    mGoogleSignInApi.signOut(mGoogleApiClient);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }
}
