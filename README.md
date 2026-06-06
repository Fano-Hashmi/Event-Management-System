# Event Management System

A desktop-based Event Management System built with Java Swing, SQLite, and custom data structures. The application supports event creation, user registration, paid event passes, staff assignment, QR-based attendance verification, feedback, certificates, waiting lists, and admin analytics.

This project is suitable for academic submission, DSA viva preparation, and Java desktop application practice.

## Features

### Admin

- Admin dashboard with event, user, registration, revenue, and popularity stats.
- Create, update, and delete events.
- Assign staff members to events.
- Manage users, staff accounts, bans, and deletions.
- Undo recently deleted events using a stack.
- View event participants and feedback.
- Delete assigned staff safely by unassigning their events first.
- Reuses the smallest available user and event IDs after deletion.

### Staff

- Staff dashboard for assigned events.
- View assigned event details, seats, and participants.
- Verify QR event passes.
- Mark attendance automatically after successful QR verification.
- View participant attendance status.

### User

- Register and log in securely.
- Browse, search, and filter events.
- Register for free events.
- Pay for paid events and receive a QR event pass.
- Join a waiting list when an event is full.
- Cancel registration and automatically move the next waiting user into the event.
- Submit ratings and feedback.
- Generate and print participation certificates.

## DSA Concepts Used

This project includes practical DSA implementations:

| DSA Concept | Used For |
| --- | --- |
| Binary Search Tree | Fast event search by date |
| Recursion | BST insert, search, traversal, and delete |
| Stack | Undo deleted event feature |
| Queue | Waiting list for full events |
| LinkedList | Queue implementation for waiting users |
| HashMap | Event-wise waiting lists and participant mapping |
| ArrayList | In-memory event, user, and DAO result storage |
| Linear Search / Filtering | Name, category, and type filtering |
| Inorder Traversal | Sorted event traversal by date |

## Tech Stack

- Java
- Java Swing
- FlatLaf UI theme
- SQLite database
- JDBC
- Custom DSA classes
- Batch scripts for build and run

## Project Structure

```text
Event Project/
+-- src/
|   +-- dao/          # Database access classes
|   +-- database/     # SQLite connection and table initialization
|   +-- ds/           # Custom data structures
|   +-- gui/          # Swing UI panels and dashboards
|   +-- main/         # Main application entry point
|   +-- model/        # Model classes
|   +-- service/      # Business logic
|   +-- util/         # Validation, session, QR, and ID utilities
+-- lib/              # External JAR files
+-- out/              # Compiled classes
+-- event.db          # SQLite database
+-- build.bat         # Build script
+-- run.bat           # Run script
```

## Requirements

- JDK 17 or later
- Windows recommended for the included `.bat` scripts
- SQLite JDBC JAR included in `lib/`
- FlatLaf JAR included in `lib/`

## How To Run

1. Clone or download the project.
2. Open the project folder.
3. Build the project:

```bat
build.bat
```

4. Run the application:

```bat
run.bat
```

The database tables are created automatically when the application starts.

## Default Admin Login

```text
Email: admin@admin.com
Password: admin123
```

## Database Tables

The application creates these SQLite tables automatically:

- `users`
- `events`
- `registrations`
- `payments`
- `passes`
- `waiting_list`
- `feedback`
- `attendance`

## Important Implementation Notes

- Event date searching is powered by a Binary Search Tree.
- Deleted events are stored in an undo stack.
- Waiting users are managed using FIFO queue behavior.
- QR pass verification marks attendance and invalidates the pass in one transaction.
- Attendance uses an upsert approach, so QR verification updates existing attendance records safely.
- User and event insertion reuses the smallest available ID after deletions.
- Staff deletion does not break assigned events; events are safely unassigned.

## Troubleshooting

### Java 25 SQLite Warning

If you see a warning like:

```text
WARNING: A restricted method in java.lang.System has been called
```

it is caused by SQLite JDBC loading a native library. It is not an application logic error.

To reduce this warning, update `run.bat` like this:

```bat
java --enable-native-access=ALL-UNNAMED -cp "out;lib\*" main.Main
```

### Red Compiler Text About SQLite JAR

On some Java 25 setups, `javac` may print an `AccessDeniedException` related to `sqlite-jdbc`. If the build still completes and the application runs, the project code is not the cause. Using JDK 17 or JDK 21 LTS is recommended for smoother compilation.

## Suggested Screenshots

Add screenshots here before publishing on GitHub:

- Login screen
- Admin dashboard
- Event list
- Payment and pass screen
- Staff QR verification
- Generated certificate

## Author

Developed as a Java Swing Event Management System project.
