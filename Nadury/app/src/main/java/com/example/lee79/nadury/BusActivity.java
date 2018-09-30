package com.example.lee79.nadury;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.odsay.odsayandroidsdk.URL;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BusActivity extends AppCompatActivity {
    XmlPullParser xpp;
    private static final String SERVICE_KEY = "BopQLms61qJWbMIRxXjzER51oXHat6TF%252FdTPGrt6zChxCrM4fIfd7A3mCbLgcTZdhkG3FzxTwKAL%252B%252FpKx%252FJNnQ%253D%253D";
    String busNum, startName, stationID;
    TextView busNum2, busStop, busStopTime;
    String arsID;
    String arsID2, data;
    int path;
    String road_stx, road_sty;
    double road_stx2, road_sty2;
    Context context;
    TMapView tMapView2;
    Bitmap bitmap;
    java.net.URL url, url1;
    String s = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        ODsayService odsayService = ODsayService.init(getApplicationContext(), "fKtH8toBYGmogRxStaGwRNDagurMB1epwF99ClaZhd4");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        StrictMode.enableDefaults();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("저상버스 도착정보 / 정류장 위치정보");

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap2);
        tMapView2 = new TMapView(this);

        tMapView2.setSKTMapApiKey( "d72d4fac-59a7-4465-87e5-abadd92188f9" );
        linearLayoutTmap.addView( tMapView2 );
        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        tMapView2.setZoomLevel(15);

        busNum2 = findViewById(R.id.busNum);
        busStop = findViewById(R.id.busStop);
        busStopTime = findViewById(R.id.busStopTime);

        Intent intent = getIntent();
        busNum = intent.getExtras().getString("BUS_NUM");
        startName = intent.getExtras().getString("START_NAME");
        road_stx = intent.getExtras().getString("ROAD_STX");
        road_sty = intent.getExtras().getString("ROAD_STY");
        road_stx2 = Double.parseDouble(road_stx);
        road_sty2 = Double.parseDouble(road_sty);
        stationID = intent.getExtras().getString("STATION_ID");
        Log.d("path", Integer.toString(path));
        busNum2.setText(busNum + "번");
        busStop.setText(startName);

        TMapPoint tMapPoint1 = new TMapPoint(road_sty2,road_stx2); // SKT타워
        Log.d("road_stx",Double.toString(road_stx2));
// 마커 아이콘
        tMapView2.setCenterPoint(road_stx2, road_sty2 );
        markerItem1 = new TMapMarkerItem();
        try{
            java.net.URL markerUrl = new java.net.URL("http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_b_a.png");

            InputStream stream = markerUrl.openConnection().getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            markerItem1.setIcon(bitmap);
            markerItem1.setPosition(0.5f, 1.0f);
            markerItem1.setTMapPoint(tMapPoint1);
            tMapView2.addMarkerItem("markerItem1",markerItem1);
        }catch (Exception e){
            Log.d("TAG","마커 이미지 없음");
        }

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    if (api == API.BUS_STATION_INFO) {
                        String good, job;
                        arsID = odsayData.getJson().getJSONObject("result").getString("arsID");
                        Log.d("ars", arsID);
                        good = arsID.substring(0, arsID.indexOf('-'));
                        job = arsID.substring(3);
                        arsID2 = good + job;
                        Log.d("mint", arsID2);
                        new GetXMLTask().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s, API api) {
                if (api == API.BUS_STATION_INFO) {
                    Toast.makeText(BusActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        };
        odsayService.requestBusStationInfo(stationID, onResultCallbackListener);
    }
    public void onClick(View view){
        GetXMLTask2 task = new GetXMLTask2();
        task.execute("http://ws.bus.go.kr/api/rest/arrive/getLowArrInfoByStId?serviceKey=BopQLms61qJWbMIRxXjzER51oXHat6TF%2FdTPGrt6zChxCrM4fIfd7A3mCbLgcTZdhkG3FzxTwKAL%2B%2FpKx%2FJNnQ%3D%3D&stId="+s);
    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls){
            java.net.URL url;
            Document doc = null;
            try{
                url = new java.net.URL("http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey=BopQLms61qJWbMIRxXjzER51oXHat6TF%2FdTPGrt6zChxCrM4fIfd7A3mCbLgcTZdhkG3FzxTwKAL%2B%2FpKx%2FJNnQ%3D%3D&arsId="+arsID2);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("JM","error");
            }
            return doc;
        }
        @Override
        protected void onPostExecute(Document doc){
            try {
                NodeList nodeList = doc.getElementsByTagName("itemList");
                Node node = nodeList.item(0);
                Element fstElmnt = (Element) node;
                NodeList idx = fstElmnt.getElementsByTagName("stId");
                s = idx.item(0).getChildNodes().item(0).getNodeValue();
                super.onPostExecute(doc);
            }catch (Exception e){
                Log.d("error","error");
            }
            }
    }

    private class GetXMLTask2 extends AsyncTask<String, Void, Document>{
        @Override
        protected Document doInBackground(String... urls){
            java.net.URL url2;
            Document doc2 = null;
            try{
                url2 = new java.net.URL(urls[0]);
                DocumentBuilderFactory dbf2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder db2 = dbf2.newDocumentBuilder();

                doc2 = db2.parse(new InputSource(url2.openStream()));
                doc2.getDocumentElement().normalize();
            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                Log.d("JM","error");
            }
            return doc2;
        }
        @Override
        protected void onPostExecute(Document doc2){
            String w = "";
            String g = "";
            NodeList nodeList2 = doc2.getElementsByTagName("itemList");

            for (int i =0 ; i<nodeList2.getLength(); i++){
                Node node2 = nodeList2.item(i);
                Element fstElmnt2 =(Element)node2;
                NodeList idx3 = fstElmnt2.getElementsByTagName("rtNm");
                g = idx3.item(0).getChildNodes().item(0).getNodeValue();
                if (g.equals(busNum)){
                    NodeList idx2 = fstElmnt2.getElementsByTagName("arrmsg1");
                    w+= idx2.item(0).getChildNodes().item(0).getNodeValue();
                    break;
                }
            }
            Log.d("JM","error");
            busStopTime.setText(w);
            super.onPostExecute(doc2);
        }
    }
}

