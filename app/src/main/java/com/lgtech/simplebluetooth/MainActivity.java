package com.lgtech.simplebluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    //private static final UUID BT_MODULE_UUID = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
    //private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private static final UUID BT_MODULE_UUID = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");
    //private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //private static final UUID BT_MODULE_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;

    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private BottomNavigationView mMainNavi;
    private FrameLayout mMainFrame;

    private WifiFragment wifiFragment;
    private ControlFragment controlFragment;
    private String remoteDeviceName = "";

    private boolean threadInUse = false;
    EditText et1, et2;
    private boolean scanAndConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothStatus = (TextView) findViewById(R.id.bluetooth_status);
        mReadBuffer = (TextView) findViewById(R.id.read_buffer);
        mScanBtn = (Button) findViewById(R.id.scan);
        mDiscoverBtn = (Button) findViewById(R.id.discover);

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView) findViewById(R.id.devices_list_view);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                        mBluetoothStatus.setText("Connected to Device: " + msg.obj);
                    } else {
                        mBluetoothStatus.setText("Connection Failed");
                    }
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn();
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover();

                }
            });
        }

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNavi = (BottomNavigationView) findViewById(R.id.main_navi);

        wifiFragment = new WifiFragment();
        controlFragment = new ControlFragment();

        setFragment(wifiFragment);
        SendThread sendThread = new SendThread();
        sendThread.start();

        mMainNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navi_wifi:
                        setFragment(wifiFragment);
                        return true;
                    case R.id.navi_control:
                        setFragment(controlFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void setFragment(androidx.fragment.app.Fragment fragment) {
        androidx.fragment.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    //@RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    private void discover(){
        // Check if the device is already discovering
        Log.d("test","discover");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.i("info", "No fine location permissions");
            String [] myArray = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,
                    myArray,
                    1);
        }

        if(mBTAdapter.isDiscovering()){
            Log.d("test","1");
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                Log.d("test","2");
                mBTArrayAdapter.clear(); // clear items
                if (mBTAdapter.startDiscovery()) {Log.d("test","YES");}
                else {Log.d("test","NO");}
                //Log.d("test","3");
                if(mBTAdapter.isDiscovering()){Log.d("test","4"); }
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                Log.d("test","5");
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                Log.d("test","6");
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            remoteDeviceName = ((TextView) view).getText().toString();
            view.setBackgroundColor(Color.parseColor("#FFEB3B"));
            scanAndConnected = true;
            for ( int i = 0 ; i < mDevicesListView.getCount() ; i++){
                if (i != position) {
                    View v = getViewByPosition(i, mDevicesListView);
                    v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
            mBluetoothStatus.setText(remoteDeviceName);
        }
    };

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void connectToServer(String str) {
        threadInUse = true;
        if(!mBTAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            return;
        }

        //mBluetoothStatus.setText("Connecting...");
        // Get the device MAC address, which is the last 17 chars in the View
        String info = remoteDeviceName;
        //String info = "5C:F3:70:9A:40:1E";
        final String address = info.substring(info.length() - 17);
        final String name = info.substring(0,info.length() - 17);
        final String command = str;

        // Spawn a new thread to avoid blocking the GUI one
        new Thread()
        {
            @Override
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                try {
                    //Log.d("Time","time out maybe");
                    mBTSocket = createBluetoothSocket(device);
                    //Log.d("Time","time out maybe");
                } catch (IOException e) {
                    fail = true;
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!fail) {
                    mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                    mConnectedThread.start();
                    mConnectedThread.write(command);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mConnectedThread.cancel();
                    threadInUse = false;
                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                            .sendToTarget();
                }
            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            //Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createInsecureRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    //public void write(String str) {
       // mConnectedThread.write(str);
    //}

    class SendThread extends Thread {

        public SendThread() {

        }

        public void processGUI() {
            //last pressed button for every second
            if (scanAndConnected) {
                if (controlFragment.potOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 2, ON");
                    controlFragment.potOnClicked = false;
                }
                else if (controlFragment.potOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 2, OFF");
                    controlFragment.potOffClicked = false;
                }
                else if (controlFragment.pushOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 3, ON");
                    controlFragment.pushOnClicked = false;
                }
                else if (controlFragment.pushOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 3, OFF");
                    controlFragment.pushOffClicked = false;
                }
                else if (controlFragment.ledOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 4, ON");
                    controlFragment.ledOnClicked = false;
                }
                else if (controlFragment.ledOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 4, OFF");
                    controlFragment.ledOffClicked = false;
                }
                else if (controlFragment.motorOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 5, ON");
                    controlFragment.motorOnClicked = false;
                }
                else if (controlFragment.motorOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 5, OFF");
                    controlFragment.motorOffClicked = false;
                }
                else if (controlFragment.tLedOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 6, ON");
                    controlFragment.tLedOnClicked = false;
                }
                else if (controlFragment.tLedOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 6, OFF");
                    controlFragment.tLedOffClicked = false;
                }
                else if (controlFragment.tDigitalOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 7, ON");
                    controlFragment.tDigitalOnClicked = false;
                }
                else if (controlFragment.tDigitalOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 7, OFF");
                    controlFragment.tDigitalOffClicked = false;
                }
                else if (controlFragment.tRelayOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 8, ON");
                    controlFragment.tRelayOnClicked = false;
                }
                else if (controlFragment.tRelayOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 8, OFF");
                    controlFragment.tRelayOffClicked = false;
                }
                else if (controlFragment.tSensorOnClicked && !threadInUse) {
                    connectToServer("COMMAND, 9, ON");
                    controlFragment.tSensorOnClicked = false;
                }
                else if (controlFragment.tSensorOffClicked && !threadInUse) {
                    connectToServer("COMMAND, 9, OFF");
                    controlFragment.tSensorOffClicked = false;
                }
                else if (wifiFragment.wbClicked && !threadInUse) {
                    et1 = (EditText) findViewById(R.id.textInputW1);
                    et2 = (EditText) findViewById(R.id.textInputW2);
                    if (!et1.getText().toString().equals("") && !et2.getText().toString().equals("")) {
                        connectToServer("COMMAND, 1, " + et1.getText().toString() +
                                ", " + et2.getText().toString());
                    }
                    wifiFragment.wbClicked = false;
                }
            }
        }

        public void run()  {
            while (true) {
                processGUI();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
