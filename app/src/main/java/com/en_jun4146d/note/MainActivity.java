package com.en_jun4146d.note;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.io.*;
import javax.xml.namespace.*;
import android.content.*;
import android.view.View.*;
import android.util.*;

public class MainActivity extends Activity 
{
	private ListView l;
	private ImageView floatButton, bnew, bdelete, brefresh;
	private LinearLayout content;
	private EditText edit;
	private EditDialog.Builder dialog;
	
	private ArrayList<HashMap<String,Object>> data;
	private SimpleAdapter adapter;
	private String[] from, title, time;
	private int[] to;
	
	private String path = FileUtil.path;
   
	private boolean showed/*按钮显示*/, deleteMode/*删除模式*/;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		showed = false;
		deleteMode = false;
		
		l = (ListView)findViewById(R.id.list);
		floatButton = (ImageView)findViewById(R.id.floatbutton);
		bnew = (ImageView)findViewById(R.id.b_new);
		bdelete = (ImageView)findViewById(R.id.b_delete);
		brefresh = (ImageView)findViewById(R.id.b_refresh);
		content = (LinearLayout)findViewById(R.id.b_content);
		
		refreshList();
		
		l.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					closeButtons();
					if(!deleteMode){
						Intent it = new Intent(MainActivity.this, Editor.class);
						it.putExtra("title", title[p3]);
						String txt = FileUtil.readFile(title[p3], 1);
						it.putExtra("txt", txt);
						startActivity(it);
						finish();
					}else{
						File f = new File(path + "/" + title[p3]);
						if(f.delete()){
							Toast.makeText(MainActivity.this, getString(R.string.delete_success), 0).show();
							refreshList();
						}else
							Toast.makeText(MainActivity.this, getString(R.string.delete_fail), 0).show();
						deleteMode = false;
					}
				}
		});
		
		floatButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					if(!deleteMode){
						if(showed){
							closeButtons();
						}else{
							showButtons();
						}
					}else{
						deleteMode = false;
						Toast.makeText(MainActivity.this, getString(R.string.back_from_delete), 0).show();
					}
				}
			});
		bnew.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					closeButtons();
					//打开输入框
					dialog = new EditDialog.Builder(MainActivity. this);
					dialog.setTitle(getString(R.string.enter_file_name));
					LayoutInflater lif = (LayoutInflater)MainActivity.this.getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);
					View xml = lif.inflate(R.layout.edittext, null);
					edit = (EditText)xml.findViewById(R.id.edittext);
					edit.setSingleLine(true);
					dialog.setPositiveButton(getString(R.string.no), null);
					dialog.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface d, int i){
							String str = edit.getText().toString();
							if(str.length() == 0 || str.equals("")){
								return;
							}
							FileUtil.writeFile(str, FileUtil.getTime());
							Toast.makeText(MainActivity.this, getString(R.string.new_file) + str + getString(R.string.done), 0).show();
							refreshList();
						}
					});
					dialog.setContentView(xml).create().show();
				}
			});
		bdelete.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					closeButtons();
					deleteMode = true;
					Toast.makeText(MainActivity.this, getString(R.string.delete_tip), 0).show();
				}
			});
		brefresh.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					refreshList();
					Toast.makeText(MainActivity.this, getString(R.string.refresh_done), 0).show();
					closeButtons();
				}
			});
    }
	
	private void closeButtons(){//隐藏按钮
		bnew.setVisibility(View.INVISIBLE);
		bdelete.setVisibility(View.INVISIBLE);
		brefresh.setVisibility(View.INVISIBLE);
		floatButton.setRotation(0);
		showed = false;
	}
	private void showButtons(){
		bnew.setVisibility(View.VISIBLE);
		bdelete.setVisibility(View.VISIBLE);
		brefresh.setVisibility(View.VISIBLE);
		floatButton.setRotation(90);
		showed = true;
	}
	
	private void refreshList(){//刷新列表
		if(checkAppDir()){
			title = listFiles();
			try{
				time = getTime(title);
			}catch (IOException e){}
		}

		from = new String[]{"title", "time"};
		to = new int[]{R.id.title, R.id.time};
		data = new ArrayList<HashMap<String,Object>>();

		for(int i=0; i<title.length; i++){
			HashMap<String,Object> temp = new HashMap<String,Object>();
			temp.put(from[0], title[i]);
			temp.put(from[1], time[i]);
			data.add(temp);
		}
		//添加列表适配
		adapter = new SimpleAdapter(this, data, R.layout.listitem, from, to);
		l.setAdapter(adapter);
	}
	
	private String[] listFiles(){//获取文件夹内文件的名字
		File f = new File(path);
		File[] fs = f.listFiles();
		String[] names = new String[fs.length];
		for(int i = 0; i < fs.length; i++){
			names[i] = fs[i].getName();
		}
		return names;
	}
	
	private String[] getTime(String[] names) throws IOException{//获取第一行记录的时间
		String[] times = new String[names.length];
		for(int i = 0; i < names.length; i++){
			File f = new File(path + "/" + names[i]);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			times[i] = br.readLine();
		}
		return times;
	}
	
	private boolean checkAppDir(){//检查文件夹是否存在
		File f = new File(path);
		if(!f.exists()){
			f.mkdir();
			return false;
		}
		return true;
	}
	
}
