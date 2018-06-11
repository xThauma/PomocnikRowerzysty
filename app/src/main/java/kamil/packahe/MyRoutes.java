package kamil.packahe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyRoutes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
    }

    public void routesNavigationClick (View view) {
        switch (view.getId()) {
            case R.id.routesProfileButton:
                Intent profileIntent = new Intent(this,Profile.class);
                startActivity(profileIntent);
                finish();
                break;
            case R.id.routesOthersButton:
                Intent othersIntent = new Intent(this,Others.class);
                startActivity(othersIntent);
                finish();
                break;
            default:
                break;
        }
    }

    public void newRouteButtonClick (View view) {
        Intent intent = new Intent(this,RecordWorkout.class);
        startActivity(intent);
        finish();
    }
}
