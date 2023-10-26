package com.example.mqtt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msgview = (TextView) findViewById(R.id.mqtt_message);
        msgview.setText(msg+Integer.toString(cnt));

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