package com.example.caos;

import java.util.ArrayList;
import java.util.Set;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.onurkaganaldemir.ktoastlib.KToast;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class DeviceListPrimary extends AppCompatActivity implements AdapterBluetooth.OnItemClickListener{
    // Debugging


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
                doDiscovery();
            }
        });

        mUploads = new ArrayList<>();
        // Find and set up the ListView for paired devices
        recyclerViewDevices =  findViewById(R.id.recyclerViewDevices);
        mAdapter = new AdapterBluetooth(this, mUploads);
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
        //Bluetooth Devices
        Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices();
        if (paireDevices.size() > 0) {

            if (mUploads != null && !mUploads.isEmpty())
                mUploads.clear();

            for (BluetoothDevice bDevice : paireDevices) {
                if(bDevice.getName().substring(0,5).equals("CAOS-")){
                    ModeloBluetooth modelo = new ModeloBluetooth();
                    modelo.setNameBluetooth(bDevice.getName());
                    modelo.setAddressBluetooth(bDevice.getAddress());
                    mUploads.add(modelo);
                }
            }
            if(mUploads.size() == 0){
//                msj("No hay dispositivos cercanos","warning");
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
//        this.unregisterReceiver(mReceiver);
        this.finish();
    }

    // Start device discover with the BluetoothAdapter
    public void doDiscovery() {
        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));

    }


    @Override
    public void onItemClick(int position) {
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();

        }
        final ModeloBluetooth uploadCurrent = mUploads.get(position);
        final String name = uploadCurrent.getNameBluetooth().toUpperCase();
//        msj(name + " - " + uploadCurrent.getAddressBluetooth(),"normal");
        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(BluetoothState.EXTRA_DEVICE_ADDRESS, uploadCurrent.getAddressBluetooth());
        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void checkPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        }else{
           msj("Activar permisos manualmente, Android desactualizado.","error");
        }
    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.d("NUEVO", "onReceive");
//            Log.d("NUEVO", context.toString());
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
//                setProgressBarIndeterminateVisibility(true);
//                String strSelectDevice = getIntent().getStringExtra("select_device");
//                if(strSelectDevice == null)
//                    strSelectDevice = "Select a device to connect";
//                setTitle(strSelectDevice);
//            }
//        }
//    };

}
