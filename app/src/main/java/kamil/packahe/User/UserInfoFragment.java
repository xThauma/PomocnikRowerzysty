package kamil.packahe.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import kamil.packahe.R;

/**
 * Created by Kamil on 10.03.2018.
 */

public class UserInfoFragment extends Fragment {

    public static final int GET_FROM_GALLERY = 3;
    ImageView imageView2;
    TextView tv, tv2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_info_profile, container, false);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView2.setOnClickListener(e -> startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY));
        tv = view.findViewById(R.id.textView20);
        tv2 = view.findViewById(R.id.textView25);
        getHistory();
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void getHistory() {
        String pesel = SharedPrefManager.getInstance(getContext()).getUsername();
        String url = ConstantsUserAPI.URL_GET_TOTALKMS + "?pesel=" + pesel;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                int counter = 0;
                int currentValue = counter;
                String totalKm = "0";
                for (int i = 0; i < response.length(); i++) {
                    JSONObject post = response.getJSONObject(i);
                    totalKm = post.getString("count(odleglosc)");
                }
                tv.setText(String.valueOf(totalKm));
                Random r = new Random();
                tv2.setText(String.valueOf(r.nextInt(30) + 10));
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }, error -> {
            Log.e("ERROR", " " + error.toString());

        });
        RequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }
}