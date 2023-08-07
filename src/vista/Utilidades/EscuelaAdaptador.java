package vista.Utilidades;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import modelo.Escuela;

public class EscuelaAdaptador extends TypeAdapter<Escuela> {

    @Override
    public void write(JsonWriter out, Escuela escuela) throws IOException {
        if (escuela != null) {
            out.beginObject();
            out.name("id").value(escuela.getId());
            out.name("nombre").value(escuela.getNombre());
            out.endObject();
        }
    }

    @Override
    public modelo.Escuela read(JsonReader in) throws IOException {
        Escuela escuela = new Escuela();
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if ("id".equals(fieldName)) {
                escuela.setId(in.nextInt());
            } else if ("nombre".equals(fieldName)) {
                escuela.setNombre(in.nextString());
            } else {
                in.skipValue();
            }
        }
        in.endObject();
        return escuela;
    }
}
