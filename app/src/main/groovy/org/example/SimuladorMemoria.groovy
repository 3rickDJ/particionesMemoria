package org.example
// SimuladorMemoria.groovy

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SimuladorMemoria {
    static void main(String[] args) {
        // Lista de procesos a ejecutar
        def procesos = [
                new Proceso("Proceso1", 5, 50),
                new Proceso("Proceso2", 3, 30),
                new Proceso("Proceso3", 4, 40),
                new Proceso("Proceso4", 2, 20)
        ]

        // Lista de compartimientos de memoria
        def compartimientos = [
                new ParticionMemoria("Compartimiento1", 60),
                new ParticionMemoria("Compartimiento2", 40),
                new ParticionMemoria("Compartimiento3", 30)
        ]

        // Crear un pool de hilos
        def pool = Executors.newFixedThreadPool(compartimientos.size())

        // Asignar procesos a compartimientos de memoria disponibles
        procesos.each { proceso ->
            synchronized (compartimientos) {
                def particion = compartimientos.find { it.libre && it.tamaño >= proceso.tamaño }
                if (particion) {
                    particion.libre = false // Marcar la partición como ocupada
                    pool.submit { ejecutarProceso(proceso, particion) }
                } else {
                    println "No hay compartimiento de memoria disponible para ${proceso.nombre}"
                }
            }
        }

        pool.shutdown()
        pool.awaitTermination(1, TimeUnit.HOURS)
    }

    // Ejecutar un proceso en un compartimiento de memoria usando un hilo
    static void ejecutarProceso(Proceso proceso, ParticionMemoria particion) {
        println "Iniciando ${proceso.nombre} en ${particion.nombre}"
        try {
            TimeUnit.SECONDS.sleep(proceso.tiempoEjecucion)
        } catch (InterruptedException e) {
            e.printStackTrace()
        } finally {
            synchronized (particion) {
                particion.libre = true // Liberar la partición de memoria
                println "${proceso.nombre} ha terminado. ${particion.nombre} ahora está libre."
            }
        }
    }
}
