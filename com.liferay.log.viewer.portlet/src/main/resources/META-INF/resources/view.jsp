<%@page import="com.liferay.log.viewer.portlet.ComLiferayLogViewerPortlet"%>
<%@page import="com.liferay.log.viewer.constants.PortletPropsValues"%>
<%@ include file="init.jsp" %>

<liferay-ui:success
	key="success"
	message="ui-request-processed-successfully"
/>

<liferay-ui:error
	key="error"
	message="ui-request-processed-error"
/>

<portlet:resourceURL var="resourceURL" />

<script type="text/javascript">

	window.errorThreshold = 10;
	window.consecutiveErrorCount = 0;
	window.resourcePointer = "-1";

	var preId = '<portlet:namespace/>viewlog';

	function poll() {
		var resourceMappingUrl = '<%=resourceURL%>';
		console.log('resourceMappingUrl------>'+resourceMappingUrl);
		AUI().use('aui-io-request', function(A) {
			A.io.request(resourceMappingUrl, {
				method: 'POST', data: {
					"<portlet:namespace/><%= ComLiferayLogViewerPortlet.ATTRIB_POINTER %>": window.resourcePointer
				},
				dataType: 'json',
				on: {
					success: function() {
						try {
							if (typeof this.get('responseData') != 'undefined') {
								window.resourcePointer = this.get('responseData').pointer;
								document.getElementById(preId).innerHTML = document.getElementById(preId).innerHTML + this.get('responseData').content;
								document.getElementById("viewlogmode").innerHTML = this.get('responseData').mode;
								window.consecutiveErrorCount=0;
							} else {
								window.consecutiveErrorCount++;
								if (window.consecutiveErrorCount >= window.errorThreshold) {
									clearTimeout(window.pollingIntervalId);
									alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
									document.getElementById(preId).innerHTML = document.getElementById(preId).innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
								}
							}
						} catch(err) {
							window.consecutiveErrorCount++;
							if (window.consecutiveErrorCount >= window.errorThreshold) {
								clearTimeout(window.pollingIntervalId);
								alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
								document.getElementById(preId).innerHTML = document.getElementById(preId).innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
							}
						}
					},
					failure: function() {
						window.consecutiveErrorCount++;
						if (window.consecutiveErrorCount >= window.errorThreshold) {
							clearTimeout(window.pollingIntervalId);
							alert("Polling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.");
							document.getElementById(preId).innerHTML = document.getElementById(preId).innerHTML + "\n\n\n------\nPolling of the log has been stopped as the poll error limit has been reached. Please refresh the page to restart polling.\n------";
						}
					}
				}
			});
		});
	}

	function detachlogger() {
		return sendCmd('<%= ComLiferayLogViewerPortlet.OP_DETACH %>');
	}

	function attachlogger() {
		return sendCmd('<%= ComLiferayLogViewerPortlet.OP_ATTACH %>');
	}

	function clearlogger() {
		document.getElementById(preId).innerHTML = '';
	}

	function sendCmd(mycmd) {
		var resourceMappingUrl = '<portlet:resourceURL/>';
		AUI().use('aui-io-request', function(A) {
			A.io.request(resourceMappingUrl, {
				method: 'POST', data: {
					"<portlet:namespace/><%= ComLiferayLogViewerPortlet.PARAM_OP %>": mycmd
				},
				dataType: 'json',
				on: {
					success: function() {
						var result = this.get('responseData').result;
						if (result == '<%= ComLiferayLogViewerPortlet.RESULT_ERROR %>') {
							alert(this.get('responseData').error);
						}
					}
				}
			});
		});
	}

	window.pollingIntervalId = setInterval(poll, <%= String.valueOf(PortletPropsValues.LIFERAY_LOG_VIEWER_REFRESH_INTERVAL) %>);
</script>

<div class="container">

	<pre id="<portlet:namespace/>viewlog">
	</pre>

	<div class="alert alert-info" role="alert">
		<span class="alert-indicator">
			<svg class="lexicon-icon lexicon-icon-info-circle" focusable="false" role="presentation">
				<use href="/images/icons/icons.svg#info-circle"></use>
			</svg>
		</span>

		<liferay-ui:message key="the-logger-is-currently" />
		<strong><span id="viewlogmode"><liferay-ui:message key="waiting-for-status" /></span>.</strong>
		<liferay-ui:message arguments="<%= new String[] {PortletPropsValues.LIFERAY_LOG_VIEWER_REFRESH_INTERVAL_DISPLAY_SECONDS} %>" key="polling-every-x-seconds" />
	</div>


	<div class="navbar navbar-collapse-absolute navbar-expand-md ">
	<input class="btn btn-primary btn-sm" onClick="attachlogger(); return false;" type="button" value="<liferay-ui:message key="attach-logger" />" />
	<input class="btn btn-secondary btn-sm" onClick="clearlogger(); return false;" type="button" value="<liferay-ui:message key="clear-logger" />" />
	<input class="btn btn-secondary btn-sm" onClick="detachlogger(); return false;" type="button" value="<liferay-ui:message key="detach-logger" />" />
	</div>

	<p class="small">
	<em><liferay-ui:message key="you-can-set-portal-property" /> <b>liferay.log.viewer.pattern</b> <liferay-ui:message key="pattern-description" /></em><br /><br />
	<em><liferay-ui:message key="you-can-set-portal-property" /> <b>liferay.log.viewer.refreshIntervalMs</b> <liferay-ui:message key="refresh-interval-description" /></em><br /><br />
	<em><liferay-ui:message key="you-can-set-portal-property" /> <b>liferay.log.viewer.sleepIntervalMs</b> <liferay-ui:message key="sleep-interval-description" /></em><br /><br />
	</p>

</div>

<style>
#<portlet:namespace/>viewlog {
	margin: 10px 0px;
	min-height: 100px;
	background-color: #333;
	color: #ccc;
	font-size: 11px;
	max-height: 60vh;
	overflow: auto;
}
</style>