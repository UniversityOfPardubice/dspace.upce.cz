/*
 * UsageEventJDBCLogger.java
 *
 * Version: $Revision$
 *
 * Date: $Date$
 *
 * Copyright (C) 2008, the DSpace Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of the DSpace Foundation nor the names of their
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
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

package org.dspace.app.stats;

import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dspace.services.model.Event;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.usage.AbstractUsageEventListener;
import org.dspace.usage.UsageEvent;
import org.dspace.core.Constants;


/**
 * Serialize AbstractUsageEvent data to a database table. 
 * 
 * @author Angelo Miranda 
 * @version $Revision$
 */
public class UsageEventJDBCEventListener extends AbstractUsageEventListener
{
	
    /** log4j category */
    private static Logger log = Logger.getLogger(UsageEventJDBCEventListener.class);
    
	private UsageEvent ue = null;
	
	public void receiveEvent(Event event) {
		if(event instanceof UsageEvent)
		{
			try{
				log.info("[KEEP] Register event "+event.getName());
			    UsageEvent ue = (UsageEvent) event;
			    this.ue = ue;
			    this.fire();
			}
			catch(Exception e)
			{
				log.error("[Uminho Stats Addon] Error registering event",e);
			}
		}
	}

	/*
     * Serialize to database
	 */
    private void fire()
    {
        String country;
        String countryCode = null;
        String countryName = null;
                
        try
        {
            country = Country.getCountry(this.ue.getContext(), this.ue.getRequest().getRemoteAddr());
            log.info("[KEEP] Country: "+country);
            String[] temp = country.split(";");

	        if (temp.length == 2)
	        {
		        countryCode = temp[0];
		        countryName = temp[1];
		        checkCountry(countryCode, countryName);
	        }
    
	        switch (this.ue.getAction())
	        {
	            case VIEW:
	                switch (this.ue.getObject().getType())
	                {
	                    case Constants.ITEM:
	                        insertView(countryCode);
	                        break;                  
	                    case Constants.BITSTREAM:
	                        insertDownload(countryCode);
	                        break;                      
	                }
	                break;
                case SEARCH:
                    insertSearch(countryCode);
                    break;                  	            
                case LOGIN:
                    insertLogin(countryCode);
                    break;
                case ADVANCED_WORKFLOW:
                    insertAdvanceWorkflow(countryCode);
                    break;                                  
	        }
	        this.ue.getContext().commit();
        }
        catch (SQLException e)
        {
            log.error("ERROR: Cant execute sql: " + e.getMessage(),e);
        }
    }
    
	private void insertView(String countryCode) throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        sql.append("insert into stats.view ");
        sql.append("(view_id, date, time, item_id, session_id, user_id, ip, country_code) ");
        sql.append("values ");
        sql.append("(getnextid('stats.view'), ?, ?, ?, ?, ?, ?, ?);");
        
        java.util.Date now = new java.util.Date();
        log.info("[KEEP] Date: "+now.getTime());
        log.info("[KEEP] Object: "+ ((this.ue.getObject() == null) ? "NULL" : this.ue.getObject().getID()));
        log.info("[KEEP] ID: "+ this.ue.getId());
        log.info("[KEEP] Request: "+ ((this.ue.getRequest() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Context: "+ ((this.ue.getContext() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Session: "+ ((this.ue.getRequest() != null && this.ue.getRequest().getSession() == null) ? "NULL" : "NOT NULL"));
        
        Date date = new Date(now.getTime());
        Time time = new Time(now.getTime());

        params.add(date);
        params.add(time);
        params.add(new Integer(this.ue.getObject().getID()));
        params.add(this.ue.getRequest().getSession().getId());
        params.add((null == this.ue.getContext() || this.ue.getContext().getCurrentUser() == null) ? "anonymous" : this.ue.getContext().getCurrentUser().getEmail());
        params.add(this.ue.getRequest().getRemoteAddr());
        params.add(countryCode);
        
        DatabaseManager.updateQuery(this.ue.getContext(), sql.toString(), params.toArray());                           
    }

    private void insertDownload(String countryCode) throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        sql.append("insert into stats.download ");
        sql.append("(download_id, date, time, bitstream_id, item_id, session_id, user_id, ip, country_code, relative_value) ");
        sql.append("values ");
        sql.append("(getnextid('stats.download'), ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        
        Integer itemId = getItemID(this.ue.getObject().getID());
        Long numberOfBitstreams = getNumberOfBitstreams(itemId);
        Double relativeValue = 1d / numberOfBitstreams; 
        
        java.util.Date now = new java.util.Date();
        log.info("[KEEP] Date: "+now.getTime());
        log.info("[KEEP] Object: "+ ((this.ue.getObject() == null) ? "NULL" : this.ue.getObject().getID()));
        log.info("[KEEP] ID: "+ this.ue.getId());
        log.info("[KEEP] Request: "+ ((this.ue.getRequest() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Context: "+ ((this.ue.getContext() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Session: "+ ((this.ue.getRequest() != null && this.ue.getRequest().getSession() == null) ? "NULL" : "NOT NULL"));
        
        Date date = new Date(now.getTime());
        Time time = new Time(now.getTime());
        log.info("[KEEP] Date SQL: "+ ((date == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Time SQL: "+ ((time == null) ? "NULL" : "NOT NULL"));
        
        
        params.add(date);
        params.add(time);
        params.add(new Integer(this.ue.getObject().getID()));
        params.add(itemId);
        params.add(this.ue.getRequest().getSession().getId());
        params.add((null == this.ue.getContext() || this.ue.getContext().getCurrentUser() == null) ? "anonymous" : this.ue.getContext().getCurrentUser().getEmail());
        params.add(this.ue.getRequest().getRemoteAddr());
        params.add(countryCode);
        params.add(relativeValue);
        
        if (itemId != null)
            DatabaseManager.updateQuery(this.ue.getContext(), sql.toString(), params.toArray());        
    }

    private void insertSearch(String countryCode) throws SQLException
    {
        String sqlID = "select getnextid('stats.search') as id";
        TableRow row = DatabaseManager.querySingle(this.ue.getContext(), sqlID);
        Integer id = row.getIntColumn("id");
        
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        sql.append("insert into stats.search ");
        sql.append("(search_id, date, time, scope, scope_id, query, session_id, user_id, ip, country_code) ");
        sql.append("values ");
        sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

        String scope = "site";
        switch (this.ue.getObject().getType())
        {
            case Constants.COLLECTION:
                scope = "collection";
                break;
            case Constants.COMMUNITY:
                scope = "community";
                break;
            case Constants.SITE:
                scope = "site";
                break;                
        }
        String scopeID = (new Integer(this.ue.getObject().getID())).toString();
        
        String query = this.ue.getOtherInfo();
        
        java.util.Date now = new java.util.Date();
        log.info("[KEEP] Date: "+now.getTime());
        log.info("[KEEP] Object: "+ ((this.ue.getObject() == null) ? "NULL" : this.ue.getObject().getID()));
        log.info("[KEEP] ID: "+ this.ue.getId());
        log.info("[KEEP] Request: "+ ((this.ue.getRequest() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Context: "+ ((this.ue.getContext() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Session: "+ ((this.ue.getRequest() != null && this.ue.getRequest().getSession() == null) ? "NULL" : "NOT NULL"));
        
        Date date = new Date(now.getTime());
        Time time = new Time(now.getTime());
        log.info("[KEEP] Date SQL: "+ ((date == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Time SQL: "+ ((time == null) ? "NULL" : "NOT NULL"));

        params.add(id);
        params.add(date);
        params.add(time);
        params.add(scope);
        params.add(scopeID);
        params.add(query);
        params.add(this.ue.getRequest().getSession().getId());
        params.add((null == this.ue.getContext() || this.ue.getContext().getCurrentUser() == null) ? "anonymous" : this.ue.getContext().getCurrentUser().getEmail());
        params.add(this.ue.getRequest().getRemoteAddr());
        params.add(countryCode);
        
        DatabaseManager.updateQuery(this.ue.getContext(), sql.toString(), params.toArray());  
        
        
        // process words
        query = query.replace("author:", "");
        query = query.replace("title:", "");
        query = query.replace("keyword:", "");
        query = query.replace("abstract:", "");
        query = query.replace("series:", "");
        query = query.replace("sponsor:", "");
        query = query.replace("identifier:", "");
        query = query.replace("language:", "");
        query = query.replace(" AND ", "");
        query = query.replace(" OR ", "");
        query = query.replace(" NOT ", "");
        query = query.replace("(", " ");
        query = query.replace(")", " ");
        query = query.replace('"', ' ');
        query = query.replace(',', ' ');
        query = query.replace(';', ' ');
        query = query.replace('.', ' ');
        
        String[] querySplitted = query.split(" ");
        
        for (int i = 0; i < querySplitted.length; i++)
        {
            querySplitted[i].toLowerCase().trim();
            
            if (querySplitted[i].length() > 3)
            {   
                String sqlWords = "insert into stats.search_words (search_words_id, search_id, word) " +
                                  "values (getnextid('stats.download'), ?, ?)";
                params.clear();
                params.add(id);
                params.add(querySplitted[i]);
                
                DatabaseManager.updateQuery(this.ue.getContext(), sqlWords, params.toArray());  
            }
        }
    }

    private void insertLogin(String countryCode) throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();

        sql.append("insert into stats.login ");
        sql.append("(login_id, date, time, session_id, user_id, ip, country_code) ");
        sql.append("values ");
        sql.append("(getnextid('stats.login'), ?, ?, ?, ?, ?, ?);");
        
        java.util.Date now = new java.util.Date();
        log.info("[KEEP] Date: "+now.getTime());
        log.info("[KEEP] Object: "+ ((this.ue.getObject() == null) ? "NULL" : this.ue.getObject().getID()));
        log.info("[KEEP] ID: "+ this.ue.getId());
        log.info("[KEEP] Request: "+ ((this.ue.getRequest() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Context: "+ ((this.ue.getContext() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Session: "+ ((this.ue.getRequest() != null && this.ue.getRequest().getSession() == null) ? "NULL" : "NOT NULL"));
        
        Date date = new Date(now.getTime());
        Time time = new Time(now.getTime());
        log.info("[KEEP] Date SQL: "+ ((date == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Time SQL: "+ ((time == null) ? "NULL" : "NOT NULL"));

        params.add(date);
        params.add(time);
        params.add(this.ue.getRequest().getSession().getId());
        params.add((null == this.ue.getContext() || this.ue.getContext().getCurrentUser() == null) ? "anonymous" : this.ue.getContext().getCurrentUser().getEmail());
        params.add(this.ue.getRequest().getRemoteAddr());
        params.add(countryCode);
        
        DatabaseManager.updateQuery(this.ue.getContext(), sql.toString(), params.toArray());                           
    }

    private void insertAdvanceWorkflow(String countryCode) throws SQLException
    {
        StringBuffer sql = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        
        String[] extraInfo = (new String(this.ue.getOtherInfo())).split(":");
        
        Integer itemID = null;
        Integer collectionID = null;
        Integer oldState = null;
        
        if (extraInfo.length == 3)
        {
            itemID = Integer.parseInt(extraInfo[0].substring(extraInfo[0].indexOf("=") + 1)); 
            collectionID = Integer.parseInt(extraInfo[1].substring(extraInfo[1].indexOf("=") + 1));
            oldState = Integer.parseInt(extraInfo[2].substring(extraInfo[2].indexOf("=") + 1));
        }
        
        sql.append("insert into stats.workflow ");
        sql.append("(workflow_id, date, time, workflow_item_id, item_id, collection_id, old_state, session_id, user_id, ip) ");
        sql.append("values ");
        sql.append("(getnextid('stats.workflow'), ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        
        java.util.Date now = new java.util.Date();
        log.info("[KEEP] Date: "+now.getTime());
        log.info("[KEEP] Object: "+ ((this.ue.getObject() == null) ? "NULL" : this.ue.getObject().getID()));
        log.info("[KEEP] ID: "+ this.ue.getId());
        log.info("[KEEP] Request: "+ ((this.ue.getRequest() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Context: "+ ((this.ue.getContext() == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Session: "+ ((this.ue.getRequest() != null && this.ue.getRequest().getSession() == null) ? "NULL" : "NOT NULL"));
        
        Date date = new Date(now.getTime());
        Time time = new Time(now.getTime());
        log.info("[KEEP] Date SQL: "+ ((date == null) ? "NULL" : "NOT NULL"));
        log.info("[KEEP] Time SQL: "+ ((time == null) ? "NULL" : "NOT NULL"));
        
        
        params.add(date);
        params.add(time);
        params.add(new Integer(this.ue.getObject().getID()));
        params.add(itemID);
        params.add(collectionID);
        params.add(oldState);
        params.add(this.ue.getRequest().getSession().getId());
        params.add((null == this.ue.getContext() || this.ue.getContext().getCurrentUser() == null) ? "anonymous" : this.ue.getContext().getCurrentUser().getEmail());
        params.add(this.ue.getRequest().getRemoteAddr());
        
        DatabaseManager.updateQuery(this.ue.getContext(), sql.toString(), params.toArray());                           
    }

    private void checkCountry(String countryCode, String countryName) throws SQLException
    {
        TableRow row = DatabaseManager.findByUnique(this.ue.getContext(), "stats.country", "code", countryCode);

        // If the country does not exist in the table, insert it
        if (row == null)
        {
        	String sql = "insert into stats.country (code, name) values (?, ?)";
        	DatabaseManager.updateQuery(this.ue.getContext(), sql, countryCode, countryName);
        }
    }
    
    private Integer getItemID(Integer bitstreamId) throws SQLException
    {
        TableRow row = DatabaseManager.findByUnique(this.ue.getContext(), "stats.v_item2bitstream", "bitstream_id", bitstreamId);
        
        if (row == null)
            return null;
        
        return row.getIntColumn("item_id");
    }
    
    private Long getNumberOfBitstreams(Integer itemId) throws SQLException
    {
        if (itemId == null)
            return 0l;
        
        TableRow row = DatabaseManager.
                        querySingle(this.ue.getContext(), 
                                    "select count(*) as count " +
                                    "from stats.v_item2bitstream " +
                                    "where item_id = ?",
                                    itemId);
        
        if (row == null)
            return 0l;
        
        return row.getLongColumn("count");
    }
}
