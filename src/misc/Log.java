package misc;

import model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

public class Log {

    private static final String  logLocation = "src/misc/logs.txt";

    public Log () {}

    public static void logEvent (String username, boolean success, String message) {
        try (FileWriter fileWriter = new FileWriter(logLocation, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
             printWriter.println(username + (success ? "Login Success" : "Login Failure") + " " + message + Instant.now().toString());
        } catch (IOException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
    }

}

