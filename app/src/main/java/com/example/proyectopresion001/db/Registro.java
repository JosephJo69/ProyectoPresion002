package com.example.proyectopresion001.db;

public class Registro {
    private long id;
    private int sistolica;
    private Integer diastolica;
    private Integer edad;
    private String fecha;

    public Registro() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getSistolica() { return sistolica; }
    public void setSistolica(int sistolica) { this.sistolica = sistolica; }

    public Integer getDiastolica() { return diastolica; }
    public void setDiastolica(Integer diastolica) { this.diastolica = diastolica; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
