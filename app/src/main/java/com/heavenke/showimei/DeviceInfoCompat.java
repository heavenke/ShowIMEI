package com.heavenke.showimei;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

public class DeviceInfoCompat {

    /**
     * 获取 IMEI1（主卡）
     */
    @SuppressLint("HardwareIds")
    public static String getImei1(Context context) {
        return getImei(context, 0);
    }

    /**
     * 获取 IMEI2（副卡，仅双卡设备）
     */
    @SuppressLint("HardwareIds")
    public static String getImei2(Context context) {
        return getImei(context, 1);
    }

    /**
     * 获取 MEID（通常用于 CDMA 设备）
     */
    @SuppressLint("HardwareIds")
    public static String getMeid(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;

            // Android 8.0+ (API 26) 开始提供 getMeid()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.getMeid();
            }

            // 旧版本：通过 getDeviceId() 判断是否为 CDMA
            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                return tm.getDeviceId();
            }

            // 尝试反射获取 slot 0 的 deviceId（可能是 MEID）
            String meid = getDeviceIdBySlot(tm, 0);
            if (meid != null && isMeid(meid)) {
                return meid;
            }

            // 再尝试 slot 1
            meid = getDeviceIdBySlot(tm, 1);
            if (meid != null && isMeid(meid)) {
                return meid;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备序列号（SN）
     */

    public static String getSerialNumber(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0+
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return Build.getSerial();
                } else {
                    // 权限未授予，无法获取
                    return "unknown";
                }
            } else {
                // Android < 8.0
                return Build.SERIAL;
            }
        } catch (SecurityException e) {
            // 即使有权限，非特权应用在部分厂商 ROM 上仍可能抛 SecurityException
            return "unknown";
        } catch (Exception e) {
            return "error";
        }
    }
    /**
     * 【推荐替代方案】获取 ANDROID_ID（Google 官方推荐的设备标识）
     * - 在 Android 8.0+ 仍可用（每个应用-用户组合唯一）
     * - 不需要任何权限
     * - 重置设备或清除数据会变化
     */
    @NonNull
    public static String getAndroidId(@NonNull Context context) {
        try {
            String androidId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
            );
            if (androidId == null || androidId.isEmpty()) {
                return "unknown";
            }
            return androidId;
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 通用方法：根据 slotId 获取 IMEI/MEID
     */
    private static String getImei(Context context, int slotId) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;

            // Android 8.0+ (API 26) 支持 getImei(slotId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.getImei(slotId);
            }

            // 尝试反射 getDeviceId(int slotId) —— 常见于国产 ROM
            String imei = getDeviceIdBySlot(tm, slotId);
            if (imei != null && isImei(imei)) {
                return imei;
            }

            // 如果是 slot 0 且没有多卡，fallback 到 getDeviceId()
            if (slotId == 0) {
                String deviceId = tm.getDeviceId();
                if (deviceId != null && isImei(deviceId)) {
                    return deviceId;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射调用 TelephonyManager.getDeviceId(int slotId)
     */
    private static String getDeviceIdBySlot(TelephonyManager tm, int slotId) {
        try {
            // 尝试标准反射（部分厂商支持）
            Method method = TelephonyManager.class.getMethod("getDeviceId", int.class);
            return (String) method.invoke(tm, slotId);
        } catch (Exception e) {
            // 尝试其他常见方法名（兼容不同 ROM）
            try {
                Method method = TelephonyManager.class.getMethod("getImei", int.class);
                return (String) method.invoke(tm, slotId);
            } catch (Exception ignored) {
                // 华为等厂商可能使用 getSubscriberId 或其他方式，但 IMEI 通常还是 getDeviceId
            }
        }
        return null;
    }

    /**
     * 判断字符串是否为合法 IMEI（15 位数字）
     */
    private static boolean isImei(String s) {
        return s != null && s.matches("^[0-9]{15}$");
    }

    /**
     * 判断字符串是否为合法 MEID（14 位十六进制，或 18 位带校验）
     */
    private static boolean isMeid(String s) {
        if (s == null) return false;
        // 14 位十六进制（常见）
        if (s.matches("^[0-9A-F]{14}$")) return true;
        // 18 位（14 + 4 校验，如 HEX + DEC 校验）
        if (s.matches("^[0-9A-F]{14}[0-9]{4}$")) return true;
        return false;
    }
}