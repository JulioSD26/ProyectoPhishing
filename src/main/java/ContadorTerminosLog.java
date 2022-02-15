import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContadorTerminosLog extends SimpleFileVisitor<Path> {
    public static final Logger Log = Logger.getLogger(ContadorTerminosLog.class.getName());

    private boolean showWords;

    private Set<String> termSet;

    private Set<String> stopWords = null;

    public ContadorTerminosLog(boolean showWords) {
        this.showWords = showWords;
        termSet = new TreeSet<>();

        stopWords = this.loadStopWords();

        try {
            Handler fileHandler = new FileHandler("results.log", 2000, 5);

            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new CsvFormatter() );
            Log.addHandler(fileHandler);
        } catch (IOException e) {
            Log.severe(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.exit(2);
        }

        boolean showWords = false;
        if(args.length == 2){
            if(args[1].toLowerCase().equals("-v")){
                showWords = true;
            }
        }

        Path staringDir = Paths.get(args[0]);

        ContadorTerminosLog contadorLineas = new ContadorTerminosLog(showWords);

        Files.walkFileTree(staringDir, contadorLineas);
    }

    public FileVisitResult proVisitDirectory(Path dir, BasicFileAttributes attrs){
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
        String name = file.toAbsolutePath().toString();
        if (name.toLowerCase().endsWith(".txt")){
            FileReader fl = null;
            BufferedReader in = null;

            fl = new FileReader(name);
            in = new BufferedReader(fl);

            int lineCount = 0;
            int wordCount = 0;
            int numberCount = 0;

            final String delimiters = "\\s+|,\\s*|\\.\\s*|\\;\\s*|\\:\\s*|\\!\\s*|\\¡\\s*|\\¿\\s*|\\?\\s*|\\-\\s*"
                    + "|\\[\\s*|\\]\\s*|\\(\\s*|\\)\\s*|\\\"\\s*|\\_\\s*|\\%\\s*|\\+\\s*|\\/\\s*|\\#\\s*|\\$\\s*";
            Set<String> wordSet = new TreeSet<>();

            String textLine = null;

            long startTime = System.currentTimeMillis();

            while((textLine = in.readLine()) != null){
                lineCount++;
                if (textLine.trim().length() == 0) {
                    continue;
                }
                
                String words[] = textLine.split(delimiters);
                
                wordCount += words.length;
                for (String theWord : words){
                    boolean isNumeric = true;
                    String term = theWord.trim().toLowerCase();
                    
                    try{
                        Double num = Double.parseDouble(term);
                    }catch(NumberFormatException e){
                        isNumeric = false;
                    }

                    if(isNumeric){
                        numberCount++;
                        continue;
                    }
                    if(!stopWords.contains(term)){
                        wordSet.add(term);
                    }
                    
                }
            }

            long tiempoEjecucion = System.currentTimeMillis() - startTime;
            in.close();
            System.out.printf("%s %2.3f segundos %n\t%,d lineas y %,d palabras ", name, (tiempoEjecucion /1000), lineCount, wordCount - numberCount);

            System.out.printf("(%,d diferentes %4.2f)\n", wordSet.size(), (double) wordSet.size() / (wordCount - numberCount));
            String record = String.format("%s,%6.3f, %d, %d", name, (double)(tiempoEjecucion /1000.00), wordCount - numberCount, wordSet.size());

            Log.info(record);
            if (showWords){
                displayWords(wordSet);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    private void displayWords(Set<String> words){
        for (String theWord : words){
            System.out.printf("%s ", theWord);
        }
        System.out.println();
    }

    private Set<String> loadStopWords(){
        TreeSet<String> set = new TreeSet<>();

        try{
            BufferedReader in = new BufferedReader(new FileReader("stop-word-list.txt"));
            String word = null;
            while((word = in.readLine()) != null){
                set.add(word.trim());
            }
            in.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return set;
    }
}
