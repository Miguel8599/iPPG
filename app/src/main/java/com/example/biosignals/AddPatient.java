package com.example.biosignals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddPatient extends AppCompatActivity {

    private ArrayList<String> archivoPacientes;
    private ArrayList<String> nombresPacientes;
    private Paciente miPaciente;
    private String nombre, apellido, edad, genero, fecha;
    private EditText miEtNombre, miEtApellido, miEtEdad;
    private RadioGroup miRgGenero;
    private Button btnadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        createAppBar(R.id.miAppBar_add_patient, R.color.colorPrimary, false);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        inicializarVariables();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), Patients.class);
        startActivity(intent);
        finish();

    }

    //==============================================================================================
    private void createAppBar(int appBarLayaout, int colorFondoAppBar, boolean upBoton) {
        Toolbar miAppBar = (Toolbar) findViewById(appBarLayaout);
        setSupportActionBar(miAppBar);
        miAppBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorFondoAppBar));
        miAppBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        String title = getResources().getString(R.string.add_patient);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upBoton);
    }

       //**********************************************************************************************

    private void inicializarVariables () {
        miEtNombre = (EditText) findViewById(R.id.miEtNombre_agregarPaciente);
        miEtApellido = (EditText) findViewById(R.id.miEtApellidoPaterno_agregarPaciente);
        miEtEdad = (EditText) findViewById(R.id.miEtEdad_agregarPaciente);
        miRgGenero = (RadioGroup) findViewById(R.id.miRgGenero_agregarPaciente);
        btnadd = (Button) findViewById(R.id.btnadd);
        btnadd.setOnClickListener(v -> guardarDatos(v));
    }

    //**********************************************************************************************

    private void guardarDatos (View v) {
        obtenerDatos();
        if (validarDatos() && guardarEnArchivo())
            irApaciente();
    }
    //--------------------------------
    private void obtenerDatos () {

        nombre = Utilerias.normalzarEntrada(miEtNombre.getText().toString());
        apellido = Utilerias.normalzarEntrada(miEtApellido.getText().toString());
        edad = miEtEdad.getText().toString();
        genero = "";

        if ( miRgGenero.getCheckedRadioButtonId() == R.id.miRbMasculino_agregarPaciente ) {
            genero = "M";
        }
        if (miRgGenero.getCheckedRadioButtonId() == R.id.miRbFemenino_agregarPaciente) {
            genero = "F";
        }

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        fecha = df.format(date);
    }
    //--------------------------------
    private boolean validarDatos () {

        if (nombre.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_nombreObligatorio), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Utilerias.validarEntradaDatos(nombre)) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_nombreNoValido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (apellido.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_apellidoPaternoObligatorio), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Utilerias.validarEntradaDatos(apellido)) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_apellidoPaternoNoValido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edad.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_edadObligatorio), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (genero.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.mensaje_generoObligatorio), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //--------------------------------
    private boolean guardarEnArchivo () {

        boolean todoOk = false;

        String directorioPrincipal = getResources().getString(R.string.archivo_directorioPrincipal);
        File rutaAlDirectorioPrincipal = new File(Environment.getExternalStorageDirectory() + directorioPrincipal);
        String nombreArchivo = getResources().getString(R.string.archivo_pacientes);
        File rutaAlArchivo = new File(rutaAlDirectorioPrincipal, nombreArchivo);

        miPaciente = new Paciente(nombre, apellido, edad, genero, fecha);

        String directorioPaciente = "/" + miPaciente.getDirectorioPaciente() + "/";
        File rutaAlDirectorioPaciente = new File(rutaAlDirectorioPrincipal + directorioPaciente);

        archivoPacientes = new ArrayList<>();


        //A. Si se puede leer y escribir en la memoria
        if (Utilerias.comprobarEstadoMemoria()) {
            Utilerias.crearDirectorio(rutaAlDirectorioPrincipal);
            //B. Si el archivo existe
            if (rutaAlArchivo.exists()) {
                //C. Si se puede leer el archivo
                if (Utilerias.leerArchivo(rutaAlArchivo, archivoPacientes)) {
                    if (validarArchivoPacientes()) {
                        if (!buscarPaciente()) {
                            agregarPacienteAlfabeticamente();
                            if (Utilerias.escribirArchivo(rutaAlArchivo, archivoPacientes)) { //se agrega el paciente al archivo de pacientes
                                Utilerias.crearDirectorio(rutaAlDirectorioPaciente); //se crea el directorio del paciente
                                todoOk = true;
                            }
                            else
                                Toast.makeText(this, getResources().getString(R.string.error_escrituraArchivo), Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(this, getResources().getString(R.string.mensaje_pacienteYaExiste), Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, getResources().getString(R.string.error_archivoCorrupto), Toast.LENGTH_SHORT).show();
                }
                //C. No se puede leer el archivo
                else
                    Toast.makeText(this, getResources().getString(R.string.error_lecturaArchivo), Toast.LENGTH_SHORT).show();
            }
            //B. Si el archivo no existe
            else {
                archivoPacientes.add(miPaciente.getCadenaDatos());
                //si no hay error al guardar los datos
                if (Utilerias.escribirArchivo(rutaAlArchivo, archivoPacientes)) { //se agrega el paciente al archivo de pacientes
                    Utilerias.crearDirectorio(rutaAlDirectorioPaciente); //se grea el directorio del paciente
                    todoOk = true;
                }
                else
                    Toast.makeText(this, getResources().getString(R.string.error_escrituraArchivo), Toast.LENGTH_SHORT).show();
            }
        }
        //A. No se puede leer ni escribir en la memoria
        else
            Toast.makeText(this, getResources().getString(R.string.error_memoria), Toast.LENGTH_SHORT).show();


        return todoOk;
    }
    //--------------------------------
    private boolean validarArchivoPacientes () {

        boolean todoOk = false;
        ArrayList<Paciente> arrayPacientes =new ArrayList<>();
        nombresPacientes = new ArrayList<>();
        String lineaTexto;
        int i;

        for (i=0; i<archivoPacientes.size(); i++) {
            lineaTexto = archivoPacientes.get(i);
            if (Utilerias.validarCadenaDelArchivoPacientes(lineaTexto)) {
                arrayPacientes.add (new Paciente(lineaTexto));
                nombresPacientes.add(arrayPacientes.get(i).getNombreCompleto().replace(" ", ""));
            }
        }
        if (i == archivoPacientes.size())
            todoOk = true;

        return todoOk;
    }
    //--------------------------------
    private boolean buscarPaciente () {
        boolean encontrado = false;
        String nuevoPaciente = miPaciente.getNombreCompleto().replace(" ", "");

        if (nombresPacientes.contains(nuevoPaciente))
            encontrado = true;

        return encontrado;
    }
    //--------------------------------
    private void agregarPacienteAlfabeticamente () {

        String nuevoPaciente = miPaciente.getNombreCompleto().replace(" ", "");
        int i=0;
        int posicion = -1;

        while ( (posicion == -1) & (i<nombresPacientes.size()) ) {
            if (nuevoPaciente.compareTo(nombresPacientes.get(i)) < 0)
                posicion = i;
            i++;
        }

        if ( posicion != -1 )
            archivoPacientes.add(posicion, miPaciente.getCadenaDatos());
        else
            archivoPacientes.add(miPaciente.getCadenaDatos());
    }

    //*********************************************************************************************

    private void irApaciente () {
        Intent intent = new Intent(getApplicationContext(), Patients.class);
        intent.putExtra("paciente", miPaciente.getCadenaDatos());
        intent.putExtra("posPaciente", String.valueOf(0));
        startActivity(intent);
        finish();
    }


}