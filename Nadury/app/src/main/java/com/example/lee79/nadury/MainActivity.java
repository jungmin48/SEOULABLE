package com.example.lee79.nadury;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    EditText searchEdt;
    TextView detailName,detailAddrName;
    TextView checkName,checkAddrName,checkTel,checkRunTime,checkRest,checkInfo,checkForD;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerAdapter adapter;
    ArrayList<RoadInfo> roadInfoArrayList;
    RecyclerAdapter recyclerAdapter;

    int count;

    TMapData tmapdata;
    String strData;
    String strXml = null;
    String temp = null;
    View dialogView; //메뉴 대화상자
    View detaildlgView, checkDetailDlgView;
    AlertDialog.Builder dlg1;
    AlertDialog.Builder dlg2;
    double start_x,start_y,finish_x,finish_y;
    double startx, starty,finishx,finishy;
    String start_addr,finish_addr;
    double currentLatitude,currentLongitude;
    double currentX,currentY;
    private final String mapKey = "d72d4fac-59a7-4465-87e5-abadd92188f9";
    private final String seoulAPI_disabled_Key = "69586550746c65653131304c574c6c44";

    private TMapGpsManager tMapGps = null;
    private boolean m_bTrackingMode = true;
    private TMapView tMapView = null;
    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>(); //검색 POI 마커 아이디
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>(); //검색 POI 좌표
    private ArrayList<String> m_dName = new ArrayList<String>(); //서울시 공공데이터 API에서 시설 명 받아오는 배열
    private static int mMarkerID;
    private TMapMarkerItem markerItem1,markerItem2,currentMarkerItem;
    private String checkUrl;
    TMapPoint checkT;

    private ArrayList<String> dcheckName = new ArrayList<>();
    private ArrayList<String> dcheckAddrName = new ArrayList<>();
    private ArrayList<String> dcheckTel = new ArrayList<>();
    private ArrayList<String> dcheckRunTime = new ArrayList<>();
    private ArrayList<String> dcheckRest = new ArrayList<>();
    private ArrayList<String> dcheckInfo = new ArrayList<>();
    private ArrayList<String> dcheckForD = new ArrayList<>();
    private ArrayList<Double> checkX = new ArrayList<>();
    private ArrayList<Double> checkY = new ArrayList<>();

    Intent intent;
    private boolean inName = false; //장애인 시설

    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();    //xml

        count = 0;

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        roadInfoArrayList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(roadInfoArrayList);

        tMapGps = new TMapGpsManager(MainActivity.this);
        tMapGps.setMinTime(1000);
        tMapGps.setMinDistance(5);
        tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);
        tMapGps.setProvider(tMapGps.GPS_PROVIDER);
        tMapGps.OpenGps();
        setGps();



        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey(mapKey);
        linearLayoutTmap.addView(tMapView);

        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(10);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);
        tMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList arrayList, ArrayList arrayList1, TMapPoint tMapPoint) {
                try {
                    currentMarkerItem = new TMapMarkerItem();
                    Log.d("abcde","아에이오우");
                    URL markerUrl1 = new URL("http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_b_o.png");
                    InputStream stream1 = markerUrl1.openConnection().getInputStream();
                    Bitmap bitmap1 = BitmapFactory.decodeStream(stream1);
                    currentMarkerItem.setIcon(bitmap1);
                    currentMarkerItem.setPosition(0.5f,1.0f);
                    currentMarkerItem.setTMapPoint(tMapPoint);
                    currentMarkerItem.setCanShowCallout(true);
                    currentMarkerItem.setCalloutTitle(tmapdata.convertGpsToAddress(tMapPoint.getLatitude(), tMapPoint.getLongitude()));

                    tMapView.addMarkerItem("currentMarkerItem",currentMarkerItem);
                    Bitmap bitmap_i = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.hue);
                    bitmap_i = bitmap_i.createScaledBitmap(bitmap_i, bitmap_i.getWidth() / 10, bitmap_i.getHeight() / 10, true);
                    currentMarkerItem.setCalloutRightButtonImage(bitmap_i);

                    tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                        @Override
                        public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {

                            String address;
                            detaildlgView = (View) View.inflate(MainActivity.this, R.layout.detail_dlg, null);
                            dlg1 = new AlertDialog.Builder(MainActivity.this);
                            dlg1.setTitle("상세정보");
                            dlg1.setView(detaildlgView);
                            detailName = (TextView) detaildlgView.findViewById(R.id.detailName);
                            detailAddrName = (TextView)detaildlgView.findViewById(R.id.detailAddrName);
                            try {
                                final double sstartx = tMapMarkerItem.getTMapPoint().getLatitude();
                                final double sstarty = tMapMarkerItem.getTMapPoint().getLongitude();
                                final double ffinishx = tMapMarkerItem.getTMapPoint().getLatitude();
                                final double ffinishy = tMapMarkerItem.getTMapPoint().getLongitude();
                                //startroadClick(sstartx,sstarty);
                                //startroadClick(ffinishx,ffinishy);

                                detailName.setText(tMapMarkerItem.getCalloutTitle());
                                final String startroadName = tMapMarkerItem.getCalloutTitle();
                                final String finishroadName = tMapMarkerItem.getCalloutTitle();
                                final String title = tMapMarkerItem.getCalloutTitle().substring(0,3) ;
                                final Double roadX = tMapMarkerItem.getTMapPoint().getLatitude();
                                final Double roadY = tMapMarkerItem.getTMapPoint().getLongitude();
                                //final String sstartaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                //final String ffinishaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                address = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                detailAddrName.setText(" ");
                                intent = new Intent(MainActivity.this, RoadActivity.class);

                                //detailTelNo.setText(detailTelNo.getText().toString());
                                //detailAddrName.setText(detailAddrName.getText().toString());
                                dlg1.setPositiveButton("목적지 설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish_x = ffinishx;
                                        finish_y = ffinishy;
                                        finish_addr = finishroadName;
                                        intent.putExtra("START_X", start_x);
                                        intent.putExtra("START_Y", start_y);
                                        intent.putExtra("FINISH_X", finish_x);
                                        intent.putExtra("FINISH_Y", finish_y);
                                        intent.putExtra("START_ADDR",start_addr);
                                        intent.putExtra("FINISH_ADDR",finish_addr);
                                        startActivity(intent);

                                        Log.d("TTTTTTEST",String.valueOf(start_x)+String.valueOf(start_y)+String.valueOf(finish_x)+String.valueOf(finish_y));
                                    }
                                });
                                dlg1.setNegativeButton("출발지 설정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        start_x = sstartx;
                                        start_y = sstarty;
                                        start_addr = startroadName;
                                    }
                                });
                                dlg1.setNeutralButton("경로 추가", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        roadInfoArrayList.add(new RoadInfo(startroadName,title,roadX,roadY,currentX,currentY));
                                        recyclerAdapter.notifyDataSetChanged();
                                        mRecyclerView.setAdapter(recyclerAdapter);
                                    }
                                });
                                dlg1.show();
                            }catch (Exception e){
                                Log.d("ADDRESS","No Address");
                            }
                        }
                    });
                    // Toast.makeText(MainActivity.this,String.valueOf(tMapPoint.getLatitude()),Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Log.d("abcde","No marker Url");
                }
            }
        });


        tmapdata = new TMapData();
        //disabledPlace();
    }
    //옵션메뉴
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu,menu);
        return true;
    };

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.searchMenu:
                dialogView = (View)View.inflate(MainActivity.this,R.layout.search_dlg,null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("장소 검색");
                dlg.setIcon(android.R.drawable.ic_menu_search);
                dlg.setView(dialogView);
                dlg.setPositiveButton("검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mArrayMarkerID.clear();
                        m_mapPoint.clear();
                        mMarkerID = 0;
                        tMapView.removeAllMarkerItem();

                        searchEdt = (EditText)dialogView.findViewById(R.id.search);
                        strData = searchEdt.getText().toString();
                        addPoint();
                    }
                });
                dlg.show();
                break;

            case R.id.currentPosMenu:
                tMapGps = new TMapGpsManager(MainActivity.this);
                tMapGps.setMinTime(1000);
                tMapGps.setMinDistance(5);
                tMapGps.setProvider(tMapGps.NETWORK_PROVIDER);
                tMapGps.setProvider(tMapGps.GPS_PROVIDER);
                tMapGps.OpenGps();
                setGps();
                break;

            case R.id.checkMenu:
                final String[] checkArray = new String[] {"A.전체","B.사용자 추천","C.공공시설","D.공원/관광","E.문화생활","F.병원","G.슈퍼마켓등판매점","H.음식점","I.종교시설"};
                final boolean[] checked = new boolean[]{false,false,false,false,false,false,false,false,false};
                final AlertDialog.Builder dlg1 = new AlertDialog.Builder(MainActivity.this);
                dlg1.setTitle("무장애 시설 검색");
                dlg1.setIcon(android.R.drawable.ic_menu_myplaces);

                dlg1.setMultiChoiceItems(checkArray, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                });
                dlg1.setPositiveButton("검색", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(dlg1.getContext(),"검색중입니다. 기다려주세요.",Toast.LENGTH_SHORT).show();
                        mArrayMarkerID.clear();
                        m_mapPoint.clear();
                        mMarkerID = 0;
                        tMapView.removeAllMarkerItem();
                        for(int i=0;i<checked.length ;i++){
                            if(checked[i]){
                                try{
                                    boolean inRow = false;
                                    boolean inNum = false ,inName = false, inTel = false, inAddress = false, inBizhour = false,inRest = false;
                                    boolean inInformation = false,inInformati = false,inBoardlist = false, inXX=false,inYY=false;
                                    String bName = null,bTel = null, bAddress = null, bBizhour = null, bRest = null, bInformation = null, bInformati = null, bBoardlist = null;
                                    String bXX = null,bYY = null;

                                    URL url = new URL("http://openapi.seoul.go.kr:8088/"+seoulAPI_disabled_Key+"/xml/InfoBarrierFree/1/200/");
                                    XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
                                    XmlPullParser parser = parserFactory.newPullParser();
                                    parser.setInput(url.openStream(),null);

                                    int parserEvent = parser.getEventType();
                                    Log.d("BARRIERFREE","test1");
                                    while (parserEvent != XmlPullParser.END_DOCUMENT){
                                        switch (parserEvent){
                                            case XmlPullParser.START_TAG:
                                                if(parser.getName().equals("NUM")){
                                                    inNum = true;
                                                }if(parser.getName().equals("NAME")){
                                                inName = true;
                                            }if(parser.getName().equals("TEL")){
                                                inTel = true;
                                            }if(parser.getName().equals("ADDRESS")){
                                                inAddress = true;
                                            }if(parser.getName().equals("BIZHOUR")){
                                                inBizhour = true;
                                            }if(parser.getName().equals("REST")){
                                                inRest = true;
                                            }if(parser.getName().equals("INFORMATION")){
                                                inInformation = true;
                                            }if(parser.getName().equals("INFOMATI_1")){
                                                inInformati = true;
                                            }if(parser.getName().equals("BOARD_LIST")){
                                                inBoardlist = true;
                                            }if(parser.getName().equals("XX")){
                                                inXX = true;
                                            }if(parser.getName().equals("YY")){
                                                inYY = true;
                                            }
                                                break;
                                            case XmlPullParser.TEXT:
                                                if(inName){
                                                    if(parser.getText() != null){
                                                        bName = parser.getText();
                                                        inName = false;
                                                    }
                                                    else{
                                                        bName = "-";
                                                        inName = false;
                                                    }
                                                }if(inAddress){
                                                if(parser.getText() != null){
                                                    bAddress = parser.getText();
                                                    inAddress = false;
                                                }else{
                                                    bAddress = "-";
                                                    inAddress = false;
                                                }
                                            }if(inTel){
                                                if(parser.getText() != null){
                                                    bTel = parser.getText();
                                                    inTel = false;
                                                }else{
                                                    bTel = "-";
                                                    inTel = false;
                                                }
                                            }if(inBizhour){
                                                if(parser.getText() != null){
                                                    bBizhour = parser.getText();
                                                    inBizhour = false;
                                                }else{
                                                    bBizhour = "-";
                                                    inBizhour = false;
                                                }
                                            }if(inRest){
                                                if(parser.getText() != null){
                                                    bRest = parser.getText();
                                                    inRest = false;
                                                }else{
                                                    bRest = "-";
                                                    inRest = false;
                                                }
                                            }if(inInformation){
                                                if(parser.getText() != null){
                                                    bInformation = parser.getText();
                                                    inInformation = false;
                                                }else{
                                                    bInformation = "-";
                                                    inInformation = false;
                                                }
                                            }if(inInformati){
                                                if(parser.getText() != null){
                                                    bInformati = parser.getText();
                                                    inInformati = false;
                                                }else{
                                                    bInformati = "-";
                                                    inInformati = false;
                                                }
                                            }if(inBoardlist){
                                                if(parser.getText() != null){
                                                    bBoardlist = parser.getText();
                                                    inBoardlist = false;
                                                }else{
                                                    bBoardlist = "-";
                                                    inBoardlist = false;
                                                }
                                            }if(inXX){
                                                if(parser.getText() != null){
                                                    bXX = parser.getText();
                                                    inXX = false;
                                                }else{
                                                    bXX = "-";
                                                    inXX = false;
                                                }
                                            }if(inYY){
                                                if(parser.getText() != null){
                                                    bYY = parser.getText();
                                                    inYY = false;
                                                }else{
                                                    bYY = "-";
                                                    inYY = false;
                                                }
                                            }
                                                break;
                                            case XmlPullParser.END_TAG:
                                                if(parser.getName().equals("row")){
                                                    switch (i){
                                                        case 0:
                                                            //전체
                                                            if(bBoardlist.equals("공공시설") || bBoardlist.equals("체육관") || bBoardlist.equals("공원") || bBoardlist.equals("관광지") || bBoardlist.equals("공연장/영화관") || bBoardlist.equals("전시장") || bBoardlist.equals("병원") || bBoardlist.equals("슈퍼마켓 등 판매점") || bBoardlist.equals("음식점") || bBoardlist.equals("종교시설")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_a.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 1:
                                                            //사용자 추천 마커 표시
                                                            checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_b_b.png";
                                                            checkX.add(126.85608);
                                                            checkY.add(37.51095);
                                                            dcheckName.add("계남근린공원 제2공원");
                                                            dcheckAddrName.add("서울시 양천구 신정3동 621");
                                                            dcheckTel.add("02-2620-3570");
                                                            dcheckRunTime.add(" ");
                                                            dcheckRest.add(" ");
                                                            dcheckInfo.add("넓은공원. 운동시설과 약수터 등 있음.  장애인용  화장실은 넓고 좋음.  교통편이 조금 불편함.");
                                                            dcheckForD.add("장애인용화장실 지상버스접근가능");
                                                            addMarker();
                                                            count++;
                                                            break;

                                                        case 2:
                                                            //공공시설 + 체육관 마커표시
                                                            if(bBoardlist.equals("공공시설") || bBoardlist.equals("체육관")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_c.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 3:
                                                            //공원 + 관광지 마커표시
                                                            if(bBoardlist.equals("공원") || bBoardlist.equals("관광지")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_d.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 4:
                                                            //공연장/영화관 + 전시장 마커표시
                                                            if(bBoardlist.equals("공연장/영화관") || bBoardlist.equals("전시장")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_e.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 5:
                                                            if(bBoardlist.equals("병원")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_f.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 6:
                                                            //슈퍼마켓등판매점 마커표시
                                                            if(bBoardlist.equals("슈퍼마켓 등 판매점")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_g.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                        case 7:
                                                            //음식점 마커표시
                                                            if(bBoardlist.equals("음식점")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_h.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                Log.d("fhf","test1");
                                                                break;
                                                            }
                                                            else break;
                                                        case 8:
                                                            //종교시설 마커표시
                                                            if(bBoardlist.equals("종교시설")){
                                                                checkUrl = "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_i.png";
                                                                checkX.add(Double.valueOf(bXX));
                                                                checkY.add(Double.valueOf(bYY));
                                                                dcheckName.add(bName);
                                                                dcheckAddrName.add(bAddress);
                                                                dcheckTel.add(bTel);
                                                                dcheckRunTime.add(bBizhour);
                                                                dcheckRest.add(bRest);
                                                                dcheckInfo.add(bInformation);
                                                                dcheckForD.add(bInformati);
                                                                addMarker();
                                                                count++;
                                                                break;
                                                            }
                                                            else break;
                                                    }
                                                }
                                                break;
                                        }
                                        parserEvent = parser.next();
                                        Log.d("fhf","test2");
                                    }
                                }catch (Exception e){
                                    Log.d("BARRIERFREE" , "ERROR");
                                }
                            }
                        }
                    }
                });
                dlg1.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private final LocationListener mLocationListener = new LocationListener(){
        public void onLocationChanged(Location location){
            if(location != null){
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                tMapView.setLocationPoint(currentLongitude,currentLatitude);
                tMapView.setCenterPoint(currentLongitude,currentLatitude);
                Log.d("TmapTest",""+ currentLongitude +","+currentLatitude);
                currentX = currentLatitude;
                currentY = currentLongitude;
            }
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){}
    };

    public void setGps(){
        final LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,mLocationListener);
    }

    public void addPoint() {

        tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                Log.d("strData",strData);
                for (int i = 0; i < poiItem.size(); i++) {
                    if (poiItem.get(i).upperAddrName.equals("서울")) {
                        markerItem1 = new TMapMarkerItem();
                        try{
                            URL markerUrl = new URL("http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_a.png");
                            InputStream stream = markerUrl.openConnection().getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            markerItem1.setIcon(bitmap);
                            markerItem1.setPosition(0.5f, 1.0f);
                        }catch (Exception e){
                            Log.d("TAG","마커 이미지 없음");
                        }


                        TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                        TMapPoint point = item.getPOIPoint();

                        m_mapPoint.add(new MapPoint(item.getPOIName(), item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude()));

                        markerItem1.setTMapPoint(point);
                        markerItem1.setCanShowCallout(true);
                        markerItem1.setCalloutTitle(m_mapPoint.get(i).getName());
                        tMapView.setCenterPoint(item.getPOIPoint().getLongitude(),item.getPOIPoint().getLatitude());

                        Bitmap bitmap_i = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.hue);
                        bitmap_i = bitmap_i.createScaledBitmap(bitmap_i, bitmap_i.getWidth() / 10, bitmap_i.getHeight() / 10, true);
                        markerItem1.setCalloutRightButtonImage(bitmap_i);
                        //final Bitmap detailBit = poiItem.get(i).Icon;
                        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                            @Override
                            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {

                                String address;
                                detaildlgView = (View) View.inflate(MainActivity.this, R.layout.detail_dlg, null);
                                dlg1 = new AlertDialog.Builder(MainActivity.this);
                                dlg1.setTitle("상세정보");
                                dlg1.setView(detaildlgView);
                                detailName = (TextView) detaildlgView.findViewById(R.id.detailName);
                                //detailImage = (ImageView)detaildlgView.findViewById(R.id.detailImage);
                                detailAddrName = (TextView) detaildlgView.findViewById(R.id.detailAddrName);
                                try {
                                    final double sstartx = tMapMarkerItem.getTMapPoint().getLatitude();
                                    final double sstarty = tMapMarkerItem.getTMapPoint().getLongitude();
                                    final double ffinishx = tMapMarkerItem.getTMapPoint().getLatitude();
                                    final double ffinishy = tMapMarkerItem.getTMapPoint().getLongitude();
                                    //startroadClick(sstartx,sstarty);
                                    //startroadClick(ffinishx,ffinishy);

                                    detailName.setText(tMapMarkerItem.getCalloutTitle());
                                    final String roadName = tMapMarkerItem.getCalloutTitle();
                                    final String startroadName = tMapMarkerItem.getCalloutTitle();
                                    final String finishroadName = tMapMarkerItem.getCalloutTitle();
                                    final String title = tMapMarkerItem.getCalloutTitle().substring(0,3) ;
                                    final Double roadX = tMapMarkerItem.getTMapPoint().getLatitude();
                                    final Double roadY = tMapMarkerItem.getTMapPoint().getLongitude();
                                    final String sstartaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                    final String ffinishaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                    address = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                                    detailAddrName.setText(address);
                                    intent = new Intent(MainActivity.this, RoadActivity.class);

                                    //detailTelNo.setText(detailTelNo.getText().toString());
                                    //detailAddrName.setText(detailAddrName.getText().toString());
                                    dlg1.setPositiveButton("목적지 설정", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish_x = ffinishx;
                                            finish_y = ffinishy;
                                            finish_addr = finishroadName;
                                            intent.putExtra("START_X", start_x);
                                            intent.putExtra("START_Y", start_y);
                                            intent.putExtra("FINISH_X", finish_x);
                                            intent.putExtra("FINISH_Y", finish_y);
                                            intent.putExtra("START_ADDR",start_addr);
                                            intent.putExtra("FINISH_ADDR",finish_addr);
                                            startActivity(intent);

                                            Log.d("TTTTTTEST",String.valueOf(start_x)+String.valueOf(start_y)+String.valueOf(finish_x)+String.valueOf(finish_y));
                                        }
                                    });
                                    dlg1.setNegativeButton("출발지 설정", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            start_x = sstartx;
                                            start_y = sstarty;
                                            start_addr = startroadName;
                                        }
                                    });
                                    dlg1.setNeutralButton("경로 추가", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            roadInfoArrayList.add(new RoadInfo(roadName,title,roadX,roadY,currentX,currentY));
                                            recyclerAdapter.notifyDataSetChanged();
                                            mRecyclerView.setAdapter(recyclerAdapter);
                                        }
                                    });
                                    dlg1.show();
                                }catch (Exception e){
                                    Log.d("ADDRESS","No Address");
                                }
                            }
                        });
                        String strID = String.format("pmarker%d", mMarkerID++);
                        if(i==0){
                            markerItem1.setAutoCalloutVisible(true);
                        }
                        tMapView.addMarkerItem(strID, markerItem1);
                        mArrayMarkerID.add(strID);

                        //detailName.setText(poiItem.get(i).name);
                        Log.d("TAG", "명칭 : " + poiItem.get(i).name);
                        Log.d("TAG", "서울 : " + poiItem.get(i).upperBizName);
                        Log.d("DETAILLOG",""+poiItem.get(i).additionalInfo);
                        Log.d("DETAILLOG",""+poiItem.get(i).homepageURL);
                        Log.d("DETAILLOG",""+poiItem.get(i).merchanFlag);
                        Log.d("DETAILLOG",""+poiItem.get(i).desc);
                    }
                    else{
                        TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                        TMapPoint point = item.getPOIPoint();

                        m_mapPoint.add(new MapPoint(item.getPOIName(), item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude()));

                        String strID = String.format("pmarker%d", mMarkerID++);
                        mArrayMarkerID.add(strID);
                    }
                }
            }
        });
    }

    public void startroadClick(double start_x,double start_y){
        this.start_x = startx;
        this.start_y = starty;
    }
    public void finishroadClick(double finish_x,double finish_y){
        finishx = this.finish_x;
        finishy = this.finish_y;
    }

    public void addMarker(){
        Log.d("fhf","test3");
        try {
            checkT = new TMapPoint(checkY.get(count),checkX.get(count));
            markerItem2 = new TMapMarkerItem();
            URL markerUrl1 = new URL(checkUrl);
            InputStream stream1 = markerUrl1.openConnection().getInputStream();
            Bitmap bitmap1 = BitmapFactory.decodeStream(stream1);
            markerItem2.setIcon(bitmap1);
            markerItem2.setPosition(0.5f,1.0f);
            markerItem2.setTMapPoint(checkT);
            markerItem2.setCanShowCallout(true);
            markerItem2.setCalloutTitle(dcheckName.get(count));
            Log.d("fhf",dcheckName.get(count) + String.valueOf(count));

            Bitmap bitmap_i = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.hue);
            bitmap_i = bitmap_i.createScaledBitmap(bitmap_i, bitmap_i.getWidth() / 10, bitmap_i.getHeight() / 10, true);
            markerItem2.setCalloutRightButtonImage(bitmap_i);

            tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                @Override
                public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                    Log.d("fhf","test4");
                    String address;
                    checkDetailDlgView = (View) View.inflate(MainActivity.this, R.layout.checkdetail_dlg, null);
                    checkName = (TextView) checkDetailDlgView.findViewById(R.id.checkName);
                    checkAddrName = (TextView)checkDetailDlgView.findViewById(R.id.checkAddrName);
                    checkTel = (TextView) checkDetailDlgView.findViewById(R.id.checkTel);
                    checkRunTime = (TextView) checkDetailDlgView.findViewById(R.id.checkRunTime);
                    checkRest = (TextView) checkDetailDlgView.findViewById(R.id.checkRest);
                    checkInfo = (TextView) checkDetailDlgView.findViewById(R.id.checkInfo);
                    checkForD = (TextView) checkDetailDlgView.findViewById(R.id.checkForD);

                    dlg2 = new AlertDialog.Builder(MainActivity.this);
                    dlg2.setTitle("상세정보");
                    dlg2.setView(checkDetailDlgView);

                    try {
                        final double sstartx = tMapMarkerItem.getTMapPoint().getLatitude();
                        final double sstarty = tMapMarkerItem.getTMapPoint().getLongitude();
                        final double ffinishx = tMapMarkerItem.getTMapPoint().getLatitude();
                        final double ffinishy = tMapMarkerItem.getTMapPoint().getLongitude();
                        //startroadClick(sstartx,sstarty);
                        //startroadClick(ffinishx,ffinishy);
                        checkName.setText(tMapMarkerItem.getCalloutTitle());
                        checkAddrName.setText(dcheckAddrName.get(Integer.valueOf(tMapMarkerItem.getID())));
                        checkTel.setText(dcheckTel.get(Integer.valueOf(tMapMarkerItem.getID())));
                        checkRunTime.setText(dcheckRunTime.get(Integer.valueOf(tMapMarkerItem.getID())));
                        checkRest.setText(dcheckRest.get(Integer.valueOf(tMapMarkerItem.getID())));
                        checkInfo.setText(dcheckInfo.get(Integer.valueOf(tMapMarkerItem.getID())));
                        checkForD.setText(dcheckForD.get(Integer.valueOf(tMapMarkerItem.getID())));
                        Log.d("fhf",String.valueOf(count));
                        final String roadName = tMapMarkerItem.getCalloutTitle();
                        final String title = tMapMarkerItem.getCalloutTitle().substring(0,3) ;
                        final Double roadX = tMapMarkerItem.getTMapPoint().getLatitude();
                        final Double roadY = tMapMarkerItem.getTMapPoint().getLongitude();
                        final String sstartaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                        final String ffinishaddr = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                        address = tmapdata.convertGpsToAddress(tMapMarkerItem.getTMapPoint().getLatitude(), tMapMarkerItem.getTMapPoint().getLongitude());
                        intent = new Intent(MainActivity.this, RoadActivity.class);

                        //detailTelNo.setText(detailTelNo.getText().toString());
                        //detailAddrName.setText(detailAddrName.getText().toString());
                        dlg2.setPositiveButton("목적지 설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish_x = ffinishx;
                                finish_y = ffinishy;
                                finish_addr = ffinishaddr;
                                intent.putExtra("START_X", start_x);
                                intent.putExtra("START_Y", start_y);
                                intent.putExtra("FINISH_X", finish_x);
                                intent.putExtra("FINISH_Y", finish_y);
                                intent.putExtra("START_ADDR",start_addr);
                                intent.putExtra("FINISH_ADDR",finish_addr);
                                startActivity(intent);

                                Log.d("TTTTTTEST",String.valueOf(start_x)+String.valueOf(start_y)+String.valueOf(finish_x)+String.valueOf(finish_y));
                            }
                        });
                        dlg2.setNegativeButton("출발지 설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                start_x = sstartx;
                                start_y = sstarty;
                                start_addr = sstartaddr;
                            }
                        });
                        dlg2.setNeutralButton("경로 추가", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                roadInfoArrayList.add(new RoadInfo(roadName,title,roadX,roadY,currentX,currentY));
                                recyclerAdapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(recyclerAdapter);
                            }
                        });
                        dlg2.show();
                    }catch (Exception e){
                        Log.d("fhf","No Address");
                    }
                }
            });
            // Toast.makeText(MainActivity.this,String.valueOf(tMapPoint.getLatitude()),Toast.LENGTH_SHORT).show();

            String strID = String.format("%d", mMarkerID++);
            tMapView.addMarkerItem(strID, markerItem2);
            mArrayMarkerID.add(strID);
            Log.d("fhf",mArrayMarkerID.get(0)+""+mArrayMarkerID.get(1)+"");
        }catch (Exception e){
            Log.d("fhf","No marker Url");
        }
    }
}


