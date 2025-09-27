package com.example.proyectopresion001.db;

public class Registro {
    private long id;
    private int sistolica;
    private int diastolica;
    private int edad;
    private String fecha;

    public Registro() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getSistolica() { return sistolica; }
    public void setSistolica(int sistolica) { this.sistolica = sistolica; }

    public int getDiastolica() { return diastolica; }
    public void setDiastolica(int diastolica) { this.diastolica = diastolica; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
