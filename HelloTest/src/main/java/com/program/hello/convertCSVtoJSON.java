package com.program.hello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;


/**
 * Entry point for convert csv file to json files .
 * @author Amin
 * 
 */

public class convertCSVtoJSON {

	/*
	 * @max_entry maximum number of entry in each json formatted output 
	 */
    private static int max_entry = 2048;
    
    /**
     * method that will be called to convert the csv file to json formatted file
     */
    public static void convert() {

    		/*
    		 * @filepath location of the csv file in your system
    		 */
    		String filepath = "/users/Amin/Downloads/f_5500_2016_latest.csv";
    		
    		
    		/*
    		 * Open and read max_entry item from csv file and send it to generate() function to create json file 
    		 */
        try (BufferedReader input = new BufferedReader(new FileReader(filepath))) {
            String[] csvHeaders = input.readLine().split(",");
            int start = 1 - max_entry;
            int next = 1;
            int num = 1;
            while (next == start + max_entry) {
                start = next;
                next = generate(input, csvHeaders, start, num++);
            }
            
        } 
        catch (FileNotFoundException e) {
        		System.out.println("ERROR : CSV file not found.");
        		System.exit(1);
        } 
        catch (IOException e) {
        		e.printStackTrace();
        }
    }

    
    /**
     * 
     * @param in, input buffer which contain lines of csv file
     * @param headers, headers of columns in csv file
     * @param start, keep track of starting element in each file
     * @param num, keep track of number of created json files
     * @return row, number of last converted row in original csv file
     */
    private static int generate(BufferedReader in, String[] headers, int start, int num) {
        JsonFactory fac = new JsonFactory();

        int id = start;
        try(JsonGenerator myGenerator = fac.createGenerator(new File("data" + num + ".json"), JsonEncoding.UTF8)
                .setPrettyPrinter(new MinimalPrettyPrinter(""))) {

            String inputBuffer;
            while ((inputBuffer = in.readLine()) != null && (id - start) < max_entry) {
                myGenerator.writeStartObject();
                myGenerator.writeObjectFieldStart("index");
                myGenerator.writeObjectField("_index", "plans");
                myGenerator.writeObjectField("_type", "plan");
                myGenerator.writeObjectField("_id", String.valueOf(id));
                myGenerator.writeEndObject();
                myGenerator.writeEndObject();
                myGenerator.writeRaw('\n');
                id++;

                myGenerator.writeStartObject();
                String[] values = inputBuffer.split(",");
                for (int i = 0 ; i < headers.length ; i++) {
                    String value = i < values.length ? values[i] : null;
                    myGenerator.writeObjectField(headers[i], value);
                }
                myGenerator.writeEndObject();
                myGenerator.writeRaw('\n');
            }    
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
		return id;

    }

    

    public static void main(String[] args) {
        convertCSVtoJSON.convert();
    }
}

