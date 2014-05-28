package com.vphoainha.itfmobile.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;

public class TopMemberFragment extends Fragment {
	static View view;	
	
//	private List<Thread> threads;
	
	private ListView lvMemberList;
	
	String msg;

	public static TopMemberFragment newInstance() {
		TopMemberFragment searchFragment=new TopMemberFragment();
		return searchFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.frag_topmember, container, false);
		} catch (InflateException e) {
		}

		((MainActivity) getActivity()).changeMainTitleBarText("Top members");
		lvMemberList=(ListView)view.findViewById(R.id.lvMemberList);
		
		
		
		
		return view;
	}
	
	
}

