package edu.umd.lorelei.utils;

import com.google.gson.annotations.SerializedName;

public class InputDoc
{
	@SerializedName(value="ID", alternate= {"id", "Id", "iD"})
	public String ID;
	public String content;
	public int label;
}
