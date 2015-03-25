package com.chekoff.hackafeforum;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class TopicsFragment extends Fragment {
    LayoutInflater mInflater;
    ListView listViewCollection;
    TopicsAdapter adapter;
    String urlDomain = "http://frm.hackafe.org";
    String urlFetchCategory = "/latest.json/";
    String urlMoreTopicURL = urlDomain + urlFetchCategory + "?page=0";
    List<Topics> topicsList = new LinkedList<>();
    List<Categories> categoriesList = new LinkedList<>();
    String LOG = "Hackafe Forum";
    MSG msg = new MSG();
    boolean isDataLoading;
    public static boolean isSettingsChanged = false;

    public TopicsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mInflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_topics, container, false);

        if (new NetworkStatus().getInstance(getActivity()).isOnline() != msg.KEY_CONN_NO) {
            new LoadCategories().execute();
            new LoadMoreTopics().execute();
        }

        listViewCollection = (ListView) rootView.findViewById(R.id.list_main);
        adapter = new TopicsAdapter(mInflater, topicsList, getActivity());
        listViewCollection.setAdapter(adapter);

        listViewCollection.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((firstVisibleItem + visibleItemCount >= totalItemCount - 1) &&
                        (!isDataLoading) &&
                        (urlMoreTopicURL != "") &&
                        new NetworkStatus().getInstance(getActivity()).isOnline() != msg.KEY_CONN_NO) {
                    new LoadMoreTopics().execute();
                }
            }
        });

        listViewCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Showing post content...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s/t/%s/%s",
                        urlDomain,
                        topicsList.get(position).getSlug(),
                        topicsList.get(position).getTopicID()))));

                /*PostActivity postActivity = new PostActivity();
                postActivity.uri = "http://abv.bg";
                startActivity(new Intent(getActivity(), PostActivity.class));*/
            }
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSettingsChanged) {

            //urlMoreTopicURL = urlDomain + urlFetchCategory + "?page=0";
            //topicsList = new LinkedList<>();
            //new loadMoreTopics().execute();
            //this.onCreate(null);
            //isSettingsChanged = false;

            if (isSettingsChanged) {

                Intent refresh = new Intent(getActivity(), MainActivity.class);
                startActivity(refresh);//Start the same Activity
                getActivity().finish(); //finish Activity.

                isSettingsChanged = false;
            }
        }
    }


    private class LoadMoreTopics extends AsyncTask<Void, Void, List<Topics>> {
        private ProgressDialog pDialog;
        String avatarSize = "100";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog before sending http request
            isDataLoading = true;

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching topics...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected List<Topics> doInBackground(Void... urls) {

            return getTopics(avatarSize);
        }

        @Override
        protected void onPostExecute(List<Topics> result) {
            super.onPostExecute(result);
            // increment current page
            adapter.rowCount = result.size();
            topicsList = result;
            pDialog.dismiss();

            adapter.notifyDataSetChanged();

            isDataLoading = false;

        }
    }

    private class LoadCategories extends AsyncTask<Void, Void, List<Categories>> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Showing progress dialog before sending http request
            isDataLoading = true;

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching category list...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected List<Categories> doInBackground(Void... urls) {

            return getCategories();
        }

        @Override
        protected void onPostExecute(List<Categories> result) {
            super.onPostExecute(result);
            // increment current page
            categoriesList = result;
            pDialog.dismiss();

            isDataLoading = false;

        }
    }

    public List<Topics> getTopics(String avatarSize) {
        StringBuilder data = new StringBuilder();
        JSONObject jObj;
        JSONObject jObgTopicListHeader;
        JSONArray jObjTopicList;
        JSONArray jObjUsers;
        JSONArray jObgPosters;
        int posterOriginalID;
        String posterDescription;
        String userAvatarURL;
        Bitmap userAvatar;
        String username;
        String userLastPosterAvatarURL;
        String userLastPoster;
        Bitmap userLastPosterAvatar;
        String topicTitle;
        int viewsCount;
        int repliesCount;
        String lastPostedAt;
        String slug;
        int topicID;
        int category_id;

        String topicImageURL;
        Bitmap topicImageBitmap;

        URL url;
        HttpURLConnection httpURLConnection;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //String networkStatus = NetworkStatus.getInstance(getActivity()).isOnline();

        /*if (networkStatus == "NO_CONNECTION")
            return topicsList;*/


        try {
            /*if (this.urlNextPage == "")
                url = new URL(urlDomain + urlFetchCategory + "?page=0");
            else*/
            url = new URL(this.urlMoreTopicURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                try {
                    String inputLine;
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        data.append(inputLine);
                    }
                    jObj = new JSONObject(data.toString());

                    jObjUsers = jObj.getJSONArray("users");

                    jObgTopicListHeader = jObj.getJSONObject("topic_list");

                    try {
                        this.urlMoreTopicURL = this.urlDomain + jObgTopicListHeader.getString("more_topics_url");
                    } catch (Throwable t) {
                        //Log.e(LOG, t.getMessage(), t);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), "All topics are fetched", Toast.LENGTH_SHORT).show();
                            }
                        });

                        this.urlMoreTopicURL = "";

                    }

                    jObjTopicList = jObgTopicListHeader.getJSONArray("topics");

                    //get all topics
                    if (jObjTopicList.length() == 0) {
                        topicsList.add(new Topics("", "", "No topics available", 0, 0, "", "", 0, null, "", "", null, "", null, new Categories()));

                    } else {
                        for (int i = 0; i < jObjTopicList.length(); i++) {

                            topicTitle = jObjTopicList.getJSONObject(i).getString("title");
                            viewsCount = jObjTopicList.getJSONObject(i).getInt("views");
                            repliesCount = jObjTopicList.getJSONObject(i).getInt("posts_count") - 1;
                            lastPostedAt = jObjTopicList.getJSONObject(i).getString("last_posted_at");
                            slug = jObjTopicList.getJSONObject(i).getString("slug");
                            topicID = jObjTopicList.getJSONObject(i).getInt("id");
                            userLastPoster = jObjTopicList.getJSONObject(i).getString("last_poster_username");
                            category_id = jObjTopicList.getJSONObject(i).getInt("category_id");

                            Categories category = categoriesList.get(category_id);

                            topicImageURL = "";
                            topicImageBitmap = null;


                            boolean loadPostImages = sharedPref.getBoolean(SettingsActivity.KEY_LOAD_POST_IMAGES, false);
                            boolean loadPostImagesCached = sharedPref.getBoolean(SettingsActivity.KEY_LOAD_POST_IMAGES_CACHED, false);
                            boolean loadPostImagesNoCachedWiFi = sharedPref.getBoolean(SettingsActivity.KEY_LOAD_POST_IMAGES_NO_CACHED_WIFI, false);


                            if (loadPostImages) {
                                try {
                                    if (jObjTopicList.getJSONObject(i).getString("image_url") != "null") {
                                        topicImageURL = urlDomain + jObjTopicList.getJSONObject(i).getString("image_url");
                                        topicImageBitmap = getBitmapFromDiskCache(topicImageURL);

                                        if (topicImageBitmap == null) {

                                            //downloading image and store into cache
                                            if (!loadPostImagesCached) {
                                                if (loadPostImagesNoCachedWiFi) {

                                                    //checking network connection type
                                                    String networkStatus = NetworkStatus.getInstance(getActivity()).isOnline();

                                                    if (networkStatus == msg.KEY_CONN_WIFI) {
                                                        topicImageBitmap = downloadImage(topicImageURL);

                                                        putBitmapInDiskCache(topicImageURL, topicImageBitmap);
                                                    }
                                                } else {
                                                    topicImageBitmap = downloadImage(topicImageURL);

                                                    putBitmapInDiskCache(topicImageURL, topicImageBitmap);
                                                }
                                            }
                                        }
                                    }

                                } catch (Throwable t) {
                                    Log.e(LOG, t.getMessage(), t);
                                    topicImageURL = "";
                                    topicImageBitmap = null;
                                }
                            }


                            jObgPosters = jObjTopicList.getJSONObject(i).getJSONArray("posters");
                            //get original poster
                            posterOriginalID = 0;
                            userAvatarURL = "";
                            username = "";
                            userAvatar = null;
                            userLastPosterAvatarURL = "";
                            userLastPosterAvatar = null;

                            for (int j = 0; j < jObgPosters.length(); j++) {

                                posterDescription = jObgPosters.getJSONObject(j).getString("description");

                                if (posterDescription.toLowerCase().contains("Original Poster".toLowerCase())) {
                                    posterOriginalID = jObgPosters.getJSONObject(j).getInt("user_id");
                                }
                            }

                            //get user avatar url
                            //TODO To ask for another way to search JSONArray and to change {size}
                            for (int j = 0; j < jObjUsers.length(); j++) {
                                if (jObjUsers.getJSONObject(j).getInt("id") == posterOriginalID) {
                                    userAvatarURL = urlDomain + jObjUsers.getJSONObject(j).getString("avatar_template").replace("{size}", avatarSize);
                                    username = jObjUsers.getJSONObject(j).getString("username");

                                    userAvatar = getBitmapFromDiskCache(userAvatarURL);

                                    if (userAvatar == null) {
                                        //downloading image and store into cache
                                        userAvatar = downloadImage(userAvatarURL);

                                        putBitmapInDiskCache(userAvatarURL, userAvatar);
                                    }

                                }

                                //get latest poster avatar data
                                if (jObjUsers.getJSONObject(j).getString("username").equals(userLastPoster)) {
                                    userLastPosterAvatarURL = urlDomain + jObjUsers.getJSONObject(j).getString("avatar_template").replace("{size}", avatarSize);

                                    userLastPosterAvatar = getBitmapFromDiskCache(userLastPosterAvatarURL);

                                    if (userLastPosterAvatar == null) {
                                        //downloading image and store into cache
                                        userLastPosterAvatar = downloadImage(userLastPosterAvatarURL);

                                        putBitmapInDiskCache(userLastPosterAvatarURL, userLastPosterAvatar);
                                    }
                                }
                            }

                            //adding data into list
                            topicsList.add(new Topics(userAvatarURL,
                                    username,
                                    topicTitle,
                                    viewsCount,
                                    repliesCount,
                                    lastPostedAt,
                                    slug,
                                    topicID,
                                    userAvatar,
                                    userLastPoster,
                                    userLastPosterAvatarURL,
                                    userLastPosterAvatar,
                                    topicImageURL,
                                    topicImageBitmap,
                                    category));

                            //Log.e(LOG, String.format("avatar: %s, username: %s, topic: %s", userAvatarURL, username, topicTitle));
                        }


                    }

                } catch (Throwable t) {
                    Log.e(LOG, t.getMessage(), t);
                    return topicsList;
                } finally {
                    bufferedReader.close();
                    return topicsList;
                }
            }

        } catch (
                IOException e
                )

        {
            e.printStackTrace();
            return topicsList;
        } finally {

        }

        return topicsList;

    }

    public List<Categories> getCategories() {
        URL url;
        HttpURLConnection httpURLConnection;
        StringBuilder data = new StringBuilder();
        JSONArray jObjCategories;


        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try {
            url = new URL("http://chekoff.com/test/hackafe_cat.txt");

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    data.append(inputLine);
                }
            }
            jObjCategories = new JSONObject(data.toString()).getJSONArray("categories");

            //adding null element
            //categoriesList.add(new Categories(0, "", "FFFFFF", ""));

            int id;
            String slug;
            String name;
            String c_color;

            categoriesList.add(new Categories(0, "", "#FFFFFF", ""));

            for (int i = 0; i < jObjCategories.length(); i++) {

                id = jObjCategories.getJSONObject(i).getInt("id");
                name = jObjCategories.getJSONObject(i).getString("name");
                if (id == 1)
                    c_color = "#FFFFFF";
                else
                    c_color = "#" + jObjCategories.getJSONObject(i).getString("color");

                slug = jObjCategories.getJSONObject(i).getString("slug");

                if ((slug.length() == 0) || (slug == null))
                    slug = "c/" + id + "-category";
                else
                    slug = "c/" + slug;

                int cat_size = categoriesList.size();

                if (id > cat_size) {
                    for (int j = 0; j < id - cat_size; j++) {
                        categoriesList.add(new Categories(0, "", "#FFFFFF", ""));
                    }
                } else {
                    categoriesList.get(id).setId(id);
                    categoriesList.get(id).setName(name);
                    categoriesList.get(id).setSlug(slug);
                    categoriesList.get(id).setText_color(c_color);
                }

                categoriesList.add(new Categories(
                        id,
                        name,
                        c_color,
                        slug));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categoriesList;
    }

    // Creates Bitmap from InputStream and returns it
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            if (stream != null) {
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            }
        } catch (IOException e1) {
            //e1.printStackTrace();
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }

    private void putBitmapInDiskCache(String url, Bitmap avatar) {
        // Create a path pointing to the system-recommended cache dir for the app, with sub-dir named
        // thumbnails
        File cacheDir = new File(getActivity().getCacheDir(), "avatars");
        cacheDir.mkdirs();
        // Create a path in that dir for a file, named by the default hash of the url
        File cacheFile = new File(cacheDir, "av_" + String.format("%h", url.hashCode()));
        try {
            // Create a file at the file path, and open it for writing obtaining the output stream
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(cacheFile);
                // Write the bitmap to the output stream (and thus the file) in PNG format (lossless compression)
                avatar.compress(Bitmap.CompressFormat.PNG, 70, fos);
                // Flush and close the output stream
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            // Log anything that might go wrong with IO to file
            Log.e(LOG, "Image url: " + url);
            Log.e(LOG, "Error when saving image to cache. ", e);
        }
    }

    private Bitmap getBitmapFromDiskCache(String url) {
        // Open input stream to the cache file
        // Create a path in that dir for a file, named by the default hash of the url
        File cacheDir = new File(getActivity().getCacheDir(), "avatars");
        File cacheFile = new File(cacheDir, "av_" + String.format("%h", url.hashCode()));

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cacheFile);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Log.e(LOG, "Couldn't find the file. ", e);
        }

        // Read a bitmap from the file (which presumable contains bitmap in PNG format, since
// that's how files are created)
        return null;
    }

}
