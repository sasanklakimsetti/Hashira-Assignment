package org.sasank;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SharmirSecretSharing {
    static class Point{
        double x;
        double y;

        public Point() {}

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public static double findConstant(List<Point> pointsList){
        double result=0.0;
        for(int i=0;i<pointsList.size();i++){
            double term=pointsList.get(i).y;
            for(int j=0;j<pointsList.size();j++){
                if(i!=j) term*=(0.0-pointsList.get(j).x)/(pointsList.get(i).x-pointsList.get(j).x);
            }
            result+=term;
        }
        return result;
    }

    public static double processFile(String FileName) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode root=objectMapper.readTree(new File(FileName));

        int k=root.get("keys").get("k").asInt();

        List<Point> points=new ArrayList<>();
        Iterator<String>fieldNames=root.fieldNames();
        while (fieldNames.hasNext()){
            String key=fieldNames.next();
            if(key.equals("keys")) continue;

            JsonNode node=root.get(key);
            int x=Integer.parseInt(key);
            int base=Integer.parseInt(node.get("base").asText());
            String valueStr=node.get("value").asText();
            int y=Integer.parseInt(valueStr, base);

            points.add(new Point(x,y));
        }

        points.sort(Comparator.comparingInt(p-> (int) p.x));

        List<Point>selected=points.subList(0,k);

        return findConstant(selected);
    }

    public static void main(String[] args){
        try {
            // testing for first input
            double c1=processFile("input1.json");
            //testing for second input
            System.out.printf("The constant term c for input1.json is %.4f%n ",c1);
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
