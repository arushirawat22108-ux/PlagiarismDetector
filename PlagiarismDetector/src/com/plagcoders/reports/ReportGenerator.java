package com.plagcoders.reports;

import com.plagcoders.models.ComparisonReport;
import java.io.*;

/**
 * ReportGenerator – Saves HTML and TXT plagiarism reports to disk.
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class ReportGenerator {

    public void generateHtmlReport(ComparisonReport r, String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) { pw.println(buildHtml(r)); }
    }

    public void generateTxtReport(ComparisonReport r, String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            String line = "=".repeat(60);
            pw.println(line);
            pw.println("  SMART PLAGIARISM DETECTION SYSTEM — REPORT");
            pw.println("  Team: PlagCoders | TCS-408 | JAVA-IV-T167");
            pw.println(line);
            pw.println();
            pw.printf("Generated  : %s%n", r.getTimeStr());
            pw.printf("Document 1 : %s  (%d words)%n", r.getDoc1().getFileName(), r.getDoc1().getWordCount());
            pw.printf("Document 2 : %s  (%d words)%n", r.getDoc2().getFileName(), r.getDoc2().getWordCount());
            pw.println();
            pw.println("-".repeat(60));
            pw.printf("Jaccard Similarity  : %6.2f%%  (weight 30%%)%n", r.getJaccardScore()*100);
            pw.printf("Cosine Similarity   : %6.2f%%  (weight 40%%)%n", r.getCosineScore() *100);
            pw.printf("N-Gram Similarity   : %6.2f%%  (weight 30%%)%n", r.getNgramScore()  *100);
            pw.println("-".repeat(60));
            pw.printf("FINAL SCORE         : %6.2f%%%n", r.getFinalScore()*100);
            pw.printf("PLAGIARISM LEVEL    : %s%n%n",    r.getPlagiarismLevel());
            pw.println("Level Guide:");
            pw.println("  LOW    (0-30%)  : Minor / coincidental similarity");
            pw.println("  MEDIUM (30-70%) : Significant overlap, review required");
            pw.println("  HIGH   (>70%)   : Strong plagiarism detected");
            pw.println(line);
        }
    }

    private String buildHtml(ComparisonReport r) {
        String col  = switch(r.getPlagiarismLevel()) { case "LOW"->"#10b981"; case "MEDIUM"->"#f59e0b"; default->"#ef4444"; };
        String icon = switch(r.getPlagiarismLevel()) { case "LOW"->"✅"; case "MEDIUM"->"⚠️"; default->"🚨"; };
        double pct  = r.getFinalScore()*100;

        return "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<title>Plagiarism Report</title><style>" +
        "*{box-sizing:border-box;margin:0;padding:0}" +
        "body{font-family:system-ui,sans-serif;background:#f0f4f8;padding:30px;color:#1a202c}" +
        ".wrap{max-width:800px;margin:auto;background:#fff;border-radius:16px;box-shadow:0 8px 40px rgba(0,0,0,.12);overflow:hidden}" +
        ".hdr{background:linear-gradient(135deg,#0f172a,#1e3a8a);color:#fff;padding:32px;text-align:center}" +
        ".hdr h1{font-size:1.6em;font-weight:800;margin-bottom:6px}" +
        ".hdr p{opacity:.7;font-size:.9em}" +
        ".body{padding:32px}" +
        ".info{display:grid;grid-template-columns:1fr 1fr;gap:14px;margin-bottom:26px}" +
        ".ic{background:#f8fafc;border:1px solid #e2e8f0;border-radius:10px;padding:14px 16px}" +
        ".ic label{display:block;font-size:.72em;font-weight:700;color:#94a3b8;text-transform:uppercase;letter-spacing:.5px;margin-bottom:4px}" +
        ".ic span{font-weight:700;font-size:.95em}" +
        ".scores{display:grid;grid-template-columns:repeat(3,1fr);gap:14px;margin-bottom:24px}" +
        ".sc{background:#f8fafc;border:1px solid #e2e8f0;border-radius:10px;padding:18px 12px;text-align:center}" +
        ".sv{font-size:2em;font-weight:800;color:#3b82f6}" +
        ".sl{font-size:.75em;color:#64748b;margin-top:4px;font-weight:600}" +
        ".ss{font-size:.68em;color:#94a3b8}" +
        ".fin{border:3px solid "+col+";border-radius:14px;padding:28px;text-align:center;margin-bottom:22px}" +
        ".fp{font-size:4em;font-weight:900;color:"+col+"}" +
        ".fl{font-size:1.1em;font-weight:700;color:"+col+";margin-top:6px}" +
        ".bar-bg{background:#e2e8f0;border-radius:99px;height:16px;overflow:hidden;margin-bottom:24px}" +
        ".bar-fg{background:"+col+";height:100%;border-radius:99px;width:"+String.format("%.1f",pct)+"%}" +
        ".leg{background:#f8fafc;border-radius:10px;padding:18px;border:1px solid #e2e8f0}" +
        ".leg h3{font-size:.85em;text-transform:uppercase;color:#64748b;margin-bottom:12px}" +
        ".lr{display:flex;align-items:center;gap:10px;margin:6px 0;font-size:.85em}" +
        ".dot{width:10px;height:10px;border-radius:50%;flex-shrink:0}" +
        ".foot{background:#f8fafc;padding:14px;text-align:center;font-size:.78em;color:#94a3b8;border-top:1px solid #e2e8f0}" +
        "</style></head><body><div class='wrap'>" +
        "<div class='hdr'><h1>📄 Plagiarism Detection Report</h1>" +
        "<p>Smart Plagiarism Detection System &bull; PlagCoders &bull; TCS-408 &bull; JAVA-IV-T167</p></div>" +
        "<div class='body'><div class='info'>" +
        "<div class='ic'><label>Document 1</label><span>"+esc(r.getDoc1().getFileName())+"</span></div>" +
        "<div class='ic'><label>Document 2</label><span>"+esc(r.getDoc2().getFileName())+"</span></div>" +
        "<div class='ic'><label>Words (Doc 1)</label><span>"+r.getDoc1().getWordCount()+"</span></div>" +
        "<div class='ic'><label>Words (Doc 2)</label><span>"+r.getDoc2().getWordCount()+"</span></div>" +
        "<div class='ic'><label>Generated</label><span>"+r.getTimeStr()+"</span></div>" +
        "<div class='ic'><label>Report ID</label><span>#"+(r.getId()>0?r.getId():"–")+"</span></div>" +
        "</div><div class='scores'>" +
        "<div class='sc'><div class='sv'>"+String.format("%.1f",r.getJaccardScore()*100)+"%</div><div class='sl'>Jaccard Similarity</div><div class='ss'>Vocabulary Overlap · 30%</div></div>" +
        "<div class='sc'><div class='sv'>"+String.format("%.1f",r.getCosineScore() *100)+"%</div><div class='sl'>Cosine Similarity</div><div class='ss'>Word Frequency · 40%</div></div>" +
        "<div class='sc'><div class='sv'>"+String.format("%.1f",r.getNgramScore()  *100)+"%</div><div class='sl'>N-Gram Similarity</div><div class='ss'>Phrase Matching · 30%</div></div>" +
        "</div><div class='fin'><div style='font-size:.85em;color:#64748b;font-weight:600;margin-bottom:6px'>FINAL PLAGIARISM SCORE</div>" +
        "<div class='fp'>"+String.format("%.1f",pct)+"%</div>" +
        "<div class='fl'>"+icon+" "+r.getPlagiarismLevel()+" PLAGIARISM</div></div>" +
        "<div class='bar-bg'><div class='bar-fg'></div></div>" +
        "<div class='leg'><h3>Classification Guide</h3>" +
        "<div class='lr'><div class='dot' style='background:#10b981'></div> LOW (&lt;30%) – Minor similarities, likely coincidental</div>" +
        "<div class='lr'><div class='dot' style='background:#f59e0b'></div> MEDIUM (30–70%) – Significant overlap, review required</div>" +
        "<div class='lr'><div class='dot' style='background:#ef4444'></div> HIGH (&gt;70%) – Strong plagiarism detected</div>" +
        "</div></div><div class='foot'>Generated by Smart Plagiarism Detection System &bull; PlagCoders &bull; JAVA-IV-T167</div></div></body></html>";
    }

    private String esc(String s) {
        return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
