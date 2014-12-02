package com.quinmantha.tool;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
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
public class Main extends UpdateCheck {
    public Main(String target){
        super(new File(target));
    }


/*
    private static void openSVG(File f){
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
            InputStream is = new FileInputStream(f);
            Document doc = svgf.createDocument(null,is);
            canvas.setDocument(doc);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    private boolean is(File f, String ext){
        String fname = f.getName();
        String t = "";
        int pos = fname.lastIndexOf(".");
        if(pos != -1){
            t= fname.substring(pos+1);
        }
        return t.equalsIgnoreCase(ext);
    }

    static ArrayList<JSVGCanvas> canvas = new ArrayList<JSVGCanvas>();
    public static void main(String[] args){
        String targetName = args[0];
        System.out.println("watch:"+args[0]);

        Main obj = new Main(targetName);
        obj.start();

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setBackground(Color.WHITE);
        f.getContentPane().setLayout(new FlowLayout());

        canvas.add(new JSVGCanvas());
        f.getContentPane().add(canvas.get(0));
        canvas.add(new JSVGCanvas());
        f.getContentPane().add(canvas.get(1));

        /*
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("File");
        bar.add(menu);
        JMenuItem out = new JMenuItem("Open SVG");
        menu.add(out);
        out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showDialog(null, "Select File");
                if(ret!=JFileChooser.APPROVE_OPTION){
                    return ;
                }
                File file = chooser.getSelectedFile();
                openSVG(file);
            }
        });
        f.setJMenuBar(bar);
        */
        f.setSize(400,300);
        f.setVisible(true);
    }

    @Override
    protected void onModified(String filename){
        System.out.println("update svg (" + filename + ")");
        File f = new File(getTargetDir(), filename);
        //if(is(f,"svg")) {
        //openSVG(f);
        //} else

        if( filename.charAt(0) != '.' ) {
            if (is(f, "puml")) {
                try {
                    openPlantUML(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openPlantUML(File f) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileInputStream in = new FileInputStream(f);
        byte buffer[] = new byte[4096];
        int n;
        while((n=in.read(buffer)) >= 0){
            out.write(buffer,0,n);
        }
        String s = out.toString("utf-8");
        SourceStringReader reader = new SourceStringReader(s,"utf-8");

        List<String> svgs = new LinkedList<String>();
        int idx=0;
        for(;;) {
            ByteArrayOutputStream svg = new ByteArrayOutputStream();
            String res = reader.generateImage(svg, idx, new FileFormatOption(FileFormat.SVG));
            if(res == null){
                break;
            }
            System.out.println(res);
            svgs.add(new String(svg.toByteArray(), "utf-8"));
            svg.close();
            System.out.println(String.format("output #%d",idx));
            idx++;
        }

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        int i=0;
        for(String svg : svgs) {
            Document doc = svgf.createDocument(null, new StringReader(svg));
            canvas.get(i).setDocument(doc);
            i++;
            if( canvas.size() <= i){
                break;
            }
        }
    }


}
