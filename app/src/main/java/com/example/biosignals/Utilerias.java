package com.example.biosignals;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilerias
{

    //---------------------------------------------------------------------------------------------
    public static boolean comprobarEstadoMemoria () {
        String estado = Environment.getExternalStorageState();
        boolean estadoOK = false;

        if (Environment.MEDIA_MOUNTED.equals(estado)) {
            // podemos escribir y leer en la memoria externa
            estadoOK = true;
        }
        return  estadoOK;
    }
    //---------------------------------------------------------------------------------------------
    public static void crearDirectorio (File rutaDirectorio) {
        if (!rutaDirectorio.exists()) {
            rutaDirectorio.mkdirs();
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
    public static String normalzarEntrada (String cadena) {
        String textoNormalizado;
        String texto = cadena.trim().replaceAll(" +", " ").toUpperCase();
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        textoNormalizado = texto.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return textoNormalizado;
    }
    //---------------------------------------------------------------------------------------------
    public static boolean validarEntradaDatos (String cadena) {
        boolean todoOk = false;

        Pattern pat = Pattern.compile("[A-Z ]+");
        Matcher mat = pat.matcher(cadena);
        if (mat.matches())
            todoOk = true;
        return todoOk;
    }
    //---------------------------------------------------------------------------------------------
    public static boolean validarCadenaDelArchivoPacientes (String cadena) {
        boolean entradaOk = false;

        //cadena: nombre::apellidoPaterno::edad::genero::fechaRegistro = 6
        Pattern pat = Pattern.compile("[A-Z ]+::[A-Z ]+::[0-9]{1,2}::[MF]::20[0-9]{2,2}/[0-9]{2,2}/[0-9]{2,2}");
        Matcher mat = pat.matcher(cadena);
        if (mat.matches())
            entradaOk = true;
        return entradaOk;
    }

    //---------------------------------------------------------------------------------------------
    public static boolean validarCadenaDelArchivoSignals (String cadena) {
        boolean entradaOk = false;

        // [RL][AMB][ie]::aaaa/mm/dd::hh/mm/ss::nombreCompleto = 4
        Pattern pat = Pattern.compile("[a-zA-Z]+::20[0-9]{2,2}/[0-9]{2,2}/[0-9]{2,2}::[0-9]{2,2}/[0-9]{2,2}/[0-9]{2,2}::[a-zA-Z]+");
        //Pattern pat = Pattern.compile("[RL][AMB][ie]::20[0-9]{2,2}/[0-9]{2,2}/[0-9]{2,2}::[0-9]{2,2}/[0-9]{2,2}/[0-9]{2,2}::[a-zA-Z]+");
        Matcher mat = pat.matcher(cadena);
        if (mat.matches())
            entradaOk = true;
        return entradaOk;
    }
    //---------------------------------------------------------------------------------------------
    public static boolean borrarArchivo (File f) {
        boolean borradoOk = true;

        File [] ficheros = f.listFiles();

        for (int i=0; i<ficheros.length;i++){
            if (ficheros[i].isDirectory()) {
                borradoOk = borrarArchivo(ficheros[i]);
                ficheros[i].delete();
            }
            else {
                try {
                    ficheros[i].delete();
                }
                catch (Exception e) {
                    return borradoOk = false;
                }
            }
        }

        return borradoOk;
    }





}
