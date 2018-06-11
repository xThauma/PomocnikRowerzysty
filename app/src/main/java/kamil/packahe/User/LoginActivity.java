package kamil.packahe.User;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.Locale;

import kamil.packahe.NavigationDrawerActivity;
import kamil.packahe.R;

import static kamil.packahe.MapsActivity.REQUEST_LOCATION_CODE;

/**
 * Created by Kamil Lenartowicz on 2018-01-09.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView usernameET, passwordET;
    private Button loginBTN, registerBTN;
    private ImageButton changeLanguageToPl, changeLanguageToEn;
    private ProgressDialog progressDialog;
    private boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //sprawdzanie wersji
            checkLocationPermission();
            checkMicroPhonePermission();
        }
        checkIfLogged();
        initViews();
        handleInputs();

    }

    public void checkIfLogged() {
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, NavigationDrawerActivity.class));
            finish();
        }
    }

    public void initViews() {
        usernameET = findViewById(R.id.username_loginET);
        passwordET = findViewById(R.id.password_loginET);
        loginBTN = findViewById(R.id.loginBTN);
        registerBTN = findViewById(R.id.register_loginBTN);
        changeLanguageToPl = findViewById(R.id.changeLenToPl);
        changeLanguageToEn = findViewById(R.id.changeLenToEng);
    }

    public void handleInputs() {
        loginBTN.setOnClickListener(e -> login());
        registerBTN.setOnClickListener(e -> register());
        changeLanguageToPl.setOnClickListener(e -> changeLanguage("pl"));
        changeLanguageToEn.setOnClickListener(e -> changeLanguage("en"));
    }

    public void login() {
        final String username = usernameET.getText().toString().trim();
        final String password = passwordET.getText().toString().trim();

        if(username.length()==0){
            Toast.makeText(this, "Nie ma takiego użytkownika!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");
        String URL = ConstantsUserAPI.URL_LOGIN + username+"&haslo="+password;
        Log.d("url: ", URL);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.d("wwwwwwwwwww", response.toString().length() + "");

            if (response.toString().length() == 8) {
                isFound = true;
            } else if (response.toString().length() == 9) {
                isFound = false;
            }
            if (!isFound) {
                Toast.makeText(this, "Nie ma takiego użytkownika!", Toast.LENGTH_SHORT).show();
            } else if (isFound) {
                SharedPrefManager.getInstance(getApplicationContext()).userLogin(username);
                finish();
                startActivity(new Intent(getApplicationContext(), NavigationDrawerActivity.class));
            }
            progressDialog.dismiss();
        }, error -> {
            Log.e("ERROR", " " + error.toString());

        });
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void register() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void changeLanguage(String language) {
        String languageToLoad = language;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getBaseContext().getResources().getDisplayMetrics());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }
            return false;
        } else
            return true;

        //Request runtime permissions in your app, giving the user the opportunity to allow or deny location permission.
        // The following code checks whether the user has granted fine location permission. If not, it requests the permission:

    }

    public boolean checkMicroPhonePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_LOCATION_CODE);

            }
            return false;
        } else
            return true;

        //Request runtime permissions in your app, giving the user the opportunity to allow or deny location permission.
        // The following code checks whether the user has granted fine location permission. If not, it requests the permission:

    }


}