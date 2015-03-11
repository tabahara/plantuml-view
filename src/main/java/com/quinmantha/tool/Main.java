package com.quinmantha.tool;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.preproc.Defines;

import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.JGVTComponent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.JSVGComponent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderListener;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherAdapter;
import org.apache.batik.swing.svg.SVGLoadEventDispatcherEvent;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.SVGSVGElement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.xml.XMLConstants;

/*
@startuml doc-files/Main.svg
hide empty member
class A
class B
A<|--B
@enduml
 */
public class Main extends UpdateCheckFile {
    public static void main(String[] args){
        if( args.length < 1){
            System.err.println("missing argument");
            return ;
        }

        String targetName = args[0];
        System.out.println("watch:"+args[0]);

        File f = new File(targetName);
        if(f.exists() && f.isFile() ) {
            Main obj = new Main(targetName);
            obj.start();
        } else {
            System.out.println("File not found.");
        }
    }

    private ArrayList<SVGDiagram> components;
    private JFrame viewFrame;
    private JTabbedPane pane;

    private String makeTitle(String fpath){
        File target = new File(fpath);

        String parentPath = target.getParent();
        if( parentPath == null ){
            parentPath = ".";
        }
        File parent = new File(parentPath);
        System.out.println(String.format("%s %s\n", parent.getName(), target.getName()));
        return parent.getName() + File.separator + target.getName();
    }

    public Main(String target){
        super(new File(target));
        zoomFactorTable = new HashMap<>();

        viewFrame = new JFrame();
        viewFrame.setTitle(makeTitle(target));
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.getContentPane().setBackground(Color.WHITE);
        viewFrame.getContentPane().setLayout(new BorderLayout());
        viewFrame.setSize(500, 600);

        JButton button = new JButton("check");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                components.get(0).setZoomFactor(0.8);
            }
        });
        viewFrame.getContentPane().add("North", button);


        pane = new JTabbedPane();
        viewFrame.getContentPane().add("Center", pane);

        components = new ArrayList<>();

        try {
            openPlantUML(new File(target));
        } catch (IOException e){
            System.out.println(e.toString());
        }

        viewFrame.setVisible(true);
    }

    @Override
    protected void onModified(File file){
        String filename = file.getName();
        System.out.println("update(" + filename + ")");

        try {
            saveZoomFactors();
            openPlantUML(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String,Double> zoomFactorTable;
    private void saveZoomFactors(){
        for(SVGDiagram d : components ){
            zoomFactorTable.put(d.getName(), d.getZoomFactor());
        }
    }

    private void openPlantUML(File f) throws IOException {
        SourceFileReader sfReader
                = new SourceFileReader(new Defines(), f, null, new LinkedList<String>(), "utf-8", new FileFormatOption(FileFormat.SVG));

        Collection<GeneratedImage> images = sfReader.getGeneratedImages();

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        int idx = 0;
        for(GeneratedImage g : images){
            System.out.println("#1:" + idx + ":" + components.size());
            Document doc = svgf.createDocument(null, new FileInputStream(g.getPngFile()));
            if( idx >= components.size()){
                System.out.println("add components");
                Double zoomFactor;
                String name = g.getPngFile().getName();
                zoomFactor = zoomFactorTable.get(name);
                if(zoomFactor == null){
                    zoomFactor = new Double(1.0f);
                }
                SVGDiagram diagram = new SVGDiagram(g.getPngFile().getName(), zoomFactor.doubleValue());
                components.add(diagram);
                pane.addTab(g.getPngFile().getName(), components.get(idx).getSvgComponent());
            }
            pane.setTitleAt(idx, g.getPngFile().getName());
            components.get(idx).setDocument(doc);
            idx++;
        }
        System.out.println("#2:" + idx + ":" + components.size());
        while(idx < components.size()){
            System.out.println("del components");
            System.out.println("#3:"+idx + ":" + components.size());
            SVGDiagram o = components.get(idx);
            pane.remove(o.getSvgComponent());
            components.remove(o);
        }
    }
}
