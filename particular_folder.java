import java.util.Scanner;
import java.io.File;
import java.io.FileWriter; // header to write the file
import java.io.FileReader; // header to read the file
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

// File creation and checking whether the file is created or not
class File_operations {
    void create_file(String address, String file_name) {
        // Check if directory exists
        File dir = new File(address);
        if (!dir.exists()) {
            // Create the directory if it doesn't exist
            dir.mkdirs(); 
        }

        // Create the specific file
        File FILE = new File(dir, file_name);
        try {
            if (FILE.createNewFile()) {
                System.out.println("\nFile created successfully: \n" + FILE.getAbsolutePath());
            } else {
                System.out.println("\nFile already exists: \n" + FILE.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("\nError in creating file.\n");
            e.printStackTrace();
        }
    }

    String file_path(String F_name){
        File f = new File(F_name);
        return f.getAbsolutePath();
    }
}

// File read and write operations
class Data_write_read {
    void data_write(String f_path, String data) {
        try {
            // Object of FileWriter class
            FileWriter F_write = new FileWriter(f_path);
            BufferedWriter B_Write = new BufferedWriter(F_write);

            B_Write.write(data); // Data is written
            B_Write.close(); // Ensure BufferedWriter is closed
            F_write.close(); // Close FileWriter as well

            System.out.println("Data entered in the file: \t" + f_path);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void data_read(String f_path) {
        try { // FileReader object
            FileReader F_Read = new FileReader(f_path);
            BufferedReader B_read = new BufferedReader(F_Read);

            String str;
            System.out.println("Reading data from file: " + f_path);

            while ((str = B_read.readLine()) != null) {
                System.out.println(str);
            }
            B_read.close(); // Close BufferedReader
            F_Read.close(); // Close FileReader
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

public class particular_folder {
    public static void main(String[] args) {
        // Menu-driven code for simple file creation, data insertion
        
        Scanner sc = new Scanner(System.in);

        File_operations fs = new File_operations(); // File system class

        Data_write_read dwr = new Data_write_read(); // Operations
        
        System.out.println("\t\t<<===== Welcome to Filesystem =====>> ");
        String Address = "code/database"; // Relative path
        String File_name = "hello2.txt"; 

        while (true) {
            System.out.println("\t\t Press 1 for file creation");
            System.out.println("\t\t Press 2 for data insertion");
            System.out.println("\t\t Press 3 for data reading");
            System.out.println("\t\t Press 4 for exit");
            
            int i = sc.nextInt();
            sc.nextLine(); // Consume the newline left by nextInt
            String f_add = fs.file_path(Address + File.separator + File_name);

            switch (i) {
                case 1: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("Enter the file name: \t" + File_name);
    
                    fs.create_file(Address, File_name);
                    break;
    
                case 2: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("The file name: \t" + File_name);

                    System.out.println("The file path: \t" + f_add);

                    System.out.println("Enter the data: \t");
                    String data = sc.nextLine(); // Use nextLine() to capture full data input
                
                    dwr.data_write(f_add, data);
                    break;

                case 3: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("The file name: \t" + File_name); 

                    System.out.println("The file path: \t" + f_add); 
                    System.out.println("Data from file:\n");
                    dwr.data_read(f_add);

                    break;

                case 4: 
                    System.out.println("<<===== Thanks =====>>");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("\t\t<<===== Invalid choice =====>>\n\n");
            }
        }
    }
}
