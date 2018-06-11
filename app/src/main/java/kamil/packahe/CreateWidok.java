package kamil.packahe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import kamil.packahe.User.ConstantsUserAPI;
import kamil.packahe.User.RequestHandler;
import kamil.packahe.User.SharedPrefManager;

public class CreateWidok extends AppCompatActivity {

    ProgressDialog progressDialog;
    Button back, make;
    RadioGroup rg;
    String msgRG = "Ladny widok";
    EditText editable;
    String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_widok);

        back = findViewById(R.id.button4powrot);
        make = findViewById(R.id.button2dodaj);
        editable = findViewById(R.id.editText7);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");

        lat = getIntent().getStringExtra("LAT");
        lng = getIntent().getStringExtra("LNG");

        back.setOnClickListener(e -> {
            startActivity(new Intent(this, NavigationDrawerActivity.class));
            finish();
        });
        make.setOnClickListener(e -> sendData());
    }

    public void onRadioButtonClicked(View view) {
        rg = findViewById(R.id.rbGroupLoseWeight);
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.zeroRB:
                if (checked)
                    msgRG = "Ladny widok";
                    break;
            case R.id.quarterRB:
                if (checked)
                    msgRG = "Niebezpieczenstwo";
                    break;
        }
        Log.d("RG", msgRG);
    }

    public void sendData() {
        String pesel = SharedPrefManager.getInstance(this).getUsername();
        String comment = editable.getText().toString();
        Log.d("po kolei: ", "?typ=" + msgRG + "&komentarz=" + comment + "&lat=" + lat + "&lng=" + lng);
        String URL = ConstantsUserAPI.URL_POST_DATA + "?typ=" + msgRG + "&komentarz=" + comment + "&lat=" + lat + "&lng=" + lng;
        Log.d("url: ", URL);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Log.d("wwwwwwwwwww", response.toString().length() + "");
            progressDialog.dismiss();
        }, error -> {
            progressDialog.dismiss();
            Log.e("ERROR", " " + error.toString());

        });
        progressDialog.dismiss();
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
        Toast.makeText(this, "Operacja przebiegła pomyślnie", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, NavigationDrawerActivity.class));
        finish();
    }


}
