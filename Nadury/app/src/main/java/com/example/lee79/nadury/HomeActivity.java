package com.example.lee79.nadury;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import kr.go.seoul.airquality.AirQualityButtonTypeA;
import kr.go.seoul.airquality.AirQualityTypeMini;
import kr.go.seoul.culturalevents.CulturalEventButtonTypeA;
import kr.go.seoul.culturalevents.CulturalEventButtonTypeB;

public class HomeActivity extends Activity {
    ImageButton mapBtn,settingBtn;
    private CulturalEventButtonTypeA infoBtn;
    private AirQualityButtonTypeA airBtn;
    private String OpenApiKey = "69586550746c65653131304c574c6c44";
    private AirQualityTypeMini air;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
            //사용자에게 권한 요청 항목을 띄움
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            //권한이 설정되있는 경우 true
        } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            //마쉬멜로우 하위 버전인경우
        }

        infoBtn = findViewById(R.id.infobtn);
        infoBtn.setButtonImage(R.drawable.infobtn);
        infoBtn.setOpenAPIKey(OpenApiKey);

        airBtn = findViewById(R.id.airBtn);
        airBtn.setButtonImage(R.drawable.airbtn);
        airBtn.setOpenAPIKey(OpenApiKey);

        mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent mapIntent = new Intent(HomeActivity.this,MainActivity.class);
                startActivity(mapIntent);
            }
        });

        settingBtn = findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

        } else {
            CALLDialog();
            Toast.makeText(getApplicationContext(), "권한 설정을 하지 않을 시, 오류가 나타날 수 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }//end of onRequestPermissionsResult()

    //사용자에게 권한요청 요구를 위한 다이어로그를 생성
    public void CALLDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("앱 권한");
        alertDialog.setMessage("해당 앱의 원할한 기능을 이용하시려면 애플리케이션 정보>권한> 에서 모든 권한을 허용해 주십시오");

        // 권한설정 클릭시 이벤트 발생
        alertDialog.setPositiveButton("권한설정",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        //취소
        alertDialog.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "권한 설정을 하지 않을 시, 오류가 나타날 수 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialog.show();
    }

}
