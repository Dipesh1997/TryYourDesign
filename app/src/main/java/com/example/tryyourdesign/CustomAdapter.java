package com.example.tryyourdesign;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> personNames;
    ArrayList<String> red;
    ArrayList<String> green;
    ArrayList<String> blue;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> personNames, ArrayList<String> red, ArrayList<String> green, ArrayList<String> blue) {
        this.context = context;
        this.personNames = personNames;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        holder.name.setText(personNames.get(position));
        int redC=Integer.parseInt(red.get(position));
        int greenC=Integer.parseInt(green.get(position));
        int blueC=Integer.parseInt(blue.get(position));
        holder.viewColor.setBackgroundColor(Color.rgb(redC, greenC, blueC));

        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String colorName=personNames.get(position);
                int redC=Integer.parseInt(red.get(position));
                int greenC=Integer.parseInt(green.get(position));
                int blueC=Integer.parseInt(blue.get(position));
                Intent i = new Intent(context, PaintingActivity.class);
                int getred=redC;
                int getgreen=greenC;
                int getblue=blueC;
                //Create the bundle
                Bundle bundle = new Bundle();

                //Add your data to bundle
                bundle.putInt("red", getred);
                bundle.putInt("green", getgreen);
                bundle.putInt("blue", getblue);
                bundle.putString("colorName", colorName);

                //Add the bundle to the intent
                i.putExtras(bundle);

                //Fire that second activity
                context.startActivity(i);
                // display a toast with person name on item click
                //Toast.makeText(context, personNames.get(position)+" "+red.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView viewColor;
        TextView name;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            viewColor = (TextView) itemView.findViewById(R.id.viewColor);

        }
    }
}
