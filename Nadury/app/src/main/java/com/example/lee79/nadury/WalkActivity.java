package com.example.lee79.nadury;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class WalkActivity extends AppCompatActivity {
    String road_stx, road_sty, road_fnx, road_fny;
    Double road_stx1, road_sty1, road_fnx1, road_fny1;
    private final String mapKey = "135fed05-084d-4c9f-8a62-12219b430862";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("도보 경로");

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap2);
        final TMapView tMapView2 = new TMapView(this);

        tMapView2.setSKTMapApiKey(mapKey);
        linearLayoutTmap.addView(tMapView2);

        Intent intent = getIntent();
        road_stx = intent.getExtras().getString("ROAD_STX");
        road_sty = intent.getExtras().getString("ROAD_STY");
        road_fnx = intent.getExtras().getString("ROAD_FNX");
        road_fny = intent.getExtras().getString("ROAD_FNY");

        road_stx1 = Double.parseDouble(road_stx);
        road_sty1 = Double.parseDouble(road_sty);
        road_fnx1 = Double.parseDouble(road_fnx);
        road_fny1 = Double.parseDouble(road_fny);
        Log.d("peak", road_stx);
        Log.d("peak", road_sty);
        Log.d("peak", road_fnx);
        Log.d("peak", road_fny);

        TMapData tmapdata = new TMapData();
        TMapPoint startpoint = new TMapPoint(road_stx1, road_sty1);
        TMapPoint endpoint = new TMapPoint(road_fny1, road_fnx1);
        TMapPolyLine polyline = new TMapPolyLine();
        tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, endpoint,
                new TMapData.FindPathDataListenerCallback() {
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tMapView2.addTMapPath(polyLine);
                    }
                }
        );
        tMapView2.setCenterPoint(road_sty1,road_stx1);
    }
}
