<%--
  - styles.css.jsp
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
  - Main DSpace Web UI stylesheet
  -
  - This is a JSP so it can be tailored for different browser types
  --%>
<%@ page import="org.dspace.app.webui.util.JSPManager" %>

<%
    // Make sure the browser knows we're a stylesheet
    response.setContentType("text/css");

    String imageUrl   = request.getContextPath() + "/image/";

    // Netscape 4.x?
    boolean usingNetscape4 = false;
    String userAgent = request.getHeader( "User-Agent" );
    if( userAgent != null && userAgent.startsWith( "Mozilla/4" ) )
    {
        usingNetscape4 = true;
    }
%>

A { color: #de281d }

BODY { font-family: Arial, Helvetica, sans-serif;
       font-size: 10pt;
       font-style: normal;
       color: #404040;
       background: #6a6a6a;
       margin: 0;
       padding: 0;
       margin-left:0px;
       margin-right:0px; 
       margin-top:0px; 
       margin-bottom:0px }

<%-- Note: Font information must be repeated for broken Netscape 4.xx --%>
H1 { margin-left: 10px;
     margin-right: 10px;
     font-size: 12pt;
     font-weight: bold;
     font-style: normal;
     font-family: "Arial", "Helvetica", sans-serif;
     color: #dd281d }

H2 { margin-left: 10px;
     margin-right: 10px;
     font-size: 12pt;
     font-style: normal;
     font-family: "Arial", "Helvetica", sans-serif;
     color: #dd281d }

H3 { margin-left: 10px;
     margin-right: 10px;
     font-size: 12pt;
     font-weight: bold;
     font-family: "Arial", "Helvetica", sans-serif;
     color: #404040 }

object { display: inline; }

p {  margin-left: 10px;
     margin-right: 10px;
     font-family: "Arial", "Helvetica", sans-serif;
     font-size: 10pt }
     
<%-- This class is here so that a "DIV" by default acts as a "P".    --%>
<%-- This is necessary since the "dspace:popup" tag must have a "DIV" --%>
<%-- (or block element) surrounding it in order to be valid XHTML 1.0 --%>
DIV { margin-left: 10px;
      margin-right: 10px;
      margin-bottom: 15px; 
      font-family: "Arial", "Helvetica", sans-serif;
      font-size: 10pt;}      

UL { font-family: "Arial", "Helvetica", sans-serif;
     font-size: 10pt }

<%-- This class is here so the standard style from "P" above can be applied --%>
<%-- to anything else. --%>
.standard { margin-left: 10px;
            margin-right: 10px;
            font-family: "Arial", "Helvetica", sans-serif;
            font-size: 10pt }                       

.langChangeOff { text-decoration: none;
                 color : #bbbbbb;
                 cursor : default;
                 font-size: 10pt }

.langChangeOn { text-decoration: underline;
                color: #336699;
                cursor: pointer;
                font-size: 10pt }

.pageBanner { width: 100%;
              border: 0;
              margin: 0;
              background: #ffffff;
              color: #404040;
              padding: 0;
              vertical-align: middle }

.tagLine { vertical-align: bottom;
           padding: 10px;
           border: 0;
           margin: 0;
           background: #ffffff;
           color: #ff6600 }

.tagLineText { background: #ffffff;
               color: #ff6600;
               font-size: 10pt;
               font-weight: bold;
               border: 0;
               margin: 0 }

.stripe { background: #336699 url(<%= imageUrl %>stripe.gif) repeat-x;
          vertical-align: top;
          border: 0;
          padding: 0;
          margin: 0;
          color: #ffffff }

.locationBar { font-size: 10pt;
               font-family: "Arial", "Helvetica", sans-serif;
               text-align: left }

.centralPane { margin: 0px;
               vertical-align: top;
               padding: 0px;
               border: 0 }

<%-- HACK: Width shouldn't really be 100%:  Really, this is asking that the --%>
<%--       table cell be the full width of the table.  The side effect of --%>
<%--       this should theoretically be that other cells in the row be made --%>
<%--       a width of 0%, but in practice browsers will only take this 100% --%>
<%--       as a hint, and just make it as wide as it can without impinging --%>
<%--       the other cells.  This, fortunately, is precisely what we want. --%>
.pageContents { FONT-FAMILY: Arial, Helvetica, sans-serif;
                background: #f6f6f6;
                color: #404040;
                vertical-align: top;
                width: 100% }

.navigationBarTable{ width: 100%;
                     padding: 2px;
                     margin: 2px;
                     border: 0 }

.navigationBar { font-family: "Arial", "Helvetica", sans-serif;
                 font-size: 10pt;
                 font-style: normal;
                 font-weight: bold;
                 color: #252645;
                 text-decoration: none;
                 background: #6a6a6a }

.navigationBarSublabel{  font-family: "Arial", "Helvetica", sans-serif;
                         font-size: 12pt;
                         font-style: normal;
                         font-weight: bold;
                         color: #404040;
                         text-decoration: none;
                         background: white;
                         white-space: nowrap }

<%-- HACK: Shouldn't have to repeat font information and colour here, it --%>
<%--       should be inherited from the parent, but isn't in Netscape 4.x, --%>
<%--       IE or Opera.  (Only Mozilla functions correctly.) --%>


.menuitem  {
border: 1px solid #404040;
background: transparent url('image/menu1.jpg') no-repeat scroll top left;
width: 160px;
height: 35px;
padding: 0;
padding-left: 20px;
margin: 0;
}

.loggedIn { font-family: "Arial", "Helvetica", sans-serif;
            font-size: 8pt;
            font-style: normal;
            font-weight: normal;
            color: white;
             }

.pageFooterBar { width: 100%;
                 border: 0;
                 margin: 0;
                 padding: 0;
                 background: #f6f6f6;
                 color: #404040;
                 vertical-align: middle }

.pageFootnote { font-family: "Arial", "Helvetica", sans-serif;
                font-size: 10px;
                font-style: normal;
                font-weight: normal;
                background: #f6f6f6;
                color: #252645;
                text-decoration: none;
                text-align: left;
                vertical-align: middle;
                margin-left: 10px;
                margin-right: 10px }

.sidebar { background: #f6f6f6;
           color: #404040 }

.communityLink { font-family: "Arial", "Helvetica", sans-serif;
}
.communityStrength {
				font-family: "Arial", "Helvetica", sans-serif;
                 font-size: 12pt;
                 font-weight: normal }

.communityDescription { margin-left: 20px;
                        margin-right: 10px;
                        font-family: "Arial", "Helvetica", sans-serif;
                        font-size: 10pt;
                        font-weight: normal;
                        list-style-type: none }

.collectionListItem { font-family: "Arial", "Helvetica", sans-serif;
 }

.collectionListItem a {
color: #404040;
}
.collectionDescription { margin-left: 20px;
                     margin-right: 10px;
                     font-family: "Arial", "Helvetica", sans-serif;
                     font-size: 10pt;
                         font-weight: normal;
                     list-style-type: none }

.miscListItem { margin-left: 20px;
                margin-right: 10px;
                font-family: "Arial", "Helvetica", sans-serif;
                font-size: 12pt;
                list-style-type: none }

.copyrightText { margin-left: 20px;
                 margin-right: 20px;
                 text-align: center;
                 font-style: italic;
                 font-family: "Arial", "Helvetica", sans-serif;
                 font-size: 10pt;
                 list-style-type: none }

.browseBarLabel { font-family: "Arial", "Helvetica", sans-serif;
                  font-size: 10pt;
                  font-style: normal;
                  font-weight: bold;
                  color: #404040;
                  background: #f6f6f6;
                  vertical-align: middle;
                  text-decoration: none }

.browseBar { font-family: "Arial", "Helvetica", sans-serif;
             font-size: 12pt;
             font-style: normal;
             font-weight: bold;
             background: #f6f6f6;
             color: #252645;
             vertical-align: middle;
             text-decoration: none }

.itemListCellOdd { font-family: "Arial", "Helvetica", sans-serif;
                   font-size: 12pt;
                   font-style: normal;
                   font-weight: normal;
                   color: #404040;
                   vertical-align: middle;
                   text-decoration: none;
                   background: #ffffff }


.itemListCellEven { font-family: "Arial", "Helvetica", sans-serif;
                    font-size: 12pt;
                    font-style: normal;
                    font-weight: normal;
                    color: #404040;
                    vertical-align: middle;
                    text-decoration: none;
                    background: #eeeeee }

.itemListCellHilight { font-family: "Arial", "Helvetica", sans-serif;
                       font-size: 12pt;
                       font-style: normal;
                       font-weight: normal;
                       color: #404040;
                       vertical-align: middle;
                       text-decoration: none;
                       background: #ddddff }

.topNavLink { margin-left: 10px;
          margin-right: 10px;
          font-family: "Arial", "Helvetica", sans-serif;
          font-size: 10pt;
          text-align: center }

.submitFormLabel { margin-left: 10px;
           margin-right: 10px;
           font-family: "Arial", "Helvetica", sans-serif;
                   font-weight: bold;
           font-size: 10pt;
           text-align: right;
           vertical-align: top }

.submitFormHelp {  margin-left: 10px;
           margin-right: 10px;
           font-family: "Arial", "Helvetica", sans-serif;
           font-size: 8pt;
           text-align: center }
           

.submitFormWarn {  margin-left: 10px;
           margin-right: 10px;
           font-family: "Arial", "Helvetica", sans-serif;
           font-weight: bold;
           font-size: 12pt;
           color: #ff6600;
           text-align: center }

.uploadHelp { margin-left: 20px;
              margin-right: 20px;
              font-family: "Arial", "Helvetica", sans-serif;
              font-size: 10pt;
              text-align: left }

.submitFormDateLabel {  margin-left: 10px;
                        margin-right: 10px;
                        font-family: "Arial", "Helvetica", sans-serif;
                        font-size: 10pt;
                        font-style: italic;
                        text-align: center;
                        vertical-align: top; }

.submitProgressTable{ margin: 0;
                      padding: 0;
                      border: 0;
                      vertical-align: top;
                      text-align: center;
                      white-space: nowrap }

.submitProgressButton{ border: 0 }

.submitProgressButtonDone{ border: 0;
                           background-image: url(<%= imageUrl %>/submit/done.gif);
                           background-position: center;
                           height: 30px;
                           width: 90px;
                           font-size: 12pt;
                           color: #404040;
                           background-repeat: no-repeat; }

.submitProgressButtonCurrent{ border: 0;
                           background-image: url(<%= imageUrl %>/submit/current.gif);
                           background-position: center;
                           height: 30px;
                           width: 90px;
                           font-size: 12pt;
                           color: white;
                           background-repeat: no-repeat; }

.submitProgressButtonNotDone{ border: 0;
                           background-image: url(<%= imageUrl %>/submit/notdone.gif);
                           background-position: center;
                           height: 30px;
                           width: 90px;
                           font-size: 12pt;
                           color: #404040;
                           background-repeat: no-repeat; }

.miscTable { font-family: "Arial", "Helvetica", sans-serif;
             font-size: 12pt;
             font-style: normal;
             font-weight: normal;
             color: #404040;
             vertical-align: middle;
             text-decoration: none;
             background: #cccccc }

.miscTableNoColor { font-family: "Arial", "Helvetica", sans-serif;
             font-size: 12pt;
             font-style: normal;
             font-weight: normal;
             color: #404040;
             vertical-align: middle;
             text-decoration: none;
             background: #f6f6f6 }

<%-- The padding element breaks Netscape 4 - it puts a big gap at the top
  -- of the browse tables if it's present.  So, we decide here which
  -- padding elements to use. --%>
<%
    String padding = "padding: 3px";

    if( usingNetscape4 )
    {
        padding = "padding-left: 3px;  padding-right: 3px; padding-top: 1px";
    }
%>

.oddRowOddCol strong {
font-weight: normal;
}

.oddRowOddCol{ font-family: "Arial", "Helvetica", sans-serif;
               font-size: 10pt;
               font-style: normal;
               font-weight: normal;
               color: #404040;
               vertical-align: middle;
               text-decoration: none;
               background: #ffffff;
               <%= padding %> }

.evenRowOddCol strong {
font-weight: normal;
}

.evenRowOddCol{ font-family: "Arial", "Helvetica", sans-serif;
                font-size: 10pt;
                font-style: normal;
                font-weight: normal;
                color: #404040;
                vertical-align: middle;
                text-decoration: none;
                background: #eeeeee;
                <%= padding %>  }

.oddRowEvenCol strong {
font-weight: normal;
}

.oddRowEvenCol{ font-family: "Arial", "Helvetica", sans-serif;
                font-size: 10pt;
                font-style: normal;
                font-weight: normal;
                color: #404040;
                vertical-align: middle;
                text-decoration: none;
                background: #f6f6f6;
                <%= padding %>  }

.evenRowEvenCol strong {
font-weight: normal;
}

.evenRowEvenCol{ font-family: "Arial", "Helvetica", sans-serif;
                 font-size: 10pt;
                 font-style: normal;
                 font-weight: normal;
                 color: #404040;
                 vertical-align: middle;
                 text-decoration: none;
                 background: #dddddd;
                 <%= padding %>  }

#t1 {
font-weight: bold;
}

#t1 strong {
font-weight: bold;
}

#t2 strong {
font-weight: bold;
}

#t2 {
font-weight: bold;
}

#t3 {
font-weight: bold;
}

#t3 strong {
font-weight: bold;
}

#t4 {
font-weight: bold;
}

#t4 strong {
font-weight: bold;
}

.highlightRowOddCol{ font-family: "Arial", "Helvetica", sans-serif;
                     font-size: 12pt;
                     font-style: normal;
                     font-weight: normal;
                     color: #404040;
                     vertical-align: middle;
                     text-decoration: none;
                     background: #ccccee;
                     <%= padding %> }

.highlightRowEvenCol{ font-family: "Arial", "Helvetica", sans-serif;
                      font-size: 12pt;
                      font-style: normal;
                      font-weight: normal;
                      color: #404040;
                      vertical-align: middle;
                      text-decoration: none;
                      background: #bbbbcc;
                      <%= padding %> }

.itemDisplayTable{ text-align: center;
                   border: 0;
                   color: #404040 }

.metadataFieldLabel{ font-family: "Arial", "Helvetica", sans-serif;
                     font-size: 10pt;
                     font-style: normal;
                     font-weight: bold;
                     color: #404040;
                     vertical-align: top;
                     text-align: right;
                     text-decoration: none;
                     white-space: nowrap;
                     <%= padding %> }

.metadataFieldValue{ font-family: "Arial", "Helvetica", sans-serif;
                     font-size: 10pt;
                     font-style: normal;
                     font-weight: normal;
                     color: #404040;
                     vertical-align: top;
                     text-align: left;
                     text-decoration: none;
                     <%= padding %> }  <%-- width 100% ?? --%>

.recentItem { margin-left: 10px;
              margin-right: 10px;
              font-family: "Arial", "Helvetica", sans-serif;
              font-size: 10pt }

.searchBox { font-family: "Arial", "Helvetica", sans-serif;
             font-size: 10pt;
             font-style: normal;
             font-weight: bold;
             color: #404040;
             vertical-align: middle;
             text-decoration: none;
             padding: 0;
             border: 0;
             margin: 0 }

.searchBoxLabel { font-family: "Arial", "Helvetica", sans-serif;
                  font-size: 10pt;
                  font-style: normal;
                  font-weight: bold;
                  color: #404040;
                  text-decoration: none;
                  vertical-align: middle }

.searchBoxLabelSmall { font-family: "Arial", "Helvetica", sans-serif;
                  font-size: 8pt;
                  font-style: normal;
                  font-weight: bold;
                  color: #404040;
                  text-decoration: none;
                  vertical-align: middle }

.attentionTable 
{
    font-style: normal;
    font-weight: normal;
    color: #404040;
    vertical-align: middle;
    text-decoration: none;
    background: #cc9966;
}

.attentionCell 
{
    background: #ffffcc;
    text-align: center;
}

.help {font-family: "Arial", "Helvetica", sans-serif;
        background: #ffffff;
        margin-left:10px;}

.help h2{text-align:center;
                font-size:18pt;
                color:#404040;}

.help h3{font-weight:bold;
         margin-left:0px;}

.help h4{font-weight:bold;
         font-size: 10pt;
         margin-left:5px;}

.help h5{font-weight:bold;
         margin-left:10px;
         line-height:.5;}

.help p {font-size:10pt;}

.help table{margin-left:8px;
            width:90%;}

.help table.formats{font-size:10pt;}

.help ul {font-size:10pt;}

.help p.bottomLinks {font-size:10pt;
                    font-weight:bold;}

.help td.leftAlign{font-size:10pt;}
.help td.rightAlign{text-align:right;
                    font-size:10pt;}
                    

<%-- The following rules are used by the controlled-vocabulary add-on --%>

ul.controlledvocabulary  {
		list-style-type:none; }

	
.controlledvocabulary ul  li ul {
	     list-style-type:none;
		display:none; }

input.controlledvocabulary  {
		border:0px; }

img.controlledvocabulary {
		margin-right:8px ! important;
		margin-left:11px ! important;
		cursor:hand; }                    

.submitFormHelpControlledVocabularies {  
		   margin-left: 10px;
           margin-right: 10px;
           font-family: "Arial", "Helvetica", sans-serif;
           font-size: 8pt;
           text-align: left; }           

.controlledVocabularyLink {  
           font-family: "Arial", "Helvetica", sans-serif;
           font-size: 8pt; }   
           
.browse_buttons
{
	float: right;
	padding: 1px;
	margin: 1px;
}

#browse_navigation
{
	margin-bottom: 10px;
}

#browse_controls
{
	margin-bottom: 10px;
}

.browse_range
{
	margin-top: 5px;
	margin-bottom: 5px;
}


.vmenu {
  width: 209px;
  margin: 0;
  border-top-width: 1px;
  border-right-width: 1px;
  border-top-style: solid;
  border-right-style: solid;
  border-top-color: #404040;
  border-right-color: #404040;
  background-image: url(image/vmenu-bg.gif);
  background-color:#f6f6f6;
}
.vmenu div {
  font-weight: normal;
  margin: 0px;
}
.vmenu a {
           font-family: "verdana", "Helvetica", sans-serif;
  display: block;
  border-left: 12px solid #7d7d7d;
  height: 10%; /* Jen a pouze pro IE6 */
  padding: 9px 5px 9px 14px;
  text-decoration:none !important;
}
.vmenu span {
  font-weight: bold;
  display: block;
  border-left: 12px solid #7d7d7d;
  height: 10%; /* Jen a pouze pro IE6 */
  padding: 9px 5px 9px 14px;
  text-decoration:none !important;
}
.vmenu a:visited,
.vmenu a:link {
  color: #404040;
}
.vmenu a:hover {
  font-weight: bold;
  color: #FFF;
  background-color: #a6a6a6;
}
.vmenu-split {
  border-bottom:1px solid #404040 !important;
  border-left: none !important;
}

.vmenu-txt {
  border-left-color: grey !important;
}

.search-txt {
   color: white;
}

.vmenu-UPa a {
  border-left-color: #dd291e !important;
}

.vmenu-UPa-a {
  background-color: #a6a6a6;
}

.vmenu-UPa-a a {
 font-weight: bold;
 color: #FFF;
}

