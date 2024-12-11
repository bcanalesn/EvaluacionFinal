// MainActivity.java

package com.santotomas.evaluacionfinal;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Firestore & MQTT";

    // Credenciales para MQTT
    private static final String MQTT_HOST = "tcp://lightningslicer555.cloud.shiftr.io:1883";
    private static final String MQTT_USER = "lightningslicer555";
    private static final String MQTT_PASSWORD = "PdRVGwURQKXOg2nR";

    // Variables para Firestore
    private FirebaseFirestore db;
    private CollectionReference messagesRef;

    // Variables para MQTT
    private MqttClient mqttClient;

    // Variables para el ListView
    private ListView lvMessages;
    private ArrayList<String> messagesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText txtMessage = findViewById(R.id.txtMensaje);
        Button btnSendToFirestore = findViewById(R.id.btnSendToFirebase);
        Button btnLoadFromFirestore = findViewById(R.id.btnLoadFromFirebase);
        lvMessages = findViewById(R.id.lvMessages);

        // Inicializar la lista y el adaptador
        messagesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        lvMessages.setAdapter(adapter);

        // Inicializar Firestore y hacer referencia a la colección EvaluacionFinal
        db = FirebaseFirestore.getInstance();
        messagesRef = db.collection("EvaluacionFinal");

        // Configurar el cliente MQTT
        setupMqttClient();

        btnSendToFirestore.setOnClickListener(v -> {
            String messageText = txtMessage.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessageToFirestore(messageText);
                sendMessageToMqtt(messageText);
                txtMessage.setText(""); // Limpiar el campo después de enviar
            }
        });

        btnLoadFromFirestore.setOnClickListener(v -> loadMessagesFromFirestore());
    }

    private void loadMessagesFromFirestore() {
        messagesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            messagesList.clear(); // Limpiar la lista antes de agregar nuevos datos

            if (value != null) {
                for (DocumentSnapshot doc : value) {
                    Message message = doc.toObject(Message.class);
                    if (message != null) {
                        messagesList.add(message.getTexto()); // Agregar texto del mensaje a la lista
                    }
                }

                adapter.notifyDataSetChanged(); // Notificar cambios al adaptador
            }
        });
    }

    private void sendMessageToFirestore(String message) {
        Message msg = new Message(message, System.currentTimeMillis());
        messagesRef.add(msg)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(MainActivity.this, "Mensaje enviado a Firestore", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error al enviar mensaje a Firestore", Toast.LENGTH_SHORT).show());
    }

    private void setupMqttClient() {
        try {
            mqttClient = new MqttClient(MQTT_HOST, MqttClient.generateClientId(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(MQTT_USER);
            options.setPassword(MQTT_PASSWORD.toCharArray());
            options.setCleanSession(true);

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e(TAG, "Connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.i(TAG, "Message arrived from topic: " + topic + ", Message: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.i(TAG, "Delivery complete for token: " + token);
                }

            });

            mqttClient.connect(options);
            Toast.makeText(this, "Conectado a MQTT", Toast.LENGTH_SHORT).show();
        } catch (MqttException e) {
            Log.e(TAG, "Error al conectar al broker", e);
            Toast.makeText(this, "Error al conectar a MQTT", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessageToMqtt(String message) {
        try {
            mqttClient.publish("publicar", new MqttMessage(message.getBytes()));
            Log.i(TAG, "Mensaje enviado a MQTT");
        } catch (MqttException e) {
            Log.e(TAG, "Error al publicar mensaje en MQTT", e);
            Toast.makeText(this, "Error al enviar mensaje a MQTT", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                Log.i(TAG, "Desconectado de MQTT");
            } catch (MqttException e) {
                Log.e(TAG, "Error al desconectar", e);
            }
        }
    }
}

