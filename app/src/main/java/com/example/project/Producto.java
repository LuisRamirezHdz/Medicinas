package com.example.project;

public class Producto {
    private String ID,nombre,marca,peso_inicial,peso_actual,caducidad;

    public Producto(String ID, String nombre, String marca, String peso_inicial, String peso_actual, String caducidad) {
        this.ID = ID;
        this.nombre = nombre;
        this.marca = marca;
        this.peso_inicial = peso_inicial;
        this.peso_actual = peso_actual;
        this.caducidad = caducidad;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPeso_inicial() {
        return peso_inicial;
    }

    public void setPeso_inicial(String peso_inicial) {
        this.peso_inicial = peso_inicial;
    }

    public String getPeso_actual() {
        return peso_actual;
    }

    public void setPeso_actual(String peso_actual) {
        this.peso_actual = peso_actual;
    }

    public String getCaducidad() {
        return caducidad;
    }

    public void setCaducidad(String caducidad) {
        this.caducidad = caducidad;
    }
}
