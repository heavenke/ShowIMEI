package com.heavenke.showimei;

import static android.content.Context.TELEPHONY_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String CHECKSUM= SystemProperties.get(getChecksumvalue());
    public static final String FUCKYOU= "FUCK";
    /**
     * 生成条形码（不支持中文）
     *
     * @param content
     * @return
     */
    public static Bitmap createBarcode(String content) {

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, 3000, 700);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成二维码
     *
     * @param content
     * @return
     */
    public static Bitmap createQrcode(String content) {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        // 支持中文配置
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 1000, 1000
                    , hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressLint("HardwareIds")
    //获取指定文件MD5
    public static String getFileMD5(String filepath){
        File file = new File(filepath);
        String md5 = getMD5(file);
        return md5;
    }
    //MD5处理(1)
    public static String getMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }
    //MD5处理(2)
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    //循环取出小写字母
    public static String getLetter(int[] num){
        String outstr="";
        String Str ="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z";
        String[] Str2=Str.split(",");
        for(int i = 0; i < num.length; i++){
            outstr=outstr+Str2[num[i]];
        }
        return outstr;
    }
    //获取文件路径并执行获取MD5
    private static String GetFilePath(int type){
        int[] a={18,24,18,19,4,12}; //整数型数组 0-25代表26个小写字母,如0=a  25=z 自行推算,通过getLetter(数组)这个方法可取出指定的字母组合,{18,24,18,19,4,12}即代表 system
        int[] b={5,17,0,12,4,22,14,17,10};//framework
        int[] c={17,4,18};//res
        int[] d={0,15,10};//apk
        //int[] e={1,20,8,11,3};//build
        //int[] f={15,17,14,15};//prop
        int[] e={18,4,17,21,8,2,4,18};//services
        int[] f={9,0,17};//jar
        //String filepath_1="/"+getLetter(a)+"/"+getLetter(b)+"/"+getLetter(b)+"-"+getLetter(c)+"."+getLetter(d);
        //String filepath_2="/"+getLetter(a)+"/"+getLetter(e)+"."+getLetter(f);
        // /system/framework/framework/framework.jar
        String filepath_1="/"+getLetter(a)+"/"+getLetter(b)+"/"+getLetter(b)+"."+getLetter(f);
        // /system/framework/framework/services.jar
        String filepath_2="/"+getLetter(a)+"/"+getLetter(b)+"/"+getLetter(e)+"."+getLetter(f);
        switch(type){
            case 1:
                String filepath_md5_1=getFileMD5(filepath_1);

                return filepath_md5_1.toUpperCase();
            case 2:
                String filepath_md5_2=getFileMD5(filepath_2);
                return filepath_md5_2.toUpperCase();
            default:
                break;
        }
        return "You are SB";
    }
    //倒转字符串
    private static String ReversalString(String input) {
        String output = new String();
        for (int i = (input.length() - 1); i >= 0; i--) {
            output += (input.charAt(i));
        }
        return output;
    }
    //启动校验MD5并返回结果
    public static boolean getMd5CheckResult(){
        String CheckSumString1=ReversalString(GetFilePath(1));
        String CheckSumString2=ReversalString(GetFilePath(2));
        String filemd5=CheckSumString1+CheckSumString2;
        if(CHECKSUM.equals(filemd5)){
            return true;
        }else{
            return false;
        }
    }
    //启动校验
    public static boolean CheckSumApp(){
        Boolean Framework_Check=getMd5CheckResult();
        if(!Framework_Check){
            return false;
        }else{
            return true;
        }
    }
    //获取校验标签
    public static String getChecksumvalue(){
        int[] a={17,14};//ro
        int[] b={3,23,19,14,14,11,18};//dxtools
        int[] c={2,7,4,2,10,18,20,12};//checksum
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    //获取主题标签
    public static String getThemevalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={19,7,4,12,4,1,11,0,2,10};//themeblack
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    //获取按钮标签
    public static String getButtonvalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={1,20,19,19,14,13};//button
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    //获取假MEID标签
    public static String getMeidfakevalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={12,4,8,3,5,0,10,4};//meidfake
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    //获取假IMEI2标签
    public static String getIMEI2fakevalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={8,12,4,8};//imei
        int[] d={5,0,10,4};//fake
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c)+"2"+getLetter(d);
        return str;
    }
    //获取假IMEI2标签
    public static String getIMEI2fromIMEI1(){
        String str = getLetter(new int[] {15,4,17,18,8,18,19})+"."+getLetter(new int[] {7,4,0,21,4,13,10,4})+"."+getLetter(new int[] {8,12,4,8})+"2"+getLetter(new int[] {5,17,14,12,8,12,4,8})+"1";
        return str;
    }
    public static String getSnfakevalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={5,0,10,4,18,13};//fakesn
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    //获取按二维码标签
    public static String getSnqrvalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={18,13,16,17};//snqr
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
    public static String getDevelopermodevalue(){
        int[] a={15,4,17,18,8,18,19};//persist
        int[] b={7,4,0,21,4,13,10,4};//heavenke
        int[] c={3,4,21,4,11,14,15,4,17};//developer
        String str = getLetter(a)+"."+getLetter(b)+"."+getLetter(c);
        return str;
    }
}
