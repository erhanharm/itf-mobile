package com.vphoainha.itfmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vphoainha.itfmobile.LoginActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Answer;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class AnswerAdapter extends ArrayAdapter<Answer> {
	public static final int ALL_ANSWER=1;
	
	Activity act;
	List<Answer> lst;
	int type;
	
	String msg;
	Answer save_ans;
	int save_pos;
	
	public AnswerAdapter(Activity context, int resource, int textViewResourceId, List<Answer> objects, int type) {
		super(context, resource, textViewResourceId, objects);
		act=context;
		this.type=type;
		lst=objects;
	}
	
	public static class ViewHolder {
		public TextView tvContent,tvAuthor, tvNumLike, tvNumDisLike;
		public LinearLayout lnLike, lnDisLike;
		public ImageView ivLike, ivDisLike;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_answer, null);
	      
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvNumLike = (TextView) convertView.findViewById(R.id.tvNumLike);
			holder.tvNumDisLike = (TextView) convertView.findViewById(R.id.tvNumDisLike);
			holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
			holder.lnLike=(LinearLayout)convertView.findViewById(R.id.lnLike);
			holder.lnDisLike=(LinearLayout)convertView.findViewById(R.id.lnDisLike);
			holder.ivLike=(ImageView)convertView.findViewById(R.id.ivLike);
			holder.ivDisLike=(ImageView)convertView.findViewById(R.id.ivDisLike);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();

		final Answer a = lst.get(position);

		holder.tvContent.setText(a.getContent());
		if(a.getUserId()==Utils.DEFAUT_VALUE) holder.tvAuthor.setText("by Anonymous");
		else holder.tvAuthor.setText("by "+a.getUserName());
		
		holder.tvNumLike.setText(a.getCountLiked()+"");
		holder.tvNumDisLike.setText(a.getCountDisliked()+"");
		
		int clike,cunlike;
		clike=act.getResources().getColor(R.color.text_like);
		cunlike=act.getResources().getColor(R.color.text_unlike);
		if(a.getIsLiked()==1){
			holder.ivLike.setImageDrawable(Utils.getDrawable(act, "like"));
			holder.tvNumLike.setTextColor(clike);
		}
		else {
			holder.ivLike.setImageDrawable(Utils.getDrawable(act, "unlike"));
			holder.tvNumLike.setTextColor(cunlike);
		}
		if(a.getIsDisliked()==1){
			holder.ivDisLike.setImageDrawable(Utils.getDrawable(act, "dislike"));
			holder.tvNumDisLike.setTextColor(clike);
		}
		else{
			holder.ivDisLike.setImageDrawable(Utils.getDrawable(act, "undislike"));
			holder.tvNumDisLike.setTextColor(cunlike);
		}

		holder.lnLike.setTag(R.string.TAG_ANSWER, a);
		holder.lnLike.setTag(R.string.TAG_POS, position);
		holder.lnDisLike.setTag(R.string.TAG_ANSWER, a);
		holder.lnDisLike.setTag(R.string.TAG_POS, position);
		
		holder.lnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Utils.isLogin){
					act.startActivity(new Intent(act,LoginActivity.class));
				}
				else {
					save_ans=(Answer)v.getTag(R.string.TAG_ANSWER);
					save_pos=(Integer)v.getTag(R.string.TAG_POS);
					if(save_ans.getIsLiked()==0)
						accessLikeDisLikeWebservice(WsUrl.URL_LIKE_ANSWER, 0, save_ans.getId());
					else accessLikeDisLikeWebservice(WsUrl.URL_UNLIKE_ANSWER, 1, save_ans.getId());
				}
			}
		});
		holder.lnDisLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Utils.isLogin){
					act.startActivity(new Intent(act,LoginActivity.class));
				}
				else {
					save_ans=(Answer)v.getTag(R.string.TAG_ANSWER);
					save_pos=(Integer)v.getTag(R.string.TAG_POS);
					if(save_ans.getIsDisliked()==0)
						accessLikeDisLikeWebservice(WsUrl.URL_DISLIKE_ANSWER, 2, save_ans.getId());
					else accessLikeDisLikeWebservice(WsUrl.URL_UNDISLIKE_ANSWER, 3, save_ans.getId());
				}
			}
		});
		
		return convertView;
	}

	//=======================================//
		public void accessLikeDisLikeWebservice(String url, int taskIndex, int answerId) {
			(new JsonReadTaskSolved()).execute(new String[] { url,
					Integer.toString(Utils.saveUser.getId()),
					Integer.toString(answerId), Integer.toString(taskIndex) });
		}

		public class JsonReadTaskSolved extends AsyncTask<String, Void, Integer> {
			ProgressDialog pd;
			int taskIndex;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd=new ProgressDialog(act);
				pd.setMessage("Processing...");
				pd.setCancelable(false);
				pd.show();
			}
			
			@Override
			protected Integer doInBackground(String... params) {
				taskIndex=Integer.parseInt(params[3]);
				
				List<NameValuePair> par = new ArrayList<NameValuePair>();
				par.add(new BasicNameValuePair("userId", params[1]));
				par.add(new BasicNameValuePair("answerId", params[2]));
				
				JSONParser jsonParser = new JSONParser();
				JSONObject json = jsonParser
						.makeHttpRequest(params[0], "POST", par);
				Log.d("Create Response", json.toString());
				try {
					int success = json.getInt(JsonTag.TAG_SUCCESS);
					msg=json.getString(JsonTag.TAG_MESSAGE);
					if (success == 1) {
						return 1;
					} else {
						return 0;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				return 0;
			}

			@Override
			protected void onPostExecute(Integer result) {
				if(pd!=null && pd.isShowing())  pd.dismiss();
				
//				Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
				if(result==1){
					
					switch(taskIndex){
					case 0: 
						save_ans.setCountLiked(save_ans.getCountLiked()+1);
						save_ans.setIsLiked(1);
						break;
					case 1:
						save_ans.setCountLiked(save_ans.getCountLiked()-1);
						save_ans.setIsLiked(0);
						break;
					case 2:
						save_ans.setCountDisliked(save_ans.getCountDisliked()+1);
						save_ans.setIsDisliked(1);
						break;
					case 3:
						save_ans.setCountDisliked(save_ans.getCountDisliked()-1);
						save_ans.setIsDisliked(0);
						break;
					}
					lst.set(save_pos, save_ans);
					notifyDataSetChanged();
				}
			}
		}
}
