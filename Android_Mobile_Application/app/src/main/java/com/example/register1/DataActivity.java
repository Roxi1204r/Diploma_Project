package com.example.register1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    private TextView temp, humidity, moisture, light, fire, irrigation, blackr_warning, esca_warning, problem;
    private TextView month, day;

    private String title_default = "WARNING!";

    public static Date currentTime = Calendar.getInstance().getTime();

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("sensors");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        temp = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        moisture = findViewById(R.id.moisture);
        light = findViewById(R.id.lighty);
        fire = findViewById(R.id.fire);
        irrigation = findViewById(R.id.irrigation);
        blackr_warning = findViewById(R.id.blackrw);
        esca_warning = findViewById(R.id.escaw);
        problem = findViewById(R.id.problem);

        month = findViewById(R.id.month);
        day = findViewById(R.id.day);
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);

        String[] splitDate = formattedDate.split(",");

        day.setText(splitDate[0]);
        month.setText(splitDate[1]);

        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                temp.setText(dataSnapshot.child("1").child("temperature").getValue(String.class));
                moisture.setText(dataSnapshot.child("1").child("moisture").getValue(String.class));
                light.setText(dataSnapshot.child("1").child("light").getValue(String.class));
                humidity.setText(dataSnapshot.child("1").child("humidity").getValue(String.class));
                fire.setText(dataSnapshot.child("1").child("fire").getValue(String.class));
                irrigation.setText(dataSnapshot.child("1").child("irrigation").getValue(String.class));

                double temperature = Double.parseDouble(temp.getText().toString());

                int current_year = Calendar.getInstance().get(Calendar.YEAR);

                Date a = dateHelper.getDate(current_year, 3, 1);
                Date b = dateHelper.getDate(current_year,9, 1);
                Date c = dateHelper.getDate(current_year,6, 1);

                if((temperature >= 9 && temperature <= 30) && currentTime.after(a) && currentTime.before(b)) {
                    //  0 comes when two date are same,
                    //  1 comes when date1 is higher then date2
                    // -1 comes when date1 is lower then date2
//                    notification2();
                    notification("channel_2", "RISK of BLACK ROT!!!", 2);
                    blackr_warning.setText("RISK of Black Rot!!!");
                } else{
                    blackr_warning.setText("No risk of Black Rot!!!");
                }

                if((temperature >= 15 && temperature <= 28) && currentTime.after(c) && currentTime.before(b)) {
//                    notification3();
                    notification("channel_3", "RISK of ESCA!!!", 3);
                    esca_warning.setText("RISK of Esca!!!");
                } else{
                    esca_warning.setText("No risk of Esca!!!");
                }

                if(fire.getText().toString().equals("Flame detected!!!")) {
                    notification1();
                    fire.setTextColor(Color.RED);
                } else{
                    fire.setTextColor(Color.GRAY);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                problem.setText("Something went wrong!");
            }
        });

    }

//    public void openGallery(View v)
//    {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 100);
//    }

    private void notification1() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("channel_1", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
        Notification notification_f = new NotificationCompat.Builder(this, "channel_1")
                .setSmallIcon(R.drawable.icon_notification_fire)
                .setContentTitle("FIRE "+title_default)
                .setContentText("Fire has been detected!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification_f);
    }

    private void notification(String channel, String message, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(channel, channel, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
        Notification notification_f = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("DISEASE " + title_default)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, notification_f);
    }

//    private void notification2() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel2 = new NotificationChannel("channel_2", "Channel 2", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel2);
//        }
//        Notification notification1 = new NotificationCompat.Builder(this, "channel_2")
//                .setSmallIcon(R.drawable.icon_notification)
//                .setContentTitle("DISEASE "+title_default)
//                .setContentText("RISK of BLACK ROT!!!")
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(2, notification1);
//    }
//
//    private void notification3() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel3 = new NotificationChannel("channel_3", "Channel 3", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel3);
//        }
//        Notification notification2 = new NotificationCompat.Builder(this, "channel_3")
//                .setSmallIcon(R.drawable.icon_notification)
//                .setContentTitle("DISEASE "+title_default)
//                .setContentText("RISK of ESCA!!!")
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(3, notification2);
//    }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Notification")
//                .setGroup("disease")
//                .setContentText(message);
//
//        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "n")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Notification")
//                .setGroup("disease")
//                .setContentText(message2);
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(999, builder.build());
//        managerCompat.notify(999, builder2.build());

}