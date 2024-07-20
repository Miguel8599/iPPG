package com.example.biosignals;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class OperacionesArray
{

    /***********************************************************************************************
     * Operaciones float
     *
     **********************************************************************************************/

    public static void igualar (ArrayList<Float> in, ArrayList<Float> out)
    {
        int size = in.size();

        for (int i=0; i<size; i++) {
            out.add(  out.get(i)  );
        }
    }

    public static void sumar (ArrayList<Float> in1, ArrayList<Float> in2, ArrayList<Float> out)
    {
        int size1 = in1.size();
        int size2 = in2.size();

        if (size1 == size2)
        {
            for (int i=0; i<size1; i++) {
                out.add(  in1.get(i) + in2.get(i)  );
            }
        }
    }

    public static void multiplicar (ArrayList<Float> in1, ArrayList<Float> in2, ArrayList<Float> out)
    {
        int size1 = in1.size();
        int size2 = in2.size();

        if (size1 == size2) {
            for (int i=0; i<size1; i++) {
                out.add(  in1.get(i)*in2.get(i)  );
            }
        }
    }

    public static void dividirEscalar (ArrayList<Float> in, float inEscalar, ArrayList<Float> out)
    {
        int size = in.size();

        for (int i=0; i<size; i++) {
            out.add( in.get(i) / inEscalar );
        }
    }

    public static void llenarArray (int inCantidadValores, float inValor,  ArrayList<Float> out)
    {
        for (int i=0; i<inCantidadValores; i++)
            out.add(inValor);
    }

    public static void llenarArrayConIncremento (float inValorInf, float inValorSup, float inIncremento,  ArrayList<Float> out)
    {
        while (inValorInf <= inValorSup)
        {
            out.add(inValorInf);
            inValorInf = inValorInf + inIncremento;
        }
    }

    public static void  convertirFloatADouble (ArrayList<Float> in, ArrayList<Double> out)
    {
        float a;
        double b;

        for(int i=0; i<in.size(); i++) {
            a = in.get(i);
            b = (double) a;
            out.add( b );
        }
    }

    public static float obtenerPromedio (ArrayList<Float> in)
    {
        int size = in.size();
        float suma = 0;

        for (int i=0; i<size; i++) {
            suma = suma + in.get(i);
        }

        return suma/(size);
    }

    public static float obtenerMax (ArrayList<Float> in)
    {
        int size = in.size();
        float max;
        float valor;

        max = in.get(0);
        for (int i=1; i<size; i++) {
            valor = in.get(i);
            if(valor > max) {
                max = valor;
            }
        }

        return max;
    }

    public static float obtenerMin (ArrayList<Float> in)
    {
        int size = in.size();
        float min;
        float valor;

        min = in.get(0);
        for (int i=1; i<size; i++) {
            valor = in.get(i);
            if (valor < min) {
                min = valor;
            }
        }

        return min;
    }

    public static float obtenerMaxAbs (ArrayList<Float> in)
    {
        float max = obtenerMax(in);
        float min = obtenerMin(in) * -1;

        if (max > min) {
            return  max;
        }
        else {
            return min;
        }
    }



    /***********************************************************************************************
     * Operaciones Signals
     *
     **********************************************************************************************/
    public static void quitarMediaSig (ArrayList<Float> in, ArrayList<Float> out)
    {
        int size = in.size();
        float promedio = obtenerPromedio(in);

        for (int i=0; i<size; i++) {
            out.add( in.get(i) - promedio );
        }
    }

    public static void normalizarSig (ArrayList<Float> in, ArrayList<Float> out)
    {
        int size = in.size();
        float maxAbs = obtenerMaxAbs(in);

        for (int i=0; i<size; i++) {
            out.add( in.get(i) / maxAbs );
        }
    }


    public static void cargarFloat(File inF, ArrayList<Float> out)
    {
        ArrayList<String> valoresArchivo = new ArrayList<>();
        float valor;

        if (inF.exists())
        {
            ManejoArchivos.leerArchivo(inF, valoresArchivo);
            String lineaTexto;

            for(int i=0; i<valoresArchivo.size(); i++)
            {
                lineaTexto = valoresArchivo.get(i);
                valor = Float.parseFloat(lineaTexto);
                out.add(valor);
            }
        }
    }


    public static void cargarDoubleX(File inF, ArrayList<Double> out)
    {
        ArrayList<String> valoresArchivo = new ArrayList<>();
        double valor;

        if (inF.exists())
        {
            ManejoArchivos.leerArchivo(inF, valoresArchivo);
            String lineaTexto;

            for(int i=0; i<valoresArchivo.size(); i++)
            {
                lineaTexto = valoresArchivo.get(i);
                valor = Double.parseDouble(lineaTexto);
                out.add(valor);
            }
        }
    }


    public static void  guardarInt (File inF, ArrayList<Integer> in)
    {
        try
        {
            int size = in.size();
            FileWriter w = new FileWriter(inF);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);

            String cadena;

            for (int i = 0; i < size; i++) {
                cadena = String.valueOf(in.get(i)) + "\n";
                wr.append(cadena);
            }
            wr.close();
            bw.close();
        } catch (IOException e) {
            Log.d("","");
        }
    }


    public static void  guardarString (File inF, ArrayList<String> in)
    {
        try
        {
            int size = in.size();
            FileWriter w = new FileWriter(inF);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);

            String cadena;

            for (int i = 0; i < size; i++) {
                cadena = in.get(i) + "\n";
                wr.append(cadena);
            }
            wr.close();
            bw.close();
        } catch (IOException e) {
            Log.d("","");
        }
    }

    public static void  guardarFloat (File inF, ArrayList<Float> in)
    {
        try
        {
            int size = in.size();
            FileWriter w = new FileWriter(inF);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);

            String cadena;

            for (int i = 0; i < size; i++) {
                cadena = String.valueOf(in.get(i)) + "\n";
                wr.append(cadena);
            }
            wr.close();
            bw.close();
        } catch (IOException e) {
            Log.d("","");
        }
    }

    public static void  guardarDoubleXX (File inF, ArrayList<Double> in)
    {
        try
        {
            int size = in.size();
            FileWriter w = new FileWriter(inF);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);

            String cadena;

            for (int i = 0; i < size; i++) {
                cadena = String.valueOf(in.get(i)) + "\n";
                wr.append(cadena);
            }
            wr.close();
            bw.close();
        } catch (IOException e) {
            Log.d("","");
        }
    }





}
