package com.plagcoders.filehandler;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.zip.*;

/**
 * DocumentReader – Reads TXT (built-in), PDF (PDFBox or fallback), DOCX (POI or ZIP/XML fallback)
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class DocumentReader {

    public String extractText(File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        String name = file.getName().toLowerCase();
        if      (name.endsWith(".pdf"))  return readPdf(file);
        else if (name.endsWith(".docx")) return readDocx(file);
        else if (name.endsWith(".doc"))  return readDoc(file);
        else                             return readTxt(file);
    }

    // TXT – always works, no extra JARs needed
    private String readTxt(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
    }

    // PDF – tries PDFBox first, then pdftotext CLI, then raw byte scan
    private String readPdf(File f) throws IOException {
        // Method 1: Apache PDFBox (optional, place pdfbox-*.jar in lib/)
        try {
            Class<?> pd  = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
            Class<?> str = Class.forName("org.apache.pdfbox.text.PDFTextStripper");
            Object doc   = pd.getMethod("load", File.class).invoke(null, f);
            Object strip = str.getDeclaredConstructor().newInstance();
            String text  = (String) str.getMethod("getText", pd).invoke(strip, doc);
            pd.getMethod("close").invoke(doc);
            if (text != null && text.trim().length() > 10) return text;
        } catch (Exception ignored) {}

        // Method 2: pdftotext CLI (Linux/Mac – install poppler-utils)
        try {
            Process p = new ProcessBuilder("pdftotext", "-layout", f.getAbsolutePath(), "-")
                            .redirectErrorStream(true).start();
            String text = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            p.waitFor();
            if (text.trim().length() > 10) return text;
        } catch (Exception ignored) {}

        // Method 3: Raw byte scan (always works, basic quality)
        return rawScan(f);
    }

    private String rawScan(File f) throws IOException {
        byte[] bytes = Files.readAllBytes(f.toPath());
        StringBuilder sb = new StringBuilder(), word = new StringBuilder();
        for (byte b : bytes) {
            char c = (char)(b & 0xFF);
            if (c >= 32 && c < 127) { word.append(c); }
            else { if (word.length() > 3) sb.append(word).append(' '); word.setLength(0); }
        }
        return sb.toString();
    }

    // DOCX – tries Apache POI first, then built-in ZIP/XML fallback
    private String readDocx(File f) throws IOException {
        try {
            Class<?> xw = Class.forName("org.apache.poi.xwpf.usermodel.XWPFDocument");
            Class<?> ex = Class.forName("org.apache.poi.xwpf.extractor.XWPFWordExtractor");
            Object doc  = xw.getDeclaredConstructor(InputStream.class).newInstance(new FileInputStream(f));
            Object ext  = ex.getDeclaredConstructor(xw).newInstance(doc);
            String text = (String) ex.getMethod("getText").invoke(ext);
            ex.getMethod("close").invoke(ext);
            if (text != null && text.trim().length() > 5) return text;
        } catch (Exception ignored) {}
        return readDocxZip(f);
    }

    // DOCX fallback: unzip and strip XML tags (built-in, no extra JARs)
    private String readDocxZip(File f) throws IOException {
        try (ZipFile zip = new ZipFile(f)) {
            ZipEntry entry = zip.getEntry("word/document.xml");
            if (entry == null) return "[Cannot read DOCX – try adding poi-*.jar to lib/]";
            String xml = new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
            return xml.replaceAll("<[^>]+>", " ")
                      .replaceAll("&amp;","&").replaceAll("&lt;","<")
                      .replaceAll("&gt;",">").replaceAll("\\s+", " ").trim();
        }
    }

    // DOC (old binary) – needs Apache POI HWPF
    private String readDoc(File f) throws IOException {
        try {
            Class<?> hw = Class.forName("org.apache.poi.hwpf.HWPFDocument");
            Class<?> ex = Class.forName("org.apache.poi.hwpf.extractor.WordExtractor");
            Object doc  = hw.getDeclaredConstructor(InputStream.class).newInstance(new FileInputStream(f));
            Object ext  = ex.getDeclaredConstructor(hw).newInstance(doc);
            String text = (String) ex.getMethod("getText").invoke(ext);
            ex.getMethod("close").invoke(ext);
            return text;
        } catch (Exception ignored) {}
        return "[.doc files need poi-*.jar in lib/ folder]";
    }

    public String getExtension(File f) {
        String n = f.getName(); int i = n.lastIndexOf('.');
        return i >= 0 ? n.substring(i+1).toUpperCase() : "TXT";
    }
}
