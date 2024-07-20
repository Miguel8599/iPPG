package com.example.biosignals;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class Patients extends AppCompatActivity {

    private ArrayList<String> archivoPacientes;
    private ArrayList<Paciente> arrayPacientes;
    private ArrayList<String> nombresPacientes;
    private TextView miTv;
    private Button btnaddp;
    boolean versionOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        createAppBar(R.id.miAppBar_patients, R.color.colorPrimary, false);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getPermission2();
        } else {
            getPermission();
        }

        if(Build.VERSION.SDK_INT >= 27)
        {
            versionOk = true;
        }
        else
            Toast.makeText(Patients.this,R.string.version, Toast.LENGTH_LONG).show();


        inicializarElementosGraficos();
        cargarArchivoPacientes();

        btnaddp = (Button) findViewById(R.id.btnaddp);
        btnaddp.setOnClickListener(v -> irAagregarPaciente(v));

        String ruta = Environment.getExternalStorageDirectory() + "/Biosignals";
        File f = new File(ruta);
        Utilerias.crearDirectorio(f);



    }

    //==============================================================================================
    private void inicializarElementosGraficos ()
    {
        miTv = (TextView) findViewById(R.id.miTvMensaje_principal);

    }

    //==============================================================================================
    private void irApaciente (int posicion) {
        int permCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        if(permCamera == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(getApplicationContext(), BiosignalsCalc.class);
            intent.putExtra("paciente", arrayPacientes.get(posicion).getCadenaDatos());
            intent.putExtra("posPaciente", String.valueOf(posicion +1 ));
            startActivity(intent);
            finish();
        } else {
            getPermission3();
            Toast.makeText(Patients.this,R.string.permission, Toast.LENGTH_LONG).show();
        }
    }

    //==============================================================================================
    private void irAagregarPaciente (View v) {
        int permCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        if(permCamera == PackageManager.PERMISSION_GRANTED)
        {
            Intent intent = new Intent(getApplicationContext(), AddPatient.class);
            startActivity(intent);
            finish();
        } else {
            getPermission3();
            Toast.makeText(Patients.this,R.string.permission, Toast.LENGTH_LONG).show();
        }
    }

    //Lee el archivo de los pacientes que existen
    private void cargarArchivoPacientes () {

        String directorioPrincipal = getResources().getString(R.string.archivo_directorioPrincipal);
        File rutaAlDirectorioPrincipal = new File(Environment.getExternalStorageDirectory() + directorioPrincipal);
        if (!rutaAlDirectorioPrincipal.exists()) {
            rutaAlDirectorioPrincipal.mkdir();
        }

        String nombreArchivo = getResources().getString(R.string.archivo_pacientes);
        File rutaAlArchivo = new File(rutaAlDirectorioPrincipal, nombreArchivo);

        archivoPacientes = new ArrayList<>(); //Tipo String

        //A. Si se puede leer y escribir en la memoria
        if (Utilerias.comprobarEstadoMemoria()) {
            //B. Si el archivo existe
            if (rutaAlArchivo.exists()) {
                //C. Si se puede leer el archivo
                if (Utilerias.leerArchivo(rutaAlArchivo, archivoPacientes)) {
                    //D. Si el archivo tiene almenos un elemento correcto
                    if(validarArchivo()) {
                        miTv.setText("");
                        desplegarListaPacientes();
                    }
                    //D. El archivo esta vacio o no tiene ningun elemento valido
                    else
                        miTv.setText(getResources().getString(R.string.mensaje_agregarPaciente));
                }
                //C. No se puede leer el archivo
                else
                    miTv.setText(getResources().getString(R.string.error_lecturaArchivo));
            }
            //B. Si el archivo no existe
            else
                miTv.setText(getResources().getString(R.string.mensaje_agregarPaciente));
        }
        //A. No se puede leer ni escribir en la memoria
        else
            miTv.setText(getResources().getString(R.string.error_memoria));

    } //Fin metodo cargarArchivoPacientes
    //----------------------------------------------------
    //Se valida que exista algun paciente para trabajar con el
    private boolean validarArchivo () {

        boolean todoOk = false;
        arrayPacientes = new ArrayList<>(); //Tipo Paciente
        nombresPacientes = new ArrayList<>(); //Tipo String
        String lineaTexto;


        int i = 0;
        if (archivoPacientes.size() > 0) { //Si el archivo tiene elementos
            for (i=0; i<archivoPacientes.size(); i++) {
                lineaTexto = archivoPacientes.get(i);
                if (Utilerias.validarCadenaDelArchivoPacientes(lineaTexto)) {
                    arrayPacientes.add (new Paciente(lineaTexto));
                    nombresPacientes.add(arrayPacientes.get(i).getNombreCompleto());
                }
            }
        }

        if (arrayPacientes.size() > 0) //tiene almenos un elemento correcto
            todoOk = true;
        if (i != arrayPacientes.size())
            Toast.makeText(this, getResources().getString(R.string.error_archivoCorrupto), Toast.LENGTH_SHORT).show();

        return todoOk;
    }
    //----------------------------------------------------
    private void desplegarListaPacientes() {

        ListView miListaPacientes = (ListView) findViewById(R.id.miListaPacientes_principal);


        if(miListaPacientes != null)
        {
            AdaptadorPaciente adapter = new AdaptadorPaciente(getApplicationContext(), arrayPacientes);
            miListaPacientes.setAdapter(adapter);

            miListaPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l)
                {
                    irApaciente(posicion);
                }
            });
        }
    }


    //==============================================================================================
    private void createAppBar(int appBarLayaout, int colorFondoAppBar, boolean upBoton) {
        Toolbar miAppBar = (Toolbar) findViewById(appBarLayaout);
        setSupportActionBar(miAppBar);
        miAppBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorFondoAppBar));
        miAppBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        String title = getResources().getString(R.string.patients);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upBoton);
    }

    //==============================================================================================

    private boolean getPermission3 ()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        return true;
    }

    //==============================================================================================

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //==============================================================================================
    private boolean getPermission ()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        return true;
    }

    //==============================================================================================
    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean getPermission2 ()
    {

        if(!Environment.isExternalStorageManager()){
            Toast.makeText(Patients.this,"Please, give the management permission to the Biosignals app", Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 0);
        }


        return true;
    }


}