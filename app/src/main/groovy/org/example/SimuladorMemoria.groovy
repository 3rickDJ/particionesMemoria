package org.example
// SimuladorMemoria.groovy

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.ConcurrentLinkedDeque


class SimuladorMemoria {
    static void main(String[] args) {

        // Cola concurrente de procesos
        ConcurrentLinkedDeque<Proceso> procesos = new ConcurrentLinkedDeque<Proceso>([
                new Proceso("Proceso1", 10, 50),
                new Proceso("Proceso2", 20, 30),
                new Proceso("Proceso3", 20, 30),
                new Proceso("Proceso4", 20, 20),
                new Proceso("Proceso5", 10, 60)
        ])

        // Lista de compartimientos de memoria
        ArrayList<ParticionMemoria> compartimientos = [
                new ParticionMemoria("Compartimiento1", 60),
                new ParticionMemoria("Compartimiento2", 40),
                new ParticionMemoria("Compartimiento3", 30)
        ]

        // Crear un pool de hilos basado en el número de compartimientos
        def pool = Executors.newFixedThreadPool(compartimientos.size())

        // Ciclo para asignar procesos a compartimientos de memoria disponibles
        while (!procesos.isEmpty()) {
            Proceso proceso = procesos.poll() // Obtener el siguiente proceso de la cola
            println(proceso)
            if (proceso != null) {
                synchronized (compartimientos) {
                    // Encontrar una partición de memoria libre que pueda acomodar el proceso
                    def particion = compartimientos.find { it.libre && it.tamaño >= proceso.tamaño }
                    if (particion) {
                        particion.libre = false // Marcar la partición como ocupada
                        pool.submit { ejecutarProceso(proceso, particion) } // Ejecutar el proceso en el pool
                    } else {
                        procesos.addFirst(proceso) // Reagregar el proceso a la cola si no hay particiones disponibles
                    }
                }
            }
            TimeUnit.MILLISECONDS.sleep(100) // Esperar antes de intentar nuevamente
        }

        // Cerrar el pool de hilos una vez que todas las tareas han sido asignadas
        pool.shutdown()
        pool.awaitTermination(1, TimeUnit.HOURS)
    }

    static void ejecutarProceso(Proceso proceso, ParticionMemoria particion) {
        println "\tIniciando ${proceso.nombre} en ${particion.nombre}"
        try {
            TimeUnit.SECONDS.sleep(proceso.tiempoEjecucion) // Simular el tiempo de ejecución del proceso
        } catch (InterruptedException e) {
            e.printStackTrace()
        } finally {
            synchronized (particion) {
                particion.libre = true // Liberar la partición de memoria
                println "\t\t${proceso.nombre} ha terminado. ${particion.nombre} ahora está libre."
            }
        }
    }
}
