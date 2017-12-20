package it.grati_alexandru.provafinaleandroidacademy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private Bitmap bmap;
    private ProgressBar progressBar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView)findViewById(R.id.imageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        new LoadIconTask().execute();
    }

    class LoadIconTask extends AsyncTask<Integer, Integer, Bitmap> {
        private Integer index = 1;

        //CHIAMATO PRIMA DI TUTTI
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }


        //secondo metodo chiamato
        @Override
        protected Bitmap doInBackground(Integer... img_ids) {
            // Load bitmap

            Bitmap tmp = MapFragmentActivity.getBitmapFromVectorDrawable(getApplicationContext(),R.drawable.truck_delivery);
            imageView.setImageBitmap(tmp);
			/* Simuliamo il ritardo */
            for (int i = 1; i < 5; i++) {
                sleep();
                publishProgress(i * 20);
            }

            return tmp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            progressBar.setProgress(0);
            // Start home activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            // close splash activity
            finish();
        }

        private void sleep() {
			/* Ritardo di 0,5 secondi */
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
