package decodepcode.git;

public class Utils {

    public static String getUserName(String user) {
        String [] userData = user.split("/");
        return userData[1];
    }

    public static String getUserEmail(String user) {
        String [] userData = user.split("/");
        return userData[2];
    }
}
