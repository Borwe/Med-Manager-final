package med.borwe.com.med_manager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import constants.ActivityConstants;

import static med.borwe.com.med_manager.R.integer.GOOGLE_REQ_CODE;


//Check if User has logged in alread
//if not display login button allow them to login
//if so, send them to the MainActivity
public class LoginActivity extends AppCompatActivity {

    public SignInButton signInButton;//button for singing in
    public GoogleApiClient googleApiClient;//manage googleSingin

    private final int CALENDAR_PERMISION_REQUEST=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(LoginActivity.this).enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                //if user failed to login after clicking button, might be due to network, or just the user
                //add this later
                AlertDialog.Builder alert=new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("Sorry, Please retry later when you have better connection");
                alert.setTitle("ERROR");
                alert.setPositiveButton("Let me retry",null);
                alert.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.this.finish();
                    }
                });
                alert.create().show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions).build();

        signInButton=findViewById(R.id.sing_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start singin
                Intent i= Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(i, ActivityConstants.GOOGLE_REQ_CODE);
            }
        });


        //add calendar permisions checking
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            String permisions[]={Manifest.permission.WRITE_CALENDAR};
            ActivityCompat.requestPermissions(this,permisions,CALENDAR_PERMISION_REQUEST);
            return;
        }

        //check if user logged in, if so jump to main activity
        boolean user_logged=checkIfLoggedIn();
        if(user_logged){
            Intent goToMainActivity=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(goToMainActivity);
            this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CALENDAR_PERMISION_REQUEST:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                    dialog.setTitle("Sorry...");
                    dialog.setMessage("Please restart the application and accept permisions for the App to work\n" +
                            "We need access to your calendar to place your medications there");
                    dialog.setNegativeButton("Okay, I will restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.this.finish();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.create().show();
                }
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ActivityConstants.GOOGLE_REQ_CODE){
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()==false){
                AlertDialog.Builder alert=new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("Sorry, Please retry later when you have better connection");
                alert.setTitle("ERROR");
                alert.setPositiveButton("Let me retry",null);
                alert.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.this.finish();
                    }
                });
                alert.create().show();
                return;
            }else {
                addUserSettings(result);
            }
        }
    }

    //add result to prefrences if login is a success
    private void addUserSettings(GoogleSignInResult result){
        String user_name=result.getSignInAccount().getDisplayName();
        String email=result.getSignInAccount().getEmail();
        SharedPreferences userpres=getSharedPreferences(getString(R.string.user_prefs),MODE_PRIVATE);
        SharedPreferences.Editor editor=userpres.edit();
        editor.putString("user_name",user_name);
        editor.putString("email",email);
        editor.apply();

        //then go to main Activity
        Intent mainActivity=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainActivity);
        LoginActivity.this.finish();
    }

    //check if user logged in, true if he has, false if not
    private boolean checkIfLoggedIn(){
        SharedPreferences userPrefs=getSharedPreferences(getString(R.string.user_prefs),MODE_PRIVATE);
        //check if user_name exists
        String user_name=userPrefs.getString("user_name","--");
        if(user_name.equals("--")){
            //works if user not logged in
            return false;
        }else{
            //works if user already logged in
            return true;
        }
    }
}
