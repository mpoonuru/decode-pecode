package decodepcode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONBuilder {
    public static Gson build() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        return gson;
    }
}
