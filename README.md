# 🎓 Degreely — One-Stop Degree & Scholarship Search Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/UI-Java%20Swing%20%2B%20FlatLaf-blue?style=for-the-badge" alt="FlatLaf" />
  <img src="https://img.shields.io/badge/JSON%20Parser-Google%20Gson-red?style=for-the-badge" alt="Gson" />
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📌 Overview

**Degreely** is a modern, responsive desktop application built with **Java Swing** and styled with the elegant **FlatLaf** Look and Feel. Designed for students seeking higher education abroad, Degreely simplifies researching global universities, comparing degree programs, discovering prestigious scholarships, predicting admission probabilities, and managing application deadlines in one unified workspace.

No external database server is required — the platform runs completely self-contained using fast local JSON serialization powered by **Google Gson**.

---

## ✨ Features & Modules

### 1. 🏛️ University Search & Filter
- Browse **20 top universities** across the US, UK, Canada, Germany, Switzerland, Singapore, Australia, and Japan.
- **80+ authentic degree programs** (Bachelor, Master, PhD) with durations and credit requirements.
- **Dynamic Multi-Criteria Filter Sidebar**:
  - Filter by **Country** (United States, United Kingdom, Canada, Germany, etc.).
  - Filter by **Degree Level** (*Bachelor*, *Master*, *PhD*).
  - **Max Annual Tuition Slider** with live price label (from \$0 up to \$70,000/yr).
  - **Minimum IELTS requirement** cutoff filter.
- **Non-blocking Search**: Powered by `SwingWorker` concurrency so typing or dragging sliders never freezes the UI.
- **Visual Feedback**: Hover animations with smooth border transitions to brand orange (`#E37712`).

### 2. 📖 University Details Page
- Dedicated full-page view integrated with `CardLayout`.
- Underlined tabbed navigation with orange indicator:
  - **About**: Comprehensive institution overview, world ranking, global prestige, and degree catalog.
  - **Costs**: Tuition fees, country-specific estimated annual living expenses, and total budget estimation.
  - **Admissions**: Acceptance rates, Fall/Spring intake deadlines, application fees, and selectivity.
  - **Pre-requisites**: GPA requirement, IELTS score & TOEFL equivalents, and essential application document checklist.
- **Similar Universities Sidebar**: Smart algorithmic recommendations matching country and closest world rank.

### 3. 💰 Global Scholarships & Financial Aid
- 10 prestigious funding options (*Gates Cambridge*, *Rhodes*, *Knight-Hennessy*, *Fulbright*, *DAAD*, *ETH ESOP*, *Vanier*, etc.).
- **Smart Deadline Detection**: Automatically displays a vibrant red **`Closing soon (Xd left)`** badge if the application deadline is within 30 days.
- Direct **"Apply Link ↗"** button that opens official portals in the default web browser.

### 4. ⚖️ Side-by-Side University Compare
- Compare up to **3 universities side-by-side** simultaneously managed by a thread-safe `CompareManager` singleton.
- **Intelligent Best-Value Green Highlighting**:
  - Automatically identifies and highlights the best metric in pastel green (`#DCFCE7`) with a `★ Best` badge (e.g., lowest ranking #1, lowest tuition cost, highest acceptance rate, most accessible GPA and IELTS requirements).
- One-click **Remove** button per column and quick dropdown selector.

### 5. 🎯 Admission Chance Predictor
- Predicts personalized admission chances (0% to 100%) based on:
  - Cumulative GPA (0.0 to 4.0)
  - IELTS English Band Score (5.5 to 9.0)
  - Degree Level (Bachelor, Master, PhD)
  - Institutional selectivity & acceptance rates
- **Dynamic Visual Output**:
  - Large percentage display.
  - Smooth rounded colored progress bar:
    - 🔴 **Red (< 40%)**: *Reach / Challenging Match*
    - 🟠 **Orange (40% - 70%)**: *Competitive / Target Match*
    - 🟢 **Green (> 70%)**: *Strong / Safe Match*
  - Contextual one-line actionable advice explaining strengths and improvement areas.

### 6. 📅 Application Deadline Tracker
- Add and manage personal university applications.
- **Chronological Sorting**: Nearest upcoming deadlines automatically sort to the top.
- **Live Days Left Counter**: Displays days remaining, "Due Today!", or overdue status.
- **Status Lifecycle**: Toggle application status between *Pending* 🟡, *Applied* 🔵, and *Done* 🟢.
- **Local Persistence**: Saved automatically to `~/.degreely/deadlines.json`.

### 7. 🌙 Dark Mode / Light Mode Switcher
- One-click theme toggle in the top navigation bar powered by FlatLaf (`FlatLightLaf` ⟷ `FlatDarkLaf`).

---

## 🎨 Design Tokens & Guidelines

| Token | Hex / Value | Description |
|---|---|---|
| **Primary** | `#E37712` | Degreely signature orange |
| **Primary Hover** | `#D2500E` | Button / link hover state |
| **Text Primary** | `#2B3340` | High-contrast body & header text |
| **Text Secondary**| `#697386` | Subtitles and meta text |
| **Text Muted** | `#8C93A1` | Tertiary labels & placeholders |
| **Background** | `#FCFCFC` | Application canvas |
| **Card Surface** | `#FFFFFF` | Elevated card container |
| **Border** | `#E5E7EB` | Subtle 1px dividers |
| **Highlight Blue**| `#EFF6FF` | Soft blue badges and information alerts |
| **Highlight Green**| `#DCFCE7` | Winning comparison metric highlight |
| **Highlight Red** | `#FEE2E2` | Closing soon & reach probability warning |
| **Corner Radius** | `8px` | Cards, buttons, and input fields |
| **Typography** | `Inter` / `Segoe UI` | Clean sans-serif hierarchy |

---

## 🏗️ Architecture & Project Structure

Degreely follows clean **Model-View-Controller (MVC)** principles:

```
Degreely/
├── pom.xml                               # Maven project configuration
├── README.md                             # Documentation
├── mvnw / mvnw.cmd                       # Self-contained Maven wrapper
└── src/
    └── main/
        ├── java/
        │   └── com/degreely/app/
        │       ├── Main.java             # Entry point & Look-and-Feel initializer
        │       ├── model/
        │       │   ├── University.java           # University entity
        │       │   ├── Program.java              # Degree program entity
        │       │   ├── Scholarship.java          # Scholarship entity
        │       │   └── ApplicationDeadline.java  # User deadline entity
        │       ├── controller/
        │       │   ├── UniversityService.java    # University filtering & search logic
        │       │   ├── CompareManager.java       # Singleton comparison state manager
        │       │   ├── PredictorService.java     # Admission scoring algorithm
        │       │   └── DeadlineService.java      # Local JSON persistence & deadline manager
        │       ├── util/
        │       │   ├── Theme.java                # Global design tokens & fonts
        │       │   ├── UI.java                   # Standardized FlatLaf component factory
        │       │   └── JsonLoader.java           # Resource JSON parser using Gson
        │       └── view/
        │           ├── MainFrame.java            # Main window & CardLayout controller
        │           ├── Navbar.java               # Top navigation bar with dark mode toggle
        │           ├── HomePage.java             # Welcome landing hero
        │           ├── UniversitiesPage.java     # University catalog with live filters
        │           ├── UniversityDetailsPage.java# Deep-dive university inspection
        │           ├── ScholarshipsPage.java     # Scholarship catalog & closing alerts
        │           ├── ComparePage.java          # Multi-university comparison table
        │           ├── PredictorPage.java        # Interactive admission calculator
        │           ├── DeadlinesPage.java        # Local deadline manager
        │           └── components/
        │               ├── UniversityCard.java
        │               ├── UniversityDetailsDialog.java
        │               ├── ScholarshipCard.java
        │               └── DeadlineCard.java
        └── resources/
            └── data/
                ├── universities.json     # 20 Universities & 80+ degree programs
                └── scholarships.json     # 10 Worldwide scholarships
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 17 or higher** installed.

### Run Directly via Maven Wrapper (Recommended)
No local Maven installation needed. The included wrapper handles everything:

```powershell
# In PowerShell or Command Prompt
.\mvnw.cmd compile exec:java
```

### Run using Global Maven (if installed)
```bash
mvn compile exec:java
```

### Build a Production Executable JAR
```powershell
.\mvnw.cmd clean package
java -jar target/degreely-1.0.0-SNAPSHOT.jar
```

---

## 👨‍💻 Author & Repository

- **Repository**: [https://github.com/devlowper/Degreely](https://github.com/devlowper/Degreely)
- **Author**: MD RAKIB HOSSEN ([@devlowper](https://github.com/devlowper))
- **License**: MIT License
