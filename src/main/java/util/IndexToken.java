package util;

public class IndexToken {
    public static String requestSplit(String token) {
        String[] tokens = token.split(" ");
        return tokens[1];
    }
}
