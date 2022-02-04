import java.io.*;
import java.util.LinkedList;

public class EncontrarPalabrasDiferentes {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Falta el nombre de archivo");
            System.exit(0);
        } else {
            File carpeta = new File(args[0]);
            if (!carpeta.isDirectory()){
                System.out.println("No es un directorio");
                System.exit(1);
            }else{
                // si el usuario ingreso el comando -v muestra las palabras
                if(args.length == 2 && args[args.length-1].equals("-v")){
                    // mostrar las palabras
                    boolean mostrarPalabras = true;
                    PalabrasDiferentes(carpeta, mostrarPalabras);

                }else{
                    boolean mostrarPalabras = false;
                    PalabrasDiferentes(carpeta, mostrarPalabras);
                }

            }
        }
    }

    public static void PalabrasDiferentes(File carpeta, boolean mostrarPalabras){
        for (File FicheroEntrada : carpeta.listFiles()){
            // mostrar el nombre del archivo
            System.out.println(FicheroEntrada.getName());

            // crear un FileReader para leer el contenido de los archivos
            FileReader fi = null;

            try {
                fi = new FileReader(FicheroEntrada);
            } catch (FileNotFoundException e) {
                System.out.println("Error en leer el archivo\n"+ e);
            }

            BufferedReader inputFile = new BufferedReader(fi);

            // Declaramos las variables para conteo de:
            // Lineas, palabras y contador

            String textLine = null;
            int lineCount=0, wordCount=0, numberCount=0;

            // Delimitadores
            String delimiters = "\\s+|,\\s*|\\.\\s*|\\;\\s*|\\:\\s*|\\!\\s*|\\¡\\s*|\\¿\\s*|\\?\\s*|\\-\\s*"
                    + "|\\[\\s*|\\]\\s*|\\(\\s*|\\)\\s*|\\\"\\s*|\\_\\s*|\\%\\s*|\\+\\s*|\\/\\s*|\\#\\s*|\\$\\s*";

            // Lista con todas las palabras diferentes
            LinkedList<String> list = new LinkedList<String>();

            // Tiempo inicial
            long startTime = System.currentTimeMillis();
            try {
                while ((textLine = inputFile.readLine()) != null) {
                    lineCount++;

                    if (textLine.trim().length() == 0) {
                        continue; // la linea esta vacia, continuar
                    }

                    // separar las palabras en cada linea
                    String words[] = textLine.split(delimiters);

                    wordCount += words.length;

                    for (String theWord : words) {

                        theWord = theWord.toLowerCase().trim();

                        boolean isNumeric = true;

                        // verificar si el token es un numero
                        try {
                            Double num = Double.parseDouble(theWord);
                        } catch (NumberFormatException e) {
                            isNumeric = false;
                        }

                        // Si el token es un numero, pasar al siguiente
                        if( isNumeric ) {
                            numberCount++;
                            continue;
                        }

                        // si la palabra no esta en la lista, agregar a la lista
                        if ( !list.contains(theWord) ) {
                            list.add( theWord );
                        }
                    }
                }
                // Obtener tiempo de ejecución
                long tiempoEjecucion = System.currentTimeMillis() - startTime;
                inputFile.close();
                fi.close();

                System.out.printf("%2.3f  segundos, %2d lineas y %3d palabras\n",
                        tiempoEjecucion / 1000.00, lineCount, wordCount - numberCount);

                // Mostrar total de palabras diferentes
                System.out.printf("%5d palabras diferentes\n", list.size() );


                // mostrar cada palabra
                if(mostrarPalabras){
                    for (String word : list) {
                    System.out.println(word);
                    }
                }

            } catch (IOException ex) {
                System.out.println( ex.getMessage() );
            }
        }

    }

}
