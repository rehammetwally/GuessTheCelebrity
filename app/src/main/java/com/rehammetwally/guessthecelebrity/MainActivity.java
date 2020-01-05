package com.rehammetwally.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private String result, name, imageUrl;
    private List<String> names = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private int celebrity = 0;
    private ImageView celebrityImageView;
    private String[] answers = new String[4];
    private int locationOfCorrectAnswer;
    private Button button0, button1, button2, button3;

    public void chooseCelebrity(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(this, "Correct !!", Toast.LENGTH_SHORT).show();
            newQuestion();
        } else {
            Toast.makeText(this, "Worng!! it was " + names.get(celebrity), Toast.LENGTH_SHORT).show();
        }


    }

    private void newQuestion() {
        try {
            if (imageUrls.size()>0) {
                Random random = new Random();
                celebrity = random.nextInt(imageUrls.size());

                DownloadImageTask downloadImageTask = new DownloadImageTask();
                Bitmap bitmap = downloadImageTask.execute(imageUrls.get(celebrity)).get();
                celebrityImageView.setImageBitmap(bitmap);


                locationOfCorrectAnswer = random.nextInt(4);
                int locationOfInCorrectAnswer;
                for (int i = 0; i < 4; i++) {
                    if (i == locationOfCorrectAnswer) {
                        answers[i] = names.get(celebrity);
                    } else {
                        locationOfInCorrectAnswer = random.nextInt(imageUrls.size());
                        while (locationOfInCorrectAnswer == celebrity) {
                            locationOfInCorrectAnswer = random.nextInt(imageUrls.size());
                        }
                        answers[i] = names.get(locationOfInCorrectAnswer);
                    }
                }

                button0.setText(answers[0]);
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);
            }else {
                Log.e(TAG, "newQuestion: "+imageUrls.size() );
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celebrityImageView = findViewById(R.id.celebrityImageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        try {
            result = new DownloadTask().execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"channelListEntry\">");
            for (int i = 0; i < splitResult.length; i++) {
                Pattern pattern = Pattern.compile("<img src=\"(.*?)\"");
                Matcher matcher = pattern.matcher(splitResult[i]);
                while (matcher.find()) {
                    imageUrls.add(matcher.group(1));
                }
                pattern = Pattern.compile(" alt=\"(.*?)\"");
                matcher = pattern.matcher(splitResult[i]);
                while (matcher.find()) {
                    names.add(matcher.group(1));
                }
            }


            newQuestion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
