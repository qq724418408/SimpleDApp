package com.forms.dhttp.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.forms.xxxapp.R;

import java.util.UUID;

/**
 * 获取App信息的工具类
 *
 * @author yuhao
 */
public class AppInfoUtils {

    /**
     * 获取当前应用程序的版本号
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "Not find Version";
        }
    }

    /**
     * 获取当前应用程序的图标
     *
     * @param context
     * @return
     */
    public static int getAppIcon(Context context) {

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            int AppIconId = info.icon;
            return AppIconId;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return R.mipmap.ic_launcher;
    }

    /**
     * 获取App的名字
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String applicationName = (String) context.getPackageManager().getApplicationLabel(info);
            return applicationName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Not find AppName";
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = "" + tm.getDeviceId();
        String tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + Settings.Secure.getString(context.getContentResolver(), "android_id");
        UUID deviceUuid = new UUID(androidId.hashCode(), tmDevice.hashCode() << 32 | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}
