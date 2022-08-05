package com.lgtech.simplebluetooth;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class WifiFragment extends androidx.fragment.app.Fragment {

    View view;
    EditText et1, et2;
    Button wb;
    boolean wbClicked = false;

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wifi, container, false);

        et1 = (EditText) view.findViewById(R.id.textInputW1);
        et2 = (EditText) view.findViewById(R.id.textInputW2);
        wb  = (Button)   view.findViewById(R.id.buttonWSet);
        wb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wbClicked = true;
                wb.setBackgroundColor(Color.parseColor("#FFEB3B"));

                //Log.d("send: ", "COMMAND, 1, " + et1.getText().toString() +
                //        ", " + et2.getText().toString());
            }
        });
        buttonEffect(wb);

        return view;
    }
}