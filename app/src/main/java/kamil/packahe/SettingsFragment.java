package kamil.packahe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Kamil on 14.03.2018.
 */

public class SettingsFragment extends Fragment {

    public static final int GET_FROM_GALLERY = 3;
    Button passwordChangeButton2;
    Switch data, sound, noti;
    boolean isSaving = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        passwordChangeButton2 = view.findViewById(R.id.passwordChangeButton2);
        data = view.findViewById(R.id.datas);
        sound = view.findViewById(R.id.soundss);
        noti = view.findViewById(R.id.notifications);

        passwordChangeButton2.setOnClickListener(e -> startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY));

        sound.setOnClickListener(e -> {
            if (isSaving) {
             //   AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            //    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
            isSaving = !isSaving;
        });

        data.setOnClickListener(e -> {

            //     setMobileDataState(isSaving);
        });


        return view;
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {
                boolean mobileDataEnabled = (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);

                return mobileDataEnabled;
            }
        } catch (Exception ex) {
            Log.e("settings", "Error getting mobile data state", ex);
        }

        return false;
    }

    public void setMobileDataState(boolean mobileDataEnabled) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        } catch (Exception ex) {
            Log.e("settings", "Error setting mobile data state", ex);
        }
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
}