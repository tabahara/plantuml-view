package com.quinmantha.tool;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.preproc.Defines;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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


    private ArrayList<JSVGCanvas> canvas;
    private JFrame viewFrame;
    private JTabbedPane pane;

    public Main(String target){
        super(new File(target));

        viewFrame = new JFrame();
	viewFrame.setTitle( target );
        viewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewFrame.getContentPane().setBackground(Color.WHITE);
        viewFrame.getContentPane().setLayout(new BorderLayout(32,32));
        viewFrame.setSize(500, 600);
	pane = new JTabbedPane();
	viewFrame.getContentPane().add(pane);

        canvas = new ArrayList<JSVGCanvas>();

        /*
        canvas.add(new JSVGCanvas());
        canvas.get(0).setBackground(new Color(255,250,240));
        viewFrame.getContentPane().add(canvas.get(0));
        canvas.add(new JSVGCanvas());
        canvas.get(1).setBackground(new Color(255,250,205));
        viewFrame.getContentPane().add(canvas.get(1));
        canvas.add(new JSVGCanvas());
        canvas.get(2).setBackground(new Color(255,250,205));
        viewFrame.getContentPane().add(canvas.get(2));
        */

        try {
            openPlantUML(new File(target));
        } catch (IOException e){
            System.out.println(e.toString());
        }

        viewFrame.setVisible(true);
    }

    private boolean is(String fname, String ext){
        String t = "";
        int pos = fname.lastIndexOf(".");
        if(pos != -1){
            t= fname.substring(pos+1);
        }
        return t.equalsIgnoreCase(ext);
    }


    @Override
    protected void onModified(File file){
        String filename = file.getName();
        System.out.println("update(" + filename + ")");

        try {
            openPlantUML(file);
        } catch (IOException e) {
            e.printStackTrace();
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
	    System.out.println("#1:"+idx + ":" + canvas.size());
            Document doc = svgf.createDocument(null, new FileInputStream(g.getPngFile()));
            if( idx >= canvas.size()){
		System.out.println("add canvas");
		canvas.add(new JSVGCanvas());
		canvas.get(idx).setBackground(new Color(255,250,240));
		pane.addTab(g.getPngFile().getName(), canvas.get(idx));
            }
            canvas.get(idx).setDocument(doc);
            idx++;
        }
	System.out.println("#2:"+idx + ":" + canvas.size());
	while(idx < canvas.size()){
	    System.out.println("del canvas");
	    System.out.println("#3:"+idx + ":" + canvas.size());
	    JSVGCanvas o = canvas.get(idx);
	    pane.remove(o);
	    canvas.remove(o);
	}
    }
}
