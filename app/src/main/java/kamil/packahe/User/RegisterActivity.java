package kamil.packahe.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.regex.Pattern;

import kamil.packahe.R;


/**
 * Created by Kamil Lenartowicz on 2018-01-09.
 */

public class RegisterActivity extends AppCompatActivity {
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private ProgressDialog progressDialog;
    private boolean isFound = false;
    private String outputString = "";
    private EditText firstNameTV, surnameTV, peselTV, emailTV, phoneTV, passwordTV;
    private Button registerBTN, cancelBTN;

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initViews();
        handleInputs();
    }

    public void initViews() {
        firstNameTV = findViewById(R.id.first_name_registerET);
        surnameTV = findViewById(R.id.surname_registerET);
        peselTV = findViewById(R.id.pesel_registerET);
        emailTV = findViewById(R.id.email_registerET);
        phoneTV = findViewById(R.id.phone_registerET);
        registerBTN = findViewById(R.id.registerBTN);
        cancelBTN = findViewById(R.id.register_finishBTN);
        passwordTV = findViewById(R.id.password_registerET);
    }

    public void handleInputs() {
        registerBTN.setOnClickListener(e -> register());
        cancelBTN.setOnClickListener(e -> launchLoginScreen());
    }

    public void launchLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public boolean checkET(String text, EditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.setError("This field cannot be empty!");
            return false;
        }
        return true;
    }

    public boolean checkMail(String text, EditText editText) {
        if (TextUtils.isEmpty(text)) {
            editText.setError("This field cannot be empty!");
            return false;
        } else if (!isValidEmail(text)) {
            editText.setError("Wrong e-mail pattern!");
            return false;
        }
        return true;
    }

    public void register() {
        final String firstName = firstNameTV.getText().toString();
        final String surname = surnameTV.getText().toString();
        final String pesel = peselTV.getText().toString();
        if (pesel.length() != 11) {
            Toast.makeText(this, "Ten pesel jest zbyt krótki", Toast.LENGTH_LONG).show();
            return;
        }
        final String password = passwordTV.getText().toString();
        final String email = emailTV.getText().toString();
        final String phone = phoneTV.getText().toString();
        Log.d("Register Activity", firstName + " " + surname + " " + pesel + " " + password + " " + email + " " + phone);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");
        outputString = "";
        if (checkET(firstName, firstNameTV) && checkET(surname, surnameTV) && checkET(pesel, peselTV) && checkET(password, passwordTV) && checkET(phone, phoneTV) && checkMail(email, emailTV)) {

            String URL = ConstantsUserAPI.URL_REGISTER + "?imie=" + firstName + "&naziwsko=" + surname + "&pesel=" + pesel + "&haslo=" + password + "&email=" + email + "&numer_telefonu=" + phone;
            Log.d("url: ", URL);
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
                Log.d("wwwwwwwwwww", response.toString().length() + "");

                if (response.toString().length() == 8) {
                    isFound = true;
                } else if (response.toString().length() == 9) {
                    isFound = false;
                }
                if (isFound) {
                    Toast.makeText(this, "Ten pesel jest już zajęty!", Toast.LENGTH_SHORT).show();
                } else if (!isFound) {
                    Toast.makeText(this, "Gratulacje, zarejestrowałeś się!", Toast.LENGTH_LONG).show();
                    launchLoginScreen();
                }
                progressDialog.dismiss();
            }, error -> {
                Log.e("ERROR", " " + error.toString());

            });
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            progressDialog.dismiss();

        } else
            return;
    }

}
