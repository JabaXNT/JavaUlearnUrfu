package org.Ulearn.GraphExport;

import org.bson.Document;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Font;
import java.util.List;

import javax.swing.*;

public class DatasetGraphs {
    public void createBarChart(DefaultCategoryDataset dataset, String title, String xLabel, String yLabel) {
        try {
            JFreeChart chart = ChartFactory.createBarChart(
                    title, xLabel, yLabel, dataset);
    
            chart.setBackgroundPaint(ChartColor.white);
    
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(ChartColor.lightGray);
            plot.setDomainGridlinePaint(ChartColor.white);
            plot.setDomainGridlinesVisible(true);
            plot.setRangeGridlinePaint(ChartColor.white);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setTickUnit(new NumberTickUnit(15));
    
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, ChartColor.blue);
            renderer.setItemMargin(0.2);

            LegendTitle legend = chart.getLegend();
            legend.setFrame(new BlockBorder(ChartColor.BLACK));
            legend.setBackgroundPaint(ChartColor.WHITE);
            legend.setItemFont(new Font("Arial", Font.PLAIN, 14));
    
            ChartPanel chartPanel = new ChartPanel(chart);
            JFrame frame = new JFrame();
            frame.setContentPane(chartPanel);
            frame.pack();
            frame.setVisible(true);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
