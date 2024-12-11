package com.santotomas.evaluacionfinal;

public class Message {
    private String texto;
    private long timestamp;

    public Message() {}  // Constructor vacío para Firebase

    public Message(String texto, long timestamp) {
        this.texto = texto;
        this.timestamp = timestamp;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
