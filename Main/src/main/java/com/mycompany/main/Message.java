package com.mycompany.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Message {
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static final List<Message> allMessages = new ArrayList<>();
    private static String storagePath = "messages.json";

    // Clears the saved messages and counters (used by the unit tests).
    // It also points storing at a scratch file so tests never touch messages.json.
    public static void resetAll() {
        totalMessagesSent = 0;
        messageCounter = 0;
        allMessages.clear();
        storagePath = "target/test-messages.json";
    }

    private final int messageNumber;
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String status = "";
    private final Scanner input;

    // Original constructor (console version)
    public Message(int messageNumber, Scanner input) {
        this.messageNumber = messageNumber;
        this.input = input;
    }

    // Constructor used by Main: builds a message from a recipient and text
    public Message(String recipient, String text) {
        this(generateID(), recipient, text);
    }

    // Constructor with a given message ID (used by the unit tests)
    public Message(String messageID, String recipient, String text) {
        this.messageNumber = messageCounter++;
        this.input = null;
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = text;
        createMessageHash();
    }

    // Creates a random 10 digit message ID
    private static String generateID() {
        Random random = new Random();
        long id = 1_000_000_000L + (Math.abs(random.nextLong()) % 9_000_000_000L);
        return String.valueOf(id);
    }

    // ---------- Validation helpers used by Main ----------

    public static String validateRecipient(String recipient) {
        if (recipient != null && recipient.matches("^\\+27[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (text.length() - 250) + "; please reduce the size.";
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Accepts a South African international number (+27 plus 9 digits)
    public boolean isRecipientValid() {
        return recipient != null && recipient.matches("^\\+27[0-9]{9}$");
    }

    // Returns the success or failure message for this message's recipient
    public String checkRecipientCell() {
        return validateRecipient(recipient);
    }

    public String createMessageHash() {
        String firstTwoDigits = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        messageHash = (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return messageHash;
    }

    // ---------- Used by Main ----------

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // 1 = send, 2 = disregard, 3 = store. Returns the status message.
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                status = "Message successfully sent.";
                allMessages.add(this);
                totalMessagesSent++;
                return status;
            case 2:
                status = "Press 0 to delete the message.";
                return status;
            case 3:
                try {
                    storeMessage();
                    status = "Message successfully stored.";
                } catch (IOException e) {
                    status = "Message could not be stored: " + e.getMessage();
                }
                return status;
            default:
                status = "Invalid option. Message not sent.";
                return status;
        }
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Kept because the old name had a typo and old tests may use it
    public static int returnTotalMessagess() {
        return totalMessagesSent;
    }

    public static String printMessages() {
        StringBuilder output = new StringBuilder();
        for (Message item : allMessages) {
            output.append("Message ID: ").append(item.messageID).append("\n")
                    .append("Message Hash: ").append(item.messageHash).append("\n")
                    .append("Recipient: ").append(item.recipient).append("\n")
                    .append("Message: ").append(item.messageText).append("\n\n");
        }
        return output.toString();
    }

    @Override
    public String toString() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    public static void incrementTotalSent() {
        totalMessagesSent++;
    }

    public String getStatus() {
        return status;
    }

    // ---------- Original console version (Part 1/2) ----------

    public String SentMessage() {
        System.out.println("Choose what to do with this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter option: ");
        String choice = input.nextLine();
        switch (choice) {
            case "1":
                status = "Message successfully sent.";
                return status;
            case "2":
                System.out.println("Press 0 to delete the message.");
                System.out.print("Enter 0 to delete or any other key to keep it: ");
                String deleteChoice = input.nextLine();
                if (deleteChoice.equals("0")) {
                    status = "Message disregarded and deleted.";
                } else {
                    status = "Message disregarded.";
                }
                return status;
            case "3":
                try {
                    storeMessage();
                    status = "Message successfully stored.";
                } catch (IOException e) {
                    status = "Message could not be stored.";
                    System.out.println("Could not store message: " + e.getMessage());
                }
                return status;
            default:
                status = "Invalid option. Message not sent.";
                return status;
        }
    }

    public boolean createMessage() {
        messageID = generateID();
        while (!checkMessageID()) {
            messageID = generateID();
        }
        System.out.println("Message ID generated: " + messageID);

        System.out.print("Enter recipient cellphone number (+27 followed by 9 digits): ");
        recipient = input.nextLine();
        while (!isRecipientValid()) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
            System.out.print("Enter recipient cellphone number (+27 followed by 9 digits): ");
            recipient = input.nextLine();
        }
        System.out.println("Cell phone number successfully captured.");

        System.out.print("Enter your message (maximum 250 characters): ");
        messageText = input.nextLine();
        while (messageText.length() > 250 || messageText.trim().isEmpty()) {
            if (messageText.length() > 250) {
                System.out.println("Please enter a message of less than 250 characters.");
                System.out.println("Message exceeds 250 characters by " + (messageText.length() - 250) + "; please reduce the size.");
            } else {
                System.out.println("Please enter a message of less than 250 characters.");
            }
            System.out.print("Enter your message (maximum 250 characters): ");
            messageText = input.nextLine();
        }
        System.out.println("Message ready to send.");
        createMessageHash();
        status = SentMessage();

        if (status.equals("Message successfully sent.")) {
            allMessages.add(this);
            totalMessagesSent++;
            System.out.println("Message sent.");
            System.out.println(this);
        } else if (status.equals("Message successfully stored.")) {
            allMessages.add(this);
            System.out.println(status);
            System.out.println(this);
        } else {
            System.out.println(status);
        }
        return true;
    }

    public void storeMessage() throws IOException {
        // Store messages as a valid JSON array in messages.json.
        Path filePath = Paths.get(storagePath);
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        String existingJson = "[]";
        if (Files.exists(filePath)) {
            existingJson = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8).trim();
            if (existingJson.isEmpty()) {
                existingJson = "[]";
            }
        }

        String newObject = "{\"messageID\":\"" + escapeJson(messageID)
                + "\",\"messageHash\":\"" + escapeJson(messageHash)
                + "\",\"recipient\":\"" + escapeJson(recipient)
                + "\",\"message\":\"" + escapeJson(messageText)
                + "\",\"flag\":\"Stored\"}";

        if (existingJson.equals("[]")) {
            existingJson = "[" + newObject + "]";
        } else if (existingJson.startsWith("[") && existingJson.endsWith("]")) {
            String contents = existingJson.substring(1, existingJson.length() - 1).trim();
            if (contents.isEmpty()) {
                existingJson = "[" + newObject + "]";
            } else {
                existingJson = "[" + contents + "," + newObject + "]";
            }
        } else {
            existingJson = "[" + newObject + "]";
        }
        Files.write(filePath, existingJson.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
