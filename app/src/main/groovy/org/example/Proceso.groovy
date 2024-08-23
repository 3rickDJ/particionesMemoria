package org.example

// Proceso.groovy

class Proceso {
    String nombre
    int tiempoEjecucion // en segundos
    int tamaño // tamaño en MB

    Proceso(String nombre, int tiempoEjecucion, int tamaño) {
        this.nombre = nombre
        this.tiempoEjecucion = tiempoEjecucion
        this.tamaño = tamaño
    }

    String toString() {
        return "Proceso($nombre, $tiempoEjecucion seg, ${tamaño}MB)"
    }
}
