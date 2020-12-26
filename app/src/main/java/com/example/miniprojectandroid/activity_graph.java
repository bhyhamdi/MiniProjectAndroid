package com.example.miniprojectandroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class activity_graph extends AppCompatActivity {
    ArrayList<String> t;
    float[] y;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        Bundle b = getIntent().getExtras();
        t = b.getStringArrayList("listX");
        y = b.getFloatArray("listeY");
        String s ="";
        for (int i = 0; i < y.length; i++) {
            s+= String.valueOf(y[i])+"  ";
        }



        for (int i = 0; i < t.size(); i++) {
            series.appendData(new DataPoint(i, (int)y[i]), true,80);
        }
        graph.addSeries(series);
        graph.setTitle("precision de puissance  entrante sur 5 jour  ") ;
        graph.getViewport().setXAxisBoundsManual(true);




    }
}