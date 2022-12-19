package com.liferay.log.viewer.portlet;

import com.liferay.log.viewer.constants.PortletPropsValues;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.CharArrayWriter;
import java.io.Writer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * LogHolder
 *
 * @author prateek.sharma
 */
public class LogHolder {

	private static final Log log = LogFactoryUtil.getLog(LogHolder.class);

	public static synchronized void attach() throws Exception {
		if (!isAttached()) {
			try {
				final CharArrayWriter pwriter = new CharArrayWriter();
				viewer = new RollingLogViewer();
				runnable = new LogRunnable(pwriter, viewer);
				final Thread t = new Thread(runnable);
				t.start();

				addAppender(pwriter, "testWriter");

				attached = true;
			} catch (Exception e) {
				log.warn("Exception in attaching Logger :: ",e);
			}
		}else{
			log.warn("Logger is already attached");
		}
	}

	private static void addAppender(final Writer writer, final String writerName) {
		final LoggerContext context = LoggerContext.getContext(false);
		final Configuration config = context.getConfiguration();
		final String pattern = PortletPropsValues.LIFERAY_LOG_VIEWER_PATTERN;
		final PatternLayout pl = PatternLayout.newBuilder().withPattern(pattern).build();
		writerAppenderObj = WriterAppender.createAppender(pl, null, writer, writerName, false, true);
		writerAppenderObj.start();
		config.addAppender(writerAppenderObj);
		updateLoggers(writerAppenderObj, config);
	}

	private static void updateLoggers(final Appender appender, final Configuration config) {
		final Level level = null;
		final Filter filter = null;
		for (final LoggerConfig loggerConfig : config.getLoggers().values()) {
			loggerConfig.addAppender(appender, level, filter);
		}
		config.getRootLogger().addAppender(appender, level, filter);
	}

	public static synchronized void detach() {
		if (isAttached()) {
			try {
				runnable.setStop(true);

				final LoggerContext context = LoggerContext.getContext(false);
				final Configuration config = context.getConfiguration();
				config.getRootLogger().removeAppender(writerAppenderObj.getName());
				context.updateLoggers();
			} catch (final Exception e) {
				log.warn("Error in Detaching Logger :: ",e);
			}
		}else {
			log.warn("Logger is already detached::");
		}

		runnable = null;
		viewer = null;
		writerAppenderObj = null;
		attached = false;
	}

	public static RollingLogViewer getViewer() {
		return viewer;
	}

	public static boolean isAttached() {
		return attached;
	}

	public static class LogRunnable implements Runnable {

		public LogRunnable(CharArrayWriter writer, RollingLogViewer viewer) {
			this.writer = writer;
			this.viewer = viewer;
		}

		public void run() {
			try {
				while (true) {
					final char[] buf = writer.toCharArray();
					writer.reset();
					if (buf.length > 0) {
						viewer.write(buf, 0, buf.length);
					}

					if (stop) {
						break;
					}

					try {
						Thread.sleep(PortletPropsValues.LIFERAY_LOG_VIEWER_SLEEP_INTERVAL);
					} catch (final InterruptedException ie) {
					}
				}

			} catch (final Exception e) {
				log.warn(e);
			}
		}

		public void setStop(final boolean stop) {
			this.stop = stop;
		}

		private boolean stop = false;
		private final RollingLogViewer viewer;
		private final CharArrayWriter writer;

	}

	private static boolean attached = false;
	private static LogRunnable runnable = null;
	private static RollingLogViewer viewer = null;
	private static Appender writerAppenderObj = null;

}