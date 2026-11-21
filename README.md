# 🦉 Degreely — AI Study Abroad Counselor Desktop Application

<p align="center">
  <img src="https://img.shields.io/badge/Degreely-AI%20Counselor-orange?style=for-the-badge&logo=openai&logoColor=white" alt="Degreely AI" />
  <img src="https://img.shields.io/badge/Gemini%20API-gemini--2.5--flash-blue?style=for-the-badge" alt="Gemini" />
  <img src="https://img.shields.io/badge/UI-Claude.ai%20Style-D97757?style=for-the-badge" alt="Claude Style" />
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/UI-Java%20Swing%20%2B%20FlatLaf-blue?style=for-the-badge" alt="FlatLaf" />
  <img src="https://img.shields.io/badge/Build-Maven%20Wrapper-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📖 Overview

**Degreely** is a production-quality AI counselor desktop application built with **Java 17**, **Java Swing**, and **FlatLaf**, featuring an interface inspired by **Claude.ai** (clean, minimal, warm). 

Powered by the **Google Gemini API** (`gemini-2.5-flash` by default, with model selection for `gemini-1.5-flash` and `gemini-1.5-pro`), Degreely is tailored for students (primarily from Bangladesh and South Asia) seeking higher education abroad.

### Key Capabilities:
- **Interactive University Comparison Tables (`uni-table`)**: Automatically parses model output into structured side-by-side comparison tables with attribute groups (Admissions, Costs, Living Expenses, Deadlines, Post-study Work Rights), highlights cheapest tuition and nearest deadlines, and offers **Copy Table** and **Export CSV**.
- **Multimodal Document Upload (📎)**: Attach SOP drafts, CVs, transcripts, or university brochures (PDF, PNG, JPG, TXT, CSV, DOCX) for constructive admissions review.
- **Claude-Style Minimal Layout**:
  - 264px left sidebar with serif brand header, orange "New chat" button, instant search (`Ctrl+K`), and categorized history (*Today / Yesterday / Previous 7 days / Older*).
  - Empty state with warm spark icon, *"Welcome to Degreely"* serif greeting, and 4 starter prompt cards.
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

## 🏛️ Core Features & Capabilities

### 1. ✦ Degreely AI Study Abroad Counselor (Claude-Style Interface)
- **AI-Powered Admissions Advising**: Focused on students from Bangladesh and South Asia planning to study abroad. Answers questions on university selection, admission cutoffs, visas, exams (IELTS/TOEFL/GRE), and SOP reviews.
- **Claude.ai Minimal Layout**:
  - **Left Sidebar (264px)**: Serif app title, orange "New chat" button, instant search (`Ctrl+K`), categorized history (*Today / Yesterday / Previous 7 days / Older*), with pin, rename, and delete actions.
  - **Empty State**: Centered warm spark icon, large serif greeting (*"Welcome to Degreely"*), and 4 quick starter prompt cards.
  - **Message Stream**: Right-aligned user bubbles (`#F1F5F9`) and left-aligned assistant messages with full markdown formatting, "Thinking..." indicator, and Copy / Regenerate actions.
  - **Composer (24px radius)**: Auto-growing input, paperclip file attachment button (PDF, Images, TXT, CSV, DOCX), 2-3 university compare button (`⚖️`), and orange send button.

### 2. ⚖️ 2 & 3 University Side-by-Side Comparison Tables (`uni-table`)
- **Direct Compare Dialog**: Pick 1st, 2nd, and optional 3rd university from the top 20 global institutions with custom field of study and degree level.
- **Structured Comparison Matrix**:
  - **Admission Requirements**: Min GPA, IELTS/TOEFL cutoffs, GRE/GMAT requirements, document checklists.
  - **Cost & Financial Aid**: Annual tuition fees, monthly living costs by country, scholarships (DAAD, Vanier, Chevening, Fulbright).
  - **Deadlines & Intake**: Application deadlines and available intakes (Fall / Spring).
  - **Post-Study Work**: PSW visa rights (e.g. 18-month German Job Seeking visa, 3-year Canadian PGWP, 3-year US STEM OPT).
- **Interactive Controls**: Table header includes **Copy Table** and **Export CSV** for instant spreadsheet export.
- **Dual Engine**: Generates instantly from local database, and enriches with deep AI insights when Gemini API key is configured.

### 3. 📎 Multimodal Document Evaluation
- Attach SOP drafts, CVs, transcripts, and admission letters (PDF, PNG, JPG, TXT, CSV, DOCX) directly into the chat for constructive admissions feedback.

### 4. 👤 Personalized Student Profile
- In-app profile modal stores your current degree, CGPA, IELTS score, preferred countries, and budget, silently tailoring all AI answers to your exact background.

### 5. 💾 Local Conversation Persistence & Markdown Export
- All chats are saved locally to `~/.degreely/chats.json` with zero database setup required, and can be exported as `.md` files anytime.

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
        │       ├── Main.java             # Entry point & Look-and-Feel initializer
        │       │
        │       ├── controller/           # Business logic & services
        │       │   ├── ChatStorageService.java   # Local conversation persistence (~/.degreely/chats.json)
        │       │   ├── GeminiService.java        # Gemini REST client & streaming prompt manager
        │       │   ├── StudentProfileService.java# Profile context manager (~/.degreely/profile.json)
        │       │   └── UniversityService.java    # Institutional data & comparison table builder
        │       │
        │       ├── model/                # Clean data transfer entities
        │       │   ├── ChatMessage.java          # Message entity (role, content, files, uniTableJson)
        │       │   ├── ChatSession.java          # Conversation session (id, title, timestamp)
        │       │   ├── Program.java              # Degree program entity
        │       │   ├── Scholarship.java          # International scholarship entity
        │       │   ├── StudentProfile.java       # Academic background profile entity
        │       │   ├── UniTableData.java         # Structured 2-3 university comparison model
        │       │   └── University.java           # University institution entity
        │       │
        │       ├── util/                 # Reusable helpers & design tokens
        │       │   ├── Icons.java                # Resolution-independent vector icons (Graphics2D)
        │       │   ├── JsonLoader.java           # Classpath JSON resource loader
        │       │   ├── Theme.java                # Design tokens, color palette, typography
        │       │   └── UI.java                   # Standardized component factory (buttons, badges)
        │       │
        │       └── view/                 # User interface views & components
        │           ├── MainFrame.java            # Main application window
        │           ├── ChatPage.java             # Claude-style central chat workspace
        │           └── components/chat/
        │               ├── ApiKeyDialog.java         # Gemini API settings modal
        │               ├── ChatSidebar.java          # 264px history sidebar with search (Ctrl+K)
        │               ├── CompareDialog.java        # 2-3 university compare dialog
        │               ├── ComposerPanel.java        # 24px rounded input with attachment & compare
        │               ├── MessageBubble.java        # User bubbles, assistant markdown & HTML tables
        │               ├── ProfileDialog.java        # Academic student profile modal
        │               ├── StarterPromptsPanel.java  # "Welcome to Degreely" greeting & starter cards
        │               └── UniTableComponent.java    # Interactive comparison table with CSV export
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
