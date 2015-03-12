package com.quinmantha.tool;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.JSVGComponent;
import org.w3c.dom.Document;

import java.awt.Color;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public class SVGDiagram implements  ISVGDiagram {
    @Override
    public void setDocument(Document document) {
        mJSVGCanvas.setDocument(document);
    }

    @Override
    public JComponent getComponent() {
        return mJComponent;
    }

    @Override
    public JSVGComponent getSvgComponent() {
        return mJSVGCanvas;
    }

    public SVGDiagram(){
        mName = "";
        mAffineTransform = AffineTransform.getTranslateInstance(0,0);
        mJSVGCanvas = new JSVGCanvas();
        mJComponent = mJSVGCanvas; // new JSVGScrollPane(mJSVGCanvas);
        mJSVGCanvas.setBackground(new Color(255,250,240));
        mJSVGCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                super.gvtBuildCompleted(e);
                System.out.println(String.format("gvtBuildCompleted apply %s",
                        mAffineTransform.toString()));
                setAffineTransform(mAffineTransform);
            }
        });
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public AffineTransform getAffineTransform(){
        return mJSVGCanvas.getRenderingTransform();
    }

    public void setAffineTransform(AffineTransform affineTransform){
        mAffineTransform = affineTransform;
        mJSVGCanvas.setRenderingTransform(affineTransform);
    }

    private JComponent mJComponent;
    private JSVGCanvas mJSVGCanvas;
    private String mName;
    private AffineTransform mAffineTransform;

    public void zoom(double n){
        AffineTransform cmd = AffineTransform.getScaleInstance(n,n);
        double x = mJSVGCanvas.getWidth() / 2.0f;
        double y = mJSVGCanvas.getHeight() / 2.0f;
        AffineTransform rat = mJSVGCanvas.getRenderingTransform();
        AffineTransform t = AffineTransform.getTranslateInstance(x,y);
        t.concatenate(cmd);
        t.translate(-x, -y);
        t.concatenate(rat);
        mJSVGCanvas.setRenderingTransform(t);
    }

}
