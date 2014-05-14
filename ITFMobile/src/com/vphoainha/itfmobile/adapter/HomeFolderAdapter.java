package com.vphoainha.itfmobile.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class HomeFolderAdapter extends BaseAdapter {

	public class IndexPath {
		public int section;
		public int row;
	}
	
	public interface SectionListAdapterAdapterDelegate {
		public int sectionCount();
		public int rowsInSection(int section);
		public View viewForRowAtIndexPath(IndexPath path);
		public View viewForHeaderInSection(int section);
		public void itemSelectedAtIndexPath(IndexPath path);
		public void itemSelectedAtSection(int section);
	}

	public SectionListAdapterAdapterDelegate delegate;
	
	public OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			
			IndexPath path = HomeFolderAdapter.this.indexPathForPosition(position);
			
			if(path.row >= 0)
			{
				delegate.itemSelectedAtIndexPath(path);
			}
			else delegate.itemSelectedAtSection(path.section);
		}
	};
	
	@Override
	public int getCount() {
		
		int count = 0;
		int sections = delegate.sectionCount();
		
		for(int i = 0; i < sections; i++)
		{
			count += delegate.rowsInSection(i);
		}
		
		return count + sections;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public IndexPath indexPathForPosition(int position)
	{
		IndexPath path = new IndexPath();
		//Log.i("%%%%%%position%%%%%", position+"");
		for(int i = 0; i < delegate.sectionCount(); i++)
		{
			int rows = delegate.rowsInSection(i);
//			Log.i("%%%%%%%%%%%%", rows+"");
			if(position - rows - 1 >= 0)
			{
				position -= (rows + 1);
			}
			else
			{
				path.section = i;
				path.row = position - 1;
				return path;
			}
		}
		
		return path;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final IndexPath path = indexPathForPosition(position);
//		Log.i("======getView====position="+position, path.section+""+path.row);
		if(path.row == -1)
		{
			return delegate.viewForHeaderInSection(path.section);
		}
		else
		{
			return delegate.viewForRowAtIndexPath(path);
		}
	}
}
