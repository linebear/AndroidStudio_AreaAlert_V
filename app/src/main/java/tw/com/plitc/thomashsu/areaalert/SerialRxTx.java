package tw.com.plitc.thomashsu.areaalert;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Created by ThomasHsu on 2016/8/22.
 */


public class SerialRxTx {
    static  String TAG = "PL2303HXD_APLog";
    private static final boolean SHOW_DEBUG = true;
    static String showtext = "";
    static int[] ReadData = {-1,-1,-1,-1,-1,-1};

    public static int [] readDataFromSerial(PL2303Driver mSerial, Context Activity) {

        int len;
        byte[] rbuf = new byte[4096];
        StringBuffer sbHex = new StringBuffer();
        Log.d(TAG, "Enter readDataFromSerial");
        if (null == mSerial)

            return ReadData;

        if (!mSerial.isConnected()) {

            OtherMethod.restart(Activity, 500, "DisConnect");
        }

        len = mSerial.read(rbuf);
        if (len < 0) {
            Log.d(TAG, "Fail to bulkTransfer(read data)");
            return ReadData;
        }

        if (len > 0) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "read len : " + len);
            }
            // rbuf[len] = 0;
            for (int j = 0; j < len; j++) {
                sbHex.append((char) (rbuf[j] & 0x000000FF));
            }
            try {
                showtext = sbHex.toString();
                if (showtext.length() < 31) {
                    return ReadData;
                } else if (showtext.indexOf("@") == -1) {
                    return ReadData;
                } else if (showtext.indexOf("@") != 0) {
                    if (showtext.substring(showtext.indexOf("@") + 1).length() < 30) {
                        return ReadData;
                    }
                }
                showtext = showtext.substring(showtext.indexOf("@") + 1);
//                Toast.makeText(Activity,showtext,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.getMessage();
                OtherMethod.restart(Activity, 500, "readData");
            }
            if (OtherMethod.isNumeric(showtext.substring(0, 4)) ) {
                if(Integer.parseInt(showtext.substring(0, 4))>0)
                ReadData[0] = Integer.parseInt(showtext.substring(0, 4));
            }
            if (OtherMethod.isNumeric(showtext.substring(5, 9))) {
                if(Integer.parseInt(showtext.substring(5, 9))>0)
                ReadData[1] = Integer.parseInt(showtext.substring(5, 9));
            }
            if (OtherMethod.isNumeric(showtext.substring(10, 14))) {
                if(Integer.parseInt(showtext.substring(10, 14))>0)
                ReadData[2] = Integer.parseInt(showtext.substring(10, 14));
            }
            if (OtherMethod.isNumeric(showtext.substring(15, 19))) {
                if(Integer.parseInt(showtext.substring(15, 19))>0)
                ReadData[3] = Integer.parseInt(showtext.substring(15, 19));
            }
            if (OtherMethod.isNumeric(showtext.substring(20, 24))) {
                if(Integer.parseInt(showtext.substring(20, 24))>0)
                ReadData[4] = Integer.parseInt(showtext.substring(20, 24));
            }
            if (OtherMethod.isNumeric(showtext.substring(25, 29))) {
                if(Integer.parseInt(showtext.substring(25, 29))>0)
                ReadData[5] = Integer.parseInt(showtext.substring(25, 29));
            }
            writeDataToSerial("2", mSerial);
        } else {
            if (SHOW_DEBUG) {
                Log.d(TAG, "read len : 0 ");
            }
            return ReadData;
        }
        Log.d(TAG, "Leave readDataFromSerial");
//        Toast.makeText(Activity,ReadData[0]+"_"+ReadData[2]+"_"+ReadData[4],Toast.LENGTH_SHORT).show();
        return ReadData;
    }// readDataFromSerial

    private static void writeDataToSerial(String strWrite, PL2303Driver mSerial) {

        Log.d(TAG, "Enter writeDataToSerial");

        if (null == mSerial)
            return;

        if (!mSerial.isConnected())
            return;
        if (SHOW_DEBUG) {
            Log.d(TAG, "PL2303Driver Write 2(" + strWrite.length() + ") : "
                    + strWrite);
        }
        int res = mSerial.write(strWrite.getBytes(), strWrite.length());
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return;
        }

        Log.d(TAG, "Leave writeDataToSerial");
    }// writeDataToSerial
}
