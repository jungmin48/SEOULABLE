package com.example.lee79.nadury;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class SubwayActivity extends AppCompatActivity {
    XmlPullParser xpp;
    private static final String SERVICE_KEY = "BopQLms61qJWbMIRxXjzER51oXHat6TF%252FdTPGrt6zChxCrM4fIfd7A3mCbLgcTZdhkG3FzxTwKAL%252B%252FpKx%252FJNnQ%253D%253D";
    String subNum, subName;
    TextView subNum2, subStop, subStopTime;
    String s = "";
    String data;
    int path;
    String road_stx, road_sty;
    double road_stx2, road_sty2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("지하철역 위치정보");

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap3);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey( "135fed05-084d-4c9f-8a62-12219b430862" );
        linearLayoutTmap.addView( tMapView );
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        subNum2 = findViewById(R.id.subNum);
        subStop = findViewById(R.id.subStop);
        subStopTime = findViewById(R.id.subStopTime);

        Intent intent = getIntent();
        subNum = intent.getExtras().getString("SUB_NUM");
        subName = intent.getExtras().getString("START_NAME");
        road_stx = intent.getExtras().getString("ROAD_STX");
        road_sty = intent.getExtras().getString("ROAD_STY");
        road_stx2 = Double.parseDouble(road_stx);
        road_sty2 = Double.parseDouble(road_sty);
        Log.d("path", Integer.toString(path));
        subNum2.setText(subNum);
        subStop.setText(subName);

        TMapPoint tMapPoint1 = new TMapPoint(road_sty2,road_stx2); // SKT타워
        Log.d("road_stx",Double.toString(road_stx2));
// 마커 아이콘
        tMapView.setCenterPoint(road_stx2, road_sty2 );
        markerItem1 = new TMapMarkerItem();
        try{
            java.net.URL markerUrl = new java.net.URL("http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_a.png");
            InputStream stream = markerUrl.openConnection().getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            markerItem1.setIcon(bitmap);
            markerItem1.setPosition(0.5f, 1.0f);
            markerItem1.setTMapPoint(tMapPoint1);
            tMapView.addMarkerItem("markerItem1",markerItem1);
        }catch (Exception e){
            Log.d("TAG","마커 이미지 없음");
        }

    }
}
