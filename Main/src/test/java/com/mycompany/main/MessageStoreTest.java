package com.mycompany.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageStoreTest {

    // Loads the 5 test data messages into the arrays before every test
    @BeforeEach
    public void setUp() {
        MessageStore.clearAll();
        MessageStore.addMessage("1111111111", "+27834557896", "Did you get the cake?", "Sent");
        MessageStore.addMessage("2222222222", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
        MessageStore.addMessage("3333333333", "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        MessageStore.addMessage("0838884567", "0838884567", "It is dinner time!", "Sent");
        MessageStore.addMessage("5555555555", "+27838884567", "Ok, I am leaving without you.", "Stored");
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        assertEquals(2, MessageStore.sentMessages.size());
        assertEquals("Did you get the cake?", MessageStore.sentMessages.get(0));
        assertEquals("It is dinner time!", MessageStore.sentMessages.get(1));
    }

    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                MessageStore.longestStoredMessage());
    }

    @Test
    public void testSearchForMessageID() {
        String result = MessageStore.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time!"));
    }

    @Test
    public void testSearchByRecipient() {
        ArrayList<String> found = MessageStore.searchByRecipient("+27838884567");
        assertEquals(2, found.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", found.get(0));
        assertEquals("Ok, I am leaving without you.", found.get(1));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String hash = MessageStore.messageHashes.get(1); // Test Message 2
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                MessageStore.deleteByHash(hash));
        assertEquals(1, MessageStore.storedMessages.size());
    }

    @Test
    public void testDisplayReport() {
        String report = MessageStore.displaySentReport();
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient:"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }
}
