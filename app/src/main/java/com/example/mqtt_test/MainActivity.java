package com.example.mqtt_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private String ServerIP = "tcp://10.0.2.2:1883";
    private String TOPIC = "mqtt/test";
    private String msg = "";
    private int cnt = 0;
    TextView msgview;
    private MqttClient mqttClient = null;

    Uri video_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        msgview = (TextView) findViewById(R.id.mqtt_message);
        msgview.setText(msg+Integer.toString(cnt));

        video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                + R.raw.middle);

        VideoView video_View = (VideoView) findViewById(R.id.videoView);
        video_View.setVideoURI(video_uri);
        video_View.start();
        video_View.setVisibility(View.VISIBLE);

        try {
            mqttClient = new MqttClient(ServerIP, MqttClient.generateClientId(), null);
            mqttClient.connect();


            mqttClient.subscribe(TOPIC);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d("MQTTService", "Connection Lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    cnt +=1;
                    msg = mqttMessage.toString();
                    Log.d("MQTTService", "Message Arrived : " + msg);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgview.setText(msg+Integer.toString(cnt));

                            if (msg.equals("low")){
                                video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                                        R.raw.low);
                            } else if (msg.equals("middle")) {
                                video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                                        R.raw.middle);
                            }else if (msg.equals("high")) {
                                video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                                        R.raw.high);
                            }
                            else if (msg.equals("safety")) {
                                video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                                        R.raw.safety);
                            }else if (msg.equals("advertise")) {
                                video_uri = Uri.parse("android.resource://com.example.mqtt_test/" +
                                        R.raw.advertise);
                            }


                            video_View.setVideoURI(video_uri);
                            video_View.start();
                            video_View.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.d("MQTTService", "Delivery Complete");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }




}