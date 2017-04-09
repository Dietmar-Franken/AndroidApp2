package com.polytech.androidapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.polytech.androidapp.R;
import com.polytech.androidapp.model.HorairesHebdo;
import com.polytech.androidapp.model.HorairesJour;
import com.polytech.androidapp.model.Photo;
import com.polytech.androidapp.model.Place;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    private ListView maListView;
    double longitude;
    double latitude;
    private ArrayList<Place> places = new ArrayList<Place>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        setTitle(null);

        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setLogo(R.drawable.logo_nearby_2);

        maListView = (ListView) findViewById(R.id.list);
        maListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                this.onItemClick(parent, view, position);
            }

            private void onItemClick(AdapterView<?> adapter, View v, int position) {

                /*Integer id_place = ((Place) (adapter.getItemAtPosition(position))).getId_place();
                Log.e("id_place: ", String.valueOf(id_place));
                Intent intent = new Intent(FirstActivity.this, PlaceDetailActivity.class);
                intent.putExtra(ID_PLACE, id_place.toString());
                //based on item add info to intent
                startActivity(intent);
                */
            }
        });
        //Localisation Android
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try{
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            catch (NullPointerException e){
                e.printStackTrace();

            }
        }

        // On récupère le json de la requête
        String url_request = "localhost:8080/greeting?latitude=" + latitude + "&longitude=" + longitude;

        new JSONTask().execute(url_request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_pref){
        }
        if(id == R.id.action_carte){
        }
        return super.onOptionsItemSelected(item);
    }

    private class JSONTask extends AsyncTask<String,Integer, String > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String finalJson;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                finalJson = buffer.toString();

                return finalJson;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(final String result) {

            ArrayList<Place> temp = new ArrayList<>();
            try {

                // make an jsonObject in order to parse the response
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    Place place = new Place();
                    if (jsonArray.getJSONObject(i).has("place_id")) {
                        place.setPlace_id(jsonArray.getJSONObject(i).optString("place_id"));
                        place.setName(jsonArray.getJSONObject(i).optString("name"));
                        place.setAddress(jsonArray.getJSONObject(i).optString("address"));
                        place.setLatitude(jsonArray.getJSONObject(i).optDouble("latitude"));
                        place.setLongitude(jsonArray.getJSONObject(i).optDouble("longitude"));

                        ArrayList<String> typesArrayList= new ArrayList<>();
                        JSONArray arrayTypes = jsonArray.getJSONObject(i).getJSONArray("types");
                        for (int j = 0; j < arrayTypes.length(); j++){
                            typesArrayList.add(arrayTypes.optString(j));
                        }
                        place.setTypes(typesArrayList);

                        place.setRating(jsonArray.getJSONObject(i).optInt("rating"));

                        if(jsonArray.getJSONObject(i).has("phoneNumber")){
                            place.setPhoneNumber(jsonArray.getJSONObject(i).optString("phoneNumber"));
                        }
                        if (jsonArray.getJSONObject(i).has("website")){
                            place.setWebsite(jsonArray.getJSONObject(i).optString("website"));
                        }

                        HorairesHebdo horairesHebdo = new HorairesHebdo();
                        JSONArray arrayHoraires = jsonArray.getJSONObject(i).getJSONObject("horaires_hebdo").getJSONArray("horaires_jour");
                        ArrayList<HorairesJour> arrayJour = new ArrayList<>();
                        for (int k = 0; k < arrayHoraires.length(); k++)
                        {
                            HorairesJour horairesJour= new HorairesJour();
                            horairesJour.setOuverture(arrayHoraires.getJSONObject(k).optString("ouverture"));
                            horairesJour.setFermeture(arrayHoraires.getJSONObject(k).optString("fermeture"));
                            arrayJour.add(horairesJour);
                        }
                        horairesHebdo.setHoraires_jour(arrayJour);
                        horairesHebdo.setHorairesHebdo(jsonArray.getJSONObject(i).optString("horairesHebdo"));
                        place.setHoraires_hebdo(horairesHebdo);

                        Photo photo = new Photo();
                        photo.setHeight(jsonArray.getJSONObject(i).optInt("height"));
                        photo.setWidth(jsonArray.getJSONObject(i).optInt("width"));
                        photo.setReference(jsonArray.getJSONObject(i).optString("reference"));
                        place.setPhotoRef(photo);
                        /*if (jsonArray.getJSONObject(i).has("opening_hours")) {
                            if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) {
                                if (jsonArray.getJSONObject(i).getJSONObject("opening_hours").getString("open_now").equals("true")) {
                                    poi.setOpenNow("Oui");
                                } else {
                                    poi.setOpenNow("Non");
                                }
                            }
                        } else {
                            poi.setOpenNow("Non connu");
                        }*/
                    }
                    temp.add(place);
                }
                places =temp;


            } catch (Exception e) {
                e.printStackTrace();
            }

            PlaceAdapter adapter = new PlaceAdapter(getApplicationContext(), R.layout.row, places);
            maListView.setAdapter(adapter);
        }
    }

    private class PlaceAdapter extends ArrayAdapter {

        private List<Place> list_places;
        private int resource;
        private LayoutInflater inflater;
        private PlaceAdapter(Context context, int resource, List<Place> objects) {
            super(context, resource, objects);
            list_places = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.image = (ImageView)convertView.findViewById(R.id.imagePlace);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.dist = (TextView)convertView.findViewById(R.id.dist);
                holder.open = (TextView)convertView.findViewById(R.id.open);
                holder.rate = (RatingBar)convertView.findViewById(R.id.rate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list_places.get(position).getName());
            holder.rate.setRating(list_places.get(position).getRating()/2);

            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if (list_places.get(position).isOpen(dayOfWeek, hour, minute) == 1){
                holder.open.setTextColor(Color.GREEN);
                holder.open.setText("Ouvert");
            }
            else if(list_places.get(position).isOpen(dayOfWeek, hour, minute) == 0)
                holder.open.setText("Fermé");
            else
                holder.open.setText("N/D");

            return convertView;
        }


        class ViewHolder{
            private TextView name;
            private TextView dist;
            private TextView open;
            private RatingBar rate;
            private ImageView image;
        }

    }

}
