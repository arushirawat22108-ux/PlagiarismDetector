package com.plagcoders.models;

import java.io.File;
import java.time.LocalDateTime;

/** Document POJO – Team: PlagCoders | TCS-408 | JAVA-IV-T167 */
public class Document {
    private int    id = -1;
    private String fileName, filePath, fileType, rawText;
    private int    wordCount;
    private LocalDateTime uploadTime = LocalDateTime.now();

    public Document() {}

    public Document(File f, String rawText, String fileType) {
        this.fileName  = f.getName();
        this.filePath  = f.getAbsolutePath();
        this.fileType  = fileType;
        this.rawText   = rawText;
        this.wordCount = (rawText == null || rawText.isBlank()) ? 0
                        : rawText.trim().split("\\s+").length;
    }

    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }
    public String getFileName()        { return fileName; }
    public String getFilePath()        { return filePath; }
    public String getFileType()        { return fileType; }
    public String getRawText()         { return rawText; }
    public int    getWordCount()       { return wordCount; }
    public void   setWordCount(int c)  { wordCount = c; }
    public LocalDateTime getUploadTime(){ return uploadTime; }

    @Override public String toString() { return fileName + " [" + fileType + ", " + wordCount + " words]"; }
}
