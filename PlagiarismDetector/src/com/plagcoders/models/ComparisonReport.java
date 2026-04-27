package com.plagcoders.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** ComparisonReport POJO – Team: PlagCoders | TCS-408 | JAVA-IV-T167 */
public class ComparisonReport {
    private int      id = -1;
    private Document doc1, doc2;
    private double   jaccardScore, cosineScore, ngramScore, finalScore;
    private String   plagiarismLevel;
    private LocalDateTime comparisonTime = LocalDateTime.now();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ComparisonReport(Document d1, Document d2,
                            double j, double c, double ng, double f, String lv) {
        doc1=d1; doc2=d2; jaccardScore=j; cosineScore=c;
        ngramScore=ng; finalScore=f; plagiarismLevel=lv;
    }

    public int      getId()               { return id; }
    public void     setId(int id)         { this.id = id; }
    public Document getDoc1()             { return doc1; }
    public Document getDoc2()             { return doc2; }
    public double   getJaccardScore()     { return jaccardScore; }
    public double   getCosineScore()      { return cosineScore; }
    public double   getNgramScore()       { return ngramScore; }
    public double   getFinalScore()       { return finalScore; }
    public String   getPlagiarismLevel()  { return plagiarismLevel; }
    public LocalDateTime getTime()        { return comparisonTime; }
    public String   getTimeStr()          { return comparisonTime.format(FMT); }
}
