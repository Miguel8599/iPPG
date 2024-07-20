package com.example.biosignals;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ManejoArchivos
{

    //---------------------------------------------------------------------------------------------
    public static void crearDirectorio (File rutaDirectorio) {
        if (!rutaDirectorio.exists()) {
            rutaDirectorio.mkdir();
        }
    }
    //---------------------------------------------------------------------------------------------
    public static boolean escribirArchivo (File rutaAlArchivo, ArrayList<String> datosAguardar) {
        boolean todoOk = false;
        String contenido = "";
        int i;

        if (datosAguardar.size() > 1) {
            for (i=0; i<datosAguardar.size()-1; i++) {
                contenido = contenido + datosAguardar.get(i) + "\n";
            }
            contenido = contenido + datosAguardar.get(i);
        }
        else
            contenido = datosAguardar.get(0);

        try {
            FileOutputStream fos = new FileOutputStream(rutaAlArchivo);
            OutputStreamWriter out = new OutputStreamWriter(fos);
            out.write(contenido, 0, contenido.length());
            out.flush();
            out.close();
            todoOk = true;
        }
        catch (Exception ex) {
            //Error en el archivo
        }
        return  todoOk;
    }
    //---------------------------------------------------------------------------------------------
    //paso por referencia en miArchivo
    public static boolean leerArchivo (File rutaAlArchivo, ArrayList<String> miArchivo) {
        boolean todoOk = false;
        String lectorLinea;

        try {
            BufferedReader bReader = new BufferedReader(new FileReader(rutaAlArchivo));
            while ( (lectorLinea = bReader.readLine()) != null) {
                miArchivo.add(lectorLinea);
            }
            bReader.close();
            todoOk = true;
        }
        catch (IOException e) {
            //Error en el archivo
        }
        return todoOk;
    }


    //---------------------------------------------------------------------------------------------

    //para guardar en la carpeta principal
    public static void salvarPruebas(String nombreArchivo, ArrayList<Float> s) {
        String ruta = Environment.getExternalStorageDirectory() + "/Crepitancias/OtrasSeniales";
        File fDir = new File(ruta);
        File f = new File(fDir, nombreArchivo);

        OperacionesArray.guardarFloat(f, s);

    }


    public static void cargarPruebas(String nombreArchivo, ArrayList<Float> s) {
        String ruta = Environment.getExternalStorageDirectory() + "/Crepitancias/OtrasSeniales";
        File fDir = new File(ruta);
        File f = new File(fDir, nombreArchivo);
        OperacionesArray.cargarFloat(f, s);

    }


    public static void salvarPruebasInt(String nombreArchivo, ArrayList<Integer> s) {
        String ruta = Environment.getExternalStorageDirectory() + "/Crepitancias/";
        File fDir = new File(ruta);
        File f = new File(fDir, nombreArchivo);

        OperacionesArray.guardarInt(f, s);

    }
    //*********************************************************************************************


}
