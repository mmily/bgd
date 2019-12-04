package com.test.excelTojson;

import com.bgd.PropertiesUtil;
import net.sf.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * @author dengyu
 * @data 2019/12/4 - 9:09
 */
public class ToJson {

    public static void main(String[] args) {

        Properties load = PropertiesUtil.load("fileDir.properties");
        String execlfile = load.getProperty("Execl.File.Dir");
        String jsonfile = load.getProperty("Out.File.Dir");
        List<JSONObject> jsonObjects = EXCELUtil.readFile(execlfile);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonfile,true));
            for (JSONObject jsonObject : jsonObjects) {
                System.out.println(jsonObject);
                writer.write(jsonObject.toString());
                writer.newLine();
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
