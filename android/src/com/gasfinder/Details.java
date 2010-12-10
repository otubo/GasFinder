package com.gasfinder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends Activity {
	
	double posto_latitude;
	double posto_longitude;
	double eu_latitude;
	double eu_longitude;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		Intent callerIntent;
		String postoid = null;
		callerIntent = getIntent();
		Bundle myBundle;
		myBundle = callerIntent.getExtras();
		if (myBundle != null)
			// "com.gasfinder.postodetalhe.codigo"
			postoid = myBundle.getString("postoid");
		else
			postoid = "faio :-(";
		
		eu_latitude = myBundle.getDouble("eu_latitude");
		eu_longitude = myBundle.getDouble("eu_longitude");
		Log.i("DETAIL", "LOCATION ------------ " + eu_latitude 	+ eu_longitude);

		URL url = null;
		URLConnection con = null;
		String jsonTxt = null;
		JSONObject json = null;
		JSONArray postos = null;

		try {
			url = new URL(
					"http://developer.meuspostos.com.br/api/posto.json?posto="
							+ postoid);
			con = url.openConnection();
			jsonTxt = IOUtils.toString(con.getInputStream());
			
//			jsonTxt = "{ \"data\" : { \"Posto\" : [ { \"latitude\": -9.96939, \"longitude\":-67.821499, \"posto\":4, \"nome\": \"ALDEMIR SOUZA ROCHA - ME.\", \"telefone\": \"N/D\", \"endereco\": \"AVENIDA CEARÁ, 2767 \", \"bairro\": \"ABRAHÃO ALAB\", \"dt_pesquisa\":\"08/11/2010\", \"bandeira\": \"EQUADOR\", \"icone\": \"http://meusgastos.com.br/img/null.png\", \"gasolina\":2.940, \"nota_gasolina\": 1, \"alcool\": 2.350, \"in_alcool\": 1, \"diesel\": 0.000, \"in_diesel\": 0, \"gnv\": 2.310, \"in_gnv\": 1, \"qt_nota1\": 0, \"qt_nota2\": 0, \"qt_nota3\": 0, \"qt_nota4\": 0, \"qt_nota5\": 0, \"comentarios\" : [ ] } ] }, \"status\" : \"200\", \"detail\" : \"OK\", \"resquest_uri\" : \"http://developer.meuspostos.com.br/api/posto.json?posto=4\", \"created_at\" : \"2010-12-09T01:41:33-02:00\", \"elapsed_time\" : \"109\" }";
			
			json = new JSONObject(jsonTxt);
			postos = json.getJSONObject("data").getJSONArray("Posto");
			JSONObject posto = null;
			posto = postos.getJSONObject(0);

			URL myFileUrl = null;
			Bitmap bmImg = null;

			myFileUrl = new URL(posto.getString("icone"));

			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();

			bmImg = BitmapFactory.decodeStream(is);
			posto_latitude = posto.getDouble("latitude");
			posto_longitude = posto.getDouble("longitude");
			

			ImageView ImageViewicone = (ImageView) findViewById(R.id.icone);
			TextView textViewname = (TextView) findViewById(R.id.nome);
			TextView textViewaddr = (TextView) findViewById(R.id.endereco);
			TextView textViewgas = (TextView) findViewById(R.id.gasolina);
			TextView textViewalcohol = (TextView) findViewById(R.id.alcool);
			TextView textViewGNV = (TextView) findViewById(R.id.gnv);
			TextView textViewdiesel = (TextView) findViewById(R.id.diesel);
			ImageView iconegasolina = (ImageView) findViewById(R.id.iconegasolina);
			ImageView iconealcool = (ImageView) findViewById(R.id.iconealcool);
			ImageView iconediesel = (ImageView) findViewById(R.id.iconediesel);
			ImageView iconegnv = (ImageView) findViewById(R.id.iconegnv);
			
			iconegasolina.setImageDrawable(getResources().getDrawable(R.drawable.iconegasolina));
			iconealcool.setImageDrawable(getResources().getDrawable(R.drawable.iconealcool));
			iconediesel.setImageDrawable(getResources().getDrawable(R.drawable.iconediesel));
			iconegnv.setImageDrawable(getResources().getDrawable(R.drawable.iconegnv));
			
			ImageViewicone.setImageBitmap(bmImg);
			textViewname.setText(posto.getString("nome"));
			textViewaddr.setText(posto.getString("endereco"));
			textViewgas.setText(posto.getString("gasolina"));
			textViewalcohol.setText(posto.getString("alcool"));
			textViewGNV.setText(posto.getString("gnv"));
			textViewdiesel.setText(posto.getString("diesel"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void CreateMenu(Menu menu) {
		menu.setQwertyMode(true);
		MenuItem mnu1 = menu.add(0, 0, 0, "Ligar");
		{
			mnu1.setAlphabeticShortcut('l');
			mnu1.setIcon(R.drawable.ic_menu_answer_call);
		}
		MenuItem mnu2 = menu.add(0, 1, 1, "Ver no Mapa");
		{
			mnu2.setAlphabeticShortcut('b');
			mnu2.setIcon(R.drawable.ic_menu_mapmode);
		}
		MenuItem mnu3 = menu.add(0, 2, 2, "Compartilhar");
		{
			mnu3.setAlphabeticShortcut('c');
			mnu3.setIcon(R.drawable.ic_menu_share);
		}
	}

	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(this, "You clicked on Item 2", Toast.LENGTH_LONG)
			.show();
			Intent myIntent = new Intent();
			Bundle stats = new Bundle();


			return true;
		case 1:
			Intent nextIntent = new Intent();
			Bundle nextstats = new Bundle();
			Log.i("DETAIL", "LOCATION2 ------------ " + eu_latitude 	+ eu_longitude);
			nextstats.putDouble("posto_latitude", posto_latitude);
			nextstats.putDouble("posto_longitude", posto_longitude);
			
			nextstats.putDouble("eu_latitude", eu_latitude);
			nextstats.putDouble("eu_longitude", eu_longitude);
			
			nextIntent.putExtras(nextstats);
			nextIntent.setClass(getApplicationContext(), com.gasfinder.Map.class);
			startActivity(nextIntent);
			return true;
		case 2:
			Toast.makeText(this, "You clicked on Item 3", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
}