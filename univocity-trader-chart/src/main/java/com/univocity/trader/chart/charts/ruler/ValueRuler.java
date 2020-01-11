package com.univocity.trader.chart.charts.ruler;


import com.univocity.trader.candles.*;
import com.univocity.trader.chart.charts.*;

import java.awt.*;
import java.text.*;

import static com.univocity.trader.chart.charts.ruler.DrawingProfile.Profile.*;


public class ValueRuler extends Ruler<ValueRulerController> {

	private Point gradientStart = new Point(0, 0);
	private Point gradientEnd = new Point(0, 0);
	private static final Color glassGray = new Color(222, 222, 222, 180);
	private static final Color glassWhite = new Color(255, 255, 255, 128);

	public ValueRuler(BasicChart<?> chart) {
		super(chart);
	}

	private int getRightValueTagSpacing() {
		return getController().getRightValueTagSpacing();
	}

	private void drawGlass(Graphics2D g) {
		int width = insets.right + getRightValueTagSpacing();
		int start = chart.getBoundaryRight() - width;
		int end = insets.right + width;

		gradientStart.x = start - 200;
		gradientStart.y = 0;
		gradientEnd.x = end + 200;
		gradientEnd.y = chart.getHeight();


		g.setPaint(new GradientPaint(gradientStart, glassGray, gradientEnd, glassWhite));
		g.fillRect(start, 0, end, chart.getHeight());
	}

	protected void drawBackground(Graphics2D g, int width) {
		setProfile(DEFAULT);

		drawGlass(g);

		final double yIncrement = getFontHeight();

		int y = chart.getHeight() - chart.getYCoordinate(chart.getMaximum());

		int insetRight = 0;
		while (y > 0) {
			String tag = getValueFormat().format(chart.getValueAtY(y));
			int tagWidth = getController().getMaxStringWidth(tag, g);

			insetRight = Math.max(insetRight, tagWidth);

			int yy = chart.getHeight() - y;
			text(g);
			g.drawString(tag, chart.getBoundaryRight() - tagWidth - getRightValueTagSpacing(), yy + (getFontHeight() / 2));
			y -= yIncrement;
		}

		setProfile(DEFAULT);

		insets.right = insetRight;
	}

	private int getMinimumWidth() {
		return getController().getMinimumWidth();
	}

	public int getRulerWidth() {
		return getMinimumWidth();
	}

	private void drawLine(int y, Graphics2D g, int length, int width) {
		drawing(g);
		g.drawLine(width - length, y, width, y);
	}

	protected void drawSelection(Graphics2D g, int width, Candle candle, Point location) {
		setProfile(SELECTION);

		final int y = chart.getYCoordinate(chart.getCentralValue(candle));
		final int fontHeight = getController().getFontHeight();
		int stringY = getController().centralizeYToFontHeight(y);

		if (stringY + fontHeight > chart.getHeight()) {
			stringY = chart.getHeight() - fontHeight;
		} else if (stringY < 0) {
			stringY = 0;
		}
		String tag = readFieldFormatted(candle);
		int tagWidth = getController().getMaxStringWidth(tag, g);
		drawStringInBox(chart.getBoundaryRight() - tagWidth - getController().getRightValueTagSpacing(), stringY, chart.getWidth(), tag, g, 1);
	}

	private void drawGrid(int y, Graphics2D g, int width) {
		if (isShowingGrid()) {
			g.setColor(getGridColor());
			g.drawLine(0, y, width, y);
		}
	}

	@Override
	protected ValueRulerController newController() {
		return new ValueRulerController(this);
	}

	protected String readFieldFormatted(Candle candle) {
		return getValueFormat().format(chart.getCentralValue(candle));
	}

	protected Format getValueFormat() {
		return Candle.PRICE_FORMAT.get();
	}

	@Override
	public Z getZ() {
		return Z.FRONT;
	}
}
