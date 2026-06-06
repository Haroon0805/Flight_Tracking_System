# ✈️ Flight Tracking System

A desktop flight management application built with **Java Swing**, featuring real-time route visualization on an interactive OpenStreetMap, full CRUD operations, and a modern dark/light theme toggle.

---

## 📸 Screenshots

> _Add screenshots to the `/screenshots` folder and they will appear here._

<!-- ![Main Dashboard](screenshots/dashboard.png) -->
<!-- ![Map View](screenshots/map_view.png) -->

---

## 🚀 Features

- ✈️ **Full Flight Management** — Add, update, delete, and search flights
- 🗺️ **Live Map Visualization** — View flight routes on OpenStreetMap via JMapViewer
- 🗄️ **SQLite Backend** — Persistent local database with pre-seeded airports
- 🔍 **Dynamic Search & Filter** — Search flights by number, airline, origin, or destination
- 🔄 **Status Management** — Track flight status: `Scheduled`, `In Air`, `Landed`, `Delayed`, `Cancelled`
- ✅ **Input Validation** — Thorough validation on all user inputs
- 🌙 **Dark / Light Theme** — Toggle between themes powered by FlatLaf

---

## 🛠️ Built With

| Technology | Purpose |
|---|---|
| Java (JDK 17+) | Core application language |
| Java Swing | Desktop GUI framework |
| SQLite + sqlite-jdbc | Local database |
| JMapViewer | OpenStreetMap route visualization |
| FlatLaf | Modern UI look and feel |
| JDatePicker | Date picker component |
| SwingX | Extended Swing components |

---

## 📁 Project Structure

```
Flight_Tracking_System/
├── src/
│   ├── Main.java               # Entry point
│   ├── database/
│   │   └── DatabaseManager.java    # SQLite setup, CRUD operations, seeding
│   ├── models/
│   │   ├── Flight.java             # Flight data model
│   │   └── Airport.java            # Airport data model
│   ├── ui/
│   │   ├── MainFrame.java          # Main application window
│   │   ├── FlightFormPanel.java    # Add/Edit flight form
│   │   ├── MapDialog.java          # Map popup dialog
│   │   ├── MapPanel.java           # OpenStreetMap rendering panel
│   │   ├── ThemeManager.java       # Dark/Light theme handling
│   │   └── ToolbarPanel.java       # Top toolbar with search & actions
│   └── utils/
│       └── InputValidator.java     # Input validation logic
├── lib/
│   ├── flatlaf-3.4.1.jar
│   ├── jdatepicker-1.3.2.jar
│   ├── jmapviewer-2.24.jar
│   ├── sqlite-jdbc-3.42.0.0.jar
│   └── swingx-all-1.6.5-1.jar
├── screenshots/                # App screenshots (add yours here)
├── docs/                       # Additional documentation
├── .gitignore
└── README.md
```

---

## ⚙️ Setup & Running

### Prerequisites
- Java JDK 17 or higher
- An IDE like **IntelliJ IDEA** (recommended) or Eclipse

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Haroon0805/Flight-Tracking-System.git
   cd Flight-Tracking-System
   ```

2. **Open in IntelliJ IDEA**
   - Go to `File → Open` and select the project folder
   - IntelliJ will auto-detect the project structure

3. **Add Libraries**
   - Go to `File → Project Structure → Libraries`
   - Click `+` and add all `.jar` files from the `/lib` folder

4. **Run the application**
   - Open `src/Main.java`
   - Click the green **Run** button or press `Shift + F10`

> ⚠️ The SQLite database (`flights.db`) is auto-created on first run with pre-seeded airports and sample flights.

---

## 🗄️ Database Schema

```sql
-- Airports Table
CREATE TABLE Airports (
    airport_code TEXT PRIMARY KEY,
    airport_name TEXT NOT NULL,
    city         TEXT NOT NULL,
    country      TEXT NOT NULL,
    latitude     REAL NOT NULL,
    longitude    REAL NOT NULL
);

-- Flights Table
CREATE TABLE Flights (
    flight_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    flight_number  TEXT NOT NULL UNIQUE,
    airline        TEXT NOT NULL,
    origin         TEXT NOT NULL,
    destination    TEXT NOT NULL,
    departure_date TEXT,
    departure_time TEXT,
    status         TEXT CHECK(status IN ('Scheduled','In Air','Landed','Delayed','Cancelled'))
                   DEFAULT 'Scheduled',
    FOREIGN KEY (origin)      REFERENCES Airports(airport_code),
    FOREIGN KEY (destination) REFERENCES Airports(airport_code)
);
```

---

## 👨‍💻 Author

**Muhammad Haroon**
- GitHub: [@Haroon0805](https://github.com/Haroon0805)
- LinkedIn: [mharoon0805](https://linkedin.com/in/mharoon0805)
- Email: m.haroon.0805@gmail.com

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
