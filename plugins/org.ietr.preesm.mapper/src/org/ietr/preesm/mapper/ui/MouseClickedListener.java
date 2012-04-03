/**
 * 
 */
package org.ietr.preesm.mapper.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

/**
 * Listening for mouse events to refresh correctly the awt display when Gantt
 * chart is changed.
 * 
 * @author mpelcat
 */
public class MouseClickedListener implements MouseMotionListener,
		ChartMouseListener, MouseListener {

	private Frame frame;
	boolean dragged = false;

	public MouseClickedListener(Frame frame) {
		this.frame = frame;
	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		dragged = true;
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
	}

	@Override
	public void chartMouseClicked(ChartMouseEvent arg0) {
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent arg0) {
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		if (dragged) {
			Dimension d = frame.getSize();
			frame.setSize(new Dimension(1, 1));
			frame.setSize(d);

			dragged = false;
		}
	}

}
