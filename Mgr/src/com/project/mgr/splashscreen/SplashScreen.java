/**
 * @author Marcin
 *
 */
package com.project.mgr.splashscreen;

//import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.project.mgr.MainActivity;
import com.project.mgr.R;
 
public class SplashScreen extends FragmentActivity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView image=(ImageView)findViewById(R.id.imgLogo);
        // Step1 : create the  RotateAnimation object
        RotateAnimation anim = new RotateAnimation(0f, 360f, 150, 150);
        // Step 2:  Set the Animation properties
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(3000);

        // Step 3: Start animating the image
         image.startAnimation(anim);
         
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);//MainActivityGCM works
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}