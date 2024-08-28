package org.example

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SimuladorMemoria {
    static void main(String[] args) {

        def lista = SimuladorMemoria.class.getResource('/listaProcesos.csv').text.split('\n').collect({ line ->
            def fields = line.split(',')
            new Proceso(fields[0], fields[1] as int, fields[2] as int)
        })
        println lista

        // Lista de procesos
        ArrayList<Proceso> procesos = new ArrayList<Proceso>(lista)

        // Lista de compartimientos de memoria
        ArrayList<ParticionMemoria> compartimientos = [
                new ParticionMemoria("Compartimiento1", 100),
                new ParticionMemoria("Compartimiento2", 500),
                new ParticionMemoria("Compartimiento3", 1000)
        ]

        // Crear un pool de hilos basado en el número de compartimientos
        def pool = Executors.newFixedThreadPool(compartimientos.size())

        // Ciclo para asignar procesos a compartimientos de memoria disponibles
        while (!procesos.isEmpty()) {
            Proceso proceso
            synchronized (procesos) {
                if (!procesos.isEmpty()) {
                    proceso = procesos.remove(0) // Obtener y eliminar el primer proceso de la lista
                }
            }
            if (proceso != null) {
                println procesos
                synchronized (compartimientos) {
                    // Encontrar una partición de memoria libre que pueda acomodar el proceso
                    def particion = compartimientos.find { it.libre && it.tamaño >= proceso.tamaño }
                    if (particion) {
                        particion.libre = false // Marcar la partición como ocupada
                        pool.submit { ejecutarProceso(proceso, particion) } // Ejecutar el proceso en el pool
                    } else {
                        synchronized (procesos) {
                            procesos.add(1,proceso) // Reagregar el proceso a la lista si no hay particiones disponibles
                        }
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
