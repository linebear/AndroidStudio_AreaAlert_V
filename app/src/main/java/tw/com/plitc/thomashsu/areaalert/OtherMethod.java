package tw.com.plitc.thomashsu.areaalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by ThomasHsu on 2016/8/22.
 */

public class OtherMethod {
    static FileWriter FWriter;
    static BufferedWriter bw;
    static Calendar NowTime;
    static String time="",days="";

    public static int color_red = 0xffd80000,color_orange = 0xffff9c00,color_green = 0xff00d312,color_gray = 0xffE8E8E8;
    final static float mVtoPsi = (float) 0.075, BartoKg = (float) 1.01972,
            KgtoPsi = (float) 14.2231, PsitoBar = (float) 0.06895,
            PsitoKg = (float) 0.07031, KgtoKpa = (float) 98.0665,
            PsitoKpa = (float) 6.89475729, KpatoPsi = (float) 0.145037738,
            mmHgtoinHg = (float) 0.0393702, inHgtommHg = (float) 25.3999,
            psitommHg = (float) 51.71484, KpatoinHg = (float) 0.29529988,
            mmHgtoKpa = (float) 0.13332237,BartoPsi = (float) 14.5037738,BartommHg = (float) 750.061683;

    public static void  restart(Context context, int delay, String where) {
        NowTime = Calendar.getInstance();
       try {
            time = String.format("%02d:%02d:%02d", NowTime.get(Calendar.HOUR_OF_DAY), NowTime.get(Calendar.MINUTE),
                    NowTime.get(Calendar.SECOND));
            days = String.format("%04d-%02d-%02d", NowTime.get(Calendar.YEAR), NowTime.get(Calendar.MONTH) + 1,
                    NowTime.get(Calendar.DAY_OF_MONTH));
            FWriter = new FileWriter("/sdcard/" + days + ".txt", true);
            bw = new BufferedWriter(FWriter);
            String S = days + "_" + time + "-----ReStart_" + where + "_-----";
            bw.write(S);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("", "restarting app");
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay,
                intent);
        System.exit(0);
    }

    public static String change_Unit_mV(String Unit_name, double Unit_amount_mV,
                                  Boolean record, Boolean High_Air) {
//        double readdata =Unit_amount_mV;
//        readdata = (float) ((Unit_amount_mV - mVvariation) * mVtoPsi); // mV
        // to
        String Unit = ""; // psi

        if (Unit_name.equals("Bar")) {
            Unit_amount_mV *= PsitoBar;
        }
        if (Unit_name.equals("Kg")) {
            Unit_amount_mV *= PsitoKg;
        }
        if (Unit_name.equals("Psi")) {
            Unit_amount_mV *= 1;
        }
        if (Unit_name.equals("inHg")) {
            Unit_amount_mV *= mmHgtoinHg;
        }
        if (Unit_name.equals("mmHg")) {
            Unit_amount_mV *= 1;
        }
        if (Unit_name.equals("Kpa") && High_Air == true) {
            Unit_amount_mV *= PsitoKpa;
        }
        if (Unit_name.equals("Kpa") && High_Air == false) {

            Unit_amount_mV *= mmHgtoKpa;
        }
        if (record) {
            DecimalFormat df = new DecimalFormat("0.0000000");
            Unit = df.format(Unit_amount_mV);
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            Unit = df.format(Unit_amount_mV);
        }
        return Unit;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^-?[0-9]+");
        return pattern.matcher(str).matches();
    }

    public static float CPBarMax(float Max,String Unit_name,boolean High_Air){
        if (Unit_name.equals("Bar")) {
            Max *= PsitoBar;
        }
        if (Unit_name.equals("Kg")) {
            Max *= PsitoKg;
        }
        if (Unit_name.equals("Psi")) {
            Max *= 1;
        }
        if (Unit_name.equals("inHg")) {
            Max *= mmHgtoinHg;
        }
        if (Unit_name.equals("mmHg")) {
            Max *= 1;
        }
        if (Unit_name.equals("Kpa") && High_Air == true) {
            Max *= PsitoKpa;
        }
        if (Unit_name.equals("Kpa") && High_Air == false) {

            Max *= mmHgtoKpa;
        }
        return Max;
    }
    public static MediaPlayer playMedia(MediaPlayer mp,int play_sound,int sound_static,Context Activity) {
        if (mp == null) {
            mp = new MediaPlayer();
        }
        if (play_sound == 1) {
            try {
                // mp.setDataSource("file:///android_res/raw/alert_highpitch.wav");
                if (!mp.isPlaying()) {
                    mp = MediaPlayer.create(Activity,
                            R.raw.alert_highpitch);
                    mp.setLooping(true);
                    mp.start();
                    sound_static = play_sound;
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (play_sound == 2) {
            try {
                // mp.setDataSource("file:///android_res/raw/alert_lowpitch.wav");
                if (!mp.isPlaying()) {
                    mp = MediaPlayer.create(Activity,
                            R.raw.alert_lowpitch);
                    mp.setLooping(true);
                    mp.start();
                    sound_static = play_sound;
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (play_sound != sound_static && mp == null) {
            stopPlaying(mp);
        }
        
        return mp;
    }

    public static MediaPlayer stopPlaying(MediaPlayer mp) {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        return mp;
    }

    public static float change_Unit(String Unit_name, float Unit_amount,
                               Boolean record, Boolean High_Air) {
        DecimalFormat df;
        if (record) {
            if (Unit_name.equals("Bar")) {
                Unit_amount *= BartoKg;
            } else if (Unit_name.equals("Kg")) {
                Unit_amount *= KgtoKpa;
            } else if (Unit_name.equals("Psi")) {
                Unit_amount *= PsitoBar;
            } else if (Unit_name.equals("mmHg")) {
                Unit_amount *= mmHgtoKpa;
            } else if (Unit_name.equals("inHg")) {
                Unit_amount *= inHgtommHg;
            } else if (Unit_name.equals("Kpa") && High_Air == true) {
                Unit_amount *= KpatoPsi;
            } else if (Unit_name.equals("Kpa") && High_Air == false) {
                Unit_amount *= KpatoinHg;
            }
            df = new DecimalFormat("0.0000000");
        } else {
            df = new DecimalFormat("0.00");
        }
        String Unit = df.format(Unit_amount);

        // String Unit = String.valueOf(Unit_amount);
        return Unit_amount;
    }

    public static int detectLimit(double Air_pressure,String Air_unit,int Air_limit_max,int Air_limit_min,boolean High_Air,boolean flash){
        int color = 0;
        if (Air_unit.equals("Bar")) {
            if (Air_pressure * PsitoBar > Air_limit_max * PsitoBar) {
                if (flash) {
                    color = color_red;
                } else {
                    color = color_gray;
                }

            } else if (Air_pressure * PsitoBar < Air_limit_min * PsitoBar) {
                if (flash) {
                    color = color_orange;
                } else {
                    color = color_gray;
                }

            } else {
                color = color_green;
            }
        }
        if (Air_unit.equals("Kg")) {
            if (Air_pressure * PsitoKg > (Air_limit_max * PsitoKg)) {
                if (flash) {
                    color = color_red;
                } else {
                    color = color_gray;
                }

            } else if (Air_pressure * PsitoKg < Air_limit_min * PsitoKg) {
                if (flash) {
                    color = color_orange;
                } else {
                    color = color_gray;
                }

            } else {
                color = color_green;
            }
        }
        if (Air_unit.equals("Kpa")) {
            if (High_Air == true) {
                if (Air_pressure * PsitoKpa > (Air_limit_max * PsitoKpa)) {
                    if (flash) {
                        color = color_red;
                    } else {
                        color = color_gray;
                    }
                } else if (Air_pressure * PsitoKpa < Air_limit_min * PsitoKpa) {
                    if (flash) {
                        color = color_orange;
                    } else {
                        color = color_gray;
                    }
                } else {
                    color = color_green;
                }
            } else {
                if (Air_pressure * mmHgtoKpa > (Air_limit_max * mmHgtoKpa)) {
                    if (flash) {
                        color = color_red;
                    } else {
                        color = color_gray;
                    }
                } else if (Air_pressure * mmHgtoKpa < Air_limit_min * mmHgtoKpa) {
                    if (flash) {
                        color = color_orange;
                    } else {
                        color = color_gray;
                    }
                } else {
                    color = color_green;
                }
            }
        }
        if (Air_unit.equals("Psi")) {
            if (Air_pressure > (Air_limit_max)) {
                if (flash) {
                    color = color_red;
                } else {
                    color = color_gray;
                }

            } else if (Air_pressure < (Air_limit_min)) {
                if (flash) {
                    color = color_orange;
                } else {
                    color = color_gray;
                }

            } else {
                color = color_green;
            }
        }
        if (Air_unit.equals("mmHg")) {
            if (Air_pressure > (Air_limit_max)) {
                if (flash) {
                    color = color_red;
                } else {
                    color = color_gray;
                }

            } else if (Air_pressure < (Air_limit_min)) {
                if (flash) {
                    color = color_orange;
                } else {
                    color = color_gray;
                }

            } else {
                color = color_green;
            }
        }
        if (Air_unit.equals("inHg")) {
            if (Air_pressure * mmHgtoinHg > (Air_limit_max * mmHgtoinHg)) {
                if (flash) {
                    color = color_red;
                } else {
                    color = color_gray;
                }

            } else if (Air_pressure * mmHgtoinHg < (Air_limit_min * mmHgtoinHg)) {
                if (flash) {
                    color = color_orange;
                } else {
                    color = color_gray;
                }

            } else {
                color = color_green;
            }
        }
        return  color;
    }

}
