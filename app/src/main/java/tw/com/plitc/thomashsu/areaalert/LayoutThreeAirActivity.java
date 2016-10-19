package tw.com.plitc.thomashsu.areaalert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class LayoutThreeAirActivity extends Activity {
    private CircleProgressBar Air1_CPBar, Air2_CPBar, Air3_CPBar;
    private Button btn_Air1_unit, btn_Air2_unit, btn_Air3_unit,
            btn_Air1_limit, btn_Air2_limit, btn_Air3_limit, btn_Reset, btn_Mute;
    private TextView tv_Air1, tv_Air2, tv_Air3, tv_Air1_unit, tv_Air2_unit, tv_Air3_unit;
    private ImageView iv_logo;
    private static Boolean flash = true, Air1_mute = true, Air2_mute = true, Air3_mute = true;
    private int Shared_Air1_max, Shared_Air2_max, Shared_Air3_max, Shared_Air1_min, Shared_Air2_min, Shared_Air3_min;
    private static int runtime = 0, color_medicalAir = 0xfff3d408, color_Oxygen = 0xff58de5b, color_Vacuum = 0xff7F7676;
    private float Air1_float = 0, Air2_float = 0, Air3_float = 0;
    int play_sound = 0, sound_static = 0;
    int[] ReadData = {0, 0, 0, 0, 0, 0};
    double Air1_Data, Air2_Data, Air3_Data;
    DecimalFormat df = new DecimalFormat("0.00");
    private Boolean[] alert = new Boolean[3];
    static SharedPreferences shared;
    MediaPlayer mp;
    PL2303Driver mSerial;
    String TAG = "PL2303HXD_APLog";

    private static final String ACTION_USB_PERMISSION = "tw.com.plitc.thomashsu.areaalert.USB_PERMISSION";
    private static final boolean SHOW_DEBUG = true;

    // BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
    private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B115200;
    private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
    private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
    private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
    private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_threepressure);
        SerialPort();
        FindViewById();
        SetOnclickListener();
        readshared();

    }

    private void FindViewById() {
        Air1_CPBar = (CircleProgressBar) findViewById(R.id.AIR1_CPBar);
        Air2_CPBar = (CircleProgressBar) findViewById(R.id.AIR2_CPBar);
        Air3_CPBar = (CircleProgressBar) findViewById(R.id.AIR3_CPBar);
        btn_Air1_unit = (Button) findViewById(R.id.btn_AIR1_unit);
        btn_Air2_unit = (Button) findViewById(R.id.btn_AIR2_unit);
        btn_Air3_unit = (Button) findViewById(R.id.btn_AIR3_unit);
        btn_Air1_limit = (Button) findViewById(R.id.btn_AIR1_limit);
        btn_Air2_limit = (Button) findViewById(R.id.btn_AIR2_limit);
        btn_Air3_limit = (Button) findViewById(R.id.btn_AIR3_limit);
        btn_Reset = (Button) findViewById(R.id.btn_Reset);
        btn_Mute = (Button) findViewById(R.id.btn_Mute);
        tv_Air1 = (TextView) findViewById(R.id.tv_AIR1);
        tv_Air2 = (TextView) findViewById(R.id.tv_AIR2);
        tv_Air3 = (TextView) findViewById(R.id.tv_AIR3);
        tv_Air1_unit = (TextView) findViewById(R.id.tv_AIR1_unit);
        tv_Air2_unit = (TextView) findViewById(R.id.tv_AIR2_unit);
        tv_Air3_unit = (TextView) findViewById(R.id.tv_AIR3_unit);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
    }

    private void SetOnclickListener() {
        btn_Air1_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Air1_float = Float.parseFloat(tv_Air1.getText().toString());
                Air1_float = OtherMethod.change_Unit(tv_Air1_unit.getText().toString(), Air1_float, true, true);
                tv_Air1.setText(df.format(OtherMethod.change_Unit(tv_Air1_unit.getText().toString(), Air1_float, false, true)));
                if (tv_Air1_unit.getText().equals("Bar"))
                    tv_Air1_unit.setText("Kg");
                else if (tv_Air1_unit.getText().equals("Kg"))
                    tv_Air1_unit.setText("Kpa");
                else if (tv_Air1_unit.getText().equals("Kpa"))
                    tv_Air1_unit.setText("Psi");
                else {
                    tv_Air1_unit.setText("Bar");
                }
                shared.edit().putString("Air1_unit", tv_Air1_unit.getText().toString()).commit();
                int color = OtherMethod.detectLimit(Air1_Data, tv_Air1_unit.getText().toString(), Shared_Air1_max, Shared_Air1_min, true, flash);
                tv_Air1.setTextColor(color);
            }
        });
        btn_Air2_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Air2_float = Float.parseFloat(tv_Air2.getText().toString());
                Air2_float = OtherMethod.change_Unit(tv_Air2_unit.getText().toString(), Air2_float, true, true);
                tv_Air2.setText(df.format(OtherMethod.change_Unit(tv_Air2_unit.getText().toString(), Air2_float, false, true)));
                if (tv_Air2_unit.getText().equals("Bar"))
                    tv_Air2_unit.setText("Kg");
                else if (tv_Air2_unit.getText().equals("Kg"))
                    tv_Air2_unit.setText("Kpa");
                else if (tv_Air2_unit.getText().equals("Kpa"))
                    tv_Air2_unit.setText("Psi");
                else {
                    tv_Air2_unit.setText("Bar");
                }
                shared.edit().putString("Air3_unit", tv_Air2_unit.getText().toString()).commit();
                int color = OtherMethod.detectLimit(Air2_Data, tv_Air2_unit.getText().toString(), Shared_Air2_max, Shared_Air2_min, true, flash);
                tv_Air2.setTextColor(color);
            }
        });
        btn_Air3_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Air3_float = Float.parseFloat(tv_Air3.getText().toString());
                Air3_float = OtherMethod.change_Unit(tv_Air3_unit.getText().toString(), Air3_float, true, false);
                tv_Air3.setText(df.format(OtherMethod.change_Unit(tv_Air3_unit.getText().toString(), Air3_float, false, false)));
                if (tv_Air3_unit.getText().equals("mmHg"))
                    tv_Air3_unit.setText("Kpa");
                else if (tv_Air3_unit.getText().equals("inHg"))
                    tv_Air3_unit.setText("mmHg");
                else if (tv_Air3_unit.getText().equals("Kpa"))
                    tv_Air3_unit.setText("inHg");
                shared.edit().putString("Air5_unit", tv_Air3_unit.getText().toString()).commit();
                int color = OtherMethod.detectLimit(Air3_Data, tv_Air3_unit.getText().toString(), Shared_Air3_max, Shared_Air3_min, false, flash);
                tv_Air3.setTextColor(color);
            }
        });
        btn_Air1_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readshared();
                GreatDialog.GreatPassword(LayoutThreeAirActivity.this, Shared_Air1_max, Shared_Air1_min, "Shared_Air1_max", "Shared_Air1_min");
            }
        });
        btn_Air2_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readshared();
                GreatDialog.GreatPassword(LayoutThreeAirActivity.this, Shared_Air2_max, Shared_Air2_min, "Shared_Air3_max", "Shared_Air3_min");
            }
        });
        btn_Air3_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readshared();
                GreatDialog.GreatPassword(LayoutThreeAirActivity.this, Shared_Air3_max, Shared_Air3_min, "Shared_Air5_max", "Shared_Air5_min");
            }
        });
        btn_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OtherMethod.restart(LayoutThreeAirActivity.this, 500, "timer");
            }
        });
        btn_Mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Air1_mute = false;
                Air2_mute = false;
                Air3_mute = false;
                mp = OtherMethod.stopPlaying(mp);
                play_sound = 0;
            }
        });
    }

    private void readshared() {
        shared = this.getSharedPreferences("AreaAlert_DATA", MODE_PRIVATE);
        Shared_Air1_max = shared.getInt("Shared_Air1_max", Shared_Air1_max);
        Shared_Air1_min = shared.getInt("Shared_Air1_min", Shared_Air1_min);
        Shared_Air2_max = shared.getInt("Shared_Air3_max", Shared_Air2_max);
        Shared_Air2_min = shared.getInt("Shared_Air3_min", Shared_Air2_min);
        Shared_Air3_max = shared.getInt("Shared_Air5_max", Shared_Air3_max);
        Shared_Air3_min = shared.getInt("Shared_Air5_min", Shared_Air3_min);
        if (!shared.getString("Air1_unit", "Psi").equalsIgnoreCase(""))
            tv_Air1_unit.setText(shared.getString("Air1_unit", "Psi"));
        if (!shared.getString("Air3_unit", "Psi").equalsIgnoreCase(""))
            tv_Air2_unit.setText(shared.getString("Air3_unit", "Psi"));
        if (!shared.getString("Air5_unit", "mmHg").equalsIgnoreCase(""))
            tv_Air3_unit.setText(shared.getString("Air5_unit", "mmHg"));
    }

    private void SerialPort() {
        // get service
        mSerial = new PL2303Driver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);

        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported()) {

            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "No Support USB host API");
            mSerial = null;

        }

        Log.d(TAG, "Leave onCreate");

        int res = 0;
        try {
            res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res < 0) {
            Log.d(TAG, "fail to setup");
            return;
        }
    }

    protected void onStop() {
        Log.d(TAG, "Enter onStop");
        super.onStop();
        if (mSerial != null) {
            mSerial.end();
            mSerial = null;
        }
        this.finish();
        Log.d(TAG, "Leave onStop");
    }

    @Override
    public void onBackPressed() {
        if (mSerial != null) {
            mSerial.end();
            mSerial = null;
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Enter onDestroy");
        if (mSerial != null) {
            mSerial.end();
            mSerial = null;
        }
        super.onDestroy();
        Log.d(TAG, "Leave onDestroy");
    }

    public void onStart() {
        Log.d(TAG, "Enter onStart");
        super.onStart();
        Log.d(TAG, "Leave onStart");
    }

    //
    public void onResume() {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action = getIntent().getAction();
        Log.d(TAG, "onResume:" + action);
        if (!mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "New instance : " + mSerial);
            }

            if (!mSerial.enumerate()) {

                Toast.makeText(this, "no more devices found",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d(TAG, "onResume:enumerate succeeded!");

            }
        }// if isConnected
        Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Leave onResume");
        openUsbSerial();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Timer timer01 = new Timer();
                timer01.schedule(task, 0, 300);
                Timer timer02 = new Timer();
                timer02.schedule(task2, 0, 1800000);
            }
        }, 1000);

    }

    private TimerTask task = new TimerTask() {

        @Override
        public void run() {
            Message message = new Message();
            message.what = 3;
            handler1.sendMessage(message);
        }

    };
    private TimerTask task2 = new TimerTask() {

        @Override
        public void run() {
            runtime++;
            if (runtime > 48) { // 24h=30(m)*48
                OtherMethod.restart(LayoutThreeAirActivity.this, 500, "timer");
            }
            OtherMethod.NowTime = Calendar.getInstance();
            OtherMethod.time = String.format("%02d:%02d:%02d", OtherMethod.NowTime.get(Calendar.HOUR_OF_DAY), OtherMethod.NowTime.get(Calendar.MINUTE),
                    OtherMethod.NowTime.get(Calendar.SECOND));
            OtherMethod.days = String.format("%04d-%02d-%02d", OtherMethod.NowTime.get(Calendar.YEAR), OtherMethod.NowTime.get(Calendar.MONTH) + 1,
                    OtherMethod.NowTime.get(Calendar.DAY_OF_MONTH));
            try {
                OtherMethod.FWriter = new FileWriter("/sdcard/" + OtherMethod.days + ".txt", true);
                OtherMethod.bw = new BufferedWriter(OtherMethod.FWriter);
                OtherMethod.bw.write(OtherMethod.days + "_" + OtherMethod.time + "  s1:"
                        + tv_Air1.getText().toString()
                        + tv_Air1_unit.getText().toString() + "  s2:"
                        + tv_Air2.getText().toString()
                        + tv_Air2_unit.getText().toString() + "  s3:"
                        + tv_Air3.getText().toString()
                        + tv_Air3_unit.getText().toString());
                OtherMethod.bw.newLine();
                OtherMethod.bw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    };

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int color = 0;
            if (flash) {
                flash = false;
            } else {
                flash = true;
            }
            if (play_sound > 0) {
                mp = OtherMethod.playMedia(mp, play_sound, sound_static, LayoutThreeAirActivity.this);
            }

            ReadData = SerialRxTx.readDataFromSerial(mSerial, LayoutThreeAirActivity.this);
//            Air1_Data = (25 * (((double) ReadData[0] / 1000) - 0.5) / 4) * OtherMethod.BartoPsi;
//            Air2_Data = (25 * (((double) ReadData[2] / 1000) - 0.5) / 4) * OtherMethod.BartoPsi;
//            Air3_Data = OtherMethod.BartommHg + ((((((double) ReadData[4] / 1000) - 0.5) / 4) - 1) * OtherMethod.BartommHg);
            Air1_Data = ((ReadData[0] - 700) * 0.075);
            Air2_Data = ((ReadData[2] - 700) * 0.075);
            Air3_Data = ((ReadData[4] - 700) * 0.075 * OtherMethod.psitommHg);

//            Toast.makeText(LayoutThreeAirActivity.this,String.valueOf(ReadData[0]) + String.valueOf(ReadData[2])+String.valueOf(ReadData[4]),Toast.LENGTH_SHORT).show();
            readshared();
            color = OtherMethod.detectLimit(Air1_Data, tv_Air1_unit.getText().toString(), Shared_Air1_max, Shared_Air1_min, true, flash);
            tv_Air1.setText(OtherMethod.change_Unit_mV(tv_Air1_unit.getText().toString(), Air1_Data, false, true));
            tv_Air1.setTextColor(color);
            Air1_CPBar.setProgress(Float.parseFloat(tv_Air1.getText().toString()), color_Oxygen, OtherMethod.CPBarMax(300, tv_Air1_unit.getText().toString(), true));
            if (color == OtherMethod.color_red) {
                if (Air1_mute == true) {
                    play_sound = 1;
                }
            } else if (color == OtherMethod.color_orange) {
                if (Air1_mute == true) {
                    play_sound = 2;
                }
            } else if (color == OtherMethod.color_green) {
                Air1_mute = true;
            }
            color = OtherMethod.detectLimit(Air2_Data, tv_Air2_unit.getText().toString(), Shared_Air2_max, Shared_Air2_min, true, flash);
            tv_Air2.setText(OtherMethod.change_Unit_mV(tv_Air2_unit.getText().toString(), Air2_Data, false, true));
            tv_Air2.setTextColor(color);
            Air2_CPBar.setProgress(Float.parseFloat(tv_Air2.getText().toString()), color_medicalAir, OtherMethod.CPBarMax(300, tv_Air2_unit.getText().toString(), true));
            if (color == OtherMethod.color_red) {
                if (Air2_mute == true) {
                    play_sound = 1;
                }
            } else if (color == OtherMethod.color_orange) {
                if (Air2_mute == true) {
                    play_sound = 2;
                }
            } else if (color == OtherMethod.color_green) {
                Air2_mute = true;
            }
            color = OtherMethod.detectLimit(Air3_Data, tv_Air3_unit.getText().toString(), Shared_Air3_max, Shared_Air3_min, false, flash);

            tv_Air3.setText(df.format(Float.parseFloat(OtherMethod.change_Unit_mV(tv_Air3_unit.getText().toString(), Air3_Data, false, false))));
            tv_Air3.setTextColor(color);
            Air3_CPBar.setProgress(Float.parseFloat(tv_Air3.getText().toString()), color_Vacuum, OtherMethod.CPBarMax(760, tv_Air3_unit.getText().toString(), false));
            if (color == OtherMethod.color_red) {
                if (Air3_mute == true) {
                    play_sound = 1;
                }
            } else if (color == OtherMethod.color_orange) {
                if (Air3_mute == true) {
                    play_sound = 2;
                }
            } else if (color == OtherMethod.color_green) {
                Air3_mute = true;
            }

            if (tv_Air1.getCurrentTextColor() == OtherMethod.color_green && tv_Air2.getCurrentTextColor() == OtherMethod.color_green && tv_Air3.getCurrentTextColor() == OtherMethod.color_green && true) {
                play_sound = 0;
                mp = OtherMethod.stopPlaying(mp);
            }

        }
    };


    private void openUsbSerial() {
        Log.d(TAG, "Enter  openUsbSerial");
        if (null == mSerial)
            return;

        if (mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "openUsbSerial : isConnected ");
            }
            if (!mSerial.InitByBaudRate(mBaudrate, 700)) {
                if (!mSerial.PL2303Device_IsHasPermission()) {
                    Toast.makeText(this, "cannot open, maybe no permission",
                            Toast.LENGTH_SHORT).show();
                }

                if (mSerial.PL2303Device_IsHasPermission()
                        && (!mSerial.PL2303Device_IsSupportChip())) {
                    Toast.makeText(
                            this,
                            "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
            }
        }// isConnected
        else {
            OtherMethod.restart(LayoutThreeAirActivity.this, 500, "openUsbSerial");
        }
        Log.d(TAG, "Leave openUsbSerial");
    }// openUsbSerial


}