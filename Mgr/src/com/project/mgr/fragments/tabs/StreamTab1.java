/**
 * 
 */
package com.project.mgr.fragments.tabs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.project.mgr.MainActivity;
import com.project.mgr.R;
import com.project.mgr.fragments.tabs.DetectScrollView.OnScrollViewListener;

public class StreamTab1 extends Fragment {
	
	private MediaPlayer mPlayer;
	private boolean mPlaying = false;
	
	private PreviewGifPlayer lastPlayed = null;
	private String calculatedDays = null;
	private Integer calculatedDaysNo = null;
	private String calculatedMins = null;
	private String calculatedSecs = null;
	private String calculatedTime = null;
	private int currentPost = 0;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {

    		final View rootView = inflater.inflate(R.layout.stream_tab1, container, false);
    		    		
    		final Session session = Session.getActiveSession();
        	if (session != null && session.isOpened()) {
        		// If the session is open, make an API call to get user data
        	    // and define a new callback to handle the response
        	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
    	    	    @Override
    	    	    public void onCompleted(GraphUser user, Response response) {
    	    	               // If the response is successful
    		    	    if (session == Session.getActiveSession()) {
    		    	    	if (user != null) {
    		    	    		final String user_id = user.getId();//user id
    		    	    		final String[] params = {user_id, "0"};
    		    	            
    		    	            new RetriveAllPosts().execute(params);
    		    	            
    		    	            DetectScrollView detectScrollView = (DetectScrollView) rootView.findViewById(R.id.postsScroll);
    		    	    		detectScrollView.setOnScrollViewListener( new OnScrollViewListener() {
    		    	    		    public void onScrollChanged( DetectScrollView v, int l, int t, int oldl, int oldt ) {
    		    	    		    	//int a = v.getScrollY();
    		    	    		    	View view = (View) v.getChildAt(v.getChildCount()-1);
    		    	    		        int diff = (view.getBottom()-(v.getHeight()+v.getScrollY()));// Calculate the scrolldiff
    		    	    		        
    		    	    		        if( diff == 0 ){  // if diff is zero, then the bottom has been reached
    		    	    		        	currentPost+=2;
    		    	    		        	
    		    	    		        	String current_post = Integer.toString(currentPost);
    		    	    		        	String[] newParams = {user_id, current_post};
    		    	    		        	new RetriveAllPosts().execute(newParams);
    		    	    		        }
    		    	    		    }
    		    	    		});
    		    	    	}   
    		    	    }   
    	    	    }   
        	    }); 
        	    Request.executeBatchAsync(request);
        	}
    		
        	return rootView;
    }
        	
	class displayAllPosts extends AsyncTask<String, String, Void> {
		
		private ProgressDialog progressDialog = new ProgressDialog(getActivity());
	    InputStream is = null ;
	    String result = "";
	    
	    final PreviewGifPlayer loader = new PreviewGifPlayer(getActivity());
	    final LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
	    
	    protected void onPreExecute() {
	    	loader.setMovieResource(R.drawable.loading);	    	
	    	posts.addView(loader);
	    	//progressDialog.setMessage("Downloading files...");
	    	//progressDialog.show();
	    	/*progressDialog.setOnCancelListener(new OnCancelListener() {
			 @Override
			  public void onCancel(DialogInterface arg0) {
			  task.this.cancel(true);
			    }
			 });*/
	     }

		@Override
	    protected Void doInBackground(final String... params) {
			if (downloadAllPosts(params[0],params[2]) && downloadAllPosts(params[0],params[3])) {
				final PreviewGifPlayer postGif = new PreviewGifPlayer(getActivity());
				   final LinearLayout postGifLay = new LinearLayout(getActivity());
				   final RelativeLayout profileLL = new RelativeLayout(getActivity());
				   final LinearLayout dateLL = new LinearLayout(getActivity());
				   final TextView creationDate = new TextView(getActivity());
				   final TextView fullName = new TextView(getActivity());
				   final LinearLayout likesLL = new LinearLayout(getActivity());
				   final TextView likes = new TextView(getActivity());
				   final ImageView heart = new ImageView(getActivity());
				   final ImageView profilePicture = displayUserPicture(params[0]);
				   final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
				            (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				   lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				   final RelativeLayout.LayoutParams prof = new RelativeLayout.LayoutParams
				            (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				   prof.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				   final RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams
				            (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				   
				   final RelativeLayout.LayoutParams rlpDate = new RelativeLayout.LayoutParams
				            (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				   final RelativeLayout.LayoutParams matchParent = new RelativeLayout.LayoutParams
				            (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				   
				   fullName.setId(generateViewId());
				   profilePicture.setId(generateViewId());
				   rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				   rlp.addRule(RelativeLayout.RIGHT_OF, profilePicture.getId());
				   rlpDate.addRule(RelativeLayout.BELOW, fullName.getId());
				   rlpDate.addRule(RelativeLayout.RIGHT_OF, profilePicture.getId());
				   LinearLayout.LayoutParams heartSize = new LinearLayout.LayoutParams(50,50);
				   	
				   profileLL.addView(profilePicture);
				   fullName.setText(getUserFBname(params[0]));
				   profileLL.addView(fullName, rlp);
				   
				   creationDate.setText(calculateDate(params[1]));
				   creationDate.setTextColor(Color.WHITE);
				   profileLL.addView(creationDate, rlpDate);
				   
				   likes.setText(params[4]);
				   likesLL.addView(likes);
				   heart.setImageResource(R.drawable.heart);
				   heart.setLayoutParams(heartSize);
				   likesLL.addView(heart);
				   
				   likesLL.setOnClickListener(new View.OnClickListener() {
		    	    	@Override
		    	        public void onClick(View v) {
		    	    		final Session session = Session.getActiveSession();
		    	        	if (session != null && session.isOpened()) {
		    	        		// If the session is open, make an API call to get user data
		    	        	    // and define a new callback to handle the response
		    	        	    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
		    	    	    	    @Override
		    	    	    	    public void onCompleted(GraphUser user, Response response) {
		    	    	    	               // If the response is successful
		    	    		    	    if (session == Session.getActiveSession()) {
		    	    		    	    	if (user != null) {
		    	    		    	    		String user_id = user.getId();//user id
		    	    		    	            String[] likeParams = {params[5],user_id};
		    	    		    	    		new AddLike().execute(likeParams);
		    	    		    	    	}   
		    	    		    	    }   
		    	    	    	    }   
		    	        	    }); 
		    	        	    Request.executeBatchAsync(request);
		    	        	}
		    	    	}
				   });
				   final RelativeLayout post = new RelativeLayout(getActivity());
				   final LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
				   try {
			        	InputStream is = new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/MgrApp/stream/"+params[2]);
			            byte[] array = streamToBytes(is);
			    	    Movie movie = Movie.decodeByteArray(array, 0, array.length);
			    	    postGif.setMovie(movie);
			    	    postGif.setTag(params[0]+"/"+params[2]);
			    	    postGif.setPaused(true);
			    	    postGif.setOnClickListener(new View.OnClickListener() {
			    	    	@Override
			    	        public void onClick(View v) {
			    	    		PreviewGifPlayer temp = (PreviewGifPlayer) v;
			    	    		if (temp != null){
				    	    		if (lastPlayed != null) {
				    	    			if (lastPlayed == temp) {
				    	    				lastPlayed.setPaused(!lastPlayed.isPaused());
				    	    				if (!mPlaying) {
						    	    			preparePlaying(params[3]);
						    	    			mPlaying = !mPlaying;
						    	    		} else {
						    	    			stopPlaying();
						    	    			mPlaying = !mPlaying;
						    	    		}
				    	    			} else {
					    	    			lastPlayed.setPaused(true);
					    	    			temp.setPaused(false);
						    	    		lastPlayed = temp;
						    	    		if (mPlaying) {
							    	    		stopPlaying();
							    	    		mPlaying = !mPlaying;
						    	    		}
						    	    		if (!mPlaying) {
						    	    			preparePlaying(params[3]);
						    	    			mPlaying = !mPlaying;
						    	    		} else {
						    	    			stopPlaying();
						    	    			mPlaying = !mPlaying;
						    	    		}
				    	    			}
				    	    		} else {
				    	    			temp.setPaused(!temp.isPaused());
				    	    			lastPlayed = temp;
				    	    			if (!mPlaying) {
					    	    			preparePlaying(params[3]);
					    	    			mPlaying = !mPlaying;
					    	    		} else {
					    	    			stopPlaying();
					    	    			mPlaying = !mPlaying;
					    	    		}
				    	    		}
			    	    		}
			    	        }
			        	});
			        	
			    	    getActivity().runOnUiThread(new Runnable() {
			    	        @Override
			    	        public void run() {
			    	        	//post.setPadding(0, 0, 0, 0);
			    	        	post.setBackgroundColor(Color.rgb(51,181,229));
			    	        	postGifLay.addView(postGif);
			    	        	postGifLay.setGravity(Gravity.CENTER);
			    	        	postGifLay.setPadding(0, 200, 0, 60);
			    	        	post.addView(profileLL, prof);
			    	        	post.addView(postGifLay);
			    	        	//post.addView(dateLL);
			    	        	post.addView(likesLL, lp);
			    	        	posts.addView(post, matchParent);
			    	        }
			    	   });
				   } catch(Exception e) {
					   System.out.println(e);
				   }
			   }
			return null;
		}
		
		protected void onPostExecute(Void v) {
			//this.progressDialog.dismiss();
			
	    	LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
	    	
	    	posts.removeView(loader);
		}
	}
	
	class RetriveAllPosts extends AsyncTask<String, String, Void> {
		private ProgressDialog progressDialog = new ProgressDialog(getActivity());
	    InputStream is = null ;
	    String result = "";
	    
	    final PreviewGifPlayer loader = new PreviewGifPlayer(getActivity());
	    final LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
	    
	    protected void onPreExecute() {
	    	loader.setMovieResource(R.drawable.loading);
	    	posts.addView(loader);
	    	//progressDialog.setMessage("Fetching data...");
	       //progressDialog.show();
	       /*progressDialog.setOnCancelListener(new OnCancelListener() {
			 @Override
			  public void onCancel(DialogInterface arg0) {
			  task.this.cancel(true);
			    }
			 });*/
	     }

	    @Override
	    protected Void doInBackground(String... params) {
	    	String url_select = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/selectAll.php";

	    	HttpClient httpClient = new DefaultHttpClient();
	    	HttpPost httpPost = new HttpPost(url_select);
	    	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
	    	param.add(new BasicNameValuePair("user_id",params[0]));
	    	param.add(new BasicNameValuePair("current_post",params[1]));
	        try {
	        	httpPost.setEntity(new UrlEncodedFormEntity(param));
			    HttpResponse httpResponse = httpClient.execute(httpPost);
			    HttpEntity httpEntity = httpResponse.getEntity();
			    //read content
			    is =  httpEntity.getContent();     
		    } catch (Exception e) {
		    	Log.e("log_tag", "Error in http connection "+e.toString());
		    }
		    try {
		    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    StringBuilder sb = new StringBuilder();
			    String line = "";
			    while((line=br.readLine())!=null) {
			        sb.append(line+"\n");
			    }
			    is.close();
			    result=sb.toString();    
		    } catch (Exception e) {
		        // TODO: handle exception
		        Log.e("log_tag", "Error converting result "+e.toString());
		    }
		    return null;
	    }
	    
	    protected void onPostExecute(Void v) {
	    	try {
	    		JSONArray Jarray = new JSONArray(result);
	    		//LinearLayout posts = (LinearLayout) getActivity().findViewById(R.id.posts);
	    		//String[] fields = {"user_id","created_at","gif","audio"};
	    		for(int i=0;i<Jarray.length();i++) {
	    			JSONObject Jasonobject = null;
	    			Jasonobject = Jarray.getJSONObject(i);
					   
	    			String id = Jasonobject.getString("id");
	    			String user_id = Jasonobject.getString("user_id");
	    			String created_at = Jasonobject.getString("created_at");
	    			String gif = Jasonobject.getString("gif");
	    			String audio = Jasonobject.getString("audio");
	    			String likes = Jasonobject.getString("likes");
					   
	    			String[] fields = {user_id,created_at,gif,audio,likes,id};
					
	    			new displayAllPosts().execute(fields);
	    		}
	    		//this.progressDialog.dismiss();
				posts.removeView(loader);
		
	    	} catch (Exception e) {
	    		// TODO: handle exception
	    		//Log.e("log_tag", "Error parsing data "+e.toString());
	    		//this.progressDialog.dismiss();
			   	posts.removeView(loader);
	    	}
	    }
	}
	
	public boolean downloadAllPosts(String user_id, String fileName) {
		try {
	        //set the download URL, a url that points to a file on the internet
	        //this is the file to be downloaded
			String fileToDownload = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/uploadedFiles/"+user_id+"/"+fileName;
	        URL url = new URL(fileToDownload);

	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();

	        //set the path where we want to save the file
	        //in this case, going to save it on the root directory of the
	        //sd card.
	        String postsDir = Environment.getExternalStorageDirectory()+"/MgrApp/stream/";
	        File SDCardRoot = new File(postsDir);
	        if (!SDCardRoot.exists()) {
	        	SDCardRoot.mkdirs();
	        }
	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        File file = new File(SDCardRoot,fileName);
	        if (!file.exists()) {
		        //this will be used to write the downloaded data into the file we created
		        FileOutputStream fileOutput = new FileOutputStream(file);
	
		        //this will be used in reading the data from the internet
		        InputStream inputStream = urlConnection.getInputStream();
	
		        //this is the total size of the file
		        int totalSize = urlConnection.getContentLength();
		        //variable to store total downloaded bytes
		        int downloadedSize = 0;
	
		        //create a buffer...
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0; //used to store a temporary size of the buffer
	
		        //now, read through the input buffer and write the contents to the file
		        
		        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
		                //add the data in the buffer to the file in the file output stream (the file on the sd card
		                fileOutput.write(buffer, 0, bufferLength);
		                //add up the size so we know how much is downloaded
		                downloadedSize += bufferLength;
		                //this is where you would do something to report the prgress, like this maybe
		                //updateProgress(downloadedSize, totalSize);
	
		        }
		        //close the output stream when done
		        fileOutput.close();
	        }
	        return true;
		//catch some possible errors...
		} catch (MalformedURLException e) {
		        e.printStackTrace();
		} catch (IOException e) {
		        e.printStackTrace();
		}
		return false;
	}
	
	private static byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }
	
	
	public void onGifClick(View v) {
		PreviewGifPlayer gif = (PreviewGifPlayer) v;
		gif.setPaused(!gif.isPaused());
	}
	
	private void preparePlaying(String audio) {
        mPlayer = new MediaPlayer();
        try {
        	String audioFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MgrApp/stream/"+audio;
        	//File dir = new File(mAllFiles);
        	//File[] files = dir.listFiles();
        	mPlayer.setDataSource(audioFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
                Log.d("asdasd", "prepare() failed when trying to play recorded audio");
        }
        //return mPlayer;
    }
	
	private void stopPlaying() {
		mPlayer.release();
	    mPlayer = null;
	}
	
	private String calculateDate(String created_at) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {
			Date date = format.parse(created_at);
			Long asd = new Date(System.currentTimeMillis()).getTime();// - date.getTime();// - date.getTime();
			Long dsa = date.getTime();
			Long qwe = (asd-dsa)/1000;
			int difference = Integer.parseInt(qwe.toString());
			
			int secs = difference;
			int mins = secs / 60;
			int hours = mins / 60;
			int days = hours / 24;
			int months = days / 30;
			int years = months / 12;
			
			if (!(secs > 30)) {
				calculatedTime = "Just now";
			} else {
				calculatedTime = Integer.toString(secs)+" seconds ago";
			}
			if (mins == 1) {
				calculatedTime = "1 minute ago";
			} else if (mins != 1 && mins != 0){
				calculatedTime = Integer.toString(mins)+" minutes ago";
			}
			if (hours == 1) {
				calculatedTime = "1 hour ago";
			} else if (hours != 1 && hours != 0) {
				calculatedTime = Integer.toString(hours)+" hours ago";
			}
			if (days == 1) {
				calculatedTime = "1 day ago";
			} else if (days != 0 && days != 1) {
				calculatedTime = Integer.toString(days)+" days ago";
			}
			if (months == 1) {
				calculatedTime = "1 month ago";
			} else if (months != 0 && months != 1) {
				calculatedTime = Integer.toString(months)+" months ago";
			}
			if (years == 1) {
				calculatedTime = "1 year ago";
			} else if (years != 0 && years != 1) {
				calculatedTime = Integer.toString(years)+" years ago";
			}
		} catch(Exception e) {
			
		}
		return calculatedTime;
	}
	
	class AddLike extends AsyncTask<String, String, Void> {
		InputStream is = null;
		String result = "";
		
	    @Override
	    protected Void doInBackground(String... params) {
	    	String url_select = "http://wierzba.wzks.uj.edu.pl/~09_ziolekm/MgrApp/addLike.php";

	      	HttpClient httpClient = new DefaultHttpClient();
	      	HttpPost httpPost = new HttpPost(url_select);
	      	ArrayList<NameValuePair> param = new ArrayList<NameValuePair>(1);
	      	param.add(new BasicNameValuePair("post_id", params[0]));
	      	param.add(new BasicNameValuePair("user_id", params[1]));
	      	System.out.println(params[0]);
	      	System.out.println(params[1]);
	        try {
			     httpPost.setEntity(new UrlEncodedFormEntity(param));
			     HttpResponse httpResponse = httpClient.execute(httpPost);
			     HttpEntity httpEntity = httpResponse.getEntity();
		
			     //read content
			     is =  httpEntity.getContent();     

	        } catch (Exception e) {
	        	Log.e("log_tag", "Error in http connection "+e.toString());
	        }
	        try {
		        BufferedReader br = new BufferedReader(new InputStreamReader(is));
			     StringBuilder sb = new StringBuilder();
			     String line = "";
			     while((line=br.readLine())!=null)
			     {
			        sb.append(line+"\n");
			     }
			      is.close();
			      result=sb.toString();    

		       } catch (Exception e) {
		        // TODO: handle exception
		        Log.e("log_tag", "Error converting result "+e.toString());
		       }
	        return null;
	    }
	    
	    protected void onPostExecute(Void v) {
	    	Toast.makeText(getActivity(), "Thanks for like", 1500).show();
	    }
	}

	private ImageView displayUserPicture(String user_id) {
		final ImageView userPicture = new ImageView(getActivity());
		URL img = null;
		try {
			img = new URL("https://graph.facebook.com/"+user_id+"/picture?type=large&height=200&width=200");
			HttpURLConnection connection = (HttpURLConnection) img.openConnection();
			
			InputStream is = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			connection.disconnect();
			userPicture.setImageBitmap(getRoundedShape(bm));
			//userPicture.setBackgroundResource(R.drawable.rounded_picture);
			return userPicture;
		} catch (Exception e) {
			System.out.print(e);
		}
		return null;
	}
	
	private String getUserFBname(String user_id) {
		try {
		URI url = new URI("https://graph.facebook.com/"+user_id);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(url);
		HttpResponse response = client.execute(request);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
		        .getEntity().getContent()));
		String line = "";

		while ((line = in.readLine()) != null) {

		    JSONObject jObject = new JSONObject(line);

		    if (jObject.has("name")) {
		        String fullName = jObject.getString("name");
		        return fullName;
		    }

		}
		} catch (Exception w) {}
		return null;
	}
	
	private int generateViewId() {
		String viewId = null;
		Random rand = new Random(); 
		
		return rand.nextInt(999999999);
	}
	
	 /*
	  * Making image in circular shape
	  */
	 public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	  // TODO Auto-generated method stub
	  int targetWidth = 200;
	  int targetHeight = 200;
	  Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                            targetHeight,Bitmap.Config.ARGB_8888);
	  
	                Canvas canvas = new Canvas(targetBitmap);
	  Path path = new Path();
	  path.addCircle(((float) targetWidth - 1) / 2,
	  ((float) targetHeight - 1) / 2,
	  (Math.min(((float) targetWidth), 
	                ((float) targetHeight)) / 2),
	          Path.Direction.CCW);
	  
	                canvas.clipPath(path);
	  Bitmap sourceBitmap = scaleBitmapImage;
	  canvas.drawBitmap(sourceBitmap, 
	                                new Rect(0, 0, sourceBitmap.getWidth(),
	    sourceBitmap.getHeight()), 
	                                new Rect(0, 0, targetWidth,
	    targetHeight), null);
	  return targetBitmap;
	 }
	 
}