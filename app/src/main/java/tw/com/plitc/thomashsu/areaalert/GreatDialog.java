package tw.com.plitc.thomashsu.areaalert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ThomasHsu on 2016/8/23.
 */
public class GreatDialog {

    static final String Password = "a7654321";
    static int Air_max, Air_min, Air_max_old, Air_min_old;
    static String shared_Air_max,shared_Air_min;
    public static void GreatPassword(Context context,int AIR_max,int AIR_min,String shared_AIR_max,String shared_AIR_min) {
        Air_max = AIR_max;
        Air_min = AIR_min;
        Air_max_old = Air_max;
        Air_min_old = Air_min;
        shared_Air_max = shared_AIR_max;
        shared_Air_min = shared_AIR_min;
        final Context Activity = context;
        LayoutInflater password = LayoutInflater
                .from(Activity);
        final View textpassword = password.inflate(
                R.layout.layout_password, null);
        AlertDialog dlg_password = new AlertDialog.Builder(
                Activity)
                .setTitle("請輸入管理者設定密碼")
                .setView(textpassword)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                EditText et_password = (EditText) textpassword
                                        .findViewById(R.id.et_password);
                                if (et_password.getText().toString()
                                        .trim().equals(Password)) {
                                    greatDialog(Activity);
                                } else {
                                    showToast(Activity, "密碼輸入錯誤");
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                            }
                        }).create();
        dlg_password.show();
    }

    private static void greatDialog(Context context) {
        final Context Activity = context;

        LayoutInflater factory = LayoutInflater
                .from(Activity);
        final View textEntryView = factory
                .inflate(
                        R.layout.layout_dialog,
                        null);
        AlertDialog dlg = new AlertDialog.Builder(
                Activity)
                .setTitle("氣源警報值輸入(單位：Psi)")
                .setMessage(
                        "原氣源最大值:"
                                + Air_max
                                + " ,原氣源最小值:"
                                + Air_min)
                .setView(textEntryView)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int whichButton) {
                                EditText abnormal_max = (EditText) textEntryView
                                        .findViewById(R.id.et_abnormal_max);
                                EditText abnormal_min = (EditText) textEntryView
                                        .findViewById(R.id.et_abnormal_min);
                                if (abnormal_max
                                        .getText()
                                        .length() > 0) {
                                    if (OtherMethod.isNumeric(abnormal_max
                                            .getText()
                                            .toString())) {
                                        Air_max = Integer
                                                .parseInt(abnormal_max
                                                        .getText()
                                                        .toString());
                                        LayoutThreeAirActivity.shared.edit()
                                                .putInt(shared_Air_max,
                                                        Air_max)
                                                .commit();
                                    } else {
                                        showToast(Activity, "最大值輸入格式錯誤");
                                    }
                                } else {
                                    showToast(Activity, "請輸入最大值");
                                }
                                if (abnormal_min
                                        .getText()
                                        .length() > 0) {
                                    if (OtherMethod.isNumeric(abnormal_min
                                            .getText()
                                            .toString())) {
                                        Air_min = Integer
                                                .parseInt(abnormal_min
                                                        .getText()
                                                        .toString());
                                        LayoutThreeAirActivity.shared.edit()
                                                .putInt(shared_Air_min,
                                                        Air_min)
                                                .commit();
                                    } else {
                                        showToast(Activity, "最小值輸入格是錯誤");
                                    }
                                } else {
                                    showToast(Activity, "請輸入最小值");
                                }
                                if (Air_min >= Air_max) {
                                    showToast(Activity, "最大值请勿小于最小值");
                                    Air_max = Air_max_old;
                                    Air_min = Air_min_old;
                                    LayoutThreeAirActivity.shared.edit()
                                            .putInt(shared_Air_max,
                                                    Air_max)
                                            .commit();
                                    LayoutThreeAirActivity.shared.edit()
                                            .putInt(shared_Air_min,
                                                    Air_min)
                                            .commit();
                                }
                            }
                        })
                .setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int whichButton) {

                            }
                        }).create();
        dlg.show();
    }

    private static void showToast(Context Activity,String Errormsg){
        Toast.makeText(Activity,Errormsg,Toast.LENGTH_LONG).show();
    }

}
