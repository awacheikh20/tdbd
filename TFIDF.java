import java.io.*;
import java.util.*;

public class TFIDF {

    // Méthode pour lire un fichier ligne par ligne et extraire les mots
    public static List<String> readFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", ""); // Nettoyer les caractères spéciaux
                if (!line.trim().isEmpty()) {
                    words.addAll(Arrays.asList(line.split("\\s+"))); // Ajouter les mots
                }
            }
        }
        return words;
    }

    // Méthode pour calculer le TF (Term Frequency) d'un document
    public static Map<String, Double> calculateTF(List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        int totalWords = words.size();
        Map<String, Double> tf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            tf.put(entry.getKey(), entry.getValue() / (double) totalWords);
        }

        return tf;
    }

    // Méthode pour calculer l'IDF (Inverse Document Frequency) pour un corpus
    public static Map<String, Double> calculateIDF(List<List<String>> corpus) {
        Map<String, Integer> docCount = new HashMap<>();
        int totalDocs = corpus.size();

        // Compte le nombre de documents contenant chaque mot
        for (List<String> document : corpus) {
            Set<String> uniqueWords = new HashSet<>(document);
            for (String word : uniqueWords) {
                docCount.put(word, docCount.getOrDefault(word, 0) + 1);
            }
        }

        // Calcule l'IDF
        Map<String, Double> idf = new HashMap<>();
        for (Map.Entry<String, Integer> entry : docCount.entrySet()) {
            idf.put(entry.getKey(), Math.log((double) totalDocs / (1 + entry.getValue())));
        }

        return idf;
    }

    // Méthode pour calculer le TF-IDF d'un document
    public static Map<String, Double> calculateTFIDF(Map<String, Double> tf, Map<String, Double> idf) {
        Map<String, Double> tfidf = new HashMap<>();
        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String word = entry.getKey();
            double tfidfValue = entry.getValue() * idf.getOrDefault(word, 0.0);
            tfidf.put(word, tfidfValue);
        }
        return tfidf;
    }

    public static void main(String[] args) {
        try {
            // Lecture des fichiers (exemple avec un seul fichier pour simplifier)
            String[] filePaths = {"purchases.txt"};
            List<List<String>> corpus = new ArrayList<>();

            // Lire chaque fichier
            for (String filePath : filePaths) {
                corpus.add(readFile(filePath));
            }

            // Calcul de l'IDF pour tout le corpus
            Map<String, Double> idf = calculateIDF(corpus);

            // Calcul du TF-IDF pour chaque document
            for (int i = 0; i < filePaths.length; i++) {
                System.out.println("Document: " + filePaths[i]);
                List<String> words = corpus.get(i);
                Map<String, Double> tf = calculateTF(words);
                Map<String, Double> tfidf = calculateTFIDF(tf, idf);

                // Affiche le TF-IDF pour chaque mot
                tfidf.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // Tri par ordre décroissant
                        .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
                System.out.println();
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des fichiers : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
        }
    }
}