<%--
  - navbar-admin.jsp
  -
  - Version: $Revision$
  -
  - Date: $Date$
  -
  - Copyright (c) 2002, Hewlett-Packard Company and Massachusetts
  - Institute of Technology.  All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are
  - met:
  -
  - - Redistributions of source code must retain the above copyright
  - notice, this list of conditions and the following disclaimer.
  -
  - - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  -
  - - Neither the name of the Hewlett-Packard Company nor the name of the
  - Massachusetts Institute of Technology nor the names of their
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  - A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  - HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  - BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  - OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  - ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  - TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  - USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  - DAMAGE.
  --%>

<%--
  - Navigation bar for admin pages
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.List" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.browse.BrowseInfo" %>
<%@ page import="org.dspace.sort.SortOption" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>


<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    // Get the current page, minus query string
    String currentPage = UIUtil.getOriginalURL(request);    
    int c = currentPage.indexOf( '?' );
    if( c > -1 )
    {
        currentPage = currentPage.substring(0, c);
    }
%>

<div class="vmenu">
 <div class="<%= (currentPage.endsWith("/tools/edit-communities") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/tools/edit-communities"><fmt:message key="jsp.layout.navbar-admin.communities-collections"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/edit-epeople") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/edit-epeople"><fmt:message key="jsp.layout.navbar-admin.epeople"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/tools/group-edit") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/tools/group-edit"><fmt:message key="jsp.layout.navbar-admin.groups"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/tools/edit-item") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/tools/edit-item"><fmt:message key="jsp.layout.navbar-admin.items"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/metadata-schema-registry") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/metadata-schema-registry"><fmt:message key="jsp.layout.navbar-admin.metadataregistry"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/format-registry") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/format-registry"><fmt:message key="jsp.layout.navbar-admin.formatregistry"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/workflow") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/workflow"><fmt:message key="jsp.layout.navbar-admin.workflow"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/tools/authorize") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/tools/authorize"><fmt:message key="jsp.layout.navbar-admin.authorization"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/news-edit") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/news-edit"><fmt:message key="jsp.layout.navbar-admin.editnews"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/license-edit") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/license-edit"><fmt:message key="jsp.layout.navbar-admin.editlicense"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/dspace-admin/supervise") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/supervise"><fmt:message key="jsp.layout.navbar-admin.supervisors"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/statistics") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/statistics"><fmt:message key="jsp.layout.navbar-admin.statistics"/></a>
 </div>
 <div class="vmenu-split"></div>
 <div class="<%= (currentPage.endsWith("/metadataimport") ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/metadataimport"><fmt:message key="jsp.layout.navbar-admin.metadataimport"/></a>
 </div>
 <div class="vmenu-split"></div>

<%
	// get the browse indices
	BrowseInfo binfo = (BrowseInfo) request.getAttribute("browse.info");
%>
 <div class="<%= (binfo != null ? "vmenu-UPa-a" : "vmenu-UPa") %>">
<a href="<%= request.getContextPath() %>/dspace-admin/withdrawn"><fmt:message key="jsp.layout.navbar-admin.withdrawn"/></a>
 </div>
 <div class="vmenu-split"></div>
</div>

<br>

<div class="vmenu">
 <div class="vmenu-UPa">
<a href="https://cas.upce.cz/cosign-bin/logout?https://dspace.upce.cz/logout"><fmt:message key="jsp.layout.navbar-admin.logout"/></a>
 </div>
 <div class="vmenu-split"></div>
</div>

