package com.en_jun4146d.note;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.view.View.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;
import java.util.logging.*;
import android.text.*;
import android.view.inputmethod.*;

public class Editor extends Activity
{
	private TextView fileName, n;
	private EditText editor;
	private ImageView bfinish;
	private ImageButton bundo, bredo, bcode;
	private boolean showed, openHighLight,longClicked;
	private PerformEdit per;
	private String txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit);
		
		showed = false;
		openHighLight = false;
		longClicked = false;
		
		Intent in = getIntent();
		String title = in.getStringExtra("title");
		txt = in.getStringExtra("txt");
		
		fileName = (TextView)findViewById(R.id.filename);
		editor = (EditText)findViewById(R.id.editor);
		n = (TextView)findViewById(R.id.editn);
		bfinish = (ImageView)findViewById(R.id.b_finish);
		bundo = (ImageButton)findViewById(R.id.b_undo);
		bredo = (ImageButton)findViewById(R.id.b_redo);
		bcode = (ImageButton)findViewById(R.id.b_code);
		
		per = new PerformEdit(editor);
		
		fileName.setText(title);
		editor.setText(txt);
		
		per.setDefaultText(txt);
		
		n.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					//输入框获取焦点
					editor.setFocusable(true);
					editor.setFocusableInTouchMode(true);
					editor.requestFocus();
					//召唤输入法
					InputMethodManager imm = ( InputMethodManager ) editor.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE ); 
					imm.showSoftInput(editor,InputMethodManager.SHOW_FORCED); 
					String str = editor.getText().toString();
					if(str.length() == 0 || str.equals("")){
						editor.setSelection(0);
						return;
					}
					editor.setSelection(editor.getText().toString().length()-1);
				}
			});
		
		bfinish.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				FileUtil.writeFile(fileName.getText().toString(), FileUtil.getTime() + "\n" + editor.getText().toString());
				Toast.makeText(Editor.this, getString(R.string.save_done), 0).show();
				txt = editor.getText().toString();
			}
		});
		
		bundo.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					per.undo();
				}
			});
			
		bredo.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					per.redo();
				}
			});
		bcode.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					setHighlight(true);
				}
			});
		bcode.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(View p1)
				{
					if(!openHighLight)
						return false;
					HighlightUtil.remove(editor.getText());
					Toast.makeText(Editor.this, getString(R.string.HL_remove), 0).show();
					longClicked = true;
					return false;
				}
		});
	}

	@Override
	public void finish()
	{
		if(txt.equals(editor.getText().toString())){
			Intent it = new Intent(Editor.this, MainActivity.class);
			startActivity(it);
			Editor.super.finish();
			return;
		}
		EditDialog.Builder d= new EditDialog.Builder(this);
		d.setTitle("警告");
		d.setMessage("你还没有保存，要离开吗？");
		d.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int i){
					Intent it = new Intent(Editor.this, MainActivity.class);
					startActivity(it);
					Editor.super.finish();
				}
			});
		d.setPositiveButton(getString(R.string.no),null);
		d.create().show();
	}
	
	public void insert(View v){
		Button b = (Button)v;
		String str = b.getText().toString();
		int indexs = editor.getSelectionStart();
		int indexe = editor.getSelectionEnd();
		
		if(indexe > indexs)
			editor.getText().delete(indexs, indexe);
		editor.setSelection(indexs);
		editor.getText().insert(indexs, str);
		editor.setSelection(indexs + str.length());
		
		HighlightUtil.set(editor.getText());
	}
	
	private void setHighlight(boolean show){
		if(openHighLight)
			if(longClicked){
				longClicked = false;
				return;
			}
		if(!openHighLight){
			HighlightUtil.remove(editor.getText());
			HighlightUtil.set(editor.getText());
			openHighLight = true;
			if(show)
				Toast.makeText(Editor.this, getString(R.string.HL_refresh) +"\n"+ getString(R.string.HL_tip), 0).show();
		}else{
			HighlightUtil.remove(editor.getText());
			HighlightUtil.set(editor.getText());
			if(show)
				Toast.makeText(Editor.this, getString(R.string.HL_refresh), 0).show();
			longClicked = false;
		}
	}
}
