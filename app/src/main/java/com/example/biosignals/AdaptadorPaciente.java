package com.example.biosignals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorPaciente extends ArrayAdapter<Paciente>
{


    private Context context;
    private ArrayList<Paciente> listaArchivos;


    public AdaptadorPaciente(final Context context, final ArrayList<Paciente> listaArchivos)
    {
        super(context, 0, listaArchivos);
        this.context = context;
        this.listaArchivos = listaArchivos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (null == convertView)
        {
            convertView = inflater.inflate(R.layout.item_lista, parent,false);
        }


        ImageView iv = (ImageView) convertView.findViewById(R.id.iv_listaItemExplorador);
        TextView nombre = (TextView) convertView.findViewById(R.id.tv_listaItemExplorador);
        TextView fecha = (TextView) convertView.findViewById(R.id.tv_fechaPaciente);


        Paciente archivo = getItem(position);

        String pos = String.valueOf(position+1) + ".  ";
        nombre.setText(pos + archivo.getNombreCompleto()); //todo --------------
        //nombre.setText(pos + archivo.getNombreSimple());
        fecha.setText(archivo.getFechaRegistro());
        iv.setImageDrawable(context.getResources().getDrawable(R.drawable.image));


        return convertView;
    }


}
