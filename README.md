📄 Smart Plagiarism Detection System

Course: Programming in Java (TCS-408)  | 👩‍💻 Author: Arushi Rawat

A desktop application built with Java Swing that detects plagiarism between two documents using three similarity algorithms combined into a weighted final score.
Supports TXT, PDF, and DOCX files with an embedded database — no server setup required.
✨ Features

📂 Load any two documents (TXT, PDF, DOCX)
🔍 Three similarity algorithms — Jaccard, Cosine, N-Gram
⚖️ Weighted final plagiarism score
🟢🟡🔴 Level classification — LOW / MEDIUM / HIGH
📊 Live bar chart of all scores
💾 Export HTML + TXT report
🗄️ Embedded H2 database — saves all comparisons automatically, no MySQL needed
📋 History tab with CSV export


🚀 How to Run
Prerequisites

Install Java JDK 17+ from https://adoptium.net

First time only
Double-click SETUP.bat
This downloads the H2 database driver (~2 MB) and compiles the project. Done once, never again.
Every time after that
Double-click START.bat
The app opens in 2 seconds. The black Command Prompt window in the background is normal — don't close it.
Running from VS Code

File → Open Folder → select this folder
Install the Extension Pack for Java when prompted
Open src/com/plagcoders/PlagiarismApp.java
Click the green ▶ Run button above main()


The .vscode/launch.json inside the project already configures everything for VS Code automatically.


🧮 Algorithms
AlgorithmWeightWhat it measuresJaccard Similarity30%Vocabulary overlap between documentsCosine Similarity40%
Word frequency vector alignmentN-Gram Similarity30%Bigram + trigram phrase matching
Final Score = (Jaccard × 0.30) + (Cosine × 0.40) + (N-Gram × 0.30)
LevelScoreMeaning🟢 LOW< 30%Minor / coincidental similarity🟡 MEDIUM30–70%Significant overlap, review required🔴 HIGH> 70%Strong plagiarism detected

🗂️ Project Structure
PlagiarismDetector/
├── src/com/plagcoders/
│   ├── PlagiarismApp.java          # Entry point
│   ├── ui/
│   │   ├── MainWindow.java         # Swing GUI (4 tabs)
│   │   └── BarChartPanel.java      # Custom bar chart
│   ├── engine/
│   │   └── SimilarityEngine.java   # Jaccard + Cosine + N-Gram
│   ├── preprocessing/
│   │   └── TextPreprocessor.java   # Tokenizer + stopword removal
│   ├── filehandler/
│   │   └── DocumentReader.java     # TXT / PDF / DOCX reader
│   ├── database/
│   │   └── DatabaseManager.java    # H2 embedded DB via JDBC
│   ├── models/
│   │   ├── Document.java
│   │   └── ComparisonReport.java
│   └── reports/
│       └── ReportGenerator.java    # HTML + TXT report export
├── lib/                            # h2.jar goes here (auto-downloaded)
├── .vscode/                        # VS Code run config (pre-configured)
├── SETUP.bat                       # First-time setup (Windows)
├── START.bat                       # Launch app (Windows)
└── start.sh                        # Launch app (Mac/Linux)

🛠️ Tech Stack
TechnologyPurposeJava 17+Core languageJava SwingDesktop GUIH2 DatabaseEmbedded DB (no server needed)JDBC PreparedStatementSecure DB 
queriesJava ZIP APIBuilt-in DOCX parsing
