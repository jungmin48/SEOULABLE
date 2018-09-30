package com.example.lee79.nadury;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoadDetailActivity extends AppCompatActivity {
    ArrayList<Detail2> Items2;
    ArrayAdapter<String> Adapter2;
    ListView listView2;
    int path;
    JSONObject obj;
    CustomAdapter2 cAdapter2;
    double startx, starty, finishx, finishy;
    String sectionTime3, ww="";
    double sectionTime2;

    TextView allTime, busStopTime;
    int totalTime2=0;
    String totalTime, startName, endName, subwayCode, distance;
    TextView cost, walkTime;
    String firstStartStation,busNo,subwayTransitCount,lastEndStation,payment,trafficType, totalWalk, sectionTime, trafficType2;
    String[] road_stx=new String[10],road_sty=new String[10],road_fnx=new String[10],road_fny=new String[10], stationID = new String[10];
    String[] start_name = new String[10],bus_No = new String[10], sub_No = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_detail);
        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("경로 상세 정보");

        path = intent.getExtras().getInt("NUM");
        startx = intent.getExtras().getDouble("START_X");
        starty = intent.getExtras().getDouble("START_Y");
        finishx = intent.getExtras().getDouble("FINISH_X");
        finishy = intent.getExtras().getDouble("FINISH_Y");
        Log.d("cafe", ""+path);

        Items2 = new ArrayList<Detail2>();
        listView2 = findViewById(R.id.listView2);
        listView2.setAdapter(Adapter2);

        allTime = findViewById(R.id.allTime);
        cost = findViewById(R.id.cost);
        walkTime = findViewById(R.id.walktime);

        ODsayService odsayService = ODsayService.init(getApplicationContext(), "fKtH8toBYGmogRxStaGwRNDagurMB1epwF99ClaZhd4");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        double sum = 0.0;
                        obj = odsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(path);
                        firstStartStation = obj.getJSONObject("info").getString("firstStartStation");
                        subwayTransitCount = obj.getJSONObject("info").getString("subwayTransitCount");
                        lastEndStation = obj.getJSONObject("info").getString("lastEndStation");
                        payment = obj.getJSONObject("info").getString("payment");
                        totalWalk = obj.getJSONObject("info").getString("totalWalk");

                        int s = obj.getJSONArray("subPath").length();
                        int w = 0;

                        for (int l = 0; l < s; l++) {
                            trafficType = obj.getJSONArray("subPath").getJSONObject(l).getString("trafficType");
                            sectionTime = obj.getJSONArray("subPath").getJSONObject(l).getString("sectionTime");
                            Log.d("busbus",ww);
                            if (trafficType.equals("1")) {
                                startName = obj.getJSONArray("subPath").getJSONObject(l).getString("startName");
                                subwayCode = obj.getJSONArray("subPath").getJSONObject(l).getJSONArray("lane").getJSONObject(0).getString("name");
                                totalTime2 += Integer.parseInt(sectionTime);
                                start_name[l] = startName;
                                sub_No[l] = subwayCode;
                                road_stx[l] = road_fnx[l-1];
                                road_sty[l] = road_fny[l-1];
                                Items2.add(new Detail2(R.drawable.subway, startName + "역", subwayCode, sectionTime + "분"));
                            } else if (trafficType.equals("2")) {
                                stationID[l] = obj.getJSONArray("subPath").getJSONObject(l).getString("startID");
                                totalTime2 += Integer.parseInt(sectionTime);
                                startName = obj.getJSONArray("subPath").getJSONObject(l).getString("startName");
                                busNo = obj.getJSONArray("subPath").getJSONObject(l).getJSONArray("lane").getJSONObject(0).getString("busNo");
                                Items2.add(new Detail2(R.drawable.bus2, startName, busNo + "번", sectionTime + "분"));
                                start_name[l] = startName;
                                bus_No[l] = busNo;
                                road_stx[l] = road_fnx[l-1];
                                road_sty[l] = road_fny[l-1];
                            } else if (trafficType.equals("3")) {
                                distance = obj.getJSONArray("subPath").getJSONObject(l).getString("distance");
                                sectionTime2 = Double.parseDouble(sectionTime) * 1.5;
                                sectionTime3 = Double.toString(sectionTime2);
                                totalTime2 += sectionTime2;
                                if (l == 0) {
                                    Items2.add(new Detail2(R.drawable.walk, "출발지", "도보 " + distance + "m", sectionTime3 + "분"));
                                    road_stx[l] = Double.toString(startx);
                                    road_sty[l] = Double.toString(starty);
                                    road_fnx[l] = obj.getJSONArray("subPath").getJSONObject(l + 1).getString("startX");
                                    road_fny[l] = obj.getJSONArray("subPath").getJSONObject(l + 1).getString("startY");
                                    w++;
                                } else if (l == s - 1) {
                                    endName = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endName");
                                    Items2.add(new Detail2(R.drawable.walk, endName + " 하차", "도보 " + distance + "m", sectionTime3 + "분"));
                                    road_sty[l] = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endX");
                                    road_stx[l] = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endY");
                                    road_fnx[l] = Double.toString(finishy);
                                    road_fny[l] = Double.toString(finishx);
                                } else {
                                    endName = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endName");
                                    Items2.add(new Detail2(R.drawable.walk, endName + " 하차", "도보 " + distance + "m", sectionTime3 + "분"));
                                    road_sty[l] = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endX");
                                    road_stx[l] = obj.getJSONArray("subPath").getJSONObject(l - 1).getString("endY");
                                    road_fnx[l] = obj.getJSONArray("subPath").getJSONObject(l + 1).getString("startX");
                                    road_fny[l] = obj.getJSONArray("subPath").getJSONObject(l + 1).getString("startY");
                                    w++;
                                }
                                sum += Double.parseDouble(sectionTime3);

                            }
                        }
                        totalTime = Integer.toString(totalTime2);
                        allTime.setText(totalTime2 + "분");
                        walkTime.setText("도보 " + sum + "분 | ");
                        cost.setText("카드 " + payment + "원");

                        cAdapter2 = new CustomAdapter2(Items2, RoadDetailActivity.this);
                        listView2.setAdapter(cAdapter2);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SEARCH_PUB_TRANS_PATH) {
                    Toast.makeText(RoadDetailActivity.this,"error",Toast.LENGTH_LONG).show();
                }
            }
        };
        odsayService.requestSearchPubTransPath(String.valueOf(starty), String.valueOf(startx), String.valueOf(finishy), String.valueOf(finishx),"0","0","0",onResultCallbackListener);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent2 = new Intent(RoadDetailActivity.this, WalkActivity.class);
                    Intent intent3 = new Intent(RoadDetailActivity.this, BusActivity.class);
                    Intent intent4 = new Intent(RoadDetailActivity.this, SubwayActivity.class);
                    try{
                        trafficType2 = obj.getJSONArray("subPath").getJSONObject(i).getString("trafficType");
                        Log.d("good","gooood");
                    if (trafficType2.equals("3")) {
                        intent2.putExtra("ROAD_STX", road_stx[i]);
                        intent2.putExtra("ROAD_STY", road_sty[i]);
                        intent2.putExtra("ROAD_FNX", road_fnx[i]);
                        intent2.putExtra("ROAD_FNY", road_fny[i]);
                        Log.d("ultra", "walk");
                        setResult(RESULT_OK, intent2);
                        startActivity(intent2);
                    } else if (trafficType2.equals("1")) {
                        intent4.putExtra("ROAD_STX", road_stx[i]);
                        intent4.putExtra("ROAD_STY", road_sty[i]);
                        Log.d("ultra", "subway");
                        intent4.putExtra("SUB_NUM",sub_No[i]);
                        intent4.putExtra("START_NAME",start_name[i]);
                        setResult(RESULT_OK, intent4);
                        startActivity(intent4);
                    } else if (trafficType2.equals("2")) {
                        Log.d("ultra", road_stx[i]);
                        intent3.putExtra("ROAD_STX", road_stx[i]);
                        intent3.putExtra("ROAD_STY", road_sty[i]);
                        intent3.putExtra("STATION_ID",stationID[i]);
                        intent3.putExtra("BUS_NUM",bus_No[i]);
                        intent3.putExtra("START_NAME",start_name[i]);
                        setResult(RESULT_OK, intent3);
                        startActivity(intent3);
                    }}catch (Exception e){
                        Log.d("errorrr","errorrr");
                    }
            }
        });
    }
}

class Detail2{
    int imageView;
    String name2, number, texttext;
    public Detail2(int imageView, String name2, String number, String texttext){
        this.imageView = imageView;
        this.name2 = name2;
        this.number = number;
        this.texttext = texttext;
    }
    public int getImageView(){
        return imageView;
    }
    public String getName2(){
        return name2;
    }
    public String getNumber(){
        return number;
    }
    public String getTexttext() {
        return texttext;
    }
}
class CustomAdapter2 extends ArrayAdapter<Detail2>{
    private ArrayList<Detail2> dataSet2;
    Context mContext2;

    private static class ViewHolder{
        ImageView imageView1;
        TextView txt1;
        TextView txt2;
        TextView txt3;
    }
    public CustomAdapter2(ArrayList<Detail2> data2, Context context2){
        super(context2, R.layout.listitem2, data2);
        this.dataSet2 = data2;
        this.mContext2 = context2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Detail2 dataModel2 = getItem(position);
        ViewHolder viewHolder2;
        final View result2;

        if (convertView == null){
            viewHolder2 = new ViewHolder();
            LayoutInflater inflater2 = LayoutInflater.from(getContext());
            convertView = inflater2.inflate(R.layout.listitem2, parent, false);
            viewHolder2.imageView1 = convertView.findViewById(R.id.image);
            viewHolder2.txt1 = convertView.findViewById(R.id.name2);
            viewHolder2.txt2 = convertView.findViewById(R.id.number);
            viewHolder2.txt3 = convertView.findViewById(R.id.texttext);

            result2 = convertView;
            convertView.setTag(viewHolder2);
        }else{
            viewHolder2 = (ViewHolder)convertView.getTag();
            result2 = convertView;

        }
        viewHolder2.imageView1.setImageResource(dataModel2.getImageView());
        viewHolder2.txt1.setText(dataModel2.getName2());
        viewHolder2.txt2.setText(dataModel2.getNumber());
        viewHolder2.txt3.setText(dataModel2.getTexttext());

        return convertView;
    }

}
