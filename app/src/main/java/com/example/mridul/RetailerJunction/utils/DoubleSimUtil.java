package com.example.mridul.RetailerJunction.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.example.mridul.RetailerJunction.ui.RetailerApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DoubleSimUtil {
    private static final String SPLIT = "_";

    public static String getImei(Context context) {

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String imei = getMtkFirstSim(context, tm);
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        imei = getMtkSecondSim(context, tm);
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        imei = getMtkThirdSim(context, tm);
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        imei = getMtkFourthSim(context, tm);
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        imei = getSpreadDoubleSim(context, tm);
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        imei = getQualcommDoubleSim(context, tm);
        if (TextUtils.isEmpty(imei)) {
            return tm.getDeviceId();
        }
        return imei;
    }

    private static String getMtkThirdSim(Context context, TelephonyManager tm) {
        try {
            Integer simId_1;
            Integer simId_2;
            Class<?> c = Class.forName(tm.getClass().getName()); //"com.android.internal.telephony.Phone");
            try {
                Field fields1 = c.getField("GEMINI_SIM_1");
                fields1.setAccessible(true);
                simId_1 = (Integer) fields1.get(null);
                Field fields2 = c.getField("GEMINI_SIM_2");
                fields2.setAccessible(true);
                simId_2 = (Integer) fields2.get(null);
            } catch (Exception e) {
                simId_1 = Integer.valueOf(0);
                simId_2 = Integer.valueOf(1);
            }
            Method m1 = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", new Class[]{Integer.TYPE});
            String imei1 = ((String) m1.invoke(tm, new Object[]{simId_1})).trim();
            String imei2 = ((String) m1.invoke(tm, new Object[]{simId_2})).trim();
            if (TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2)) {
                return null;
            }
            return new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
        } catch (NoSuchMethodException e2) {
            return null;
        } catch (IllegalAccessException e3) {
            return null;
        } catch (IllegalArgumentException e4) {
            return null;
        } catch (InvocationTargetException e5) {
            return null;
        } catch (ClassNotFoundException e6) {
            return null;
        } catch (NullPointerException e7) {
            return null;
        }
    }

    private static String getMtkFourthSim(Context context, TelephonyManager tm) {
        try {
            Integer simId_1;
            Integer simId_2;
            Class<?> c = Class.forName(tm.getClass().getName()); //"com.android.internal.telephony.Phone");
            try {
                Field fields1 = c.getField("GEMINI_SIM_1");
                fields1.setAccessible(true);
                simId_1 = (Integer) fields1.get(null);
                Field fields2 = c.getField("GEMINI_SIM_2");
                fields2.setAccessible(true);
                simId_2 = (Integer) fields2.get(null);
            } catch (Exception e) {
                simId_1 = Integer.valueOf(0);
                simId_2 = Integer.valueOf(1);
            }
            Method mx = TelephonyManager.class.getMethod("getDefault", new Class[]{Integer.TYPE});
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, new Object[]{simId_1});
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, new Object[]{simId_2});
            String imei1 = tm1.getDeviceId().trim();
            String imei2 = tm2.getDeviceId().trim();
            if (TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2)) {
                return null;
            }
            return new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
        } catch (NoSuchMethodException e2) {
            return null;
        } catch (IllegalAccessException e3) {
            return null;
        } catch (IllegalArgumentException e4) {
            return null;
        } catch (InvocationTargetException e5) {
            return null;
        } catch (ClassNotFoundException e6) {
            return null;
        } catch (NullPointerException e7) {
            return null;
        }
    }

    private static String getMtkFirstSim(Context context, TelephonyManager tm) {
        String str = null;
        try {
            Class<?> c = Class.forName(tm.getClass().getName()); //"com.android.internal.telephony.Phone");
            Method m1 = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", new Class[]{Integer.TYPE});
            String imei1 = (String) m1.invoke(tm, new Object[]{Integer.valueOf(0)});
            String imei2 = (String) m1.invoke(tm, new Object[]{Integer.valueOf(1)});
            if (!(TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2))) {
                str = new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e2) {
        } catch (IllegalArgumentException e3) {
        } catch (InvocationTargetException e4) {
        } catch (ClassNotFoundException e5) {
        } catch (NullPointerException e6) {
        }
        return str;
    }

    private static String getMtkSecondSim(Context context, TelephonyManager tm) {
        String str = null;
        try {
            Class<?> c = Class.forName(tm.getClass().getName()); //"com.android.internal.telephony.Phone");
            Method mx = TelephonyManager.class.getMethod("getDefault", new Class[]{Integer.TYPE});
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, new Object[]{Integer.valueOf(0)});
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, new Object[]{Integer.valueOf(1)});
            String imei1 = tm1.getDeviceId();
            String imei2 = tm2.getDeviceId();
            if (!(TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2))) {
                str = new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
            }
        } catch (ExceptionInInitializerError e) {
        } catch (NoSuchMethodException e2) {
        } catch (IllegalAccessException e3) {
        } catch (IllegalArgumentException e4) {
        } catch (InvocationTargetException e5) {
        } catch (ClassNotFoundException e6) {
        } catch (NullPointerException e7) {
        }
        return str;
    }

    private static String getSpreadDoubleSim(Context context, TelephonyManager tm) {
        String str = null;
        try {
            Class<?> c = Class.forName(tm.getClass().getName()); //"com.android.internal.telephony.PhoneFactory");
            String spreadTmService = (String) c.getMethod("getServiceName", new Class[]{String.class, Integer.TYPE}).invoke(c, new Object[]{"phone", Integer.valueOf(1)});
            String imei1 = tm.getDeviceId();
            String imei2 = ((TelephonyManager) context.getSystemService(spreadTmService)).getDeviceId();
            if (!(TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2))) {
                str = new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
            }
        } catch (ExceptionInInitializerError e) {
        } catch (NoSuchMethodException e2) {
        } catch (ClassNotFoundException e3) {
        } catch (IllegalAccessException e4) {
        } catch (IllegalArgumentException e5) {
        } catch (InvocationTargetException e6) {
        } catch (NullPointerException e7) {
        }
        return str;
    }

    public static String getQualcommDoubleSim(Context context, TelephonyManager tm) {
        String str = null;
        try {
            Class<?> cx = Class.forName(tm.getClass().getName()); //Class.forName("android.telephony.MSimTelephonyManager");
            Method md = cx.getMethod("getDeviceId", new Class[]{Integer.TYPE});
            String imei1 = (String) md.invoke(tm, new Object[]{Integer.valueOf(0)});
            String imei2 = (String) md.invoke(tm, new Object[]{Integer.valueOf(1)});
            if (!(TextUtils.isEmpty(imei1) || TextUtils.isEmpty(imei2))) {
                str = new StringBuilder(String.valueOf(imei1)).append(SPLIT).append(imei2).toString();
            }
        } catch (ExceptionInInitializerError e) {
        } catch (ClassNotFoundException e2) {
        } catch (IllegalAccessException e3) {
        } catch (IllegalArgumentException e4) {
        } catch (InvocationTargetException e5) {
        } catch (NoSuchMethodException e6) {
        } catch (NullPointerException e7) {
        }
        return str;
    }
}
