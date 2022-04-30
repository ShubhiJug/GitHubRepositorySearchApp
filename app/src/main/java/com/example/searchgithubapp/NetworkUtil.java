package com.example.searchgithubapp;

import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetworkUtil {
    private static final String GITHUB_BASE_URL="https://api.github.com";
    private static final String GITHUB_USER="users";
    private static final String GITHUB_REPOSITORY="repositories";
    private static final String GITHUB_SEARCH="search";
    private static final String PARAM_QUERY="q";

    private NetworkUtil(){

    }
    public static URL buildRepoSearch(String query){
        Uri buildUri=Uri.parse(GITHUB_BASE_URL).buildUpon().appendPath(GITHUB_SEARCH)
                .appendPath(GITHUB_REPOSITORY).appendQueryParameter(PARAM_QUERY,query).build();
        URL url=null;
        try{
            url=new URL(buildUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }
    public static String getResponseFromHttp(URL url) throws IOException{
        HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
        try{
            InputStream input=urlConnection.getInputStream();
            Scanner scanner=new Scanner(input);
            scanner.useDelimiter("\\A");
            if(scanner.hasNext()){
                return scanner.next();
            }else{
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }
    public static List<GithubRepository> parseGithubRepos(String repoJson){
        List<GithubRepository> repositories=new ArrayList<>();
        if(TextUtils.isEmpty(repoJson))return repositories;
        try{
            JSONObject root=new JSONObject(repoJson);
            JSONArray repoArray=root.getJSONArray("items");
            for(int i=0;i<repoArray.length();++i){
                JSONObject repository=repoArray.getJSONObject(i);
                Integer id=repository.getInt("id");
                String name=repository.getString("name");
                String desc=repository.getString("description");
                repositories.add(new GithubRepository(id,name,desc));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return repositories;
    }
}
