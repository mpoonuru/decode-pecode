package decodepcode;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Variables {

    Map<String, String> variables;

    Variables() {
        variables = new HashMap<>();
    }

    Variables(Map<String, String> vars) {
        variables = vars;
    }

    public Properties loadAsProperties() throws ValidationException {
        Map<String, String> env = loadFromEnvironment();
        env.putAll(variables);
        Properties props = loadFromPropertiesFile();
        for (Map.Entry<String, String> e : env.entrySet()) {
            props.setProperty(e.getKey(), e.getValue());
        }
        if (this.validate()) {
            return props;
        } else {
            throw new ValidationException("Unable to validate props/variables");
        }
    }

    private static Map<String, String> loadFromEnvironment() {
        Map<String, String> env = System.getenv();
        // Java
        // Classic way to loop a map
        Map<String, String> envVariables = new HashMap<>();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            if (entry.getKey().startsWith("DP")) {
                String mappedKey = entry.getKey().split("_")[1].toLowerCase();
                envVariables.put(mappedKey, entry.getValue());
            }
        }
        return envVariables;
    }

    private static Properties loadFromPropertiesFile() {
        Properties props= new Properties();
        try {
            props.load(new FileInputStream("DecodePC.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    // todo: Check whether all necessary fields are present or not, otherwise throw exception
    private boolean validate() {
        return true;
    }

    public class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }
}
