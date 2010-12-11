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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends Activity {
	
	double posto_latitude;
	double posto_longitude;
	double eu_latitude;
	double eu_longitude;
	String telefone = null;
	String endereco = null;
	String gasolina = null;
	String alcool = null;
	String gnv = null;
	String diesel = null;
	String bandeira = null;
	
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
		telefone = myBundle.getString("telefone");
		bandeira = myBundle.getString("bandeira");

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
			
			endereco = posto.getString("endereco");
			gasolina = posto.getString("gasolina");
			alcool = posto.getString("alcool");
			gnv = posto.getString("gnv");
			diesel = posto.getString("diesel");
			

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
			iconegasolina.setPadding(6, 6, 6, 6);
			
			iconealcool.setImageDrawable(getResources().getDrawable(R.drawable.iconealcool));
			iconealcool.setPadding(6, 6, 6, 6);
			
			iconediesel.setImageDrawable(getResources().getDrawable(R.drawable.iconediesel));
			iconediesel.setPadding(6, 6, 6, 6);
			
			iconegnv.setImageDrawable(getResources().getDrawable(R.drawable.iconegnv));
			iconegnv.setPadding(6, 6, 6, 6);
			
			ImageViewicone.setImageBitmap(bmImg);
			ImageViewicone.setPadding(6, 6, 6, 6);
			
			
			textViewname.setText(posto.getString("nome"));
			textViewname.setTextSize(15);
			textViewname.setPadding(6, 6, 6, 3);
			
			textViewaddr.setText(posto.getString("endereco"));
			textViewaddr.setTextSize(13);
			textViewaddr.setPadding(6, 0, 6, 6);
			
			textViewgas.setText("R$ " + posto.getString("gasolina"));
			textViewgas.setTextSize(21);
			textViewgas.setPadding(6, 6, 6, 6);
			
			textViewalcohol.setText("R$ " + posto.getString("alcool"));
			textViewalcohol.setTextSize(21);
			textViewalcohol.setPadding(6, 6, 6, 6);
			
			textViewGNV.setText("R$ " + posto.getString("gnv"));
			textViewGNV.setTextSize(21);
			textViewGNV.setPadding(6, 6, 6, 6);
			
			textViewdiesel.setText("R$ " + posto.getString("diesel"));
			textViewdiesel.setTextSize(21);
			textViewdiesel.setPadding(6, 6, 6, 6);

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
			Log.i("DETAIL", "TELEFONE ------------ " + telefone);
			if (telefone != "n/d" && telefone != null) {
				startActivityForResult(new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + telefone)), 1);
			}else{
		        Toast.makeText(this, "Telefone não disponível", Toast.LENGTH_LONG).show();
			}
			return true;
		case 1:
			Intent nextIntent = new Intent();
			Bundle nextstats = new Bundle();
			Log.i("DETAIL", "LOCATION2 ------------ " + eu_latitude 	+ eu_longitude);
			nextstats.putDouble("posto_latitude", posto_latitude);
			nextstats.putDouble("posto_longitude", posto_longitude);
			
			nextstats.putDouble("eu_latitude", eu_latitude);
			nextstats.putDouble("eu_longitude", eu_longitude);
			nextstats.putString("bandeira", bandeira);
			
			nextIntent.putExtras(nextstats);
			nextIntent.setClass(getApplicationContext(), com.gasfinder.Map.class);
			startActivity(nextIntent);
			return true;
		case 2:
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT, "Combustível Barato!");
			i.putExtra(Intent.EXTRA_TEXT, "Combustível barato na região do + " + endereco + ": Gasolina a R$" + gasolina + ", Álcool a R$" + alcool + ", Diesel a R$" + diesel + "e GNV a R$" + gnv + " #gasfinder");
			startActivity(Intent.createChooser(i, "Compartilhe"));
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