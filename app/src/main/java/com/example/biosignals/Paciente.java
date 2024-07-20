package com.example.biosignals;

/**
 * Created by nemesis on 07/08/2016.
 */
public class Paciente {

    private String nombre; //nombre(s)
    private String apellido;
    private String edad;
    private String genero;
    private String fechaRegistro;
    private String nombreSimple; //El primer nombre
    private String cadenaDatos; //cadena: nombre::apellidoPAterno::apellidoMaterno::edad::genero::fechaRegistro = 6
    private String nombreCompleto; //nombre(s) apellidoP apellidoM
    private String directorioPaciente; //PacienteFulano

    public Paciente(String cadenaDatos) { //cadena: nombre::apellidoPAterno::apellidoMaterno::edad::genero::fechaRegistro = 6
        String [] aux1;
        String [] auxFecha2;

        this.cadenaDatos = cadenaDatos;
        aux1 = cadenaDatos.split("::");
        this.nombre = aux1[0];
        this.nombreSimple = obtenerNombreSimple();
        this.apellido = aux1[1];
        this.edad = aux1[2];
        this.genero = aux1[3];

        String auxFecha = aux1[4];
        auxFecha2 = auxFecha.split("/");
        this.fechaRegistro = auxFecha2[2] + "/" + auxFecha2[1] + "/" + auxFecha2[0];
        this.nombreCompleto = this.nombre + " " + this.apellido;
        obtenerDirectorioDelPaciente();
    }

    public Paciente(String nombre, String apellido, String edad, String genero, String fechaRegistro) {
        this.nombre = nombre;
        this.nombreSimple = obtenerNombreSimple();
        this.apellido= apellido;
        this.edad = edad;
        this.genero = genero;
        this.fechaRegistro = fechaRegistro;
        this.nombreCompleto = this.nombre + " " + this.apellido;
        this.cadenaDatos = this.nombre + "::" + this.apellido + "::" + this.edad +
                "::" + this.genero + "::" + this.fechaRegistro;
        obtenerDirectorioDelPaciente();
    }

    private String obtenerNombreSimple () {
        String [] nombreSimple;
        nombreSimple = this.nombre.split(" ");
        return nombreSimple[0];
    }

    private void obtenerDirectorioDelPaciente () {
        String enMinuscula;
        String [] enMinusculaSplit;
        char caracter;
        String primerCaracter;
        String cadena;
        String directorio = "";
        this.directorioPaciente = "";

        enMinuscula = this.nombreCompleto.toLowerCase();
        enMinusculaSplit = enMinuscula.split(" ");
        for (int i=0; i<enMinusculaSplit.length; i++) {
            //obtiene el primer caracter
            caracter = enMinusculaSplit[i].charAt(0);
            primerCaracter = String.valueOf(caracter);
            //converte carcteres a mayuscula
            primerCaracter = primerCaracter.toUpperCase();
            //concatena la cadena
            cadena = primerCaracter.concat(enMinusculaSplit[i].substring(1));
            directorio = directorio + cadena;
        }
        this.directorioPaciente = directorio;
    }


    public String getNombre() {
        return nombre;
    }

    public String getNombreSimple() {
        return nombreSimple;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public String getCadenaDatos() {
        return cadenaDatos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getDirectorioPaciente() {
        return directorioPaciente;
    }


} //Cierre de la clase
