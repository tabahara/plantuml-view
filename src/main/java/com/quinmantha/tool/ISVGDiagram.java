package com.quinmantha.tool;

import org.apache.batik.swing.svg.JSVGComponent;
import org.w3c.dom.Document;

import java.awt.geom.AffineTransform;

public interface ISVGDiagram {
    void setDocument(Document document);
    JSVGComponent getSvgComponent();
}
