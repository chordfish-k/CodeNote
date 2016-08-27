package com.en_jun4146d.note;
import java.io.*;
import java.util.*;
import java.text.*;

public class FileUtil
{
	public static String path = "mnt/sdcard/note";
	
	public static String readFile(String name){
		return readFile(name, 0);
	}
	
	public static String readFile(String name, int beginLine){
		String txt = "";
		File f = new File(path + "/" + name);
		FileReader fr = null;
		try{
			fr = new FileReader(f);
		}catch (FileNotFoundException e){}
		BufferedReader br = new BufferedReader(fr);
		try{
			String temp = br.readLine();
			for(int i = 0; i < beginLine; i++){
				temp = br.readLine();
			}
			while(temp != null){
				txt += temp + "\n";
				temp = br.readLine();
			}
			br.close();
		}catch (IOException e){}
		return txt;
	}
	
	public static void writeFile(String name, String txt){
		File f = new File(path + "/" + name);
		try{
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(txt);
			bw.close();
		}catch (IOException e){}
	}
	
	public static String getTime(){
		Date date = new Date();
		SimpleDateFormat a = new SimpleDateFormat("yyyy-MM-dd");
		return a.format(date);
	}
}
