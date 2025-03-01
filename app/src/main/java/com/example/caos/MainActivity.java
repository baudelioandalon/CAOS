package com.example.caos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caos.utils.croller.Croller;
import com.example.caos.utils.croller.OnCrollerChangeListener;
import com.example.caos.utils.joystick.Joystick;
import com.example.caos.utils.joystick.JoystickListener;
import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.onurkaganaldemir.ktoastlib.KToast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {

    private static final int STICK_UP = 1;
    private static final int STICK_UPRIGHT = 2;
    private static final int STICK_RIGHT = 3;
    private static final int STICK_DOWNRIGHT = 4;
    private static final int STICK_DOWN = 5;
    private static final int STICK_DOWNLEFT = 6;
    private static final int STICK_LEFT = 7;
    private static final int STICK_UPLEFT = 8;

    private View mDecorView;
    BluetoothSPP bt;
    Context context;
    Boolean btConnect = false;
    Button btnLeft,  btnRed, btnGreen, btnBlue;
    //    Button btnRight;
    TextView txtValue,txtmensaje;
    TextView colorSeleccionado;
    Joystick joystickLeft;
    SharedPreferences prefs;
    VideoView videoView;
    String colorled ="R";
    String varInicio = "*";
    String varFinal = "#";
    int contador=30;
    boolean acelerador = false;
    boolean frontBack = true;
    boolean inverter = false;
    String valorDireccion ="000000";
    RelativeLayout container;
    ArcSeekBar seekArc;
    Croller croller;
    String datos ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.windowBackground));
        btnRed = findViewById(R.id.btnRed);
        btnGreen = findViewById(R.id.btnGreen);
        btnBlue = findViewById(R.id.btnBlue);
        joystickLeft =  findViewById(R.id.joystickLeft);
        container = findViewById(R.id.container);
        seekArc = findViewById(R.id.seekArc);
        croller = findViewById(R.id.croller);
        txtmensaje = findViewById(R.id.txtmensaje);

        AnimationDrawable animDrawable = (AnimationDrawable) container.getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
//        joystickRight =  findViewById(R.id.joystickRightt);
        //        btnRight = findViewById(R.id.btnRight);
//        velocimeter = findViewById(R.id.velocimeter);
        btnLeft = findViewById(R.id.btnLeft);

        colorSeleccionado = findViewById(R.id.colorSeleccionado);
        // setup bluetooth
        bt = new BluetoothSPP(context);
        checkBluetoothState();

        croller.setOnCrollerChangeListener(new OnCrollerChangeListener() {
            @Override
            public void onProgressChanged(Croller croller, int progress) {
                seekArc.setProgress(progress);
                acelerador = true;
                // use the progress
            }

            @Override
            public void onStartTrackingTouch(Croller croller) {
                // tracking started
            }

            @Override
            public void onStopTrackingTouch(Croller croller) {
                croller.setProgress(0);
                seekArc.setProgress(0);
                txtValue.setText("Velocidad: 0 RPM");
                acelerador = false;
                sendBluetoothData("000000");
            }
        });


        //ELECCION DE COLOR DE LED
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorled = "G";
                sendBluetoothData("000000");
                colorSeleccionado.setText("Rojo");
                colorSeleccionado.setTextColor(getResources().getColor(R.color.red));
            }
        });

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorled = "R";
                sendBluetoothData("000000");
                colorSeleccionado.setText("Verde");
                colorSeleccionado.setTextColor(getResources().getColor(R.color.green));
            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorled = "B";
                sendBluetoothData("000000");
                colorSeleccionado.setText("Azul");
                colorSeleccionado.setTextColor(getResources().getColor(R.color.blue));
            }
        });


        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                btConnect = true;
                msj("Todo listo","success");
                exe();
            }

            public void onDeviceDisconnected() {
                btConnect = false;
                msj("Se perdio la conexion","error");
            }

            public void onDeviceConnectionFailed() {
                btConnect = false;
                msj("No conectado, verifique el equipo este encendido.","error");
            }

        });
        setup();
        // set view
        mDecorView = getWindow().getDecorView();
        hideSystemUI();


    }

  public void exe(){
      final Handler handler = new Handler();
      final Runnable r = new Runnable() {
          public void run() {

              if(contador>0){
                  contador--;
                  String valor = contador + " segundos.";
                  txtmensaje.setText(valor);
                  exe();
              }else if(contador == 0){
                  inverter = !inverter;
                  contador = 30;
                  frontBack = !frontBack;
                  sendBluetoothData(datos);
                  exe();
                  msj("Los controles han cambiado","warning");
              }
          }
      };
      handler.postDelayed(r, 1000);
  }



    private void setup() {
        // set view
        txtValue =  findViewById(R.id.value);
        // setup motion constrain for joystick right
        joystickLeft.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
//                buttonDownLeft = true;
            }

            @Override
            public void onDrag(float degrees, float offset) {

                int direction = get8Direction(degrees);

                if (direction == STICK_UP  && acelerador) {

                    if(inverter){
                        frontBack = false;
                    }else{
                        frontBack = true;
                    }
                    datos = potenciaConvert(seekArc.getProgress())+potenciaConvert(seekArc.getProgress());
                    sendBluetoothData(datos);

                } else if (direction == STICK_UPRIGHT && acelerador) {
                    if(inverter){
                        frontBack = false;
                    }else{
                        frontBack = true;
                    }
                    datos = "030"+ potenciaConvert(seekArc.getProgress());

                    sendBluetoothData(datos);

                } else if (direction == STICK_RIGHT && acelerador) {

                if(inverter){
                    frontBack = false;
                }else{
                    frontBack = true;
                }
                datos = potenciaConvert(seekArc.getProgress()) +"000";
                    sendBluetoothData(datos);

                } else if (direction == STICK_DOWNRIGHT && acelerador) {


                    if(inverter){
                        frontBack = true;
                    }else{
                        frontBack = false;
                    }
                    datos = potenciaConvert(seekArc.getProgress())+"030";
                    sendBluetoothData(datos);

                } else if (direction == STICK_DOWN && acelerador) {
                    if(inverter){
                        frontBack = true;
                    }else{
                        frontBack = false;
                    }
                    datos = potenciaConvert(seekArc.getProgress())+potenciaConvert(seekArc.getProgress());
                    sendBluetoothData(datos);

                } else if ( direction == STICK_DOWNLEFT && acelerador ) {
                    frontBack = false;
                    datos = "030"+ potenciaConvert(seekArc.getProgress());
                    sendBluetoothData(datos);

                } else if( direction == STICK_LEFT && acelerador){
                    frontBack = true;
                    datos = "000"+ potenciaConvert(seekArc.getProgress());
                    sendBluetoothData(datos);
                }else if(direction == STICK_UPLEFT && acelerador){
                    frontBack = true;
                    datos = potenciaConvert(seekArc.getProgress())+"030";
                    sendBluetoothData(datos);

                }
                else if ( direction == STICK_LEFT  && !acelerador|| direction == STICK_UPLEFT  && !acelerador|| direction == STICK_DOWNLEFT && !acelerador) {
                    frontBack = true;
                    txtValue.setText(potenciaConvert(distanceConvert(offset)) + " RPM");
                    datos = "000"+ distanceConvert(offset);
                    sendBluetoothData(datos);

                }else if ( direction == STICK_RIGHT  && !acelerador|| direction == STICK_UPRIGHT  && !acelerador|| direction == STICK_DOWNRIGHT&& !acelerador) {
                    frontBack = true;
                    txtValue.setText(potenciaConvert(distanceConvert(offset)) + " RPM");
                    datos = distanceConvert(offset)+"000";
                    sendBluetoothData(datos);

                } else{
                    sendBluetoothData("000000");
                }
            }

            @Override
            public void onUp() {

            }
        });

    }

    private void checkBluetoothState() {
        if (bt.isBluetoothEnabled()) {
            if (this.btConnect) {
                bt.disconnect();
            }
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);
            // load device list
            Intent intent = new Intent(getApplicationContext(), DeviceListPrimary.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
            setup();
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Intent intent = new Intent(getApplicationContext(), DeviceListPrimary.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // setup bluetooth
        if (!bt.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothState.REQUEST_ENABLE_BT);
        }
        // setup joystick mode after restart
//        setupJoystickMode();
//
//        setupVideoMode();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }


    public void sendBluetoothData(final String data) {

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                String direccion;

                if(frontBack){
                    direccion ="1";
                }else{
                    direccion = "0";
                }

                // *1 R 000 000 000 #

                bt.send(varInicio+direccion + colorled+data+ varFinal,true);

            }
        };
        handler.postDelayed(r, 0);
    }

    public int get8Direction(float degrees) {
        float angle = angleConvert(degrees);

        if (angle >= 66 && angle < 109) {
            return STICK_UP;
        } else if (angle >= 23 && angle < 66) {
            return STICK_UPRIGHT;
        } else if (angle >= 338 || angle < 23) {
            return STICK_RIGHT;
        } else if (angle >= 281 && angle < 337) {
            return STICK_DOWNRIGHT;
        } else if (angle >= 238 && angle < 281) {
            return STICK_DOWN;
        } else if (angle >= 195 && angle < 238) {
            return STICK_DOWNLEFT;
        } else if (angle >= 152 && angle < 195) {
            return STICK_LEFT;
        } else if (angle >= 109 && angle < 152) {
            return STICK_UPLEFT;
        }

        return 0;
    }

    public int angleConvert(float degrees) {
        int angle = 0;
        if ((int) degrees < 0) angle = (360 + (int) degrees);
        else angle = (int) degrees;
        return angle;
    }

    public int distanceConvert(float offset) {
        int pwm = (int) (offset * 255);

        return (pwm);
    }

    public String potenciaConvert(int dato){
        String finalDato;

        if(dato == 0) {
            finalDato = "000";
        }else if(dato>0 && dato<10){
            finalDato = "00"+dato;
        }else if(dato>9 && dato <100){
            finalDato = "0"+dato;
        }else{
            finalDato = String.valueOf(dato);
        }

        return finalDato;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
            hideSystemUI();
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }


//    private String joystickData(String key, float offset) {
//        final boolean pwm = prefs.getBoolean("pref_send_pwm_switch", false);
//        String data = "";
//        if ((pwm == false) && (distanceConvert(offset) >= 100)) {
//            // start
//            if (prefs.getBoolean("pref_token_switch", true) == true) {
//                data = data + prefs.getString("pref_before_token", "");
//            }
//            // data
//            data = data + prefs.getString(key, "");
//            // pwm
//            if (prefs.getBoolean("pref_send_pwm_switch", true) == true) {
//                data = data + prefs.getString("pref_pwm_separator", "");
//                data = data + distanceConvert(offset);
//            }
//            // stop
//            if (prefs.getBoolean("pref_token_switch", true) == true) {
//                data = data + prefs.getString("pref_end_token", "");
//            }
//        } else if (pwm == true) {
//            // start
//            if (prefs.getBoolean("pref_token_switch", true) == true) {
//                data = data + prefs.getString("pref_before_token", "");
//            }
//            // data
//            data = data + prefs.getString(key, "");
//            // pwm
//            if (prefs.getBoolean("pref_send_pwm_switch", true) == true) {
//                data = data + prefs.getString("pref_pwm_separator", "");
//                data = data + distanceConvert(offset);
//                msj(data,"error");
//            }
//            // stop
//            if (prefs.getBoolean("pref_token_switch", true) == true) {
//                data = data + prefs.getString("pref_end_token", "");
//            }
//        }
//        return data;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//
//        if (itemId == R.id.mnuBluetooth) {
//            checkBluetoothState();
//        } else if (itemId == R.id.mnuSetting) {
//            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
//            startActivityForResult(i, RESULT_SETTING);
//        } else if (itemId ==R.id.mnuFullscreen) {
//            hideSystemUI();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
//    private void showSystemUI() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mDecorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//    }

//    private void setupJoystickMode() {
//
//        if (prefs.getBoolean("pref_left_switch",true)==false){
//            joystickLeft.setVisibility(View.INVISIBLE);
//        }else {
//            joystickLeft.setVisibility(View.VISIBLE);
//        }
//
//        if (prefs.getBoolean("pref_right_switch",true)==false){
//            joystickRight.setVisibility(View.INVISIBLE);
//        }else {
//            joystickRight.setVisibility(View.VISIBLE);
//        }
//
//        // setup motion constrain for joystick left
//        if (prefs.getBoolean("pref_constrain_left_switch", false) == false) {
//            Log.d("LOG-JOY", "constrain normal");
//            joystickLeft.setMotionConstraint(Joystick.MotionConstraint.NONE);
//        } else if (((prefs.getBoolean("pref_constrain_left_hor", true)) == true) && ((prefs.getBoolean("pref_constrain_left_ver", true)) == false)) {
//            Log.d("LOG-JOY", "constrain hor");
//            joystickLeft.setMotionConstraint(Joystick.MotionConstraint.HORIZONTAL);
//        } else if (((prefs.getBoolean("pref_constrain_left_hor", true)) == false) && ((prefs.getBoolean("pref_constrain_left_ver", true)) == true)) {
//            Log.d("LOG-JOY", "constrain ver");
//            joystickLeft.setMotionConstraint(Joystick.MotionConstraint.VERTICAL);
//        } else {
//            joystickLeft.setMotionConstraint(Joystick.MotionConstraint.NONE);
//        }
//
//        // setup motion constrain for joystick right
//        if (prefs.getBoolean("pref_constrain_right_switch", false) == false) {
//            Log.d("LOG-JOY", "constrain normal");
//            joystickRight.setMotionConstraint(Joystick.MotionConstraint.NONE);
//        } else if (((prefs.getBoolean("pref_constrain_right_hor", true)) == true) &&
//                ((prefs.getBoolean("pref_constrain_right_ver", true)) == false)) {
//            Log.d("LOG-JOY", "constrain hor");
//            joystickRight.setMotionConstraint(Joystick.MotionConstraint.HORIZONTAL);
//        } else if (((prefs.getBoolean("pref_constrain_right_hor", true)) == false) &&
//                ((prefs.getBoolean("pref_constrain_right_ver", true)) == true)) {
//            Log.d("LOG-JOY", "constrain ver");
//            joystickRight.setMotionConstraint(Joystick.MotionConstraint.VERTICAL);
//        } else {
//            joystickRight.setMotionConstraint(Joystick.MotionConstraint.NONE);
//        }
//    }
//
//    private void setupVideoMode() {
//        // setup motion constrain for joystick left
//        if (prefs.getBoolean("pref_send_video_switch", false) == true) {
//            // start video
//            try {
//                videoView = (VideoView) findViewById(R.id.videoView);
//                videoView.setVisibility(View.VISIBLE);
//                Uri videoUri = Uri.parse(prefs.getString("pref_video", ""));
//                videoView.setVideoURI(videoUri);
//                videoView.start();
//            } catch (Exception e) {
//                Log.d("LOG", e.getMessage());
//            }
//        } else {
//            videoView = (VideoView) findViewById(R.id.videoView);
//            videoView.setVisibility(View.INVISIBLE);
//        }
//    }


}
