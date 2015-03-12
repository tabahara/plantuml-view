package com.quinmantha.tool;

import org.apache.batik.swing.svg.JSVGComponent;
import org.w3c.dom.Document;

import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public interface ISVGDiagram {
    void setDocument(Document document);
    JComponent getComponent();
    JSVGComponent getSvgComponent();
}
