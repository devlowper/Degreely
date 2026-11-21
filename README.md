# 🦉 Night Owl Study Abroad — AI Counselor Desktop Application

<p align="center">
  <img src="https://img.shields.io/badge/Night%20Owl-AI%20Counselor-orange?style=for-the-badge&logo=openai&logoColor=white" alt="Night Owl AI" />
  <img src="https://img.shields.io/badge/Gemini%20API-gemini--2.5--flash-blue?style=for-the-badge" alt="Gemini" />
  <img src="https://img.shields.io/badge/UI-Claude.ai%20Style-D97757?style=for-the-badge" alt="Claude Style" />
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/UI-Java%20Swing%20%2B%20FlatLaf-blue?style=for-the-badge" alt="FlatLaf" />
  <img src="https://img.shields.io/badge/Build-Maven%20Wrapper-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📖 Overview

**Night Owl Study Abroad** is a production-quality AI counselor desktop application built with **Java 17**, **Java Swing**, and **FlatLaf**, featuring an interface inspired by **Claude.ai** (clean, minimal, warm). 

Powered by the **Google Gemini API** (`gemini-2.5-flash` by default, with model selection for `gemini-1.5-flash` and `gemini-1.5-pro`), Night Owl is tailored for students (primarily from Bangladesh and South Asia) seeking higher education abroad.

### Key Capabilities:
- **Interactive University Comparison Tables (`uni-table`)**: Automatically parses model output into structured side-by-side comparison tables with attribute groups (Admissions, Costs, Living Expenses, Deadlines, Post-study Work Rights), highlights cheapest tuition and nearest deadlines, and offers **Copy Table** and **Export CSV**.
- **Multimodal Document Upload (📎)**: Attach SOP drafts, CVs, transcripts, or university brochures (PDF, PNG, JPG, TXT, CSV, DOCX) for constructive admissions review.
- **Claude-Style Minimal Layout**:
  - 264px left sidebar with serif brand header, orange "New chat" button, instant search (`Ctrl+K`), and categorized history (*Today / Yesterday / Previous 7 days / Older*).
  - Empty state with warm spark icon, *"Hello, night owl"* serif greeting, and 4 starter prompt cards.
  - Centered message column with user bubbles (`#F1F5F9`) and assistant markdown rendering.
  - 24px rounded composer with auto-growing textarea, file chips, and send/stop buttons.
- **Personalized Student Profile**: Injects the student's CGPA, IELTS score, budget, preferred countries, and intake into the context silently.
- **Persistent Local History**: Conversations saved automatically to `~/.degreely/chats.json` with Markdown export.

---

## ⚡ Key Highlights

- **Zero Database Setup**: Plug-and-play. All university and scholarship data are loaded from classpath JSON resources, while personal application deadlines are saved locally.
- **Modern FlatLaf UI**: Styled with clean 8px rounded corners, subtle 1px borders, smooth hover interactions, and cohesive typography.
- **Unified Discovery Hub**: Welcoming hero banner with quick statistics merged directly into the university catalog for zero-click exploration.
- **Non-Blocking Concurrency**: Data filtering and search run on background threads using `SwingWorker`, ensuring a fluid 60 FPS experience with no UI freezing.
- **Smart Admission Predictor**: Multi-variable mathematical model that computes real admission probabilities based on GPA, IELTS, and degree selectivity.
- **Built-in Maven Wrapper**: Includes `mvnw` and `mvnw.cmd` configured with automatic `JAVA_HOME` detection for a frictionless first run.

---

## 🏛️ Core Modules & Features

### 1. 🏛️ University Discovery & Search Hub
- **Integrated Hero Header**: Welcoming overview displaying total counts (20 world universities, 80+ degree programs, 8 countries, and tuition highlights).
- **Catalog**: Features **20 world-leading universities** (MIT, Stanford, Oxford, Cambridge, ETH Zurich, NUS, TUM, University of Tokyo, etc.) across 8 countries.
- **80+ Academic Programs**: Real Bachelor's, Master's, and PhD offerings with credit requirements and program durations.
- **Interactive Filter Sidebar**:
  - **Country Dropdown**: US, UK, Canada, Germany, Switzerland, Singapore, Australia, Japan.
  - **Degree Level**: Filter by *Bachelor*, *Master*, or *PhD*.
  - **Max Annual Tuition Slider**: Live range from \$0 to \$70,000/year with instant value badge.
  - **Minimum IELTS Cutoff**: Instant filtering by English proficiency score.
  - **Live Search Field**: Real-time filtering by university name, city, or country.
- **Card UI**: Clean white cards with world ranking pills, key metrics, and hover border highlights in signature orange (`#E37712`).

### 2. 📖 Comprehensive University Details
- **Dedicated Page Navigation**: Integrated into the parent `CardLayout` with a "← Back to Universities" header.
- **Underlined Orange Tab Navigation**:
  - **About**: Global overview, history, world rankings, and complete degree program catalog.
  - **Costs**: Tuition fees, estimated annual living expenses per city, and total annual budget calculation.
  - **Admissions**: Acceptance rates, Fall & Spring application deadlines, application fees, and selectivity tier.
  - **Pre-requisites**: Minimum GPA, IELTS band, TOEFL score, and required document checklist.
- **Similar Institutions Sidebar**: Recommends similar universities based on country and ranking proximity.

### 3. ✦ Night Owl AI Study Abroad Counselor (Claude-Style Chatbot)
- **AI-Powered Admissions Advising**: Focused on students from Bangladesh and South Asia planning to study abroad. Answers questions on university selection, requirements, visas, exams (IELTS/GRE), and SOP reviews.
- **Claude.ai Look & Feel**:
  - **Left Sidebar (264px)**: Serif app title, orange "New chat" button, chat search (Ctrl+K), "Recents" categorized by *Today / Yesterday / Previous 7 days / Older*, with pin, rename, and delete actions.
  - **Empty State**: Centered warm spark icon, large serif greeting (*"Hello, night owl"*), and 4 quick starter prompt cards.
  - **Message Stream**: Right-aligned user bubbles (`#F1F5F9`) and left-aligned assistant messages with full markdown formatting, "Thinking..." indicator, and Copy / Regenerate actions.
  - **Composer (24px radius)**: Auto-growing input, paperclip file attachment button (PDF, Images, TXT, CSV, DOCX), file chips with remove button, and orange send button.
- **Dynamic University Comparison Tables (`uni-table`)**:
  - Whenever comparison or lookup is requested, Gemini returns a structured JSON block which is automatically rendered as an interactive, side-by-side comparison table card with attribute row groups (Admissions, Costs, Living Expenses, Deadlines, Work Rights).
  - Highlights cheapest tuition and nearest deadline. Includes **Copy Table** and **Export CSV** buttons.
- **Multimodal Document Upload**: Send SOP drafts, CVs, and transcripts for constructive admissions evaluation.
- **Student Profile Personalization**: Local profile modal (CGPA, IELTS, target countries, budget) silently injected into Gemini system context.
- **Chat History & Export**: Conversations automatically saved to `~/.degreely/chats.json` and exportable as Markdown (`.md`).
- **Configurable API Key**: Set via `GEMINI_API_KEY` environment variable or the in-app Gemini Settings dialog (`gemini-2.5-flash`, `gemini-1.5-flash`, `gemini-1.5-pro`).

### 4. 💰 Worldwide Scholarships & Funding
- **Prestigious Aid Opportunities**: Includes Fulbright, Rhodes, Gates Cambridge, Knight-Hennessy, DAAD, ETH Zurich Excellence, Vanier CGS, and MEXT.
- **Smart Deadline Detection**: Displays a prominent red **`Closing soon (Xd left)`** badge for deadlines falling within 30 days.
- **One-Click Application**: "Apply Link ↗" opens official scholarship portals directly in the user's default browser.
- **Keyword & Country Search**: Filter funding opportunities by host country, study level, or scholarship title.

### 5. ⚖️ Side-by-Side University Comparison
- **Matrix View**: Compare up to **3 universities simultaneously**.
- **Singleton State Management**: Powered by `CompareManager` with listener callbacks to update the view when selections change.
- **Automated Best-Value Green Highlighting**:
  - Automatically identifies and highlights the best metric in soft pastel green (`#DCFCE7`) with a `★ Best` badge (e.g., best world rank, lowest annual tuition, highest acceptance rate, lowest GPA cutoff).
- **Interactive Controls**: Add universities from any card or details page; remove individual columns with one click.

### 6. 🎯 Intelligent Admission Predictor
- **Predictive Algorithm**: Evaluates applicant profile against institutional criteria using `PredictorService`:
  - GPA Delta vs. university requirement.
  - IELTS English Band Delta.
  - Degree level rigor multiplier (PhD > Master > Bachelor).
  - Institution baseline acceptance rate.
- **Visual Results**:
  - Prominent percentage score (0% to 100%).
  - Rounded color-coded progress bar:
    - 🔴 **< 40% (Reach)**: High selectivity / requires profile strengthening.
    - 🟠 **40% – 70% (Target)**: Competitive candidate with solid alignment.
    - 🟢 **> 70% (Strong Match)**: High probability profile.
  - Tailored actionable advice explaining strengths and actionable steps.

### 7. 📅 Application Deadline Tracker
- **Personalized Tracker**: Add upcoming university applications with program name, target university, and deadline date.
- **Chronological Ordering**: Closest deadlines automatically float to the top of the queue.
- **Days Left Countdown**: Real-time counter showing exact days remaining, "Due Today!", or overdue status.
- **Status Lifecycle**: Track progress across three stages:
  - 🟡 *Pending*
  - 🔵 *Applied*
  - 🟢 *Done*
- **Persistent Storage**: Automatically serialized to JSON at `~/.degreely/deadlines.json` across app restarts.

---

## 🎨 Design System & Tokens

Degreely implements a consistent, accessible design palette:

| Token | Hex / Value | Description |
|---|---|---|
| **Primary** | `#E37712` | Signature Degreely Warm Orange |
| **Primary Hover** | `#D2500E` | Button and interactive hover state |
| **Text Primary** | `#2B3340` | Deep slate for body and titles |
| **Text Secondary**| `#697386` | Medium slate for subtitles and metadata |
| **Text Muted** | `#8C93A1` | Subtle grey for captions and placeholders |
| **Background** | `#FCFCFC` | Application main workspace canvas |
| **Card Surface** | `#FFFFFF` | Clean white elevated cards |
| **Border** | `#E5E7EB` | Subtle 1px dividing borders |
| **Highlight Blue**| `#EFF6FF` | Soft blue badges and info alerts |
| **Highlight Green**| `#DCFCE7` | Winning comparison metric highlight |
| **Highlight Red** | `#FEE2E2` | Closing soon alert & reach warnings |
| **Corner Radius** | `8px` | Modern rounded corners across all components |
| **Typography** | `Inter`, `Segoe UI` | Clean sans-serif font stack |

---

## 🏗️ Project Structure

```
Degreely/
├── pom.xml                               # Maven project configuration
├── README.md                             # Project documentation
├── mvnw / mvnw.cmd                       # Maven Wrapper executables
├── .mvn/wrapper/                         # Maven Wrapper jar and properties
└── src/
    └── main/
        ├── java/
        │   └── com/degreely/app/
        │       ├── Main.java             # Entry point, Look-and-Feel initializer
        │       │
        │       ├── model/                # Data transfer objects
        │       │   ├── University.java           # University entity
        │       │   ├── Program.java              # Degree program entity
        │       │   ├── Scholarship.java          # Scholarship entity
        │       │   ├── ApplicationDeadline.java  # User deadline entity
        │       │   ├── ChatMessage.java          # Night Owl chat message entity
        │       │   ├── ChatSession.java          # Night Owl conversation session
        │       │   ├── StudentProfile.java       # Academic background profile
        │       │   └── UniTableData.java         # Structured comparison table entity
        │       │
        │       ├── controller/           # Business logic & services
        │       │   ├── UniversityService.java    # Search, sorting & filtering logic
        │       │   ├── CompareManager.java       # Singleton comparison state
        │       │   ├── PredictorService.java     # Admission scoring algorithm
        │       │   ├── DeadlineService.java      # Local JSON file persistence
        │       │   ├── GeminiService.java        # Gemini REST client & prompt manager
        │       │   ├── ChatStorageService.java   # Chat session persistence
        │       │   └── StudentProfileService.java# Profile context manager
        │       │
        │       ├── util/                 # Reusable helpers & design tokens
        │       │   ├── Theme.java                # Color palette, spacing & fonts
        │       │   ├── UI.java                   # Standardized FlatLaf component factory
        │       │   └── JsonLoader.java           # Resource JSON parser with Gson
        │       │
        │       └── view/                 # UI pages & components
        │           ├── MainFrame.java            # Main window & CardLayout host
        │           ├── Navbar.java               # Top navigation bar with active indicator
        │           ├── UniversitiesPage.java     # University catalog with hero banner & filters
        │           ├── UniversityDetailsPage.java# Deep-dive university details with tabs
        │           ├── ChatPage.java             # Night Owl AI Counselor workspace
        │           ├── ScholarshipsPage.java     # Scholarship catalog & closing alerts
        │           ├── ComparePage.java          # Multi-university comparison table
        │           ├── PredictorPage.java        # Interactive admission calculator
        │           ├── DeadlinesPage.java        # Local deadline manager
        │           └── components/
        │               ├── UniversityCard.java
        │               ├── UniversityDetailsDialog.java
        │               ├── ScholarshipCard.java
        │               ├── DeadlineCard.java
        │               └── chat/
        │                   ├── ChatSidebar.java          # 264px history sidebar
        │                   ├── ComposerPanel.java        # 24px rounded input with attachment
        │                   ├── MessageBubble.java        # User bubble & assistant markdown
        │                   ├── UniTableComponent.java    # Dynamic comparison table component
        │                   ├── StarterPromptsPanel.java  # "Hello, night owl" greeting
        │                   ├── ProfileDialog.java        # Academic profile modal
        │                   └── ApiKeyDialog.java         # Gemini API settings modal
        │
        └── resources/
            └── data/
                ├── universities.json     # 20 Universities & 80+ programs dataset
                └── scholarships.json     # 10 International scholarships dataset
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java Development Kit (JDK) 17 or higher** installed.
  - Verify your installation:
    ```bash
    java -version
    ```

### Running with Maven Wrapper (Recommended)
You do not need to install Maven manually. The repository includes a patched wrapper that automatically detects your Java 17+ installation:

**On Windows (PowerShell / Command Prompt):**
```powershell
.\mvnw.cmd compile exec:java
```

**On macOS / Linux:**
```bash
chmod +x ./mvnw
./mvnw compile exec:java
```

### Running with Global Maven
If you already have Maven in your system `PATH`:
```bash
mvn compile exec:java
```

### Building an Executable JAR
To generate a standalone distributable JAR file:
```powershell
.\mvnw.cmd clean package
java -jar target/degreely-1.0.0-SNAPSHOT.jar
```

---

## 💡 Troubleshooting & Tips

- **`JAVA_HOME` not recognized**:
  - `mvnw.cmd` includes fallback logic to find installed JDKs. If needed, manually set `JAVA_HOME`:
    ```powershell
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1"   # Example path
    ```
- **PowerShell Execution Policy Warning**:
  - If PowerShell blocks scripts:
    ```powershell
    Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
    ```
- **High-DPI / 4K Displays**:
  - FlatLaf automatically scales Swing UI components to match Windows OS scaling (125%, 150%, 200%) with crisp fonts and borders.

---

## 🤝 Contributing

Contributions, feature suggestions, and bug reports are welcome!
1. Fork the Project: `https://github.com/devlowper/Degreely`
2. Create your Feature Branch: `git checkout -b feature/NewFeature`
3. Commit your Changes: `git commit -m 'feat: Add NewFeature'`
4. Push to the Branch: `git push origin feature/NewFeature`
5. Open a Pull Request

---

## 👨‍💻 Author & License

- **Developer**: MD RAKIB HOSSEN ([@devlowper](https://github.com/devlowper))
- **Repository**: [https://github.com/devlowper/Degreely](https://github.com/devlowper/Degreely)
- **License**: Released under the [MIT License](LICENSE).
