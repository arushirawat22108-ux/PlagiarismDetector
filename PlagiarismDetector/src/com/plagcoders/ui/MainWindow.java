package com.plagcoders.ui;

import com.plagcoders.PlagiarismApp;
import com.plagcoders.engine.SimilarityEngine;
import com.plagcoders.models.ComparisonReport;
import com.plagcoders.models.Document;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * MainWindow – Java Swing GUI, 4 tabs: Compare | Results | History | About
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class MainWindow extends JFrame {

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color NAVY   = new Color(15,  23, 42);
    private static final Color BLUE   = new Color(37,  99,235);
    private static final Color BLUE2  = new Color(59, 130,246);
    private static final Color GREEN  = new Color(16, 185,129);
    private static final Color AMBER  = new Color(245,158, 11);
    private static final Color RED    = new Color(239,  68, 68);
    private static final Color SLATE  = new Color(100,116,139);
    private static final Color BG     = new Color(248,250,252);
    private static final Color CARD   = Color.WHITE;
    private static final Color BORDER = new Color(226,232,240);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_H1   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_H2   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_MONO = new Font("Consolas", Font.PLAIN, 11);
    private static final Font F_BIG  = new Font("Segoe UI", Font.BOLD,  48);

    // ── State ─────────────────────────────────────────────────────────────────
    private Document doc1, doc2;
    private ComparisonReport lastReport;
    private final List<ComparisonReport> history = new ArrayList<>();
    private boolean dbOnline = false;

    // ── Widgets ───────────────────────────────────────────────────────────────
    private JLabel lblFile1, lblFile2, lblWords1, lblWords2, lblDbStatus, lblStatus;
    private JButton btnLoad1, btnLoad2, btnCompare, btnSave, btnClear;
    private JLabel lblJ, lblC, lblN, lblFinal, lblLevel;
    private JProgressBar barMain, barLoad1, barLoad2;
    private JTextArea txtPrev1, txtPrev2;
    private BarChartPanel chart;
    private DefaultTableModel histModel;
    private JTabbedPane tabs;

    public MainWindow() {
        super("Smart Plagiarism Detection System – PlagCoders | TCS-408 | JAVA-IV-T167");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 780);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    public void setDbStatus(boolean online) {
        dbOnline = online;
        if (lblDbStatus != null) {
            lblDbStatus.setText(online ? "● DB Connected" : "● DB Offline");
            lblDbStatus.setForeground(online ? GREEN : AMBER);
        }
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NAVY);
        p.setBorder(new EmptyBorder(14, 22, 14, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel("📄");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        JPanel titles = new JPanel(new GridLayout(2,1,0,1));
        titles.setOpaque(false);
        JLabel t1 = new JLabel("Smart Plagiarism Detection System");
        t1.setFont(F_H1); t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("PlagCoders  ·  TCS-408  ·  JAVA-IV-T167");
        t2.setFont(new Font("Segoe UI",Font.PLAIN,11));
        t2.setForeground(new Color(148,163,184));
        titles.add(t1); titles.add(t2);
        left.add(icon); left.add(titles);

        lblDbStatus = new JLabel("● Checking DB...");
        lblDbStatus.setFont(new Font("Segoe UI",Font.BOLD,12));
        lblDbStatus.setForeground(new Color(148,163,184));

        p.add(left,         BorderLayout.WEST);
        p.add(lblDbStatus,  BorderLayout.EAST);
        return p;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        tabs = new JTabbedPane();
        tabs.setFont(F_H2);
        tabs.setBackground(BG);
        tabs.addTab("🔍  Compare",  buildCompareTab());
        tabs.addTab("📊  Results",  buildResultsTab());
        tabs.addTab("📋  History",  buildHistoryTab());
        tabs.addTab("ℹ   About",   buildAboutTab());
        return tabs;
    }

    // ── Compare Tab ───────────────────────────────────────────────────────────
    private JPanel buildCompareTab() {
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(16,18,12,18));

        // Two document cards
        JPanel docRow = new JPanel(new GridLayout(1,2,14,0));
        docRow.setOpaque(false);
        docRow.add(makeDocCard(1));
        docRow.add(makeDocCard(2));
        root.add(docRow, BorderLayout.NORTH);

        // Preview panes
        JPanel prev = new JPanel(new GridLayout(1,2,14,0));
        prev.setOpaque(false);
        txtPrev1 = makeTa(); txtPrev2 = makeTa();
        prev.add(scroll(txtPrev1, "Preview — Document 1"));
        prev.add(scroll(txtPrev2, "Preview — Document 2"));
        root.add(prev, BorderLayout.CENTER);

        // Actions
        JPanel acts = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        acts.setOpaque(false);
        btnCompare = btn("⚡  Compare Documents", BLUE);
        btnCompare.setEnabled(false);
        btnCompare.addActionListener(e -> doCompare());

        btnSave = btn("💾  Save Report", GREEN);
        btnSave.setEnabled(false);
        btnSave.addActionListener(e -> doSave());

        btnClear = btn("🗑   Clear", SLATE);
        btnClear.addActionListener(e -> doClear());

        acts.add(btnCompare); acts.add(btnSave); acts.add(btnClear);
        root.add(acts, BorderLayout.SOUTH);
        return root;
    }

    private JPanel makeDocCard(int num) {
        JPanel card = new JPanel(new BorderLayout(6,8));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER,1,true), new EmptyBorder(14,14,14,14)));

        JLabel title = new JLabel("Document " + num);
        title.setFont(F_H2); title.setForeground(BLUE);

        JButton b = btn("📂  Load File", BLUE2);
        b.setFont(new Font("Segoe UI",Font.PLAIN,12));

        JLabel lblName = new JLabel("No file selected");
        lblName.setFont(F_BODY); lblName.setForeground(SLATE);

        JLabel lblWc = new JLabel(" ");
        lblWc.setFont(new Font("Segoe UI",Font.ITALIC,11));
        lblWc.setForeground(SLATE);

        JProgressBar bar = new JProgressBar();
        bar.setForeground(BLUE); bar.setVisible(false);

        if (num==1){ lblFile1=lblName; lblWords1=lblWc; barLoad1=bar; btnLoad1=b; }
        else       { lblFile2=lblName; lblWords2=lblWc; barLoad2=bar; btnLoad2=b; }

        b.addActionListener(e -> loadDoc(num));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false); top.add(title,BorderLayout.WEST); top.add(b,BorderLayout.EAST);

        JPanel info = new JPanel(new GridLayout(2,1,2,2));
        info.setOpaque(false); info.add(lblName); info.add(lblWc);

        card.add(top,  BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(bar,  BorderLayout.SOUTH);
        return card;
    }

    // ── Results Tab ───────────────────────────────────────────────────────────
    private JPanel buildResultsTab() {
        JPanel root = new JPanel(new BorderLayout(12,12));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(16,18,12,18));

        // Three score cards
        JPanel scoreRow = new JPanel(new GridLayout(1,3,14,0));
        scoreRow.setOpaque(false);
        scoreRow.setPreferredSize(new Dimension(0,105));
        lblJ = new JLabel("—", SwingConstants.CENTER);
        lblC = new JLabel("—", SwingConstants.CENTER);
        lblN = new JLabel("—", SwingConstants.CENTER);
        scoreRow.add(scoreCard("Jaccard Similarity",  "Vocabulary Overlap · 30%",  lblJ));
        scoreRow.add(scoreCard("Cosine Similarity",   "Word Frequency · 40%",       lblC));
        scoreRow.add(scoreCard("N-Gram Similarity",   "Phrase Matching · 30%",      lblN));
        root.add(scoreRow, BorderLayout.NORTH);

        // Final result card
        root.add(buildFinalCard(), BorderLayout.CENTER);

        // Bar chart
        chart = new BarChartPanel();
        chart.setPreferredSize(new Dimension(0,210));
        root.add(chart, BorderLayout.SOUTH);
        return root;
    }

    private JPanel scoreCard(String name, String sub, JLabel val) {
        JPanel p = new JPanel(new BorderLayout(4,4));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER,1,true), new EmptyBorder(12,10,12,10)));
        val.setFont(new Font("Segoe UI",Font.BOLD,28));
        val.setForeground(BLUE);
        JLabel ln = new JLabel(name, SwingConstants.CENTER); ln.setFont(F_H2);
        JLabel ls = new JLabel(sub,  SwingConstants.CENTER);
        ls.setFont(new Font("Segoe UI",Font.ITALIC,10)); ls.setForeground(SLATE);
        JPanel bot = new JPanel(new GridLayout(2,1)); bot.setOpaque(false);
        bot.add(ln); bot.add(ls);
        p.add(val,BorderLayout.CENTER); p.add(bot,BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildFinalCard() {
        JPanel wrap = new JPanel(new BorderLayout(10,10));
        wrap.setOpaque(false);
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER,2,true), new EmptyBorder(22,22,22,22)));

        lblFinal = new JLabel("—", SwingConstants.CENTER);
        lblFinal.setFont(F_BIG); lblFinal.setForeground(BLUE);

        lblLevel = new JLabel("Run a comparison to see results", SwingConstants.CENTER);
        lblLevel.setFont(new Font("Segoe UI",Font.BOLD,15)); lblLevel.setForeground(SLATE);

        barMain = new JProgressBar(0,100);
        barMain.setStringPainted(true); barMain.setFont(F_H2);
        barMain.setForeground(BLUE);
        barMain.setPreferredSize(new Dimension(480,22));

        JLabel title = new JLabel("FINAL PLAGIARISM SCORE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,12)); title.setForeground(SLATE);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=0; g.fill=GridBagConstraints.HORIZONTAL;
        g.weightx=1; g.insets=new Insets(4,0,4,0);
        card.add(title,   g); g.gridy++;
        card.add(lblFinal,g); g.gridy++;
        card.add(lblLevel,g); g.gridy++;
        card.add(barMain, g);

        wrap.add(card,BorderLayout.CENTER);
        return wrap;
    }

    // ── History Tab ───────────────────────────────────────────────────────────
    private JPanel buildHistoryTab() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(14,18,14,18));

        String[] cols={"#","Doc 1","Doc 2","Jaccard","Cosine","N-Gram","Final","Level","Time"};
        histModel = new DefaultTableModel(cols,0){ @Override public boolean isCellEditable(int r,int c){return false;} };
        JTable tbl = new JTable(histModel);
        tbl.setFont(F_BODY); tbl.setRowHeight(22);
        tbl.getTableHeader().setFont(F_H2);
        tbl.setSelectionBackground(new Color(219,234,254));
        tbl.setGridColor(new Color(241,245,249));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        top.setOpaque(false);
        JButton bDb = btn("🔄  Load from DB", BLUE);
        bDb.addActionListener(e -> loadHistoryFromDb());
        JButton bEx = btn("💾  Export CSV", SLATE);
        bEx.addActionListener(e -> exportCsv());
        top.add(bDb); top.add(bEx);

        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return root;
    }

    // ── About Tab ─────────────────────────────────────────────────────────────
    private JPanel buildAboutTab() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        JTextArea ta = new JTextArea(
            "  Smart Plagiarism Detection System\n" +
            "  ══════════════════════════════════════════════\n\n" +
            "  Course  : Programming in Java (TCS-408)\n" +
            "  Team    : PlagCoders  |  JAVA-IV-T167\n\n" +
            "  Team Members:\n" +
            "    1. Rawat, Arushi    – 240211650  (Team Lead)\n" +
            "    2. Manjari, Tulsi   – 240221447\n" +
            "    3. Pathak, Vinayak  – 240221609\n\n" +
            "  Algorithms:\n" +
            "    • Jaccard Similarity   (30%)  – vocabulary overlap\n" +
            "    • Cosine Similarity    (40%)  – word frequency vectors\n" +
            "    • N-Gram Similarity    (30%)  – bigram + trigram matching\n\n" +
            "  Supported File Formats:\n" +
            "    • TXT  – built-in (always works)\n" +
            "    • PDF  – built-in fallback + PDFBox (optional)\n" +
            "    • DOCX – built-in fallback + Apache POI (optional)\n\n" +
            "  Database:\n" +
            "    • H2 Embedded (plagiarism_data.mv.db) – no server needed\n\n" +
            "  Plagiarism Levels:\n" +
            "    LOW    (0–30%)  – Minor / coincidental\n" +
            "    MEDIUM (30–70%) – Significant overlap, review required\n" +
            "    HIGH   (>70%)   – Strong plagiarism detected\n"
        );
        ta.setFont(F_MONO); ta.setEditable(false);
        ta.setBackground(CARD); ta.setBorder(new EmptyBorder(20,20,20,20));
        root.add(ta);
        return root;
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(241,245,249));
        p.setBorder(new EmptyBorder(5,14,5,14));
        lblStatus = new JLabel("Ready. Load two documents to begin.");
        lblStatus.setFont(new Font("Segoe UI",Font.ITALIC,11)); lblStatus.setForeground(SLATE);
        JLabel credit = new JLabel("PlagCoders © TCS-408");
        credit.setFont(new Font("Segoe UI",Font.PLAIN,10)); credit.setForeground(new Color(148,163,184));
        p.add(lblStatus, BorderLayout.WEST);
        p.add(credit,    BorderLayout.EAST);
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setBorder(new EmptyBorder(8,16,8,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setBackground(bg.darker());}
            public void mouseExited (MouseEvent e){b.setBackground(bg);}
        });
        return b;
    }

    private JTextArea makeTa() {
        JTextArea ta = new JTextArea(6,30);
        ta.setFont(F_MONO); ta.setEditable(false);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6,6,6,6));
        return ta;
    }

    private JScrollPane scroll(Component c, String title) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER,1,true), title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI",Font.BOLD,11), BLUE));
        return sp;
    }

    private void status(String msg) { SwingUtilities.invokeLater(()->lblStatus.setText(msg)); }

    // ── Actions ───────────────────────────────────────────────────────────────
    private void loadDoc(int num) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select Document " + num);
        fc.setFileFilter(new FileNameExtensionFilter("Documents (*.txt, *.pdf, *.docx)","txt","pdf","docx","doc"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        status("Loading: " + file.getName() + " …");
        JProgressBar bar = (num==1) ? barLoad1 : barLoad2;
        bar.setIndeterminate(true); bar.setVisible(true);

        new SwingWorker<Document,Void>() {
            protected Document doInBackground() throws Exception {
                String ext  = PlagiarismApp.reader.getExtension(file);
                String text = PlagiarismApp.reader.extractText(file);
                return new Document(file, text, ext);
            }
            protected void done() {
                bar.setIndeterminate(false); bar.setVisible(false);
                try {
                    Document d = get();
                    if (num==1){ doc1=d; lblFile1.setText(d.getFileName()); lblWords1.setText(d.getWordCount()+" words · "+d.getFileType()); txtPrev1.setText(preview(d)); }
                    else       { doc2=d; lblFile2.setText(d.getFileName()); lblWords2.setText(d.getWordCount()+" words · "+d.getFileType()); txtPrev2.setText(preview(d)); }
                    status("Loaded: " + d.getFileName());
                    btnCompare.setEnabled(doc1!=null && doc2!=null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Error loading:\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    status("Error loading file.");
                }
            }
        }.execute();
    }

    private String preview(Document d) {
        String t = d.getRawText();
        return t==null?"(empty)": (t.length()>1500 ? t.substring(0,1500)+"…" : t);
    }

    private void doCompare() {
        if (doc1==null||doc2==null){status("Load both documents first."); return;}
        btnCompare.setEnabled(false); status("Analysing…"); barMain.setIndeterminate(true);

        new SwingWorker<ComparisonReport,Void>() {
            protected ComparisonReport doInBackground() {
                SimilarityEngine.Result r = PlagiarismApp.engine.analyze(doc1.getRawText(), doc2.getRawText());
                doc1.setWordCount(r.wc1); doc2.setWordCount(r.wc2);
                ComparisonReport rep = new ComparisonReport(doc1,doc2,r.jaccard,r.cosine,r.ngram,r.weighted,r.level);
                if (dbOnline) {
                    try {
                        int i1=PlagiarismApp.db.saveDocument(doc1.getFileName(),doc1.getFilePath(),doc1.getFileType(),doc1.getWordCount());
                        int i2=PlagiarismApp.db.saveDocument(doc2.getFileName(),doc2.getFilePath(),doc2.getFileType(),doc2.getWordCount());
                        doc1.setId(i1); doc2.setId(i2);
                        int rid=PlagiarismApp.db.saveReport(i1,i2,doc1.getFileName(),doc2.getFileName(),r.jaccard,r.cosine,r.ngram,r.weighted,r.level);
                        rep.setId(rid);
                        PlagiarismApp.db.log("COMPARE",doc1.getFileName()+" vs "+doc2.getFileName()+" → "+r.level);
                    } catch (Exception ignored) {}
                }
                return rep;
            }
            protected void done() {
                barMain.setIndeterminate(false);
                try {
                    lastReport = get(); history.add(lastReport);
                    updateResults(lastReport); addToHistory(lastReport);
                    btnCompare.setEnabled(true); btnSave.setEnabled(true);
                    tabs.setSelectedIndex(1);
                    status(String.format("Done  ·  Final: %.1f%%  [%s]", lastReport.getFinalScore()*100, lastReport.getPlagiarismLevel()));
                } catch (Exception ex) {
                    btnCompare.setEnabled(true);
                    JOptionPane.showMessageDialog(MainWindow.this,"Error:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateResults(ComparisonReport r) {
        Color col = switch(r.getPlagiarismLevel()){ case "LOW"->GREEN; case "MEDIUM"->AMBER; default->RED; };
        String icon = switch(r.getPlagiarismLevel()){ case "LOW"->"✅"; case "MEDIUM"->"⚠️"; default->"🚨"; };

        lblJ.setText(String.format("%.1f%%", r.getJaccardScore()*100)); lblJ.setForeground(BLUE);
        lblC.setText(String.format("%.1f%%", r.getCosineScore() *100)); lblC.setForeground(BLUE);
        lblN.setText(String.format("%.1f%%", r.getNgramScore()  *100)); lblN.setForeground(BLUE);

        String fp = String.format("%.1f%%", r.getFinalScore()*100);
        lblFinal.setText(fp); lblFinal.setForeground(col);
        lblLevel.setText(icon + "  " + r.getPlagiarismLevel() + " PLAGIARISM"); lblLevel.setForeground(col);

        int pct = (int)(r.getFinalScore()*100);
        barMain.setValue(pct); barMain.setString(fp); barMain.setForeground(col);
        chart.setScores(r.getJaccardScore(), r.getCosineScore(), r.getNgramScore(), r.getFinalScore());
    }

    private void addToHistory(ComparisonReport r) {
        histModel.addRow(new Object[]{
            histModel.getRowCount()+1,
            r.getDoc1().getFileName(), r.getDoc2().getFileName(),
            String.format("%.1f%%",r.getJaccardScore()*100),
            String.format("%.1f%%",r.getCosineScore() *100),
            String.format("%.1f%%",r.getNgramScore()  *100),
            String.format("%.1f%%",r.getFinalScore()  *100),
            r.getPlagiarismLevel(), r.getTimeStr()
        });
    }

    private void doSave() {
        if (lastReport==null) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("plagiarism_report.html"));
        fc.setFileFilter(new FileNameExtensionFilter("HTML Report (*.html)","html"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".html")) path+=".html";
            PlagiarismApp.reporter.generateHtmlReport(lastReport, path);
            PlagiarismApp.reporter.generateTxtReport(lastReport, path.replace(".html",".txt"));
            JOptionPane.showMessageDialog(this,"Reports saved!\n"+path+"\n"+path.replace(".html",".txt"),"Saved",JOptionPane.INFORMATION_MESSAGE);
            status("Reports saved: "+path);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Error saving:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doClear() {
        doc1=doc2=null; lastReport=null;
        lblFile1.setText("No file selected"); lblFile2.setText("No file selected");
        lblWords1.setText(" "); lblWords2.setText(" ");
        txtPrev1.setText(""); txtPrev2.setText("");
        lblJ.setText("—"); lblC.setText("—"); lblN.setText("—");
        lblFinal.setText("—"); lblFinal.setForeground(BLUE);
        lblLevel.setText("Run a comparison to see results"); lblLevel.setForeground(SLATE);
        barMain.setValue(0); barMain.setString(""); barMain.setForeground(BLUE);
        chart.reset(); btnCompare.setEnabled(false); btnSave.setEnabled(false);
        status("Cleared. Load two documents to begin."); tabs.setSelectedIndex(0);
    }

    private void loadHistoryFromDb() {
        if (!dbOnline){ JOptionPane.showMessageDialog(this,"Database is offline.","DB Offline",JOptionPane.WARNING_MESSAGE); return; }
        try {
            histModel.setRowCount(0);
            ResultSet rs = PlagiarismApp.db.getAllReports();
            int row=0;
            while (rs.next()) histModel.addRow(new Object[]{
                ++row,
                rs.getString("doc1_name"), rs.getString("doc2_name"),
                String.format("%.1f%%",rs.getDouble("jaccard_score")*100),
                String.format("%.1f%%",rs.getDouble("cosine_score") *100),
                String.format("%.1f%%",rs.getDouble("ngram_score")  *100),
                String.format("%.1f%%",rs.getDouble("final_score")  *100),
                rs.getString("plagiarism_level"), rs.getString("comparison_time")
            });
            status("Loaded "+row+" reports from database.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Error loading history:\n"+ex.getMessage(),"DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("history.csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV File (*.csv)","csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
            StringBuilder sb = new StringBuilder();
            for (int c=0;c<histModel.getColumnCount();c++){ if(c>0)sb.append(","); sb.append(histModel.getColumnName(c)); }
            pw.println(sb);
            for (int r=0;r<histModel.getRowCount();r++){
                sb.setLength(0);
                for (int c=0;c<histModel.getColumnCount();c++){ if(c>0)sb.append(","); sb.append(histModel.getValueAt(r,c)); }
                pw.println(sb);
            }
            status("Exported: "+fc.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Export error:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
