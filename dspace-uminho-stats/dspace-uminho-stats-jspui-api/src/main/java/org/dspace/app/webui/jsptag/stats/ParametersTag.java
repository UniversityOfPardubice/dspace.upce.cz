/*
 * StatsParametersTag.java
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.webui.jsptag.stats;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import org.w3c.dom.Node;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import org.dspace.app.webui.util.UIUtil;
import org.dspace.core.Context;
import javax.servlet.jsp.JspWriter;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xpath.XPathAPI;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.dspace.app.webui.util.stats.StatsUtil;

/*
 * @author Angelo Miranda
 * @version $Revision$
 */
public class ParametersTag extends TagSupport
{
    /** NodePageTab */
    private Node nodePageTab;

    /** params of form whith values */
    private Map params = new HashMap();
    
    /* user belong to group admin statistics*/
    private boolean isStatsAdmin;

    public ParametersTag()
    {
        super();
    }

    public int doStartTag() throws JspException
    {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        
        try
        {
            params = (Map) request.getAttribute("parameters");
                
            // Obtain a context
            Context context = UIUtil.obtainContext(request);
            JspWriter out = pageContext.getOut();
            isStatsAdmin = StatsUtil.isAdminStatistics(context);
            
            createStatsForm(out,context, request);

        }
        catch (java.sql.SQLException e)
        {
            throw new JspException(e);
        }
        catch (IOException ie)
        {
            throw new JspException(ie);
        }

        return SKIP_BODY;            
    }

    /**
     * Get the value of PageTab.
     * 
     * @return Value of PageTab.
     */
    public Node getPageTab()
    {
        return nodePageTab;
    }
    /**
     * Set the value of PageTab.
     * 
     * @param v
     *            Value to assign to tab.
     */
    public void setPageTab(Node v)
    {
        this.nodePageTab = v;
    }

    /**
     * Get the Map of Params.
     * 
     * @return Map of params.
     */
    public Map getParams()
    {
        return params;
    }

    /**
     * Set the value of Map parameters.
     * 
     * @param px
     *            Value to assign to params.
     */
    public void setParams(Map px)
    {
        this.params = px;
    }

    public void release()
    {
        nodePageTab = null;
        params = null;
    }
    
    //Private Methods
    public void createStatsForm(JspWriter out, Context c, HttpServletRequest request) throws IOException {

        try {

            if (nodePageTab != null) {
            	
            	NodeList allParamList = XPathAPI.selectNodeList(nodePageTab, "param");
            	
            	if (allParamList != null && allParamList.getLength() > 0) {

            		out.println("<SCRIPT language=\"JavaScript\">");
            		out.println("function OnSubmitForm()");
            		out.println("{");
            		out.println("  document.sform.action =\"" + request.getContextPath() + "/stats\";");
            		out.println("}");
            		out.println("</SCRIPT>");

            		out.println("<form name='sform'  method='POST' onSubmit=\"return OnSubmitForm();\">");
                        
            		out.println("<table border='0' cellspacing='0' cellpadding='0'>");

            		// Parameters to the form
            		NodeList ParamList = XPathAPI.selectNodeList(nodePageTab, "param");
            		if (ParamList != null && ParamList.getLength() != 0) {
            			String lastParamLineBreak = "yes";
            			for (int j = 0; j < ParamList.getLength(); j++) {
            				Element eParam = (Element)ParamList.item(j);
            				
            				String paramid = eParam.getAttribute("id");
            				String paramLabel = eParam.getAttribute("label");
            				String paramWidth = eParam.getAttribute("width");
            				if (paramWidth=="") paramWidth="20";
            				String paramFormat = eParam.getAttribute("format");
            				String paramHint = eParam.getAttribute("hint");
            				String paramSrc = eParam.getAttribute("src");
            				String paramLineBreak = eParam.getAttribute("line-break");
            				if (paramLineBreak=="") paramLineBreak="yes";

            				if (lastParamLineBreak.equals("yes")) {
	            				out.println("<tr>");            				
	            				out.println("<td valign='bottom'><nobr><span class='Normal'>" + paramLabel + "</span></nobr></td>");
	            				out.println("<td width='5'></td>");
	            				out.println("<td><nobr>");
            				} else {
            					out.println("&nbsp;" + paramLabel);
            				}
            				if (paramSrc=="") {
            					String value = (String) params.remove(paramid);
            					if(value == null) value = "";
            					out.println("<input class='frmInput' type='text' size='" + paramWidth + "' name='"
            									+ paramid + "' value='" + value + "'/>");
            				} else {
            					processSubQuery(c, paramSrc, out, paramid);
            				}
            				if (paramid.equals("start")) {
            					out.println("<a href=\"javascript:cal1.popup();\">" +
            							"<img src=\"" + request.getContextPath() + "/stats/img/cal.gif\" width='16' height='16' border='0' alt='Clique aqui para aparecer o calendario'></a>");
            				} else if (paramid.equals("end")) {
            					out.println("<a href=\"javascript:cal2.popup();\">" +
            							"<img src=\"" + request.getContextPath() + "/stats/img/cal.gif\" width='16' height='16' border='0' alt='Clique aqui para aparecer o calendario'></a>");
            				}
            				if (paramFormat!=null) {
            					out.println("<span class='Normal'>" + paramFormat + "</span>");
            				}
            				if (paramHint!=null) {
            					out.println("<span class='Normal'>" + paramHint + "</span>");
            				}
            				if (paramLineBreak.equals("yes")) {
	            				out.println("</nobr></td>");
	            				out.println("</tr>");
            				}
            				lastParamLineBreak = paramLineBreak;
            			}            		
            		}

            		// put all rest of parameters in the form
            		String [] keys = (String []) params.keySet().toArray(new String [params.size()]);
            		for (int j =0; j<keys.length; j++) {
            			out.println("<input type='hidden' name='" +
            							keys[j] + "' value='"
            							+ params.get(keys[j])
            							+ "'/>");
            		}

            		Document doc = nodePageTab.getOwnerDocument();
            		Element stats = (Element)XPathAPI.selectSingleNode(doc, "statistics");
            		String submitLabel = stats.getAttribute("submit-label");

            		out.println("<tr><td height='35' valign='bottom'> <input type='submit' value=' " + submitLabel + " ' class='button'/></td>");
            		out.println("</tr>");
            		out.println("</table></form>");
            		out.println("<script language=\"JavaScript\">");
            		out.println("var cal1 = new calendar1(document.forms['sform'].elements['start']);");
            		out.println("cal1.year_scroll = true;");
            		out.println("cal1.time_comp = false;");
            		out.println("var cal2 = new calendar1(document.forms['sform'].elements['end']);");
            		out.println("cal2.year_scroll = true;");
            		out.println("cal2.time_comp = false;");
            		out.println("</script>");
            	}
            }
        } catch (IOException e) {
        	System.err.println("IOException " + e.toString());
        } catch (TransformerException e) {
        	System.err.println("TransformerException " + e.toString());
        }
    }

	
	public void processSubQuery(Context c, String qName, JspWriter out , String paramID) throws IOException {

		String report = "";
		Element eQuery = null;

		String queriesFilePath = StatsUtil.getLocalizedFileName(
				         StatsUtil.getSessionLocale((HttpServletRequest)pageContext.getRequest()), 
				         StatsUtil.getStatsPath() + "stats-queries", 
				         ".xml");
		
		report = qName;
		eQuery = getSubQueryAsElement(queriesFilePath, qName);

		if (eQuery == null) {
			out.println("Subquery " + qName + " doesn't exist");

		} else {
			try {
				String sql = (String) XPathAPI.selectSingleNode(eQuery, "sql")
						.getFirstChild().getNodeValue();
				NodeList paramDefs = XPathAPI.selectNodeList(eQuery, "param");
				if (paramDefs != null) {
					sql = getParamsFromContext(sql, c, paramDefs);
				}
				Statement statement = c.getDBConnection().createStatement();
				ResultSet rs = statement.executeQuery(sql);
				out.println("<select class='frmSelect' name='" + paramID + "' id='"
						+ paramID + "' >");
				String value = (String) params.remove(paramID);
				out.println(subQuery2ListBox(c, rs, value, paramID, qName));
				out.println("</select>");
				

			} catch (SQLException e) {
				System.err.println("SQLException" + e.toString());
			} catch (TransformerException e) {
				System.err.println("TransformerException" + e.toString());
			}
		}
	}

	private String subQuery2ListBox(Context c, ResultSet results, String storedValue, String nameStoredValue, String qname){

		String resutlString = "";
		int columns;
		
		try {
			ResultSetMetaData meta = results.getMetaData();
			
			columns = meta.getColumnCount() + 1;
			while (results.next()) {
				String optionValue = "";
				String optionText = "";
				for (int i = 1; i < columns; i++) {
					String colValue = "";
					String colName = meta.getColumnName(i);
					int jdbctype = meta.getColumnType(i);

					if (jdbctype == Types.BIT) {
						colValue = new Boolean(results.getBoolean(i))
								.toString();
					} else if (jdbctype == Types.INTEGER
							|| jdbctype == Types.NUMERIC
							|| jdbctype == Types.DECIMAL) {
						colValue = new Integer(results.getInt(i)).toString();
					} else if (jdbctype == Types.BIGINT) {
						colValue = new Integer(results.getInt(i)).toString();
					} else if (jdbctype == Types.VARCHAR) {
						try {
							byte[] bytes = results.getBytes(i);

							if (bytes != null) {
								colValue = new String(results.getBytes(i),"UTF-8");

							} else {
								colValue = results.getString(i);
							}
						} catch (UnsupportedEncodingException e) {
							// do nothing, UTF-8 is built in!
						}
					} else if (jdbctype == Types.DATE) {
						colValue = results.getDate(i).toString();
					} else if (jdbctype == Types.TIME) {
						colValue = results.getTime(i).toString();
					} else if (jdbctype == Types.TIMESTAMP) {
						colValue = results.getTimestamp(i).toString();
					} else {
						throw new IllegalArgumentException(
								"Unsupported JDBC type: " + jdbctype);
					}
					if (colName.equals("value"))
						optionValue += colValue;
					else {
						//TODO: hardcoded formatting for months for localization
						// fix to a solution like in queries -> the format defined on the xml
						if (qname.equals("monthlist")) {
							SimpleDateFormat formatter = new SimpleDateFormat("MMMM", StatsUtil.getSessionLocale((HttpServletRequest)pageContext.getRequest()));
							optionText = formatter.format(results.getDate(i));
						} else {
							optionText = colValue;
						}
						
					}
				}
                           //validate if user heve permission to view this object id
                           if(haveAccess(c, optionValue, nameStoredValue))
                           {
                                
                            resutlString += "<option value='" + optionValue + "'";
                            if (storedValue != null && storedValue.equals(optionValue))
                                resutlString += "SELECTED";
                            resutlString +=">" + optionText + "</option>\n";
                           }
			}
		}catch (SQLException e) {
			System.err.println("SQLException" + e.toString());
		}

		return resutlString;
	}
	
	
	static String getParamsFromContext(String sql, Context c, NodeList paramDef) {
		String newSql = sql;

		for (int i = 0; i < paramDef.getLength(); i++) {
			Node param = paramDef.item(i);
			String id = param.getAttributes().getNamedItem("id")
					.getFirstChild().getNodeValue();
			if (id.equals("EPERSON-ID")) {
				newSql = newSql.replaceAll(id, new Integer(c.getCurrentUser()
						.getID()).toString());
			} else {

			}
		}
		return newSql;
	}



	private Element getQueryAsElement(String queryListFilePath, String queryName) {
		Element eQuery = null;
		Document doc = null;
		try {
			DOMParser parser = new DOMParser();
			parser.parse(queryListFilePath);
			doc = parser.getDocument();
			eQuery = (Element) XPathAPI.selectSingleNode(doc,
					"querylist/query[@name='" + queryName + "']");
			if (eQuery == null || !eQuery.hasChildNodes()) {
				return null;
			}
		} catch (SAXException e) {
			System.err.println("SAXException " + e.toString());
		} catch (IOException e) {
			System.err.println("IOException " + e.toString());
		} catch (TransformerException e) {
			System.err.println("TransformerException " + e.toString());
		}

		return eQuery;
	}

	private Element getSubQueryAsElement(String queryListFilePath,
			String queryName) {
		Element eQuery = null;
		Document doc = null;
		try {
			DOMParser parser = new DOMParser();
			parser.parse(queryListFilePath);
			doc = parser.getDocument();
			eQuery = (Element) XPathAPI.selectSingleNode(doc,
					"querylist/sub-query[@name='" + queryName + "']");
			if (eQuery == null || !eQuery.hasChildNodes()) {
				return null;
			}
		} catch (SAXException e) {
			System.err.println("SAXException " + e.toString());
		} catch (IOException e) {
			System.err.println("IOException " + e.toString());
		} catch (TransformerException e) {
			System.err.println("TransformerException " + e.toString());
		}

		return eQuery;
	}
 private boolean haveAccess(Context c, String value, String subQueryId)
 {
     if (isStatsAdmin)
         return true;
     
     String objectType = (String) params.get("level");
     if (objectType != null && subQueryId.equals("object-id"))
        return StatsUtil.isAuthorized(c, objectType, value);
     return true;
 }
 
}
