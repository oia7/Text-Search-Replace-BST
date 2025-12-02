import java.io.*;
import java.util.*;

// BST Node class to store words and their positions
class BSTNode {
    String word;
    List<Integer> positions; // Stores line numbers where word appears
    BSTNode left, right;
    
    public BSTNode(String word, int lineNumber) {
        this.word = word;
        this.positions = new ArrayList<>();
        this.positions.add(lineNumber);
        left = right = null;
    }
    
    public void addPosition(int lineNumber) {
        if (!positions.contains(lineNumber)) {
            positions.add(lineNumber);
        }
    }
}

// Binary Search Tree implementation
class WordBST {
    BSTNode root;
    
    public WordBST() {
        root = null;
    }
    
    // Insert a word into BST
    public void insert(String word, int lineNumber) {
        root = insertRec(root, word.toLowerCase(), lineNumber);
    }
    
    private BSTNode insertRec(BSTNode node, String word, int lineNumber) {
        if (node == null) {
            return new BSTNode(word, lineNumber);
        }
        
        int compare = word.compareTo(node.word);
        
        if (compare < 0) {
            node.left = insertRec(node.left, word, lineNumber);
        } else if (compare > 0) {
            node.right = insertRec(node.right, word, lineNumber);
        } else {
            // Word already exists, add position
            node.addPosition(lineNumber);
        }
        
        return node;
    }
    
    // Search for a word in BST
    public BSTNode search(String word) {
        return searchRec(root, word.toLowerCase());
    }
    
    private BSTNode searchRec(BSTNode node, String word) {
        if (node == null || node.word.equals(word)) {
            return node;
        }
        
        if (word.compareTo(node.word) < 0) {
            return searchRec(node.left, word);
        }
        
        return searchRec(node.right, word);
    }
    
    // Check if word exists
    public boolean contains(String word) {
        return search(word) != null;
    }
    
    // Get total unique words in BST
    public int getUniqueWordCount() {
        return countNodes(root);
    }
    
    private int countNodes(BSTNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    
    // Display BST in-order (sorted words)
    public void displayInOrder() {
        System.out.println("\n Unique Words in File (Sorted Alphabetically):");
        System.out.println("=".repeat(50));
        displayInOrderRec(root);
    }
    
    private void displayInOrderRec(BSTNode node) {
        if (node != null) {
            displayInOrderRec(node.left);
            System.out.printf("%-15s : Appears in lines %s%n", 
                node.word, node.positions);
            displayInOrderRec(node.right);
        }
    }
    
    // Get all words for statistics
    public void printStatistics() {
        System.out.println("\n File Statistics:");
        System.out.println("=".repeat(50));
        System.out.println("Total unique words: " + getUniqueWordCount());
        System.out.println("Tree height: " + getHeight(root));
    }
    
    private int getHeight(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }
}

// Main file processor class
class FileProcessor {
    private WordBST wordTree;
    private String inputFilePath;
    private String outputFilePath;
    
    public FileProcessor() {
        wordTree = new WordBST();
    }
    
    // Build BST from file
    public void buildBSTFromFile(String filePath) throws IOException {
        this.inputFilePath = filePath;
        System.out.println(" Building BST from file: " + filePath);
        
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int lineNumber = 1;
        
        while ((line = reader.readLine()) != null) {
            // Split line into words (considering punctuation)
            String[] words = line.split("\\s+");
            
            for (String word : words) {
                // Remove punctuation and convert to lowercase
                String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (!cleanWord.isEmpty()) {
                    wordTree.insert(cleanWord, lineNumber);
                }
            }
            lineNumber++;
        }
        
        reader.close();
        System.out.println("BST built successfully!");
        System.out.println(" Processed " + (lineNumber-1) + " lines");
    }
    
    // Search and replace in file
    public void searchAndReplace(String searchWord, String replaceWord) throws IOException {
        System.out.println("\n Searching for: \"" + searchWord + "\"");
        System.out.println("Replacing with: \"" + replaceWord + "\"");
        
        // Check if word exists in BST
        BSTNode foundNode = wordTree.search(searchWord.toLowerCase());
        
        if (foundNode == null) {
            System.out.println("Word \"" + searchWord + "\" not found in file!");
            return;
        }
        
        System.out.println("Found \"" + searchWord + "\" in lines: " + foundNode.positions);
        
        // Read original file and create modified content
        BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
        List<String> modifiedLines = new ArrayList<>();
        String line;
        int lineNumber = 1;
        int replacementCount = 0;
        
        while ((line = reader.readLine()) != null) {
            String modifiedLine = line.replaceAll("\\b" + searchWord + "\\b", replaceWord);
            
            // Count replacements in this line
            int count = countReplacements(line, modifiedLine, searchWord);
            replacementCount += count;
            
            if (count > 0) {
                System.out.println("   Line " + lineNumber + ": Replaced " + count + " occurrence(s)");
            }
            
            modifiedLines.add(modifiedLine);
            lineNumber++;
        }
        
        reader.close();
        
        System.out.println("\n Total replacements made: " + replacementCount);
        
        // Ask for output file
        if (outputFilePath == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("\n Enter output file path: ");
            outputFilePath = scanner.nextLine();
        }
        
        // Write to output file
        writeOutputFile(modifiedLines);
        System.out.println(" Modified content saved to: " + outputFilePath);
    }
    
    private int countReplacements(String original, String modified, String searchWord) {
        // Simple way to count differences (for exact word matches)
        String[] originalWords = original.split("\\s+");
        String[] modifiedWords = modified.split("\\s+");
        
        int count = 0;
        for (int i = 0; i < Math.min(originalWords.length, modifiedWords.length); i++) {
            if (!originalWords[i].equals(modifiedWords[i]) && 
                originalWords[i].equals(searchWord)) {
                count++;
            }
        }
        return count;
    }
    
    private void writeOutputFile(List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
        
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        
        writer.close();
    }
    
    // Set output file path
    public void setOutputFilePath(String path) {
        this.outputFilePath = path;
    }
    
    // Get the BST for display
    public WordBST getWordTree() {
        return wordTree;
    }
    
    // Display file content
    public void displayFileContent(String filePath) throws IOException {
        System.out.println("\n File Content:");
        System.out.println("=".repeat(50));
        
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int lineNum = 1;
        
        while ((line = reader.readLine()) != null) {
            System.out.printf("%2d: %s%n", lineNum, line);
            lineNum++;
        }
        
        reader.close();
        System.out.println("=".repeat(50));
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileProcessor processor = new FileProcessor();
        
        System.out.println("=".repeat(60));
        System.out.println(" SEARCH AND REPLACE USING BINARY SEARCH TREE");
        System.out.println("=".repeat(60));
        
        try {
            // Get input file
            System.out.print("\n Enter input file path (or press Enter for default): ");
            String inputPath = scanner.nextLine().trim();
            
            if (inputPath.isEmpty()) {
                inputPath = createSampleFile();
                System.out.println(" Created sample file: " + inputPath);
            }
            
            // Display original content
            processor.displayFileContent(inputPath);
            
            // Build BST
            processor.buildBSTFromFile(inputPath);
            
            // Display BST information
            WordBST tree = processor.getWordTree();
            tree.displayInOrder();
            tree.printStatistics();
            
            // Get search and replace words
            System.out.println("\n" + "=".repeat(50));
            System.out.println(" SEARCH AND REPLACE OPERATION");
            System.out.println("=".repeat(50));
            
            System.out.print(" Enter word to search: ");
            String searchWord = scanner.nextLine();
            
            System.out.print(" Enter replacement word: ");
            String replaceWord = scanner.nextLine();
            
            // Get output file
            System.out.print(" Enter output file path (or press Enter for 'output.txt'): ");
            String outputPath = scanner.nextLine().trim();
            
            if (!outputPath.isEmpty()) {
                processor.setOutputFilePath(outputPath);
            }
            
            // Perform search and replace
            processor.searchAndReplace(searchWord, replaceWord);
            
            // Display results
            System.out.println("\n" + "=".repeat(50));
            System.out.println(" OPERATION COMPLETE");
            System.out.println("=".repeat(50));
            
            // Show original vs modified
            System.out.println("\n COMPARISON:");
            System.out.println("\nORIGINAL FILE:");
            processor.displayFileContent(inputPath);
            
            String outputFile = outputPath.isEmpty() ? "output.txt" : outputPath;
            System.out.println("\nMODIFIED FILE:");
            processor.displayFileContent(outputFile);
            
            // Show BST after operation (would be same unless we rebuild)
            System.out.println("\n Final BST Statistics:");
            tree.printStatistics();
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    // Create a sample file for testing
    private static String createSampleFile() throws IOException {
        String content = "hello world\nthis is a simple test\nhello Java\nhello again world";
        String fileName = "sample_input.txt";
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
        
        return fileName;
    }
}