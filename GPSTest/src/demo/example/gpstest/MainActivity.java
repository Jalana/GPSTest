package demo.example.gpstest;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity {

	private GoogleMap mapView;
	final double LAT_MAX = 67.0;
	final double LAT_MIN = 49.0;
	final double LNG_MAX = -65.0;
	final double LNG_MIN = -130.0;
	double lat = 0.0;
	double lng = 0.0;
	double elevation = -10.0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        
        LatLng kamloops = new LatLng(50.74, -120.28);
        mapView.addMarker(new MarkerOptions()
        		.title("Kamloops")
        		.position(kamloops));
        
        
        
        generateRandomPoint(5);
    }
	
	private void generateRandomPoint(int limit)
	{
		LatLng otherPoint;
		
		for (int count = 1; count <= limit; count++)
		{
			lat = Math.random() * (LAT_MAX - LAT_MIN) + LAT_MIN;
			lng = Math.random() * (LNG_MAX - LNG_MIN) + LNG_MIN;
			otherPoint = new LatLng(lat, lng);
			
			//NetworkTask networkCheck = new NetworkTask();
			//elevation = networkCheck.doInBackground(lat, lng);
			elevation = 5.0;
			
	        if (elevation > 0.0)
	        {
	        	mapView.addMarker(new MarkerOptions()
	        			.title("Other Point: #" + count)
	        			.position(otherPoint));
	        }
	        else
	        {
	        	count--;
	        }
	    }
	}
	
	private double getElevation(double lat, double lng)
	{
		double result = Double.NaN;
		HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://maps.googleapis.com/maps/api/elevation/"
                + "xml?locations=" + String.valueOf(lat)
                + "," + String.valueOf(lng)
                + "&sensor=true";
        HttpGet httpGet = new HttpGet(url);
        try 
        {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) 
            {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                {
                    respStr.append((char) r);
                }
                String tagOpen = "<elevation>";
                String tagClose = "</elevation>";
                if (respStr.indexOf(tagOpen) != -1) 
                {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
                }
                instream.close();
            }
        } 
        catch (ClientProtocolException e) {} 
        catch (IOException e) {}
        
        elevation = result;
        return result;
	}
	
	private class NetworkTask extends AsyncTask<Double, Void, Double>
	{
		@Override
		protected Double doInBackground(Double... params) 
		{
			Double result = getElevation(params[0], params[1]);
			return result;
		}
		
		@Override
        protected void onPostExecute(Double result) 
		{
            
		}
	}
}