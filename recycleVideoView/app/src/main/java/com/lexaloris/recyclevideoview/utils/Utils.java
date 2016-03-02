package com.lexaloris.recyclevideoview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Utils {

    private static final String TAG = "Utils";
    SharedPreferences sPref;
    final static private String myPreferencesName = "myPref";
    final static private String TRUE_STATE = "true";
    final static private String FALSE_STATE = "false";


    public String readPreferences(Context context, String url) {
        sPref = context.getSharedPreferences(myPreferencesName, Context.MODE_PRIVATE);
        String savedText = sPref.getString(url, FALSE_STATE);
        if (savedText.equals(TRUE_STATE)) {
            return TRUE_STATE;
        }
        return FALSE_STATE;
    }

    public void savePreferences(Context context, String url) {
        sPref = context.getSharedPreferences(myPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(url, TRUE_STATE);
        ed.apply();
    }


    public int convertDpToPixel(int screenWidthDp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = (float)screenWidthDp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    public int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    // функция возвращает список элементов, которые как минимум на половину видны на экране
    public ArrayList<Integer> calculateVisibleElements(RecyclerView recyclerView, int firstVisiblePosition, int lastVisibleposition) {
        ArrayList<Integer> visibleElements = new ArrayList<>();
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());

        View fv = layoutManager.getChildAt(0);
        if (fv != null) {
            int offsetTop = fv.getTop();
            int height = fv.getHeight();
            if (height / 2 > Math.abs(offsetTop)) {
                visibleElements.add(firstVisiblePosition);
            }
        }

        View lv = layoutManager.getChildAt(lastVisibleposition-firstVisiblePosition);
        if (lv != null) {
            int offsetBottom = lv.getBottom();
            int height = lv.getHeight();
            if (height/2 > Math.abs(layoutManager.getHeight() - Math.abs(offsetBottom))) {
                visibleElements.add(lastVisibleposition);
            }
        }
        for (int i = firstVisiblePosition + 1; i < lastVisibleposition; i++) {
            visibleElements.add(i);
        }

        return visibleElements;
    }

    public String getEntireFileName(String urlStr) {
        return md5Custom(urlStr) + urlStr.substring(urlStr.lastIndexOf("/")+1);
    }

    public static String md5Custom(String st) {
        MessageDigest messageDigest;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}
