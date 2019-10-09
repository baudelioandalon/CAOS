package com.example.caos;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onurkaganaldemir.ktoastlib.KToast;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class DeviceListPrimary extends AppCompatActivity {
    // Debugging
    private static final String TAG = "BluetoothSPP";

    // Member fields

    private BluetoothAdapter mBluetoothAdapter;
    private AdapterBluetooth mAdapter;
    private ArrayList<ModeloBluetooth> mUploads;
    private Button scanButton;
    RecyclerView recyclerViewDevices;
    int REQUEST_CODE = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_primary);

        // Initialize the button to perform device discovery
        scanButton = findViewById(R.id.btnScan);

        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                msj("Iniciando escaneo...","normal");
                doDiscovery();
            }
        });

        mUploads = new ArrayList<>();
        // Find and set up the ListView for paired devices
        recyclerViewDevices =  findViewById(R.id.recyclerViewDevices);
        mAdapter = new AdapterBluetooth(this, mUploads);
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));



        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Bluetooth Devices
        Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices();
        if (paireDevices.size() > 0) {

            if (mUploads != null && !mUploads.isEmpty())
                mUploads.clear();

            for (BluetoothDevice bDevice : paireDevices) {
                ModeloBluetooth modelo = new ModeloBluetooth();
                modelo.setNameBluetooth(bDevice.getName());
                modelo.setAddressBluetooth(bDevice.getAddress());
                mUploads.add(modelo);
            }
            recyclerViewDevices.setAdapter(mAdapter);

        } else {
            msj("No se detectan dispositivos","warning");
        }
    }//fin onCreate

    public void msj (String mensaje,String type){
        if(type.equals("success")){
            KToast.successToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
        } else if(type.equals("info")){
            KToast.infoToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
        } else if(type.equals("normal")){
            KToast.normalToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.drawable.ic_arroba);
        }else if(type.equals("warning")) {
            KToast.warningToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
        }else if(type.equals("error")) {
            KToast.errorToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
        }else if(type.equals("customColor")) {
            KToast.customColorToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.color.colorPrimary, R.drawable.ic_arroba);
        }else if(type.equals("customBackground")) {
            KToast.customBackgroudToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.drawable.ic_arroba,
                    null ,R.drawable.ic_arroba);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        this.finish();
    }

    // Start device discover with the BluetoothAdapter
    private void doDiscovery() {
        if (!mBluetoothAdapter.isEnabled()) {//if bluetooth is turned off this will return true
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//ACTION_REQUEST_ENABLE is
            //bluetooth if bluetooth is turned off
            startActivityForResult(i, REQUEST_CODE);//this will call override methods -->onStartActivityForResult
        } else {//this block is executed if bluetooth is already turned on

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //ACTION_REQUEST_DISCOVERABLE means if bluetooth  makes visible tour device bluetooth
            // for 120 seconds
            //NOTE VISIBLE TIME DEPENDS UPON THE ANDROID VERSION YOU ARE USING
            startActivityForResult(i, REQUEST_CODE);

        }
        //this will start discovering new devices
        mBluetoothAdapter.startDiscovery();
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    ModeloBluetooth modelo = new ModeloBluetooth();
                    modelo.setNameBluetooth(device.getName());
                    modelo.setAddressBluetooth(device.getAddress());
                    mUploads.add(modelo);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);

                if (mUploads.size() == 0) {
                    msj("No se detectan dispositivos","warning");
                }
            }
        }
    };//fin BroadcastReceiver

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            if(mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();

            String strNoFound = getIntent().getStringExtra("no_devices_found");
            if(strNoFound == null)
                strNoFound = "No devices found";
            if(!((TextView) v).getText().toString().equals(strNoFound)) {
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length() - 17);

                // Create the result Intent and include the MAC address
                Intent intent = new Intent();
                intent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, address);

                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            // When discovery finds a device
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Get the BluetoothDevice object from the Intent
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                // If it's already paired, skip it, because it's been listed already
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    ModeloBluetooth modelo = new ModeloBluetooth();
//                    modelo.setNameBluetooth(device.getName());
//                    modelo.setAddressBluetooth(device.getAddress());
//                    mUploads.add(modelo);
//                }
//
//                // When discovery is finished, change the Activity title
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
//                String strSelectDevice = getIntent().getStringExtra("select_device");
//                if(strSelectDevice == null)
//                    strSelectDevice = "Select a device to connect";
//                setTitle(strSelectDevice);
//            }
//        }
//    };

}
