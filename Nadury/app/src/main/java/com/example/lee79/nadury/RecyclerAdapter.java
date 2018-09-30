package com.example.lee79.nadury;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    TextView roadTv;
    String rName;
    ArrayList<Double> rX = new ArrayList<>();
    ArrayList<Double> rY = new ArrayList<>();
    int rPosition;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView roadTv;
        View mView;
        MyViewHolder(View view){
            super(view);
            mView = itemView;
            roadTv = view.findViewById(R.id.roadTv);
        }


    }

    private ArrayList<RoadInfo> roadInfoArrayList;
    RecyclerAdapter(ArrayList<RoadInfo> roadInfoArrayList){
        this.roadInfoArrayList = roadInfoArrayList;
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row,parent, false);
        return new MyViewHolder(v);
    }
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.roadTv.setText(roadInfoArrayList.get(position).title);
        rX.add(roadInfoArrayList.get(position).roadX);
        rY.add(roadInfoArrayList.get(position).roadY);
        rName = roadInfoArrayList.get(position).roadName;
        //roadInfoArrayList.get(position).position = position;
        //rPosition = roadInfoArrayList.get(position).position;

        myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Toast.makeText(context, roadInfoArrayList.get(position).roadName, Toast.LENGTH_SHORT).show();
            }
        });
        myViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent12 = new Intent(v.getContext(), RoadActivity.class);
                intent12.putExtra("START_X",roadInfoArrayList.get(position).currentX);
                intent12.putExtra("START_Y",roadInfoArrayList.get(position).currentY);
                intent12.putExtra("START_ADDR","현재위치");
                intent12.putExtra("FINISH_ADDR",roadInfoArrayList.get(position).roadName);
                intent12.putExtra("FINISH_X",roadInfoArrayList.get(position).roadX);
                intent12.putExtra("FINISH_Y",roadInfoArrayList.get(position).roadY);
                v.getContext().startActivity(intent12);
                return true;
            }
        });

    }
    public int getItemCount(){
        return roadInfoArrayList.size();
    }
}
