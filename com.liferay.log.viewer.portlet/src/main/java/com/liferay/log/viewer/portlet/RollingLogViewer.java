package com.liferay.log.viewer.portlet;

/**
 * RollingLogViewer
 *
 * @author prateek.sharma
 */
public class RollingLogViewer {

	/**
	 * size of the character buffer array used to read and store logs
	 */
	public static final int CHAR_SIZE = 5 * 1024 * 1024;

	public RollingLogViewer() {
		intbuf = new char[CHAR_SIZE];
		pnt = 0;
	}

	public char[] getBuffer(final int oldpointerparam, final int newpointer) {
		int oldpointer = oldpointerparam;

		if (!bufwrapped && newpointer < oldpointer) {
			oldpointer = -1;
		}

		if (oldpointer == -1) {
			if (bufwrapped) {
				oldpointer =
					((newpointer - BACK_FILL_SIZE) + intbuf.length) % intbuf.length;
			} else {
				oldpointer = newpointer - BACK_FILL_SIZE;

				if (oldpointer < 0) {
					oldpointer = 0;
				}
			}
		}

		if (newpointer == oldpointer) {
			return new char[0];
		}

		if (newpointer > oldpointer) {
			final char[] toReturn = new char[newpointer - oldpointer];
			System.arraycopy(
				intbuf, oldpointer, toReturn, 0, newpointer - oldpointer);

			return toReturn;
		} else {

			// loop around

			final char[] toReturn =
				new char[intbuf.length - oldpointer + newpointer];
			final int offlength = intbuf.length - oldpointer;
			System.arraycopy(intbuf, oldpointer, toReturn, 0, offlength);
			System.arraycopy(intbuf, 0, toReturn, offlength, newpointer);

			return toReturn;
		}
	}

	public int getCurrentPointer() {
		return pnt;
	}

	public synchronized void write(
		final char[] buf, final int origoffset, final int origlength) {

		int offset = origoffset;
		int length = origlength;

		if (length > intbuf.length) {

			// there is going to be log truncation because the log volume in larger than the buffer

			offset = offset + (length - intbuf.length);
			length = intbuf.length;
		}

		if (pnt + length > intbuf.length) {
			final int offlength = intbuf.length - pnt;
			System.arraycopy(buf, offset, intbuf, pnt, offlength);
			System.arraycopy(
				buf, offset + offlength, intbuf, 0, length - offlength);
		} else {
			System.arraycopy(buf, offset, intbuf, pnt, length);
		}

		if (pnt + length >= intbuf.length) {
			bufwrapped = true;
		}

		pnt = (pnt + length) % intbuf.length;
	}

	/**
	 * number of characters of log to return at start
	 */
	private static final int BACK_FILL_SIZE = 1000;

	private boolean bufwrapped = false;
	private final char[] intbuf;
	private int pnt;

}