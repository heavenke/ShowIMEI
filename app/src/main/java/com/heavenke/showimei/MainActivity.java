package com.heavenke.showimei;

import static com.heavenke.showimei.Utils.CheckSumApp;
import static com.heavenke.showimei.Utils.getButtonvalue;
import static com.heavenke.showimei.Utils.getChecksumvalue;
import static com.heavenke.showimei.Utils.getIMEI2fakevalue;
import static com.heavenke.showimei.Utils.getIMEI2fromIMEI1;
import static com.heavenke.showimei.Utils.getMeidfakevalue;
import static com.heavenke.showimei.Utils.getSnfakevalue;
import static com.heavenke.showimei.Utils.getSnqrvalue;
import static com.heavenke.showimei.Utils.getThemevalue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private ConstraintLayout CL;
    private Button BTN;
    ImageView iv_meid;
    ImageView iv_imei1;
    ImageView iv_imei2;
    ImageView iv_sn;
    ImageView iv_warring;
    TextView tv_app_title;
    TextView tv_imei1_title;
    TextView tv_imei1;
    TextView tv_imei2_title;
    TextView tv_imei2;
    TextView tv_sn_title;
    TextView tv_sn;
    TextView tv_meid_title;
    TextView tv_meid;
    TextView tv_warring;
    public static String MEIDFAKE = "";
    public static String MEID = "";
    public static String IMEI1 = "";
    public static String IMEI2 = "";
    public static String SN = "";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        setStatusBarTranslucent(this);
        getPhoneInfo();
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //初始化各组件
    private void InitView(){
        BTN = findViewById(R.id.button);
        BTN.setOnClickListener(this);
        CL = (ConstraintLayout) findViewById(R.id.custom_layout);
        //APP标题
        tv_app_title = findViewById(R.id.tv_app_title);
        //IMEI1标题
        tv_imei1_title = findViewById(R.id.tv_imei1_title);
        //IMEI1文字
        tv_imei1 = findViewById(R.id.tv_imei1);
        //IMEI1条形码
        iv_imei1 = findViewById(R.id.iv_imei1);
        //IMEI2标题
        tv_imei2_title = findViewById(R.id.tv_imei2_title);
        //IMEI2文字
        tv_imei2 = findViewById(R.id.tv_imei2);
        //IMEI2条形码
        iv_imei2 = findViewById(R.id.iv_imei2);
        //SN标题
        tv_sn_title = findViewById(R.id.tv_sn_title);
        //sn文字
        tv_sn = findViewById(R.id.tv_sn);
        //sn条形码
        iv_sn = findViewById(R.id.iv_sn);
        //MEID标题
        tv_meid_title = findViewById(R.id.tv_meid_title);
        //MEID文字
        tv_meid = findViewById(R.id.tv_meid);
        //MEID条形码
        iv_meid = findViewById(R.id.iv_meid);
        //警告文字
        tv_warring = findViewById(R.id.tv_warring);
        //警告图片
        iv_warring = findViewById(R.id.iv_warring);
        //判断是否指定为黑色主题,设置项为persist.heavenke.themeblack 参数为true黑底白字,false白底黑字
        if (SystemProperties.getBoolean(getThemevalue(), false)) {
            setTvColor(tv_app_title, R.color.white);
            setTvColor(tv_imei1_title, R.color.white);
            setTvColor(tv_imei1, R.color.white);
            setTvColor(tv_imei2_title, R.color.white);
            setTvColor(tv_imei2, R.color.white);
            setTvColor(tv_sn_title, R.color.white);
            setTvColor(tv_sn, R.color.white);
            setTvColor(tv_meid_title, R.color.white);
            setTvColor(tv_meid, R.color.white);
            setTvColor(tv_warring, R.color.white);
            BTN.setBackgroundColor(0X3EF8F7F5);
            BTN.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
            CL.setBackgroundColor(getResources().getColor(R.color.black));//这样才可以
        } else {
            setTvColor(tv_app_title, R.color.black);
            setTvColor(tv_imei1_title, R.color.black);
            setTvColor(tv_imei1, R.color.black);
            setTvColor(tv_imei2_title, R.color.black);
            setTvColor(tv_imei2, R.color.black);
            setTvColor(tv_sn_title, R.color.black);
            setTvColor(tv_sn, R.color.black);
            setTvColor(tv_meid_title, R.color.black);
            setTvColor(tv_meid, R.color.black);
            setTvColor(tv_warring, R.color.black);
            BTN.setBackgroundColor(0XBCF3F2F2);
            BTN.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
            CL.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));//这样才可以
        }
        //判断是否显示关闭按钮,设置项为persist.heavenke.button 参数为true显示,false不显示
        if (SystemProperties.getBoolean(getButtonvalue(), false)) {
            BTN.setVisibility(View.VISIBLE);
        } else {
            BTN.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceAsColor")
    //设置TEXT控件文字颜色
    public void setTvColor(TextView tv, int i) {
        tv.setTextColor(getApplicationContext().getResources().getColor(i));
    }

    @Override
    //按钮监听点击事件
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            onClickButton1(view);
        }
    }

    //按钮监听点击回调
    private void onClickButton1(View view) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    //沉浸式状态栏
    public static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void getPhoneInfo() {
        //接收Intent获取参数,如参数为空,则自行获取IMEI/MEID数据
        Intent intent = getIntent();
        MEID = intent.getStringExtra("meid");
        IMEI1 = intent.getStringExtra("imei1");
        IMEI2 = intent.getStringExtra("imei2");
        test();
        //判断是否存在传参,如果没有传入参数,则启动读取手机信息授予权限,授权完毕后读取并显示IMEI等参数
        if (TextUtils.isEmpty(MEID) &&
                TextUtils.isEmpty(IMEI1) &&
                TextUtils.isEmpty(IMEI2)) {
            PermissionUtils();
        }else{
            if(TextUtils.equals(MEID,IMEI2)){
                MEID = "";
            }
            //判断是否用Android ID来替代SN
            if(SystemProperties.getBoolean(getSnfakevalue(),false)){
                SN = DeviceInfoCompat.getAndroidId(getApplicationContext());
            }else{
                SN = DeviceInfoCompat.getSerialNumber(getApplicationContext());
            }
            setPhoneInfo();
        }
    }

    // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
    public void PermissionUtils() {
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        // 如果这3个权限全都拥有, 则直接执行备复制方法
        if (isAllGranted) {
            getDeviceInfoCompat();
            return;
        }
        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行复制方法
                getDeviceInfoCompat();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.grant_text);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.grant_go, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
    public void getDeviceInfoCompat(){
        MEID = DeviceInfoCompat.getMeid(this);
        IMEI1 = DeviceInfoCompat.getImei1(this);
        IMEI2 = DeviceInfoCompat.getImei2(this);
        //判断是否用Android ID来替代SN
        if(SystemProperties.getBoolean(getSnfakevalue(),false)){
            SN = DeviceInfoCompat.getAndroidId(getApplicationContext());
        }else{
            SN = DeviceInfoCompat.getSerialNumber(getApplicationContext());
        }
        int i = IMEI1.indexOf(",");
        if(i != -1){
            String[] str = IMEI1.split(",");
            IMEI1 = str[0];
            IMEI2 = str[1];
        }
        setPhoneInfo();
    }
    public void setPhoneInfo(){
         if(CheckSumApp()){
         iv_warring.setVisibility(View.GONE);
         tv_warring.setVisibility(View.GONE);
         MEIDFAKE = SystemProperties.get(getMeidfakevalue());
            if(TextUtils.isEmpty(MEID) && !TextUtils.isEmpty(MEIDFAKE)){
                MEID= MEIDFAKE;
            }
         }else{
         iv_warring.setVisibility(View.VISIBLE);
         tv_warring.setVisibility(View.VISIBLE);
         MEID="";
         IMEI1="";
         IMEI2="";
         SN="";
         }
        if(TextUtils.isEmpty(IMEI1)){
            tv_imei1_title.setVisibility(View.GONE);
            tv_imei1.setVisibility(View.GONE);
            iv_imei1.setVisibility(View.GONE);
        }else{
            iv_imei1.setImageBitmap(Utils.createBarcode(IMEI1));
            tv_imei1.setText(IMEI1);
        }
        if(TextUtils.isEmpty(IMEI2)){
            //判断IMEI是否强制显示为IMEI1,设置项为persist.heavenke.imei2fromimei1 参数为true显示,false为不显示
            if(SystemProperties.getBoolean(getIMEI2fromIMEI1(),false)){
                iv_imei2.setImageBitmap(Utils.createBarcode(IMEI1));
                tv_imei2.setText(IMEI1);
            }else{
                //判断是否显示假IMEI2(从IMEI1减一位),设置项为persist.heavenke.imei2fake 参数为true显示,false为不显示
                //此设置项仅在没有真实IMEI2时有效(IMEI2读取为空时)
                if(SystemProperties.getBoolean(getIMEI2fakevalue(),false)){
                    long IMEI2_LONG = Long.valueOf(IMEI1).longValue();
                    IMEI2_LONG = IMEI2_LONG-1;
                    String IMEI2FAKE = Long.toString(IMEI2_LONG);
                    iv_imei2.setImageBitmap(Utils.createBarcode(IMEI2FAKE));
                    tv_imei2.setText(IMEI2FAKE);
                }else{
                    tv_imei2_title.setVisibility(View.GONE);
                    tv_imei2.setVisibility(View.GONE);
                    iv_imei2.setVisibility(View.GONE);
                }
            }
        }else{
            iv_imei2.setImageBitmap(Utils.createBarcode(IMEI2));
            tv_imei2.setText(IMEI2);
        }
        if(TextUtils.isEmpty(SN)){
            tv_sn_title.setVisibility(View.GONE);
            tv_sn.setVisibility(View.GONE);
            iv_sn.setVisibility(View.GONE);
        }else{
            //判断是否显示SN二维码,设置项为persist.heavenke.snqr 参数为true显示二维码,false为显示条形码
            if(SystemProperties.getBoolean(getSnqrvalue(),false)){
                iv_sn.setImageBitmap(Utils.createQrcode(SN));
            }else{
                iv_sn.setImageBitmap(Utils.createBarcode(SN));
            }
            tv_sn.setText(SN);
        }
        if(TextUtils.isEmpty(MEID)){
            tv_meid_title.setVisibility(View.GONE);
            tv_meid.setVisibility(View.GONE);
            iv_meid.setVisibility(View.GONE);
        }else{
            iv_meid.setImageBitmap(Utils.createBarcode(MEID));
            tv_meid.setText(MEID);
        }
    }
    public void test(){
        MEID = "A0000000000000";
        IMEI1 = "860683040303812";
        IMEI2 = "860683040303820";
        SN = "SW1960A0003";
    }
}