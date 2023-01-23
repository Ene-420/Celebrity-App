package com.example.celebrityapp;

import static java.lang.System.in;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
ArrayList<String> celebPics = new ArrayList<String>();
ArrayList<String> celebNames = new ArrayList<String>();
ArrayList<String> celebNamesForButtons = new ArrayList<String>();
ImageView imageView;
Button button1,button2,button3,button4;
int locationOfCeleb;
//boolean correctCeleb = true;

Random rand = new Random();


    public void nextImage(View view ){
        //getCelebUrl(imageView);
        //Log.i("info", celebNames.get(0));

        if(Integer.toString(locationOfCeleb).equals(view.getTag().toString())){
            //correctCeleb = true;
            newCeleb();
        } else{
            //correctCeleb = false;
        }



    }



    public void newCeleb() {

            int imageUrlNo = rand.nextInt(74);
        //getCelebUrl(findViewById(R.id.imageView));

        //Log.i("info", celebNames.get(1));

        DownloadTask task = new DownloadTask();
        String urlLink = null;

        try {
            urlLink = task.execute("https://web.archive.org/web/20190119082828/www.posh24.se/kandisar").get();

            String[] splitUrlLink = urlLink.split("<div class=\"listedArticles\">");
            String[] link = splitUrlLink[0].split("<div class=\"channelList\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(link[1]);

            while (m.find()) {
                celebPics.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(link[1]);

            while (m.find()) {
                celebNames.add(m.group(1));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageDownloader getImage = new ImageDownloader();
        Bitmap myImage;

        try {
            myImage = getImage.execute(celebPics.get(imageUrlNo)).get();

            imageView.setImageBitmap(myImage);

        } catch (Exception e) {
            e.printStackTrace();

        }

        locationOfCeleb = rand.nextInt(4);
        celebNamesForButtons.clear();

        for (int i = 0; i < 4; i++) {
            if (i == locationOfCeleb) {
                celebNamesForButtons.add(celebNames.get(imageUrlNo));
            } else {
                int notCeleb = rand.nextInt(74);

                while (notCeleb == imageUrlNo) {
                    notCeleb = rand.nextInt(74);
                }
                celebNamesForButtons.add(celebNames.get(notCeleb));
            }
        }

        button1.setText(celebNamesForButtons.get(0));
        button2.setText(celebNamesForButtons.get(1));
        button3.setText(celebNamesForButtons.get(2));
        button4.setText(celebNamesForButtons.get(3));




    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);



        newCeleb();





/*        Pattern p = Pattern.compile("src=\"(.*?).jpg\"");
        Matcher m = p.matcher(urlLink);

        while(m.find()){
            celebPics.add(m.group(1)+".jpg");
        }

        p = Pattern.compile("alt=\"(.*?)-\"");
        m = p.matcher(urlLink);

        while(m.find()){
            celebNames.add(m.group(1));
        }*/

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in = connection.getInputStream();

                Bitmap myBitmap  = BitmapFactory.decodeStream(in);

                return myBitmap;

            }catch(Exception e){
                e.printStackTrace();

                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>  {
        @Override
        protected String doInBackground(String... urls) {
            Log.i("celebtag", "CELEB url = " + urls[0]);
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            URL url;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (MalformedURLException e) {
                result.append("Error: MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result.toString();
        }
    }
}