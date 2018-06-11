package kamil.packahe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Others extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);
    }

    public void othersNavigationClick (View view) {
        switch (view.getId()) {
            case R.id.othersProfileButton:
                Intent profileIntent = new Intent(this,Profile.class);
                startActivity(profileIntent);
                finish();
                break;
            case R.id.othersRoutesButton:
                Intent routesIntent = new Intent(this,MyRoutes.class);
                startActivity(routesIntent);
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
