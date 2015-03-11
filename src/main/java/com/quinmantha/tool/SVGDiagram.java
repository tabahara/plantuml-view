package com.quinmantha.tool;

import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderListener;
import org.apache.batik.swing.svg.JSVGComponent;
import org.w3c.dom.Document;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.nio.file.attribute.AclFileAttributeView;

public class SVGDiagram implements  ISVGDiagram {
    public String getName(){
        return mName;
    }

    @Override
    public void setDocument(Document document) {
        mJSVGComponent.setDocument(document);
    }

    @Override
    public JSVGComponent getSvgComponent() {
        return mJSVGComponent;
    }

    public SVGDiagram(String name, double zoomFactor){
        mName = name;
        mZoomFactor = zoomFactor;
        mJSVGComponent = new JSVGComponent(null,false,false);
        mJSVGComponent.setBackground(new Color(255,250,240));
        mJSVGComponent.addGVTTreeBuilderListener( new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                super.gvtBuildCompleted(e);
                zoom(mZoomFactor);
            }
        });
    }

    public double getZoomFactor(){
        return mZoomFactor;
    }

    public void setZoomFactor(double zoomFactor){
        mZoomFactor = zoomFactor;
        zoom(mZoomFactor);
        mJSVGComponent.invalidate();
    }

    private JSVGComponent mJSVGComponent;
    private double mZoomFactor;
    private String mName;

    public void zoom(double n){
        AffineTransform cmd = AffineTransform.getScaleInstance(n,n);
        double x = mJSVGComponent.getWidth() / 2.0f;
        double y = mJSVGComponent.getHeight() / 2.0f;
        AffineTransform rat = mJSVGComponent.getRenderingTransform();
        AffineTransform t = AffineTransform.getTranslateInstance(x,y);
        t.concatenate(cmd);
        t.translate(-x, -y);
        t.concatenate(rat);
        mJSVGComponent.setRenderingTransform(t);
    }

}
