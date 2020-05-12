package com.example.project.modelo;

public class Medicamento {
    private String ID;
    private String nombre;
    private String indicacionTerapeutica;
    private String dosis;
    private String vecesAlDia;
    private String hora;
    private String periodoEnDias;
    private String nombreDr;

    public Medicamento() {
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

    public String getIndicacionTerapeutica() {
        return indicacionTerapeutica;
    }

    public void setIndicacionTerapeutica(String indicacionTerapeutica) {
        this.indicacionTerapeutica = indicacionTerapeutica;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getVecesAlDia() {
        return vecesAlDia;
    }

    public void setVecesAlDia(String vecesAlDia) {
        this.vecesAlDia = vecesAlDia;
    }
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPeriodoEnDias() {
        return periodoEnDias;
    }

    public void setPeriodoEnDias(String periodoEnDias) {
        this.periodoEnDias = periodoEnDias;
    }

    public String getNombreDr() {
        return nombreDr;
    }

    public void setNombreDr(String nombreDr) {
        this.nombreDr = nombreDr;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
