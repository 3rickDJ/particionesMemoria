// Definir el directorio donde se crearán los archivos
def inputDir = new File("${project.projectDir}/src/main/resources/")

// Crear el directorio si no existe
if (!inputDir.exists()) {
    inputDir.mkdirs()
}

def seconds = 1..60
def kb_size = 2..1000
def lista = (1..900).collect {
    def s = seconds.shuffled().first()
    def k = kb_size.shuffled().first()
    "${it},${s},${k}"
}

def fileContent = lista.join('\n')

// Crear y escribir en el archivo
def inputFile = new File(inputDir, "listaProcesos.csv")
inputFile.text = fileContent

println "Archivo de entrada creado en: ${inputFile.absolutePath}"
