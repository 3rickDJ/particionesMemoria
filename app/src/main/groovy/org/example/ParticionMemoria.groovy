package org.example
// ParticionMemoria.groovy

class ParticionMemoria {
    String nombre
    int tamaño // tamaño en MB
    boolean libre = true // inicialmente libre

    ParticionMemoria(String nombre, int tamaño) {
        this.nombre = nombre
        this.tamaño = tamaño
    }

    String toString() {
        return "Particion($nombre, ${tamaño}MB, libre: $libre)"
    }
}
