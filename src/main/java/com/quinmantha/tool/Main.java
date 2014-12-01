package com.quinmantha.tool;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

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
public class Main {
    private static JSVGCanvas canvas = null;
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
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setBackground(Color.WHITE);
        f.getContentPane().setLayout(new BorderLayout());
        canvas = new JSVGCanvas();
        f.getContentPane().add(canvas, BorderLayout.CENTER);
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
        f.setSize(400,300);
        f.setVisible(true);
    }
}
