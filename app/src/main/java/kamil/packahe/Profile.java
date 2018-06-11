package kamil.packahe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import kamil.packahe.User.LoginActivity;
import kamil.packahe.User.SharedPrefManager;

public class Profile extends AppCompatActivity {
    private final String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //    checkIfUserIsLogged();
        if(!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        Button logout = findViewById(R.id.logoutBTN);
        logout.setOnClickListener(e -> {
                SharedPrefManager.getInstance(this).logout();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
        });
    }

    public void profileNavigationClick(View view) {
        switch (view.getId()) {
            case R.id.profileRoutesButton:
                Intent routesIntent = new Intent(this, MyRoutes.class);
                startActivity(routesIntent);
                break;
            case R.id.profileOthersButton:
                Intent othersIntent = new Intent(this, MapsActivity.class);
                startActivity(othersIntent);
                break;
            default:
                break;
        }
    }

    public void newRouteButtonClick(View view) {
        Intent intent = new Intent(this, RecordWorkout.class);
        startActivity(intent);
    }

    public void checkIfUserIsLogged() {
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Log.d(TAG, "not logged");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else
            Log.d(TAG, "logged in");
    }
}
