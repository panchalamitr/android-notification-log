package org.hcilab.projects.nlogx.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hcilab.projects.nlogx.R;
import org.hcilab.projects.nlogx.firebase.FirebaseConst;
import org.hcilab.projects.nlogx.firebase.MyNotification;
import org.hcilab.projects.nlogx.misc.Const;
import org.hcilab.projects.nlogx.misc.DatabaseHelper;
import org.hcilab.projects.nlogx.misc.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

class BrowseAdapter extends RecyclerView.Adapter<BrowseViewHolder> {

	private final static int LIMIT = Integer.MAX_VALUE;
	private final static String PAGE_SIZE = "20";

	private DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());

	private Activity context;
	private ArrayList<DataItem> data = new ArrayList<>();
	private HashMap<String, Drawable> iconCache = new HashMap<>();
	private Handler handler = new Handler();

	private String lastDate = "";
	private boolean shouldLoadMore = true;

	BrowseAdapter(Activity context) {
		this.context = context;
		//loadMore(Integer.MAX_VALUE);
		readNotification(context);
	}

	@NonNull
	@Override
	public BrowseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
		BrowseViewHolder vh = new BrowseViewHolder(view);
		vh.item.setOnClickListener(v -> {
			String id = (String) v.getTag();
			if(id != null) {
				Intent intent = new Intent(context, DetailsActivity.class);
				intent.putExtra(DetailsActivity.EXTRA_ID, id);
				if(Build.VERSION.SDK_INT >= 21) {
					Pair<View, String> p1 = Pair.create(vh.icon, "icon");
					@SuppressWarnings("unchecked") ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, p1);
					context.startActivityForResult(intent, 1, options.toBundle());
				} else {
					context.startActivityForResult(intent, 1);
				}
			}
		});
		return vh;
	}

	@Override
	public void onBindViewHolder(@NonNull BrowseViewHolder vh, int position) {
		DataItem item = data.get(position);

		if(iconCache.containsKey(item.getPackageName()) && iconCache.get(item.getPackageName()) != null) {
			vh.icon.setImageDrawable(iconCache.get(item.getPackageName()));
		} else {
			vh.icon.setImageResource(R.mipmap.ic_launcher);
		}

		vh.item.setTag("" + item.getId());
		vh.name.setVisibility(View.VISIBLE);
		vh.name.setText(item.getPackageName());
		vh.text.setVisibility(View.VISIBLE);
		vh.text.setText(item.getText());
//		if(item.getPreview().length() == 0) {
//			vh.preview.setVisibility(View.GONE);
//			vh.text.setVisibility(View.VISIBLE);
//			vh.text.setText(item.getText());
//		} else {
//			vh.text.setVisibility(View.GONE);
//			vh.preview.setVisibility(View.VISIBLE);
//			vh.preview.setText(item.getPreview());
//		}

//		if(item.shouldShowDate()) {
//			vh.date.setVisibility(View.VISIBLE);
//			vh.date.setText(item.getDate());
//		} else {
//			vh.date.setVisibility(View.GONE);
//		}

		if(position == getItemCount() - 1) {
			//loadMore(item.getId());
			//loadFromFirebase();
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	private void loadFromFirebase(){
		ArrayList<MyNotification> myNotificationArrayList = FirebaseConst.readNotification(context);
		for (int i = 0; i < myNotificationArrayList.size(); i++) {
			DataItem dataItem = new DataItem();
			MyNotification myNotification = myNotificationArrayList.get(i);
			dataItem.packageName = myNotification.getPackageName();
			dataItem.text = myNotification.getText();
			data.add(dataItem);

		}
	}
	private void readNotification(Context context){
		//ArrayList<MyNotification> myNotificationArrayList = new ArrayList<>();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference mPostReference = database.getReference(FirebaseConst.NAME);
		ValueEventListener postListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Get Post object and use the values to update the UI
				for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
					MyNotification myNotification = postSnapshot.getValue(MyNotification.class);
					//myNotificationArrayList.add(post);
					DataItem dataItem = new DataItem();
					dataItem.setPackageName(myNotification.getPackageName());
					dataItem.setText(myNotification.getText());
					data.add(dataItem);
				}
				Collections.reverse(data);
				notifyDataSetChanged();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				// Getting Post failed, log a message
				databaseError.toException();
			}
		};
		mPostReference.addValueEventListener(postListener);
		//return myNotificationArrayList;
	}

	private void loadMore(long afterId) {
		if(!shouldLoadMore) {
			if(Const.DEBUG) System.out.println("not loading more items");
			return;
		}

		if(Const.DEBUG) System.out.println("loading more items");
		int before = getItemCount();
		try {
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			SQLiteDatabase db = databaseHelper.getReadableDatabase();

			Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
					new String[] {
							DatabaseHelper.PostedEntry._ID,
							DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
					},
					DatabaseHelper.PostedEntry._ID + " < ?",
					new String[] {""+afterId},
					null,
					null,
					DatabaseHelper.PostedEntry._ID + " DESC",
					PAGE_SIZE);

			if(cursor != null && cursor.moveToFirst()) {
				for(int i = 0; i < cursor.getCount(); i++) {
				//	DataItem dataItem = new DataItem(context, cursor.getLong(0), cursor.getString(1));
					DataItem dataItem = new DataItem();
					String thisDate = dataItem.getDate();
					if(lastDate.equals(thisDate)) {
						dataItem.setShowDate(false);
					}
					lastDate = thisDate;

					data.add(dataItem);
					cursor.moveToNext();
				}
				cursor.close();
			}

			db.close();
			databaseHelper.close();
		} catch (Exception e) {
			if(Const.DEBUG) e.printStackTrace();
		}
		int after = getItemCount();

		if(before == after) {
			if(Const.DEBUG) System.out.println("no new items loaded: " + getItemCount());
			shouldLoadMore = false;
		}

		if(getItemCount() > LIMIT) {
			if(Const.DEBUG) System.out.println("reached the limit, not loading more items: " + getItemCount());
			shouldLoadMore = false;
		}

		handler.post(() -> notifyDataSetChanged());
	}

	private class DataItem {

		public void setId(long id) {
			this.id = id;
		}

		private long id;
		private String packageName;
		private String appName;
		private String text;
		private String preview;

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setPreview(String preview) {
			this.preview = preview;
		}

		public void setDate(String date) {
			this.date = date;
		}

		private String date;
		private boolean showDate;

//		DataItem(Context context, long id, String str) {
//			this.id = id;
//			try {
//				JSONObject json = new JSONObject(str);
//				packageName = json.getString("packageName");
//				appName = Util.getAppNameFromPackage(context, packageName, false);
//				text = str;
//
//				String title = json.optString("title");
//				String text = json.optString("text");
//				preview = (title + "\n" + text).trim();
//
//				if(!iconCache.containsKey(packageName)) {
//					iconCache.put(packageName, Util.getAppIconFromPackage(context, packageName));
//				}
//
//				date = format.format(json.optLong("systemTime"));
//				showDate = true;
//			} catch (JSONException e) {
//				if(Const.DEBUG) e.printStackTrace();
//			}
//		}

		public long getId() {
			return id;
		}

		public String getPackageName() {
			return packageName;
		}

		public String getAppName() {
			return appName;
		}

		public String getText() {
			return text;
		}

		public String getPreview() {
			return preview;
		}

		public String getDate() {
			return date;
		}

		public boolean shouldShowDate() {
			return showDate;
		}

		public void setShowDate(boolean showDate) {
			this.showDate = showDate;
		}

	}

}
