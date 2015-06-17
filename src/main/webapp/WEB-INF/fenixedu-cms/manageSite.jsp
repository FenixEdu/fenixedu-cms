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

<c:set var="locale" value="<%= org.fenixedu.commons.i18n.I18N.getLocale() %>"/>
<script type="text/javascript">
	var db = ${views != null ? views : '[]'}
</script>

	<div class="page-header">
	  	<h1>Site</h1>
	  	<a href="${pageContext.request.contextPath}/cms/sites"><h2><small>${site.name.content}</small></h2></a>
	</div>

	<div class="row">
	  <div class="col-sm-8">

	    <button type="button" class="btn btn-primary"><i class="icon icon-plus"></i> New...</button>
	    <div class="btn-group">
	    <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenuDivider" data-toggle="dropdown" aria-expanded="true">
        	View...
        	<span class="caret"></span>
      	</button>
      	<ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenuDivider">
	        <li role="presentation"><a role="menuitem" href="${pageContext.request.contextPath}/cms/posts/${site.slug}">Posts</a></li>
	        <li role="presentation"><a role="menuitem" href="${pageContext.request.contextPath}/cms/pages/${site.slug}">Pages</a></li>
	        <li role="presentation"><a role="menuitem" href="${pageContext.request.contextPath}/cms/categories/${site.slug}">Categories</a></li>
	        <li role="presentation"><a role="menuitem" href="${pageContext.request.contextPath}/cms/menus/${site.slug}">Menus</a></li>
      	</ul>
      	</div>
	    <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit" class="btn btn-default">Settings</a>
		<c:if test="${site.published}">
			<a href="${site.fullUrl}" target="_blank" class="btn btn-default"><spring:message code="action.link"/></a>
		</c:if>
	  </div>
	  <div class="col-sm-4">
	    <input type="search" id="search-query" class="form-control pull-right" placeholder="Search posts...">
	  </div>
	</div>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.5/d3.js"></script>
	<div class="row">
		<div class="col-sm-12">
			<h3>Analytics</h3>

			<div class="graph">
			<div class="row">
				<div class="col-sm-12">
				<div class="btn-group pull-right">
					<button type="button" class="btn btn-default btn-xs">30 Days</button>
					<button type="button" class="btn btn-default btn-xs">7 Days</button>
				</div>
				</div>
			</div>
			<svg id="visualisation" width="100%" height="350">

				<defs>
				  <pattern id="pattern1"
				           x="0" y="0" width="49" height="49"
				           patternUnits="userSpaceOnUse" >

				      <rect x="0" y="0" width="50" height="50" style="fill:white;stroke-width:2;stroke:#f3f3f3;"/>

				  </pattern>
				</defs>

				<rect x="0" y="0" width="100%" height="450" style=" fill: url(#pattern1);" />
			</svg>
				<p class="help-block">
					This Analytics view is provided by <a href="http://google.com/analytics">Google Analytics</a>. To get more insights about your data, visit their site.
				</p>
			</div>

			<script type="text/javascript">
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


			</script>
		</div>
	</div>
	<style type="text/css">
	.activity-line{

		padding-bottom: 20px;

		line-height: 22px;
	}
	.glance{
		padding-top: 10px;
		padding-bottom: 10px;
	}

	.activity-day{
		border-bottom:1px solid #f3f3f3; margin-bottom:10px;
	}
	.activity-day i{
		font-size: 21px; line-height: 1px;
		padding-right: 15px;
		color:#878787;
	}
	</style>
	<div class="row">
		<div class="col-sm-7">
			<h3 class="sub-header"> Activity </h3>
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
			        <c:forEach var="activity" items="${activities}">
			           	<div class="row activity-day">
							<div class="col-sm-2">
								${activity.date}
							</div>
							<div class="col-sm-10">
								<c:forEach var="item" items="${activity.items}">
									<div class="activity-line">
										${item.getRender()}
									</div>
								</c:forEach>
							</div>
						</div>
			        </c:forEach>
			    </c:otherwise>
			</c:choose>

		</div>
		<div class="col-sm-5">
			<h3 class="sub-header">On Glance</h3>

<div class="row">
	<div class="col-sm-6 glance ">
		<i class="glance-icon glyphicon glyphicon-pushpin"></i> <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}">${site.postSet.size()} Posts</a>
	</div>
	<div class="col-sm-6 glance ">
		<i class="glance-icon glyphicon glyphicon-file"></i> <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}"">${site.pagesSet.size()} Pages</a>
	</div>
</div>
<div class="row">
	<div class="col-sm-6 glance ">
		<i class="glance-icon glyphicon glyphicon-th-list"></i> <a href="${pageContext.request.contextPath}/cms/pages/${site.slug}"">${site.menusSet.size()} Menus</a>
	</div>
	<div class="col-sm-6 glance ">
		<i class="glance-icon glyphicon glyphicon-edit"></i> <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit">Published</a>
	</div>
</div>

<div class="row">
	<div class="col-sm-6 glance ">
		<i class="glance-icon icon icon-brush"></i> <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit">${site.theme.name}</a>
	</div>
	<div class="col-sm-6 glance ">
		<i style="color:#9AC338;" class="glance-icon glyphicon glyphicon-eye-open"></i> Public
	</div>
</div>
		</div>
	</div>


<style>
.sub-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.pretty-number {
	border-radius: 50%;
	height: 100px;
	width: 100px;
	line-height: 100px;
	background-color: #147ad2;
	margin-left: auto;
	margin-right: auto;
	text-align: center;
	font-weight: bold;
	color: white;
	font-size: 20px;
}
.green {
	background-color: #33d69c;
}
.placeholders {
  margin-bottom: 30px;
  text-align: center;
}
.placeholders h4 {
  margin-bottom: 0;
}
</style>

<script type="application/javascript">
	(function () {
		$('#search-query').keypress(function (e) {
			if (e.which == 13) {
				debugger;
				var searchQuery = $('#search-query').val();
				if(searchQuery) {
					window.location.href = "${pageContext.request.contextPath}/cms/posts/${site.slug}?query=" + searchQuery;
				}
			}
		});
	})();
</script>
