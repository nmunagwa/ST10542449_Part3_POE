package com.mycompany.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds the Part 3 arrays and the operations that work on them.
 * Everything is populated at runtime (user input or the JSON file), nothing is hard-coded.
 */
public class MessageStore {

    // Required arrays
    public static ArrayList<String> sentMessages = new ArrayList<>();
    public static ArrayList<String> disregardedMessages = new ArrayList<>();
    public static ArrayList<String> storedMessages = new ArrayList<>();
    public static ArrayList<String> messageHashes = new ArrayList<>();
    public static ArrayList<String> messageIDs = new ArrayList<>();

    // Parallel arrays: index i in each list describes the same message
    // (these are what messageHashes / messageIDs line up with)
    public static ArrayList<String> allRecipients = new ArrayList<>();
    public static ArrayList<String> allTexts = new ArrayList<>();
    public static ArrayList<String> allFlags = new ArrayList<>();

    // Empties every array (used by the unit tests)
    public static void clearAll() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        allRecipients.clear();
        allTexts.clear();
        allFlags.clear();
    }

    // Hash = first 2 digits of ID : message number : FIRSTWORDLASTWORD
    public static String createHash(String id, int number, String text) {
        String[] words = text.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        String prefix = id.length() >= 2 ? id.substring(0, 2) : id;
        return (prefix + ":" + number + ":" + first + last).toUpperCase();
    }

    // Adds a message to the correct arrays. flag is "Sent", "Stored" or "Disregard"
    public static void addMessage(String id, String recipient, String text, String flag) {
        String hash = createHash(id, allTexts.size() + 1, text);
        messageIDs.add(id);
        messageHashes.add(hash);
        allRecipients.add(recipient);
        allTexts.add(text);
        allFlags.add(flag);

        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages.add(text);
        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages.add(text);
        } else {
            disregardedMessages.add(text);
        }
    }

    // Reads the JSON file and stores each message in the arrays
    public static int loadFromJson(String path) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        Matcher objects = Pattern.compile("\\{[^{}]*\\}").matcher(json);
        int count = 0;
        while (objects.find()) {
            String obj = objects.group();
            String id = readField(obj, "messageID");
            String recipient = readField(obj, "recipient");
            String text = readField(obj, "message");
            String flag = readField(obj, "flag");
            if (!text.isEmpty()) {
                addMessage(id, recipient, text, flag.isEmpty() ? "Stored" : flag);
                count++;
            }
        }
        return count;
    }

    // Gets the value of one "key": "value" pair from a JSON object string
    private static String readField(String obj, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").matcher(obj);
        return m.find() ? m.group(1).replace("\\\"", "\"") : "";
    }

    // a. Sender and recipient of all stored messages
    public static String displaySenderAndRecipient() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allTexts.size(); i++) {
            if (allFlags.get(i).equalsIgnoreCase("Stored")) {
                sb.append("Sender: Developer | Recipient: ").append(allRecipients.get(i)).append("\n");
            }
        }
        return sb.length() == 0 ? "No stored messages." : sb.toString();
    }

    // b. Longest stored message
    public static String longestStoredMessage() {
        String longest = "";
        for (String m : storedMessages) {
            if (m.length() > longest.length()) {
                longest = m;
            }
        }
        return longest;
    }

    // c. Search by message ID: returns recipient and message
    public static String searchByMessageID(String id) {
        int index = messageIDs.indexOf(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return "Recipient: " + allRecipients.get(index) + "\nMessage: " + allTexts.get(index);
    }

    // d. All stored (and sent) messages for one recipient
    public static ArrayList<String> searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < allTexts.size(); i++) {
            String flag = allFlags.get(i);
            if (allRecipients.get(i).equals(recipient)
                    && (flag.equalsIgnoreCase("Stored") || flag.equalsIgnoreCase("Sent"))) {
                found.add(allTexts.get(i));
            }
        }
        return found;
    }

    // e. Delete using the message hash
    public static String deleteByHash(String hash) {
        int index = messageHashes.indexOf(hash.toUpperCase());
        if (index == -1) {
            return "Message hash not found.";
        }
        String text = allTexts.get(index);
        String flag = allFlags.get(index);

        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages.remove(text);
        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages.remove(text);
        } else {
            disregardedMessages.remove(text);
        }
        messageHashes.remove(index);
        messageIDs.remove(index);
        allRecipients.remove(index);
        allTexts.remove(index);
        allFlags.remove(index);
        return "Message: \"" + text + "\" successfully deleted.";
    }

    // f. Report listing the full details of the stored messages
    public static String displayReport() {
        StringBuilder sb = new StringBuilder("----- STORED MESSAGES REPORT -----\n");
        boolean any = false;
        for (int i = 0; i < allTexts.size(); i++) {
            if (allFlags.get(i).equalsIgnoreCase("Stored")) {
                any = true;
                sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n");
                sb.append("Message ID:   ").append(messageIDs.get(i)).append("\n");
                sb.append("Recipient:    ").append(allRecipients.get(i)).append("\n");
                sb.append("Message:      ").append(allTexts.get(i)).append("\n\n");
            }
        }
        return any ? sb.toString() : "No stored messages.";
    }

    // Report of the SENT messages (hash, recipient, message), as the unit test table asks
    public static String displaySentReport() {
        StringBuilder sb = new StringBuilder("----- SENT MESSAGES REPORT -----\n");
        for (int i = 0; i < allTexts.size(); i++) {
            if (allFlags.get(i).equalsIgnoreCase("Sent")) {
                sb.append("Message Hash: ").append(messageHashes.get(i)).append("\n");
                sb.append("Recipient:    ").append(allRecipients.get(i)).append("\n");
                sb.append("Message:      ").append(allTexts.get(i)).append("\n\n");
            }
        }
        return sb.toString();
    }
}