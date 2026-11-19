# QuickChat – Part 3: Store Data and Display

**Module:** PROG5121 – Programming 1A  
**Student number:** ST10542449

QuickChat is a Java console application. A user registers, logs in, and then sends, disregards or stores messages. Part 3 adds arrays that hold the messages, a JSON file for stored messages, a "Stored Messages" menu, and automated unit tests that run on GitHub.

---

## Features

### Registration and login (Part 1)
- Username: exactly 5 characters and contains an underscore.
- Password: at least 8 characters with an uppercase letter, a lowercase letter, a number and a special character.
- Cell phone number: `+27` followed by exactly 9 digits.
- Only a successfully logged-in user can use QuickChat.

### Messaging (Part 2)
- Recipient number is validated (international code required).
- Message must be 250 characters or fewer.
- A 10-digit message ID and a message hash are generated for every message.
- Each message can be **sent**, **disregarded** or **stored** to send later.

Message hash format: `first 2 digits of ID : message number : FIRSTWORDLASTWORD` in capitals, for example `00:0:HITONIGHT`.

### Arrays and stored messages (Part 3)
The arrays are filled while the program runs (from user input and the JSON file). Nothing is hard-coded.

| Array | Contents |
|---|---|
| Sent Messages | All messages that were sent |
| Disregarded Messages | All messages that were disregarded |
| Stored Messages | Messages that were stored, including those read from `messages.json` |
| Message Hash | The hashes of all messages |
| Message ID | The IDs of all messages |

The **Stored Messages** menu (main menu option 3) lets the user:
1. Display the sender and recipient of all stored messages.
2. Display the longest stored message.
3. Search for a message ID and display the recipient and message.
4. Search for all messages stored for a particular recipient.
5. Delete a message using the message hash.
6. Display a report listing the full details (hash, ID, recipient, message) of all stored messages.

---

## Project structure

```
ST10542449_Part3_POE/
├── .github/workflows/TestJava.yml        GitHub Actions workflow that runs the tests
├── README.md
└── Main/
    ├── messages.json                      Stored messages read when the app starts
    ├── pom.xml                            Maven build file
    └── src/
        ├── main/java/com/mycompany/main/
        │   ├── Main.java                  Registration, login and menus
        │   ├── Message.java               Message creation, ID, hash, validation
        │   └── MessageStore.java          Part 3 arrays and their operations
        └── test/java/com/mycompany/main/
            ├── MessageTest.java           Tests for Message
            └── MessageStoreTest.java      Tests for the Part 3 arrays and functions
```

---

## Requirements
- Java JDK 17 or newer
- Apache Maven
- NetBeans (or any Java IDE)

## How to run

**In NetBeans:** open the `Main` project, right-click it and choose **Run**.

**From the command line** (inside the `Main` folder):
```
mvn compile
mvn exec:java -Dexec.mainClass="com.mycompany.main.Main"
```

`messages.json` must be in the folder you run the program from (the project folder, next to `pom.xml`).

### Example flow
1. Register a user (example: username `ab_cd`, password `Pass@1234`, phone `+27718693002`).
2. Log in with the same details.
3. Enter how many messages you want to write.
4. Choose **1) Send Messages**, enter a recipient and text, and choose to send, disregard or store each message.
5. Choose **3) Stored Messages** to use the search, delete and report options.
6. Choose **4) Quit** to exit.

---

## messages.json format

```json
[
  {
    "messageID": "1234567890",
    "recipient": "+27838884567",
    "message": "Where are you? You are late! I have asked you to be on time.",
    "flag": "Stored"
  }
]
```

The program reads this file when QuickChat starts and adds each entry to the arrays. When a user stores a message, it is appended to this file.

---

## Test data used in the unit tests

| # | Recipient | Message | Flag |
|---|---|---|---|
| 1 | +27834557896 | Did you get the cake? | Sent |
| 2 | +27838884567 | Where are you? You are late! I have asked you to be on time. | Stored |
| 3 | +27834484567 | Yohoooo, I am at your gate. | Disregard |
| 4 | 0838884567 | It is dinner time! | Sent |
| 5 | +27838884567 | Ok, I am leaving without you. | Stored |

## Unit tests

Run all tests (inside the `Main` folder) with:
```
mvn test
```

`MessageStoreTest` checks:
- The Sent Messages array contains the expected messages.
- The longest stored message is displayed correctly.
- Searching by message ID returns the right message.
- Searching by recipient `+27838884567` returns messages 2 and 5.
- Deleting by message hash removes the message and returns the confirmation text.
- The report lists the hash, recipient and message.

`MessageTest` checks message length, recipient validation, hash creation, message ID creation, the send/disregard/store options and the total messages sent.

All 16 tests pass.

## Automated tests (GitHub Actions)

The workflow in `.github/workflows/TestJava.yml` runs `mvn test` on every push and pull request. Open the **Actions** tab of the repository to see the results.

