package com.example.searchgithubapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.searchgithubapp.databinding.ActivityMainBinding;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    TextView textView;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);
  //      textView=findViewById(R.id.tv_output);
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchterm=binding.etSearchTerm.getText().toString();
                if(TextUtils.isEmpty(searchterm))return;
                URL searchurl=NetworkUtil.buildRepoSearch(searchterm);
                new GithubQueryTask().execute(searchurl);
            }
        });

    }
    public class GithubQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... params) {
            URL searchurl =params[0];
            String githubsearchresults=null;
            try{
                githubsearchresults=NetworkUtil.getResponseFromHttp(searchurl);
            }catch(IOException e){
                e.printStackTrace();
            }
            return githubsearchresults;
        }
        @Override
        protected void onPostExecute(String str){
            if(str!=null && !str.equals("")){
                parseandDisplayRepos(str);
            }
        }
        private void parseandDisplayRepos(String json){
            List<GithubRepository> repositories=NetworkUtil.parseGithubRepos(json);
            StringBuilder sb=new StringBuilder();
            for(GithubRepository repository:repositories){
                sb.append("Id: ").append(repository.getId()).append("\n").append("Name: ").append(repository.getName())
                .append("\n\n");
            }
            binding.tvOutput.setText(sb.toString());
        }
    }


}