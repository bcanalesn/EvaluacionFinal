
public class Message {
    private String texto;  // Atributo para almacenar el contenido del mensaje
    private long timestamp; // Atributo para almacenar la marca de tiempo

    // Constructor vacío requerido para Firebase
    public Message() {}

    // Constructor para crear un nuevo mensaje
    public Message(String texto, long timestamp) {
        this.texto = texto;
        this.timestamp = timestamp;
    }

    // Getter para obtener el texto del mensaje
    public String getTexto() {
        return texto;
    }

    // Setter para establecer el texto del mensaje
    public void setTexto(String texto) {
        this.texto = texto;
    }

    // Getter para obtener la marca de tiempo
    public long getTimestamp() {
        return timestamp;
    }

    // Setter para establecer la marca de tiempo
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
