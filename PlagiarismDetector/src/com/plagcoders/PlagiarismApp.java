package com.plagcoders;

import com.plagcoders.database.DatabaseManager;
import com.plagcoders.engine.SimilarityEngine;
import com.plagcoders.filehandler.DocumentReader;
import com.plagcoders.preprocessing.TextPreprocessor;
import com.plagcoders.reports.ReportGenerator;
import com.plagcoders.ui.MainWindow;

import javax.swing.*;

/**
 * PlagiarismApp – Application entry point.
 * Connects to H2 embedded DB, launches Swing GUI.
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class PlagiarismApp {

    public static final DatabaseManager  db       = DatabaseManager.getInstance();
    public static final DocumentReader   reader   = new DocumentReader();
    public static final TextPreprocessor preproc  = new TextPreprocessor();
    public static final SimilarityEngine engine   = new SimilarityEngine();
    public static final ReportGenerator  reporter = new ReportGenerator();
  
    public static void main(String[] args) {
        System.out.println("=".repeat(55));
        System.out.println("  Smart Plagiarism Detection System");
        System.out.println("  Team: PlagCoders | TCS-408 | JAVA-IV-T167");
        System.out.println("=".repeat(55));

        // System look and feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Connect to H2 embedded database
        boolean dbOk = db.connect();
        System.out.println("[DB] " + (dbOk ? "Ready" : "Offline – reports won't be saved"));

        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            MainWindow win = new MainWindow();
            win.setDbStatus(dbOk);
            win.setVisible(true);
        });
    }
}
