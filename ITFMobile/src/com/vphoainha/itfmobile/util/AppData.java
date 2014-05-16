package com.vphoainha.itfmobile.util;

import java.util.ArrayList;
import java.util.List;

import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.model.User;

public class AppData {
public static final int DEFAUT_VALUE=-1; 
	
	public static boolean isLogin=false;
	public static User saveUser=null;
	
	public static List<Folder> allFolders;
	public static List<Folder> folders;
	
	public static List<Folder> getSubFolders(int folderId){
		List<Folder> temp=new ArrayList<Folder>();
		for(Folder f:allFolders)
			if(f.getParrentId()==folderId)
				temp.add(f);
		return temp;
	}
	
	
}
