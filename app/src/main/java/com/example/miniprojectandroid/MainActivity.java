package com.example.miniprojectandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    TextView langeurpanneau;
    TextView largeurpanneau;
    Button calculeButton;
    Spinner dates;
    TextView resulta;
    JSONArray array;
    ArrayList<String> t ;
    ArrayList<Float>  y ;
    Button graph,cal ;
    ProgressBar progressBar ;

    Float langeur,largeur;
    float temperatuer;
    String desc ;
    int x;
    Boolean test=false ;

    class Weather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        progressBar = findViewById(R.id.progressBar);
        graph = findViewById(R.id.ghraphe);
        langeurpanneau= findViewById(R.id.langeurpanneau);
        largeurpanneau= findViewById(R.id.largeurpannaeu);
        cal= findViewById(R.id.pEsmiet);


        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(test==false){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("ATTENTION !!");
                    alert.setMessage("IL FAUT APPUYER SUR LE BOUTON  puissance estimee TOUT D'ABORD");
                    alert.show();
                }
                else {
                Intent i = new Intent(MainActivity.this , activity_graph.class );
                i.putExtra("listX",t);
                float[] floatArray = new float[y.size()];
                int x = 0;

                for (Float f : y) {
                    floatArray[x++] = (f != null ? f : Float.NaN); // Or whatever default you want.
                }
                i.putExtra("listeY", floatArray );
                startActivity(i);
            }}
        });




        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName = findViewById(R.id.city);
                if (langeurpanneau.getText().toString().equals("")||largeurpanneau.getText().toString().equals("")|| cityName.getText().toString().equals("") ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("ATTENTION !!");
                    alert.setMessage("IL FAUT REMPLIR TOUS LES CHAMPS");
                    alert.show();
                }


                else{
                 progressBar.setVisibility(View.VISIBLE);



                    test = true;
                 langeur = Float.parseFloat(langeurpanneau.getText().toString());
                largeur = Float.parseFloat(largeurpanneau.getText().toString());
                resulta = findViewById(R.id.res);
                cityName = findViewById(R.id.city);
                calculeButton = findViewById(R.id.pEsmiet);
                dates = findViewById(R.id.date);
                String cname = cityName.getText().toString();
                String contenet;

                Weather weather = new Weather();
                try {
                    cname= cname.substring(0,1).toUpperCase() + cname.substring(1).toLowerCase();

                    contenet = weather.execute("http://api.openweathermap.org/data/2.5/forecast?q="+cname+"&appid=b04760b8f013f1cc4fd7985f68303ede").get();
                    progressBar.setVisibility(v.INVISIBLE);
                    JSONObject jsonObject = new JSONObject(contenet);
                    final String[] weatherData = {jsonObject.getString("list")};
                    array = new JSONArray(weatherData[0]);

                    t  = new ArrayList<>();
                    y = new ArrayList<>();

                    for (int i = 0; i < 40; i++) {
                        t.add(array.getJSONObject(i).getString("dt_txt"));
                        y.add( 1000*((langeur * largeur)/10000) *pourcentageciel(array.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description")) );

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, t);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dates.setAdapter(adapter);

                    dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            x = dates.getSelectedItemPosition();
                            try {
                                temperatuer = (float) (Float.parseFloat(array.getJSONObject(x).getJSONObject("main").getString("temp"))- 273.15);

                                resulta.setText("Temparature : " +String.valueOf( temperatuer) + " °C\n");
                                desc= array.getJSONObject(x).getJSONArray("weather").getJSONObject(0).getString("description");
                                resulta.append("Description : " + array.getJSONObject(x).getJSONArray("weather").getJSONObject(0).getString("description") + "\n");
                                resulta.append("Puissance: " + 1000 * ((langeur * largeur) / 10000) * pourcentageciel(array.getJSONObject(x).getJSONArray("weather").getJSONObject(0).getString("description")));
                                String s = array.getJSONObject(x).getJSONArray("weather").getJSONObject(0).getString("description");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                }
            }
        });


    }

    public float pourcentageciel(String s) {
        switch (s) {
            case "clear sky":
                return 1;
            case "few clouds":
                return (float) 0.9;
            case "scattered clouds":
                return (float) 0.8;
            case "broken clouds":
                return (float) 0.75;
            case "overcast clouds":
                return (float) 0.7;

            case "shower rain":
                return (float) 0.6;
            case "light rain":
                return (float) 0.65;
            case "moderate rain":
                return (float) 0.55;
            case "rain":
                return (float) 0.5;
            case "thunderstorm":
                return (float) 0.4;
            case "snow":
                return (float) 0.3;
            case "mist":
                return (float) 0.2;


        }
        return 0;


    }




}