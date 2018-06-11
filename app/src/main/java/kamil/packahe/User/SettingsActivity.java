package kamil.packahe.User;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kamil.packahe.R;

public class SettingsActivity extends AppCompatActivity {

    private boolean iAmPrettySure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        iAmPrettySure = false;
    }


    public void dataSwitch (View view) {

    }

    public void soundsSwitch (View view) {

    }

    public void notificationsSwitch (View view) {

    }

    public void passwordChange (View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
        dialog.setTitle("Password change");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText oldPassword = new EditText(this);
        final EditText newPassword = new EditText(this);
        final EditText repeatPassword = new EditText(this);
        oldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPassword.setHint("Old password");
        newPassword.setHint("New password");
        repeatPassword.setHint("Repeat new password");
        layout.addView(oldPassword);
        layout.addView(newPassword);
        layout.addView(repeatPassword);
        dialog.setView(layout);
        dialog.setPositiveButton("OK", (dialog1, which) -> {
            String oldPasswordString = oldPassword.getText().toString();
            String newPasswordString = newPassword.getText().toString();
            String repeatPasswordString = repeatPassword.getText().toString();
            Pattern p = Pattern.compile("(([A-Z].*[0-9]))");
            Matcher m = p.matcher(newPasswordString);
            boolean b = m.find();
            boolean errors = false;
            if (oldPasswordString==newPasswordString || oldPasswordString==repeatPasswordString || newPasswordString!=repeatPasswordString
                || newPasswordString.length()<8 || !b)
                errors = true;


            /* TO DO

            Jak logowanie będzie działać, to pobierać w tym momencie ID aktualnie zalogowanego użytkownika i wysyłać do bazy zapytanie UPDATE
            z nowymi danymi do konta
            Trzeba jeszcze napisać lepszą obsługę błędów, to ustali się kiedy indziej

             */

            if (errors)
                return;
            else {
                /*
                String SQL = new StringBuilder().append("UPDATE USERS SET password=").append("\').append(newPasswordString).append("\' WHERE ID=
                ........
                .append(";").toString();
                */
            }});
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


    public void settingsLogout (View view) {
        // TO DO
        // Wylogowanie (chociaż najpierw logowanie by się przydało)
        Snackbar.make(findViewById(R.id.myLinearLayout),"Logout process successfull.",Snackbar.LENGTH_SHORT).show();
    }
}
