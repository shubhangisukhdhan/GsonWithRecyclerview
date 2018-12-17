package shubhangi.gsonwithrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import shubhangi.gsonwithrecyclerview.adapters.UserAdapter;
import shubhangi.gsonwithrecyclerview.models.User;
import shubhangi.gsonwithrecyclerview.models.UserResponse;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://reqres.in/api/";
    private ProgressBar progressBar;
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private UserResponse userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(BASE_URL + "users?page=2");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        userAdapter = new UserAdapter(userList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userAdapter);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return getData(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String json) {
            progressBar.setVisibility(View.GONE);
            if (TextUtils.isEmpty(json)) {
                Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    userResponse = new UserResponse();
                    JSONObject mainObject = new JSONObject(json);
                    String page = mainObject.getString("page");
                    int per_page = mainObject.getInt("per_page");
                    int total = mainObject.getInt("total");
                    int total_pages = mainObject.getInt("total_pages");
                    Log.e("TEST", "Pages : " + page + "\nPer Page : " + per_page + "\nTotal : " + total + "\nTotal Pages : " + total_pages);
                    userResponse.setPage(page);
                    userResponse.setPerPage(per_page);
                    userResponse.setTotal(total);
                    userResponse.setTotalPages(total_pages);
                    ArrayList<User> users = new ArrayList<>();
                    JSONArray jsonArray = mainObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String first_name = obj.getString("first_name");
                        String last_name = obj.getString("last_name");
                        String avatar = obj.getString("avatar");
                        User user = new User(id, first_name, last_name, avatar);
                        userList.add(user);
                    }
                    for (User user1 : userList) {
                        Log.e("TEST", "Users : " + user1.toString());
                    }
                    userResponse.setUserArrayList(userList);
                    userAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.getMessage();
                }
            }
        }
    }

    public String getData(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}