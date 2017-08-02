package org.frice.game.utils.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class JSONPreference implements Database {
    private final JSONObject jsonObject;
    private final File jsonFile;
    public JSONPreference(String filepath) throws JSONException, IOException {
        jsonFile = new File(filepath);
        if(jsonFile.exists()) {
            jsonObject = new JSONObject(FileUtils.file2String(filepath));
        } else {
            jsonFile.createNewFile();
            jsonObject = new JSONObject();
            save();
        }
    }
    public JSONPreference(File jsonFile) throws JSONException, IOException {
        this(jsonFile.getPath());
    }
    @Override public void insert(String s, Object o) {
        try {
            jsonObject.put(s,o);
            save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override public <T> T queryT(String s, T t) {
        return (T)query(s, t);
    }
    @Override public Object query(String s, Object o) {
        try {
            if(jsonObject.get(s) instanceof JSONObject) {
                return this.jsonToMap((JSONObject) jsonObject.get(s));
            } else if(jsonObject.get(s) instanceof JSONArray) {
                return this.jsonToList((JSONArray)jsonObject.get(s));
            } else {
                return jsonObject.get(s);
            }
        } catch (JSONException e) {
            return o;
        }
    }
    public Object queryAsJson(String s, Object o) {
        try {
            return jsonObject.get(s);
        } catch (Exception e) {
            return o;
        }
    }
    private Map<String, Object> jsonToMap(JSONObject json) throws JSONException{
        Map<String, Object> result = new HashMap<>();
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object value = json.get(key);
            if(value instanceof JSONObject) {
                result.put(key, this.jsonToMap((JSONObject)value));
            } else if(value instanceof JSONArray) {
                result.put(key, jsonToList((JSONArray)value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
    private List jsonToList(JSONArray json) throws JSONException{
        List<Object> result = new ArrayList<>();
        for(int i = 0; i < json.length(); i++) {
            if(json.get(i) instanceof JSONObject) {
                result.add(this.jsonToMap((JSONObject)json.get(i)));
            } else if(json.get(i) instanceof JSONArray) {
                result.add(jsonToList((JSONArray)json.get(i)));
            } else {
                result.add(json.get(i));
            }
        }
        return result;
    }
    private void save() throws FileNotFoundException {
        FileUtils.string2File(jsonObject.toString(), jsonFile);
    }
}
