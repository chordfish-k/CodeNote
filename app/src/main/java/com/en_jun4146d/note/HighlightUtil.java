package com.en_jun4146d.note;
import android.text.*;
import java.util.regex.*;
import android.text.style.*;
import android.graphics.*;
import android.widget.*;
import android.content.*;
import java.util.*;

public class HighlightUtil
{
	public static void set(Editable e){
		//remove(e);
		String str = e.toString();
		Pattern p = Pattern.compile("(^|\n)--code--\n(.|\n)+\n--code--($|\n)");
		Matcher mm, m = p.matcher(str);
		while(m.find()){
			Object[][] reg = new Object[][]{
				{"([^\u2E80-\u9FFFA-z0-9_]|^)(([1-9]\\d*\\.?\\d*[LlFf]?)|(0\\.\\d*[1-9][LlFf)]?))([^\u2E80-\u9FFFA-z0-9_]|$)", Color.RED, 2},//匹配十进制数值
				{"([^\u2E80-\u9FFFA-z0-9_]|^)(true|false|0x[0-9A-F]+|0\\d+)([^\u2E80-\u9FFFA-z0-9_]|$)", Color.RED, 2},//匹配八进制和十六进制数值
				{"(\\{|\\}|\\(|\\)|\\[|\\]|[;,.])", Color.argb(0xff, 0x00, 0xbf, 0xff), 1},//匹配{}()[];,.
				{"([^\u2E80-\u9FFFA-z0-9_]|^)([A-Z][A-z]*)([^\u2E80-\u9FFFA-z0-9_]|$)", Color.argb(0xff, 0x00, 0xbf, 0xff), 2},//匹配由大写字母开头的类
				{"([^\u2E80-\u9FFFA-z0-9_]|^|\\s)((if|else|switch|case|break|continue|default|for|while|do|try|catch|throw|const|inline|auto|short)|(long|int|char|double|folat|delete|new|bool|enum|explicit|asm|typedef|struct|class|public|private)|(protected|export|typeid|volatile|unsigned|signed|extern|static|void|volatile|goto|sizeof|return|register|union|this)|(friend|virtual|mutable|operator|template|typename|namespaceusing|and|and_eq|bitand|bitor|compl|not|not_eq|or|or_eq|xor|xor_eq))([^\u2E80-\u9FFFA-z0-9_]|$|\\s)", Color.argb(0xff, 0x1e, 0x90, 0xff), 2},
				{"([^\u2E80-\u9FFFA-z0-9_]|^|\\s)((boolean|import|extends|byte|finally|implements|abstract|assert|final|typeof|instanceof|interface)|(strictfp|synchronized|throws|transientpackage|super|native|function))([^\u2E80-\u9FFFA-z0-9_]|$|\\s)", Color.argb(0xff, 0x1e, 0x90, 0xff), 2},//匹配java，c++，javascript的关键字
				{"([+\\-/*?<>=/:&%|!])", Color.argb(0xff, 0x33, 0x99, 0), 1},//匹配常用运算符
				{"((\\/\\/.*($|\n))|(\\/\\*(.|\\s)*\\*\\/))", Color.argb(0xff, 0x33, 0x99, 0), 1},//匹配注释
				{"(\".*\")", Color.RED, 1},//匹配字符串
			};
			int flagLength = "--code--\n".length();
			String strv = str.substring(m.start() + flagLength, m.end() - flagLength);
			
			for(int i = 0; i < reg.length; i++){
				p = Pattern.compile((String)reg[i][0], Pattern.MULTILINE);
				mm = p.matcher(strv);
				while(mm.find()){
					e.setSpan(new ForegroundColorSpan((int)reg[i][1]), mm.start((int)reg[i][2]) + m.start() + flagLength, mm.end((int)reg[i][2]) + m.start() + flagLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
	}
	public static void remove(Editable e){
		e.setSpan(new ForegroundColorSpan(Color.BLACK), 0, e.toString().length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
}

