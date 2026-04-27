package com.plagcoders.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * BarChartPanel – Custom-painted similarity score bar chart.
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class BarChartPanel extends JPanel {

    private double jaccard, cosine, ngram, finalScore;
    private boolean hasData = false;

    private static final Color C_J  = new Color(59,  130, 246);
    private static final Color C_C  = new Color(16,  185, 129);
    private static final Color C_N  = new Color(245, 158,  11);
    private static final Color C_F  = new Color(239,  68,  68);

    public BarChartPanel() {
        setBackground(new Color(248,250,252));
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226,232,240),1,true),
            new EmptyBorder(8,8,8,8)));
    }

    public void setScores(double j, double c, double ng, double f) {
        jaccard=j; cosine=c; ngram=ng; finalScore=f; hasData=true; repaint();
    }
    public void reset() { hasData=false; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W=getWidth(), H=getHeight();
        int padL=44, padR=16, padT=28, padB=46;
        int cW=W-padL-padR, cH=H-padT-padB;

        if (!hasData) {
            g2.setColor(new Color(148,163,184));
            g2.setFont(new Font("Segoe UI",Font.ITALIC,13));
            String m="Run a comparison to see the chart";
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(m,(W-fm.stringWidth(m))/2,H/2);
            g2.dispose(); return;
        }

        // Grid + Y axis labels
        for (int pct=0; pct<=100; pct+=25) {
            int y=padT+cH-(int)(cH*pct/100.0);
            g2.setColor(new Color(226,232,240));
            g2.setStroke(new BasicStroke(.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0,new float[]{4,4},0));
            g2.drawLine(padL,y,W-padR,y);
            g2.setColor(new Color(100,116,139));
            g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
            FontMetrics fm=g2.getFontMetrics();
            String lbl=pct+"%";
            g2.drawString(lbl, padL-fm.stringWidth(lbl)-4, y+4);
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(203,213,225));
        g2.drawLine(padL,padT,padL,padT+cH);
        g2.drawLine(padL,padT+cH,W-padR,padT+cH);

        double[] vals={jaccard,cosine,ngram,finalScore};
        Color[]  cols={C_J,C_C,C_N,C_F};
        String[] labs={"Jaccard","Cosine","N-Gram","Final"};
        String[] subs={"30%","40%","30%","Weighted"};

        int n=4, slotW=cW/n, barW=(int)(slotW*0.54);
        for (int i=0; i<n; i++) {
            int barH=Math.max(4,(int)(cH*vals[i]));
            int x=padL+i*slotW+(slotW-barW)/2;
            int y=padT+cH-barH;

            g2.setColor(new Color(0,0,0,18));
            g2.fillRoundRect(x+3,y+4,barW,barH,8,8);

            g2.setPaint(new GradientPaint(x,y,cols[i].brighter(),x,y+barH,cols[i]));
            g2.fillRoundRect(x,y,barW,barH,8,8);

            String ps=String.format("%.0f%%",vals[i]*100);
            g2.setFont(new Font("Segoe UI",Font.BOLD,11));
            FontMetrics fm=g2.getFontMetrics();
            int tx=x+(barW-fm.stringWidth(ps))/2;
            if (barH>=22) { g2.setColor(Color.WHITE); g2.drawString(ps,tx,y+16); }
            else          { g2.setColor(cols[i].darker()); g2.drawString(ps,tx,y-5); }

            g2.setColor(new Color(30,41,59));
            g2.setFont(new Font("Segoe UI",Font.BOLD,11));
            fm=g2.getFontMetrics();
            g2.drawString(labs[i],x+(barW-fm.stringWidth(labs[i]))/2,padT+cH+16);
            g2.setColor(new Color(100,116,139));
            g2.setFont(new Font("Segoe UI",Font.PLAIN,10));
            fm=g2.getFontMetrics();
            g2.drawString(subs[i],x+(barW-fm.stringWidth(subs[i]))/2,padT+cH+30);
        }
        g2.dispose();
    }
}
