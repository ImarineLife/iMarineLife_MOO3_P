package nl.imarinelife.moo3;

import nl.imarinelife.lib.MainActivity;
import nl.imarinelife.moo3.catalog.CurrentCatalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // just to initialize catalog
        CurrentCatalog.getInstance(this);

        final Context splashContext = this;
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent openStartingPoint = new Intent(splashContext, MainActivity.class);
                    startActivity(openStartingPoint);
                    finish();
                }
            }
        };
        timer.start();
    }



}

