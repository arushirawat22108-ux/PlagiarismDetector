package com.plagcoders.engine;

import com.plagcoders.preprocessing.TextPreprocessor;
import java.util.*;

/**
 * SimilarityEngine – Jaccard (30%) + Cosine (40%) + N-Gram (30%) = Weighted Score
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class SimilarityEngine {

    private final TextPreprocessor pp = new TextPreprocessor();

    public static final double W_JACCARD = 0.30;
    public static final double W_COSINE  = 0.40;
    public static final double W_NGRAM   = 0.30;

    /** Jaccard: |A∩B| / |A∪B| */
    public double jaccard(List<String> t1, List<String> t2) {
        if (t1.isEmpty() || t2.isEmpty()) return 0;
        Set<String> s1 = new HashSet<>(t1), s2 = new HashSet<>(t2);
        Set<String> inter = new HashSet<>(s1); inter.retainAll(s2);
        Set<String> union = new HashSet<>(s1); union.addAll(s2);
        return union.isEmpty() ? 0 : (double) inter.size() / union.size();
    }

    /** Cosine: (A·B) / (|A||B|) */
    public double cosine(List<String> t1, List<String> t2) {
        if (t1.isEmpty() || t2.isEmpty()) return 0;
        Map<String,Double> v1 = pp.buildTfVector(t1), v2 = pp.buildTfVector(t2);
        Set<String> vocab = new HashSet<>(v1.keySet()); vocab.addAll(v2.keySet());
        double dot=0, m1=0, m2=0;
        for (String w : vocab) {
            double a = v1.getOrDefault(w,0.0), b = v2.getOrDefault(w,0.0);
            dot+=a*b; m1+=a*a; m2+=b*b;
        }
        double denom = Math.sqrt(m1)*Math.sqrt(m2);
        return denom==0 ? 0 : dot/denom;
    }

    /** N-gram overlap score */
    public double ngram(List<String> t1, List<String> t2, int n) {
        if (t1.size()<n || t2.size()<n) return 0;
        List<String> ng1=pp.generateNgrams(t1,n), ng2=pp.generateNgrams(t2,n);
        Map<String,Integer> f1=pp.buildFreqMap(ng1), f2=pp.buildFreqMap(ng2);
        int shared=0;
        for (Map.Entry<String,Integer> e : f1.entrySet())
            shared += Math.min(e.getValue(), f2.getOrDefault(e.getKey(),0));
        int mx = Math.max(ng1.size(), ng2.size());
        return mx==0 ? 0 : (double) shared/mx;
    }

    public double combinedNgram(List<String> t1, List<String> t2) {
        return (ngram(t1,t2,2) + ngram(t1,t2,3)) / 2.0;
    }

    public double weightedScore(double j, double c, double ng) {
        return j*W_JACCARD + c*W_COSINE + ng*W_NGRAM;
    }

    public String classifyLevel(double score) {
        if (score*100 < 30) return "LOW";
        if (score*100 < 70) return "MEDIUM";
        return "HIGH";
    }

    public Result analyze(String text1, String text2) {
        List<String> t1 = pp.preprocess(text1), t2 = pp.preprocess(text2);
        double j=jaccard(t1,t2), c=cosine(t1,t2), ng=combinedNgram(t1,t2);
        double w=weightedScore(j,c,ng);
        return new Result(j,c,ng,w,classifyLevel(w),t1.size(),t2.size());
    }

    public static class Result {
        public final double jaccard, cosine, ngram, weighted;
        public final String level;
        public final int    wc1, wc2;
        public Result(double j,double c,double ng,double w,String lv,int w1,int w2){
            jaccard=j; cosine=c; ngram=ng; weighted=w; level=lv; wc1=w1; wc2=w2;
        }
    }
}
