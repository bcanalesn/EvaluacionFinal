package com.santotomas.evaluacionfinal;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MainMqtt {

    private MqttClient client;

    // Conectar al servidor MQTT
    public void connect(String brokerUrl, String clientId) {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Enviar un mensaje a un tema
    public void sendMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);  // Calidad de servicio (opcional, ajusta según necesidad)
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Suscribirse a un tema y recibir mensajes
    public void subscribe(String topic, IMqttMessageListener messageListener) {
        try {
            client.subscribe(topic, messageListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Cerrar la conexión
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
