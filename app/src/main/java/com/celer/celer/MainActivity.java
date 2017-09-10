package com.celer.celer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class MainActivity extends AppCompatActivity {
    public TextView output;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getProgram(){
        try(
                ReadableByteChannel in= Channels.newChannel(
                        new URL("https://whispering-stream-80938.herokuapp.com/uploads/sample.java").openStream());

                FileChannel out=new FileOutputStream(getFilesDir() + "/sample.java").getChannel() ) {
                             out.transferFrom(in, 0, Long.MAX_VALUE);
                     }
    }

    private void printOutput(String newText){


        String content = output.getText().toString();
        output.setText(content + "\n" + newText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);

        if(isNetworkAvailable()){
            printOutput(getResources().getText(R.string.connected).toString());
        }else{
            printOutput(getResources().getText(R.string.not_connected).toString());
        }
    }



}