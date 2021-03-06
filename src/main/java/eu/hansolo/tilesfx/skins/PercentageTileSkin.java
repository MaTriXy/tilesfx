/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.tilesfx.skins;

import eu.hansolo.tilesfx.Section;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.fonts.Fonts;
import eu.hansolo.tilesfx.tools.Helper;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static eu.hansolo.tilesfx.tools.Helper.clamp;


/**
 * Created by hansolo on 19.12.16.
 */
public class PercentageTileSkin extends TileSkin {
    private Region    barBackground;
    private Rectangle barClip;
    private Rectangle bar;
    private Text      titleText;
    private Text      valueText;
    private Text      unitText;
    private Text      percentageText;
    private Text      percentageUnitText;
    private Rectangle maxValueRect;
    private Text      maxValueText;
    private Text      maxValueUnitText;
    private Color     barColor;


    // ******************** Constructors **************************************
    public PercentageTileSkin(final Tile TILE) {
        super(TILE);
    }


    // ******************** Initialization ************************************
    @Override protected void initGraphics() {
        super.initGraphics();

        barColor = getSkinnable().getBarColor();

        barBackground = new Region();
        barBackground.setBackground(new Background(new BackgroundFill(getSkinnable().getBarBackgroundColor(), new CornerRadii(0.0, 0.0, 0.025, 0.025, true), Insets.EMPTY)));

        barClip = new Rectangle();

        bar = new Rectangle();
        bar.setFill(getSkinnable().getBarColor());
        bar.setStroke(null);
        bar.setClip(barClip);

        titleText = new Text();
        titleText.setFill(getSkinnable().getTitleColor());
        Helper.enableNode(titleText, !getSkinnable().getTitle().isEmpty());

        valueText = new Text(String.format(locale, formatString, ((getSkinnable().getValue() - minValue) / range * 100)));
        valueText.setFill(getSkinnable().getValueColor());
        Helper.enableNode(valueText, getSkinnable().isValueVisible());

        unitText = new Text(getSkinnable().getUnit());
        unitText.setFill(getSkinnable().getUnitColor());
        Helper.enableNode(unitText, !getSkinnable().getUnit().isEmpty());

        percentageText = new Text();
        percentageText.setFill(getSkinnable().getBarColor());

        percentageUnitText = new Text("%");
        percentageUnitText.setFill(getSkinnable().getBarColor());

        maxValueRect = new Rectangle();
        maxValueRect.setFill(getSkinnable().getThresholdColor());

        maxValueText = new Text();
        maxValueText.setFill(getSkinnable().getBackgroundColor());

        maxValueUnitText = new Text(getSkinnable().getUnit());
        maxValueUnitText.setFill(getSkinnable().getBackgroundColor());

        getPane().getChildren().addAll(barBackground, bar, titleText, valueText, unitText, percentageText, percentageUnitText, maxValueRect, maxValueText, maxValueUnitText);
    }

    @Override protected void registerListeners() {
        super.registerListeners();

    }


    // ******************** Methods *******************************************
    @Override protected void handleEvents(final String EVENT_TYPE) {
        super.handleEvents(EVENT_TYPE);

        if ("VISIBILITY".equals(EVENT_TYPE)) {
            Helper.enableNode(titleText, !getSkinnable().getTitle().isEmpty());
            Helper.enableNode(valueText, getSkinnable().isValueVisible());
            Helper.enableNode(unitText, !getSkinnable().getUnit().isEmpty());
        }
    };

    @Override protected void handleCurrentValue(final double VALUE) {
        double targetValue = (clamp(minValue, maxValue, VALUE) - minValue) * stepSize;
        bar.setWidth(targetValue);
        valueText.setText(String.format(locale, formatString, VALUE));
        percentageText.setText(String.format(locale, formatString, ((VALUE - minValue) / range * 100)));
        maxValueRect.setFill(VALUE > maxValue ? barColor : getSkinnable().getThresholdColor());
        resizeDynamicText();
        if (sectionsVisible && !sections.isEmpty()) { setBarColor(VALUE); }
    };

    private void setBarColor(final double VALUE) {
        Color color = barColor;
        for(Section section : sections) {
            if (section.contains(VALUE)) {
                color = section.getColor();
                break;
            }
        }
        bar.setFill(color);
        percentageText.setFill(color);
        percentageUnitText.setFill(color);
    }


    // ******************** Resizing ******************************************
    @Override protected void resizeDynamicText() {
        double maxWidth = unitText.isVisible() ? size * 0.725 : size * 0.9;
        double fontSize = size * 0.24;
        valueText.setFont(Fonts.latoRegular(fontSize));
        if (valueText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(valueText, maxWidth, fontSize); }
        if (unitText.isVisible()) {
            valueText.relocate(size * 0.925 - valueText.getLayoutBounds().getWidth() - unitText.getLayoutBounds().getWidth(), size * 0.15);
        } else {
            valueText.relocate(size * 0.95 - valueText.getLayoutBounds().getWidth(), size * 0.15);
        }

        percentageUnitText.relocate(percentageText.getLayoutBounds().getMaxX() + size * 0.075, size * 0.75);
    };
    @Override protected void resizeStaticText() {
        double maxWidth = size * 0.9;
        double fontSize = size * 0.06;

        titleText.setFont(Fonts.latoRegular(fontSize));
        if (titleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(titleText, maxWidth, fontSize); }
        titleText.relocate(size * 0.05, size * 0.05);

        maxWidth = size * 0.15;
        fontSize = size * 0.12;
        unitText.setFont(Fonts.latoRegular(fontSize));
        if (unitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(unitText, maxWidth, fontSize); }
        unitText.relocate(size * 0.95 - unitText.getLayoutBounds().getWidth(), size * 0.27);

        maxWidth = size * 0.45;
        fontSize = size * 0.18;
        percentageText.setFont(Fonts.latoRegular(fontSize));
        if (percentageText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(percentageText, maxWidth, fontSize); }
        percentageText.relocate(size * 0.05, size * 0.695);

        maxWidth = size * 0.1;
        fontSize = size * 0.12;
        percentageUnitText.setFont(Fonts.latoRegular(fontSize));
        if (percentageUnitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(percentageUnitText, maxWidth, fontSize); }
        percentageUnitText.relocate(percentageText.getLayoutBounds().getMaxX() + size * 0.075, size * 0.75);

        maxWidth = size * 0.2;
        fontSize = size * 0.05;
        maxValueUnitText.setFont(Fonts.latoRegular(fontSize));
        if (maxValueUnitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(maxValueUnitText, maxWidth, fontSize); }
        maxValueUnitText.setX((size * 0.925) - maxValueUnitText.getLayoutBounds().getWidth());
        maxValueUnitText.setY(size * 0.855);

        maxWidth = size * 0.45;
        fontSize = size * 0.08;
        maxValueText.setFont(Fonts.latoRegular(fontSize));
        if (maxValueText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(maxValueText, maxWidth, fontSize); }
        if (unitText.isVisible()) {
            maxValueText.setX((size * 0.925) - (size * 0.01 + maxValueText.getLayoutBounds().getWidth() + maxValueUnitText.getLayoutBounds().getWidth()));
        } else {
            maxValueText.setX((size * 0.925) - maxValueText.getLayoutBounds().getWidth());
        }
        maxValueText.setY(size * 0.855);
    };

    @Override protected void resize() {
        super.resize();
        barBackground.setPrefSize(size, size * 0.035);
        barBackground.relocate(0, size * 0.965);

        barClip.setX(0);
        barClip.setY(size * 0.95);
        barClip.setWidth(size);
        barClip.setHeight(size * 0.05);
        barClip.setArcWidth(size * 0.025);
        barClip.setArcHeight(size * 0.025);

        bar.setX(0);
        bar.setY(size * 0.965);
        bar.setWidth(clamp(minValue, maxValue, getSkinnable().getCurrentValue()) * stepSize);
        bar.setHeight(size * 0.035);

        maxValueRect.setWidth((maxValueText.getLayoutBounds().getWidth() + maxValueUnitText.getLayoutBounds().getWidth()) + size * 0.06);
        maxValueRect.setHeight(maxValueText.getLayoutBounds().getHeight() * 1.01);
        maxValueRect.setX((size * 0.95) - maxValueRect.getWidth());
        maxValueRect.setY(size * 0.7775);
        maxValueRect.setArcWidth(size * 0.025);
        maxValueRect.setArcHeight(size * 0.025);
    };

    @Override protected void redraw() {
        super.redraw();
        titleText.setText(getSkinnable().getTitle());
        percentageText.setText(String.format(locale, "%." + getSkinnable().getDecimals() + "f", getSkinnable().getValue() / range * 100));
        maxValueText.setText(String.format(locale, "%." + getSkinnable().getTickLabelDecimals() + "f", getSkinnable().getMaxValue()));
        maxValueUnitText.setText(getSkinnable().getUnit());

        resizeStaticText();

        barBackground.setBackground(new Background(new BackgroundFill(getSkinnable().getBarBackgroundColor().brighter().brighter(), new CornerRadii(0.0, 0.0, size * 0.025, size * 0.025, false), Insets.EMPTY)));
        barColor = getSkinnable().getBarColor();

        if (sectionsVisible && !sections.isEmpty()) {
            setBarColor(getSkinnable().getValue());
        } else {
            bar.setFill(barColor);
        }

        titleText.setFill(getSkinnable().getTitleColor());
        maxValueText.setFill(getSkinnable().getBackgroundColor());
        maxValueUnitText.setFill(getSkinnable().getBackgroundColor());
        valueText.setFill(getSkinnable().getValueColor());
        unitText.setFill(getSkinnable().getUnitColor());
    };
}
