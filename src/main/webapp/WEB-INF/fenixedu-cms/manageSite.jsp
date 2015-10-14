<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu CMS.

    FenixEdu CMS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu CMS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://fenixedu.com/cms/permissions" prefix="permissions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"></script>

<c:set var="locale" value="<%= org.fenixedu.commons.i18n.I18N.getLocale() %>"/>

	<div class="page-header">
	  	<h1>Site</h1>
	  	<a href="${pageContext.request.contextPath}/cms/sites"><h2><small>${site.name.content}</small></h2></a>
	</div>
	<div class="row">
		<div class="col-sm-6">
			<h3 class="sub-header">At a glance</h3>
			
			<div class="input-group">
				<div class="input-group-btn">
					<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						New...
						<span class="caret"></span>
						<span class="sr-only">Toggle Dropdown</span>
					</button>
					<ul class="dropdown-menu">
						<c:if test="${permissions:canDoThis(site, 'CREATE_POST')}">
							<li><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}#new">Post</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_PAGE')}">
							<li><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}#new">Page</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_CATEGORY')}">
							<li><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}#new">Category</a></li>
						</c:if>
						<c:if test="${permissions:canDoThis(site, 'CREATE_MENU')}">
							<li><a href="${pageContext.request.contextPath}/cms/menus/${site.slug}#new">Menu</a></li>
						</c:if>
					</ul>
				</div>
				<input type="text" class="form-control" placeholder="Search this website..." id="search-query" autofocus>
			</div>
		
			<br />
			
			<ul class="list-group">
				<c:if test="${permissions:canDoThis(site, 'EDIT_POSTS')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}">Posts<span class="badge pull-right">${site.postSet.size()}</span></a></li>
				</c:if>
				<c:if test="${permissions:canDoThis(site, 'SEE_PAGES,EDIT_PAGE')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}">Pages<span class="badge pull-right">${site.pagesSet.size()}</span></a></li>
				</c:if>
				<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/media/${site.slug}">Media<span class="badge pull-right">${site.filesSet.size()}</span></a></li>
				<c:if test="${permissions:canDoThis(site, 'LIST_CATEGORIES')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}">Categories<span class="badge pull-right">${site.categoriesSet.size()}</span></a></li>
				</c:if>
				<c:if test="${permissions:canDoThis(site, 'LIST_MENUS')}">
					<li class="list-group-item"><a href="${pageContext.request.contextPath}/cms/menus/${site.slug}">Menus<span class="badge pull-right">${site.menusSet.size()}</span></a></li>
				</c:if>
			</ul>

			<h3 class="sub-header">Properties</h3>
            <dl class="dl-entity-horizontal">
                <dt>Theme</dt>
            	<c:choose>
            		<c:when test="${site.theme != null}"><dd>${site.theme.name}</dd></c:when>
            		<c:otherwise><dd><span class="label label-warning">None</span></dd></c:otherwise>
            	</c:choose>

                <dt>Visibility</dt>
				<dd>${site.canViewGroup}</dd>

                <dt>Published</dt>
                <dd>
                    <div class="switch switch-success">
                        <input type="checkbox" ${site.published ? 'checked' : ''} id="success" class="disabled">
                        <label for="success">Published</label>
                    </div>
                </dd>

                <dt>Homepage</dt>
            	<c:choose>
            		<c:when test="${site.initialPage != null}"><dd><a href="${site.initialPage.editUrl}">${site.initialPage.name.content}</a></dd></c:when>
            		<c:otherwise><dd><span class="label label-warning">None</span></dd></c:otherwise>
            	</c:choose>

                <dt>Author</dt>
                <dd>${site.createdBy.displayName}</dd>

                <dt>Creation Date</dt>
                <dd><fmt:formatDate value="${site.creationDate.toDate()}" dateStyle="FULL" /></dd>
            </dl>

		</div>
		<div class="col-sm-6">
    		<div class="graph" style="display: none;">
				<h3>Analytics</h3>

				<svg id="visualisation" width="100%" height="255">
					
					<defs>
					  <pattern id="pattern1" x="0" y="0" width="49" height="49" patternUnits="userSpaceOnUse" >
					      <rect x="0" y="0" width="50" height="50" style="fill:white;stroke-width:2;stroke:#f3f3f3;"/>
					  </pattern>
					</defs>

					<rect x="0" y="0" width="100%" height="450" style=" fill: url(#pattern1);" />    
				</svg>
			</div>
			<h3 class="sub-header">Activity</h3>
			<c:set var="activities" value="${site.lastFiveDaysOfActivity}"/>
		
			<c:choose>
			    <c:when test="${activities.size() == 0}">
				    <div class="panel panel-default">
			          <div class="panel-body">
			            <spring:message code="site.manage.label.emptySites"/>
			          </div>
			        </div>
			    </c:when>

			    <c:otherwise>
				    <ul class="events">
				    	<c:forEach var="activity" items="${activities}">
				    		<c:forEach var="item" items="${activity.items}">
								<li>${item.getRender()}<time class="pull-right">${activity.date}</time></li>
							</c:forEach>
						</c:forEach>
					</ul>
				</c:otherwise>
		    </c:choose>
		</div>
	</div>

</div>


<style>
.sub-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
ul.events {
	display: table;
	list-style: none;
	margin-left: 0;
	position: relative;
	width: 100%;
}
ul.events:before {
	border-left: 1px solid #ddd;
	bottom: 5px;
	content: "";
	left: 10px;
	position: absolute;
	top: 12px;
}
ul.events li {
	clear: both;
	color: #888;
	position: relative;
}
ul.events li:before {
	background-color: #ddd;
	border: 1px solid #ddd;
	border-radius: 50%;
	box-shadow: inset 0 0 0 2pt #fff;
	content: "";
	display: block;
	height: 13px;
	left: -36px;
	margin-top: 5px;
	position: absolute;
	width: 13px;
}
ul.events li .avatar {
	display: table-cell;
	float: left;
	margin-top: 0;
	width: 30px;
}
ul.events li .avatar+p {
	display: table-cell;
	float: left;
	width: 70%;
}
ul.events li .avatar+p+time {
	display: table-cell;
	float: right;
	text-align: right;
	width: 22%;
}
ul.events li.expanded {
	margin-top: 15px;
}
ul.events li.expanded:before {
	background-color: #bbb;
	border: 1px solid #bbb;
	box-shadow: inset 0 0 0 2pt #fff;
	height: 30px;
	margin-left: -8px;
	margin-top: -2px;
	width: 30px;
}
ul.events li a {
	color: #444;
}
ul.events li time
{
	font-style: italic;
}
.avatar{ 
	margin-top:4px
}
.avatar img {
	width:22px;
	height:auto;
	border-radius:2px;
	margin-right:5px
}
</style>

<script type="application/javascript">

	function loadAnalyticsGraph(db) {
		var listDb = []
		var i = 0;
		for (var x in db) {
			db[x].i = i++; 
			listDb.push(db[x]);
		};
		function genGraph(){
			$("path", $("#visualisation")).remove();
			$("circle", $("#visualisation")).remove();
		var vis = d3.select('#visualisation'),
		    WIDTH = $("#visualisation").width(),
		    HEIGHT = $("#visualisation").height(),
		    MARGINS = {
		      top: 20,
		      right: 20,
		      bottom: 20,
		      left: 20
		    },
		    xRange = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(listDb, function(d) {
		      return parseInt(d.i);
		    }), d3.max(listDb, function(d) {
		      return parseInt(d.i);
		    })]),
		    yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([d3.min(listDb, function(d) {
		      return parseInt(d.pageviews);
		    }), d3.max(listDb, function(d) {
		      return parseInt(d.pageviews);
		    })]);


		var lineFunc = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return yRange(parseInt(d.pageviews));
		})
		.interpolate('cardinal');

		var lineFuncV = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return yRange(parseInt(d.visitors));
		})
		.interpolate('cardinal');

		var lineInitFunc = d3.svg.line()
		.x(function(d) {
			return xRange(parseInt(d.i));
		})
		.y(function(d) {
			return $("#visualisation").height();
		})
		.interpolate('cardinal');

		vis.append('svg:path')
		  .attr('d', lineInitFunc(listDb))
		  .attr('stroke', '#3399FF')
		  .attr('stroke-width', 2)
		  .attr('fill', 'none').transition().attr('d', lineFunc(listDb)).duration(1000).each("end",function(){
	  			vis.selectAll("foo").data(listDb).enter().append("svg:circle")
						.attr("stroke", "#3399FF")
	         		.attr("fill", function(d, i) { return "#3399FF" })
	         		.attr("cx", function(d, i) { return xRange(parseInt(d.i)); })
	         		.attr("cy", function(d, i) { return yRange(parseInt(d.pageviews)); })
	         		.attr("r", function(d, i) { return 3 });
		  });


		vis.append('svg:path')
		  .attr('d', lineInitFunc(listDb))
		  .attr('stroke', '#9AC338')
		  .attr('stroke-width', 2)
		  .attr('fill', 'none').transition().attr('d', lineFuncV(listDb)).duration(1000).each("end",function(){
	  			vis.selectAll("bar").data(listDb).enter().append("svg:circle")
						.attr("stroke", "#9AC338")
	         		.attr("fill", function(d, i) { return "#9AC338" })
	         		.attr("cx", function(d, i) { return xRange(parseInt(d.i)); })
	         		.attr("cy", function(d, i) { return yRange(parseInt(d.visitors)); })
	         		.attr("r", function(d, i) { return 3 });
		  });
		
		};
		genGraph();
		$( window ).resize(function() {
		  genGraph();
		});
	}

	$(document).ready(function() {
		$.get('${pageContext.request.contextPath}/cms/sites/${site.slug}/analytics').done(function(analyticsData) {
			if(analyticsData && !$.isEmptyObject(analyticsData) &&  !$.isEmptyObject(analyticsData.google)) {
				$('.graph').fadeIn();
				loadAnalyticsGraph(analyticsData.google)
			}
		}).error(function(err){
			$('.graph').hide();
		});

		$('#search-query').keypress(function (e) {
			if (e.which == 13) {
				var searchQuery = $('#search-query').val();
				if(searchQuery) {
					window.location.href = "${pageContext.request.contextPath}/cms/posts/${site.slug}?query=" + searchQuery;
				}
			}
		});
	});
</script>