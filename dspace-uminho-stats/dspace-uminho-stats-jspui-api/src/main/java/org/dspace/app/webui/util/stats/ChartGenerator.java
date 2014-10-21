package org.dspace.app.webui.util.stats;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Locale;

import javax.servlet.http.HttpSession;


import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.dspace.core.ConfigurationManager;
import org.jfree.data.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.chart.labels.*;
import org.jfree.chart.urls.*;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.servlet.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.jdbc.JDBCPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.jdbc.JDBCXYDataset;
import org.jfree.data.general.DefaultPieDataset;

import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.dspace.core.Context;


import java.sql.SQLException;

public class ChartGenerator {

	private Context context = null;
	private HttpSession session = null;
	private PrintWriter pw = null;
	private Document doc = null;
	private Document queries = null;
	private Locale locale = null;
	
	String width = null;
	String height = null;
	String sql = null;
	String tickUnit = null;
	String tickCount = null;
	String tickFormat = null;
	String orientation = null;
	
	Node nChart = null;
	
		
	public ChartGenerator(Context context, HttpSession session, PrintWriter pw, Document doc, Document queries, Locale locale) {
		this.context = context;
		this.session = session;
		this.pw = pw;
		this.doc = doc;
		this.queries = queries;
		this.locale = locale;
	}
	
	public String getChartFileName(String chartName, Map inParams, NodeList paramDefs) {
		
		String filename = null;
		String chartType = null;

		try {
			
			nChart = XPathAPI.selectSingleNode(doc,
					"/charts/chart[@name='" + chartName + "']");
			
			if (nChart != null) {
				chartType = ((Element)nChart).getAttribute("type");
				Node eSize = XPathAPI.selectSingleNode(nChart, "size");
				width = ((Element)eSize).getAttribute("width");
				height = ((Element)eSize).getAttribute("height"); 
				
				Node nQuery = XPathAPI.selectSingleNode(nChart, "query[@src]");
				
				if (nQuery == null) {
					// doesnt have a src attribute
					sql = (String) XPathAPI.selectSingleNode(nChart, "query").getFirstChild().getNodeValue();
				} else {
					// fetch sql from querylist
					nQuery = XPathAPI.selectSingleNode(nChart, "query");
					String srcQuery = ((Element)nQuery).getAttribute("src");
					
					
					Node nSrc = XPathAPI.selectSingleNode(queries,
							"/querylist/query[@name='" + srcQuery + "']/sql");
					
					sql = nSrc.getFirstChild().getNodeValue();
				}
				if (paramDefs != null) {
					sql = processParams(sql, inParams, paramDefs);
				}				
			}

			if (chartType.equals("pie")) {
				filename = generatePieChart();
			} else if (chartType.equals("bar")) {
				orientation = (String) XPathAPI.selectSingleNode(nChart, "orientation").getFirstChild().getNodeValue();
				
				filename = generateBarChart();
			} else if (chartType.equals("time")) {
				Node eTick = XPathAPI.selectSingleNode(nChart, "tick");
				tickUnit = ((Element)eTick).getAttribute("unit");
				tickCount = ((Element)eTick).getAttribute("count");
				tickFormat = ((Element)eTick).getAttribute("format");
				
				filename = generateTimeChart();
			}
		} catch (TransformerException e) {
		        System.err.println("TransformerException " + e.toString());
		}
		
		return filename;
	}

	public String generateBarChart() {
		String filename = null;
		
		PlotOrientation po = null;
		boolean showLegend = false;
		
		try {
			
			CategoryDataset dataset = readCategoryData();

			if (orientation.equals("vertical")) {
				po = PlotOrientation.VERTICAL;
			} else {
				po = PlotOrientation.HORIZONTAL;
			}
			
			if (dataset.getRowCount() > 1) {
				showLegend = true;
			} else {
				showLegend = false;
			}

			
	        // create the chart...
	        JFreeChart chart = ChartFactory.createBarChart(
	        	null,       // chart title
	            null,               // domain axis label
	            null,                  // range axis label
	            dataset,                  // data
	            po, 			// orientation
	            showLegend,                    // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	        // set the background color for the chart...
	        chart.setBackgroundPaint(Color.white);

	        // get a reference to the plot for further customisation...
	        CategoryPlot plot = chart.getCategoryPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        RectangleInsets r = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
	        plot.setAxisOffset(r);
	        plot.setRangeCrosshairVisible(true);
	        
	        NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");
	        for (int i = 0; i < seriesList.getLength(); i++) {
	        	String index = ((Element)seriesList.item(i)).getAttribute("index");
	        	String color = ((Element)seriesList.item(i)).getAttribute("color");
	        	plot.getRenderer().setSeriesPaint(Integer.parseInt(index), Color.decode(color));
	        }
	        
	        // set the range axis to display integers only...
	/*        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setUpperMargin(0.15);
	*/        
	        // disable bar outlines...
	        CategoryItemRenderer renderer = plot.getRenderer();        
	        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
	        
	        CategoryAxis domainAxis = plot.getDomainAxis();
	        if (orientation.equals("vertical")) {
	        	domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	        } else {
	        	domainAxis.setMaximumCategoryLabelWidthRatio(0.4f);
	        	domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
	        }

			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	        
		} catch (Exception e) {
            System.out.println("Exception - " + e.toString());
            e.printStackTrace(System.out);			
		}
		return filename;
	}
	public String generatePieChart() {
		String filename = null;
		try {

			PieDataset data = readPieData();
			
			PiePlot plot = new PiePlot(data);
			plot.setInsets(new RectangleInsets(0, 5, 5, 5));
			
			plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} - ({2})"));
			plot.setLabelBackgroundPaint(java.awt.Color.white);
			plot.setLabelOutlinePaint(java.awt.Color.white);
			plot.setLabelShadowPaint(null);
			//plot.setOutlinePaint(null);
			//plot.setInteriorGap(0.25);

			JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			
			chart.setBackgroundPaint(java.awt.Color.white);
			
			//JFreeChart chart = ChartFactory.createPieChart(null, //title 
			//		data, true, true, true);
	
			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	        
		} catch (Exception e) {
            System.out.println("Exception - " + e.toString());
            e.printStackTrace(System.out);			
		}
		return filename;
	}
	public String generateTimeChart() {
		String filename = null;
		boolean showLegend = false;
		
		try {

			XYDataset dataset = readXYData();

			if (dataset.getSeriesCount() > 1) {
				showLegend = true;
			} else {
				showLegend = false;
			}

	        JFreeChart chart = ChartFactory.createTimeSeriesChart(
	                null, // Title
	                null, // x-axis title
	                null, // y-axis title
	                dataset,
	                showLegend,
	                true,
	                true
	            );
	        
	        chart.setBackgroundPaint(Color.white);
	        
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            
            RectangleInsets r = new RectangleInsets(5.0, 5.0, 5.0, 5.0);
            plot.setAxisOffset(r);
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);
            
	        NodeList seriesList = XPathAPI.selectNodeList(nChart, "series");
	        for (int i = 0; i < seriesList.getLength(); i++) {
	        	String index = ((Element)seriesList.item(i)).getAttribute("index");
	        	String color = ((Element)seriesList.item(i)).getAttribute("color");
	        	plot.getRenderer().setSeriesPaint(Integer.parseInt(index), Color.decode(color));
	        }
            
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            if (tickUnit.equals("year")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.YEAR, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            } else if (tickUnit.equals("month")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));            	
            } else if (tickUnit.equals("day")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            } else if (tickUnit.equals("hour")) {
            	axis.setTickUnit(new DateTickUnit(DateTickUnit.HOUR, 
            			Integer.parseInt(tickCount),new SimpleDateFormat(tickFormat, locale)));
            }
            
            axis.setVerticalTickLabels(true);        
            
            //StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
            
            //renderer.setPlotImages(true);
            //renderer.setPlotShapes(true);
            //renderer.setSeriesShapesFilled(0, Boolean.TRUE);
            //renderer.setSeriesShapesFilled(1, Boolean.FALSE);
	
			//  Write the chart image to the temporary directory
	        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	        filename = ServletUtilities.saveChartAsPNG(chart, Integer.parseInt(width), Integer.parseInt(height), info, session);
	 
		} catch (Exception e) {
            System.out.println("Exception - " + e.toString());
            //e.printStackTrace(System.out);			
		}
		return filename;
	}
	private PieDataset readPieData() {
		JDBCPieDataset data = null;
		try {
			data = new JDBCPieDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("SQLException: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.print("Exception: ");
			System.err.println(e.getMessage());
		}
		return data;
	}
	private CategoryDataset readCategoryData() {
		JDBCCategoryDataset data = null;
		try {
			data = new JDBCCategoryDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("SQLException: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.print("Exception: ");
			System.err.println(e.getMessage());
		}
		return data;
	}
	private XYDataset readXYData() {
		JDBCXYDataset data = null;
		try {
			data = new JDBCXYDataset(context.getDBConnection());
			data.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("SQLException: ");
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.print("Exception: ");
			System.err.println(e.getMessage());
		}
		return data;
	}

	private String processParams(String sql, Map inParams, NodeList paramDef) {
		String newSql = sql;
                
		for (int i = 0; i < paramDef.getLength(); i++) {
			Node param = paramDef.item(i);
			String id = param.getAttributes().getNamedItem("id")
					.getFirstChild().getNodeValue();
			String value [] = {(String) inParams.get(id)};
			String values = "";
			if (value == null || value[0] == null || value[0].length() == 0) {
				String name = param.getAttributes().getNamedItem("name")
				.getFirstChild().getNodeValue();
			} else {
				for(int j = 0 ; j < value.length - 1; j++){
				values += value[j] +",";
				}
				values += value[value.length - 1];
				newSql = newSql.replaceAll(id, values);
			}
		}
		return newSql;
	}

}
