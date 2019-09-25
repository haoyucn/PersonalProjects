package tsvdao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSVReaderWriter {


    private static final String FILENAME = "tsv_database.dao.tsv";
    private static final String DELIMITER = "\t";
    private static final String NEW_LINE = "\n";

    private String[] header;

    public TSVReaderWriter(String[] header) {
        this.header = header;
    }

    public List<String[]> readAll() {

        List<String[]> objects = new ArrayList<>();
        String line = null;
        String[] tokens = null;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(FILENAME));

//          skipping header
            reader.readLine();

//          read all lines in the file
            while((line = reader.readLine()) != null) {
                tokens = line.split(DELIMITER);
                if(tokens.length > 0) {
                    objects.add(tokens);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("No File");
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e) {
                System.out.println("Reader null");
            }
        }

        return objects;
    }

    public void writeHeader() {

        FileWriter writer = null;

        try {
            writer = new FileWriter(FILENAME);

            for(String s : header) {
                writer.append(s);
                writer.append(DELIMITER);
            }
            writer.append(NEW_LINE);

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void writeLine(String[] row) {

        FileWriter writer = null;

        try {
            writer = new FileWriter(FILENAME, true);

            for(String s : row) {
                writer.append(s);
                writer.append(DELIMITER);
            }
            writer.append(NEW_LINE);

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void writeLines(List<String[]> toWrite) {

        writeHeader();

        FileWriter writer = null;

        try {
            writer = new FileWriter(FILENAME, true);

            for(String[] row : toWrite) {
                for(String s : row) {
                    writer.append(s);
                    writer.append(DELIMITER);
                }
                writer.append(NEW_LINE);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void clear() {
        File file = new File(FILENAME);
        file.delete();
    }

}
