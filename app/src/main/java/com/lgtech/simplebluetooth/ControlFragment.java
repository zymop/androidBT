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

import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends androidx.fragment.app.Fragment {

    View view;
    Button PotOn, PotOff, PushOn, PushOff, LedOn, LedOff,
            MotorOn, MotorOff, TLedOn, TLedOff, TDigitalOn,
            TDigitalOff, TRelayOn, TRelayOff, TSensorOn, TSensorOff;
    Timer ledTimer, digitalTimer, relayTimer, sensorTimer;
    Boolean LTimerOn = false, DTimerOn = false, RTimerOn = false, STimerOn = false;
    boolean potOnClicked, potOffClicked, pushOnClicked, pushOffClicked,
            ledOnClicked, ledOffClicked, motorOnClicked, motorOffClicked,
            tLedOnClicked, tLedOffClicked, tDigitalOnClicked, tDigitalOffClicked,
            tRelayOnClicked, tRelayOffClicked, tSensorOnClicked, tSensorOffClicked;

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

    public class myTimerTask extends TimerTask {
        String num, onOff;
        public myTimerTask(String str) {
            num = str;
            onOff = "ON";
        }
        @Override
        public void run() {
            if (onOff.equals("ON")) {

            }
        }
    }

    public void clearAllColor() {
        PotOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        PotOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        PushOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        PushOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        LedOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        LedOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        MotorOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        MotorOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TLedOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TLedOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TDigitalOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TDigitalOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TRelayOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TRelayOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TSensorOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
        TSensorOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
    }

    public void clearAllClickedButton() {
        potOnClicked = false;
        potOffClicked = false;
        pushOnClicked = false;
        pushOffClicked = false;
        ledOnClicked = false;
        ledOffClicked = false;
        motorOnClicked = false;
        motorOffClicked = false;
        tLedOnClicked = false;
        tLedOffClicked = false;
        tDigitalOnClicked = false;
        tDigitalOffClicked = false;
        tRelayOnClicked = false;
        tRelayOffClicked = false;
        tSensorOnClicked = false;
        tSensorOffClicked = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_control, container, false);
        PotOn = (Button) view.findViewById(R.id.buttonPotOn);



        PotOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                potOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 2, ON");
                clearAllColor();
                PotOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(PotOn);

        PotOff = (Button) view.findViewById(R.id.buttonPotOff);
        PotOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                potOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 2, OFF");
                clearAllColor();
                PotOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(PotOff);

        PushOn = (Button) view.findViewById(R.id.buttonPushOn);
        PushOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                pushOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 3, ON");
                clearAllColor();
                PushOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(PushOn);

        PushOff = (Button) view.findViewById(R.id.buttonPushOff);
        PushOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                pushOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 3, OFF");
                clearAllColor();
                PushOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(PushOff);

        LedOn = (Button) view.findViewById(R.id.buttonLedOn);
        LedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                ledOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 4, ON");
                clearAllColor();
                LedOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(LedOn);

        LedOff = (Button) view.findViewById(R.id.buttonLedOff);
        LedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                ledOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 4, OFF");
                clearAllColor();
                LedOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(LedOff);

        MotorOn = (Button) view.findViewById(R.id.buttonMotorOn);
        MotorOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                motorOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 5, ON");
                clearAllColor();
                MotorOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(MotorOn);

        MotorOff = (Button) view.findViewById(R.id.buttonMotorOff);
        MotorOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                motorOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 5, OFF");
                clearAllColor();
                MotorOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        });
        //buttonEffect(MotorOff);

        ledTimer = new Timer();
        TLedOn = (Button) view.findViewById(R.id.buttonTLedOn);
        TLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tLedOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 6, ON");
                clearAllColor();
                TLedOn.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (!LTimerOn) {
                    LTimerOn = true;
                    ledTimer.schedule(new myTimerTask("6"), 0, 1000);
                    TLedOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TLedOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }
                */

            }
        });
        //buttonEffect(TLedOn);

        TLedOff = (Button) view.findViewById(R.id.buttonTLedOff);
        TLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tLedOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 6, OFF");
                clearAllColor();
                TLedOff.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (LTimerOn) {
                    if (ledTimer != null)
                        ledTimer.cancel();
                    LTimerOn = false;
                    TLedOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TLedOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }

                 */
            }
        });
        //buttonEffect(TLedOff);

        digitalTimer = new Timer();
        TDigitalOn = (Button) view.findViewById(R.id.buttonDigitalOn);
        TDigitalOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tDigitalOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 7, ON");
                clearAllColor();
                TDigitalOn.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (!DTimerOn) {
                    DTimerOn = true;
                    digitalTimer.schedule(new myTimerTask("7"), 0, 1000);
                    TDigitalOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TDigitalOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }
                */
            }
        });
        //buttonEffect(TDigitalOn);

        TDigitalOff = (Button) view.findViewById(R.id.buttonDigitalOff);
        TDigitalOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tDigitalOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 7, OFF");
                clearAllColor();
                TDigitalOff.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (DTimerOn) {
                    if (digitalTimer != null)
                        digitalTimer.cancel();
                    DTimerOn = false;
                    TDigitalOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TDigitalOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }
                */
            }
        });
        //buttonEffect(TDigitalOff);

        relayTimer = new Timer();
        TRelayOn = (Button) view.findViewById(R.id.buttonRelayOn);
        TRelayOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tRelayOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 8, ON");
                clearAllColor();
                TRelayOn.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (!RTimerOn) {
                    RTimerOn = true;
                    relayTimer.schedule(new myTimerTask("8"), 0, 1000);
                    TRelayOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TRelayOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }
                */
            }
        });
        //buttonEffect(TRelayOn);

        TRelayOff = (Button) view.findViewById(R.id.buttonRelayOff);
        TRelayOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tRelayOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 8, OFF");
                clearAllColor();
                TRelayOff.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (RTimerOn) {
                    if (relayTimer != null)
                        relayTimer.cancel();
                    RTimerOn = false;
                    TRelayOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TRelayOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }

                 */
            }
        });
        //buttonEffect(TRelayOff);

        sensorTimer = new Timer();
        TSensorOn = (Button) view.findViewById(R.id.buttonSensorOn);
        TSensorOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tSensorOnClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 9, ON");
                clearAllColor();
                TSensorOn.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (!STimerOn) {
                    STimerOn = true;
                    sensorTimer.schedule(new myTimerTask("9"), 0, 1000);
                    TSensorOn.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TSensorOff.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }

                 */
            }
        });
        //buttonEffect(TRelayOn);

        TSensorOff = (Button) view.findViewById(R.id.buttonSensorOff);
        TSensorOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllClickedButton();
                tSensorOffClicked = true;
                //((MainActivity)getActivity()).MsgInQuery("COMMAND, 9, OFF");
                clearAllColor();
                TSensorOff.setBackgroundColor(Color.parseColor("#FFEB3B"));

                /*
                if (STimerOn) {
                    if (sensorTimer != null)
                        sensorTimer.cancel();
                    STimerOn = false;
                    TSensorOff.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    TSensorOn.setBackgroundColor(Color.parseColor("#190C0A0A"));
                }

                 */
            }
        });
        //buttonEffect(TSensorOff);

        return view;
    }
}