package com.celer.celer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView output;
    private static final String E_TAG = "TAG.CELER";
    private int user_id;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int createUniqueId(){
        Random rand = new Random();

        return rand.nextInt(9999999) + 1;
    }

    public void getProgram(){
        File store = new File(getCacheDir().getAbsoluteFile() + "/Sample.java");
        try{
                ReadableByteChannel in = Channels.newChannel(new URL("https://whispering-stream-80938.herokuapp.com/uploads/Sample.java").openStream());

                FileChannel out = new FileOutputStream(store).getChannel();
                out.transferFrom(in, 0, Long.MAX_VALUE);

            printOutput("> Reading Program");
    }catch (Exception e){
            Log.e(E_TAG , e.toString());
            printOutput("> Error in Fetching Program");

            if(store.exists()){
                printOutput("> Store file found");
            }else{
                printOutput("> Store file not found");
                printOutput("> Creating new store file");
                store.mkdir();
            }
        }

    }

    private void printOutput(String newText){
        String content = output.getText().toString();
        output.setText(content + "\n" + newText);
    }

    public void getRequest() throws IOException{

        String urlLink = "https://whispering-stream-80938.herokuapp.com/javaNetworking.php?unique_id=" + user_id;
        URL url = new URL(urlLink);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuffer sb = new StringBuffer();
        String line;

        while((line = in.readLine()) != null){
            sb.append(line);
        }

        in.close();
        printOutput("> " + sb.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);

        if(isNetworkAvailable()){
            printOutput(getResources().getText(R.string.connected).toString());
            user_id = createUniqueId();
            try{
                getRequest();
            }catch (Exception e){
                Log.e(E_TAG , e.toString());
                printOutput("> Error getting request");
            }
            getProgram();
        }else{
            printOutput(getResources().getText(R.string.not_connected).toString());
        }
    }



}