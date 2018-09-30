package com.example.lee79.nadury;

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

public class RoadActivity extends AppCompatActivity {

    ArrayList<Detail> Items;
    ArrayAdapter<String> Adapter;
    ListView listView;
    String totalTime=null, pathType, sectionTime3;
    CustomAdapter cAdapter;
    String busNo, trafficType, bussub = "", subwayCode, sectionTime, distance;
    JSONObject obj;
    int w;
    double startx=0.0, starty=0.0, finishx=0.0, finishy=0.0;
    String startaddr=null,finishaddr=null;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("경로 리스트");

        Items = new ArrayList<Detail>();
        listView = findViewById(R.id.listView);
        listView.setAdapter(Adapter);
        Intent intent = getIntent();
        startx = intent.getExtras().getDouble("START_X");
        starty = intent.getExtras().getDouble("START_Y");
        finishx = intent.getExtras().getDouble("FINISH_X");
        finishy = intent.getExtras().getDouble("FINISH_Y");
        startaddr = intent.getExtras().getString("START_ADDR");
        finishaddr = intent.getExtras().getString("FINISH_ADDR");
        textView1 = findViewById(R.id.textView1);
        textView1.setText(startaddr +  " → " + finishaddr);

        ODsayService odsayService = ODsayService.init(getApplicationContext(), "fKtH8toBYGmogRxStaGwRNDagurMB1epwF99ClaZhd4");
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
            @Override
            public void onSuccess(ODsayData odsayData, API api) {
                try {
                    if (api == API.SEARCH_PUB_TRANS_PATH) {
                        int k = odsayData.getJson().getJSONObject("result").getJSONArray("path").length();
                        int sum=0;
                        int s;
                        for (int l = 0; l < k; l++) {
                            obj = odsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(l);
                            s = obj.getJSONArray("subPath").length();

                            totalTime = obj.getJSONObject("info").getString("totalTime");
                            pathType = obj.getString("pathType");
                            String payment = obj.getJSONObject("info").getString("payment");
                            w = obj.getJSONArray("subPath").length();
                            int sectionTime2 = 0;

                            for (int q = 0; q<w; q++) {
                                trafficType = obj.getJSONArray("subPath").getJSONObject(q).getString("trafficType");
                                sectionTime = obj.getJSONArray("subPath").getJSONObject(q).getString("sectionTime");

                                if (trafficType.equals("1")) {
                                    subwayCode = obj.getJSONArray("subPath").getJSONObject(q).getJSONArray("lane").getJSONObject(0).getString("subwayCode");
                                    sectionTime2 += Integer.parseInt(sectionTime);
                                    if (subwayCode.equals("101")){
                                        bussub += "공항철도 ";
                                    }else{
                                        bussub += subwayCode+"호선 ";
                                    }
                                } else if (trafficType.equals("2")) {
                                    busNo = obj.getJSONArray("subPath").getJSONObject(q).getJSONArray("lane").getJSONObject(0).getString("busNo");
                                    bussub += busNo + "번\n";
                                    sectionTime2 += Integer.parseInt(sectionTime);
                                } else if (trafficType.equals("3")) {
                                    distance = obj.getJSONArray("subPath").getJSONObject(q).getString("distance");
                                    sum+=Integer.parseInt(distance);
                                    sectionTime2 += Integer.parseInt(sectionTime)*1.5;
                                } else {
                                }
                            }
                            sectionTime3 = Integer.toString(sectionTime2);

                            if(pathType.equals("1")){
                                if (sum<1000){
                                    Items.add(new Detail(sectionTime3+"분",bussub,R.drawable.subway,0,"도보 "+sum+"m","카드 "+payment+"원"));
                                }
                            }else if (pathType.equals("2")){
                                if (sum<1000){
                                    Items.add(new Detail(sectionTime3+"분",bussub,R.drawable.bus2,0,"도보 "+sum+"m","카드 "+payment+"원"));
                                }
                            }else if (pathType.equals("3")){
                                if (sum<1000){
                                    Items.add(new Detail(sectionTime3+"분",bussub,R.drawable.subway,R.drawable.bus2,"도보 "+sum+"m","카드 "+payment+"원"));
                                }
                            }

                            sum=0;
                            bussub = "";
                        }


                        cAdapter = new CustomAdapter(Items, RoadActivity.this);
                        listView.setAdapter(cAdapter);

                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int i, String s, API api) {
                if (api == API.SEARCH_PUB_TRANS_PATH) {
                    Toast.makeText(RoadActivity.this,"error",Toast.LENGTH_LONG).show();
                }
            }
        };
        odsayService.requestSearchPubTransPath(String.valueOf(starty), String.valueOf(startx), String.valueOf(finishy), String.valueOf(finishx),"0","0","0",onResultCallbackListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RoadActivity.this, RoadDetailActivity.class);
                int num2 = i;
                intent.putExtra("NUM", num2);
                intent.putExtra("START_X",startx);
                intent.putExtra("START_Y",starty);
                intent.putExtra("FINISH_X",finishx);
                intent.putExtra("FINISH_Y",finishy);
                intent.putExtra("START_ADDR",startaddr);
                intent.putExtra("FINISH_ADDR",finishaddr);
                setResult(RESULT_OK, intent);
                startActivity(intent);
            }
        });
    }
}
class Detail{
    int subwayImage, busImage;
    String time, name, walktime2, pay;
    public Detail(String time, String name, int subwayImage, int busImage, String walktime2, String pay){
        this.time = time;
        this.name = name;
        this.subwayImage = subwayImage;
        this.busImage = busImage;
        this.walktime2 = walktime2;
        this.pay = pay;
    }
    public String getTime(){
        return time;
    }
    public String getName(){
        return name;
    }
    public int getSubwayImage(){
        return subwayImage;
    }
    public int getBusImage(){
        return busImage;
    }
    public String getWalktime2(){
        return walktime2;
    }

    public String getPay() {
        return pay;
    }
}
class CustomAdapter extends ArrayAdapter<Detail>{
    private ArrayList<Detail> dataSet;
    Context mContext;

    private static class ViewHolder{
        TextView txt1;
        TextView txt2;
        ImageView imageView1;
        ImageView imageView2;
        TextView txt3;
        TextView txt4;
    }
    public CustomAdapter(ArrayList<Detail> data, Context context){
        super(context, R.layout.listitem, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Detail dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listitem, parent, false);
            viewHolder.txt1 = convertView.findViewById(R.id.time);
            viewHolder.txt2 = convertView.findViewById(R.id.name);
            viewHolder.imageView1 = convertView.findViewById(R.id.subwayImage);
            viewHolder.imageView2 = convertView.findViewById(R.id.busImage);
            viewHolder.txt3 = convertView.findViewById(R.id.walktime2);
            viewHolder.txt4 = convertView.findViewById(R.id.pay);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;

        }
        viewHolder.txt1.setText(dataModel.getTime());
        viewHolder.txt2.setText(dataModel.getName());
        viewHolder.imageView1.setImageResource(dataModel.getSubwayImage());
        viewHolder.imageView2.setImageResource(dataModel.getBusImage());
        viewHolder.txt3.setText(dataModel.getWalktime2());
        viewHolder.txt4.setText(dataModel.getPay());
        return convertView;
    }
}