package com.liferay.log.viewer.constants;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import java.text.DecimalFormat;

/**
 * PortletPropsValues
 *
 * @author prateek.sharma
 */
public class PortletPropsValues {

	public static final String LIFERAY_LOG_VIEWER_PATTERN =
		GetterUtil.getString(
			PropsUtil.get(PortletPropsKeys.LIFERAY_LOG_VIEWER_PATTERN),
			ComLiferayLogViewerPortletKeys.DEFAULT_LOG_PATTERN);

	public static final long LIFERAY_LOG_VIEWER_REFRESH_INTERVAL =
		GetterUtil.getLong(
			PropsUtil.get(PortletPropsKeys.LIFERAY_LOG_VIEWER_REFRESH_INTERVAL),
			ComLiferayLogViewerPortletKeys.DEFAULT_REFRESH_INTERVAL);

	public static final String LIFERAY_LOG_VIEWER_REFRESH_INTERVAL_DISPLAY_SECONDS =
		(new DecimalFormat("##0.0#"))
		.format(LIFERAY_LOG_VIEWER_REFRESH_INTERVAL / 1000d);

	public static final long LIFERAY_LOG_VIEWER_SLEEP_INTERVAL =
		GetterUtil.getLong(
			PropsUtil.get(PortletPropsKeys.LIFERAY_LOG_VIEWER_SLEEP_INTERVAL),
			ComLiferayLogViewerPortletKeys.DEFAULT_SLEEP_INTERVAL);

}