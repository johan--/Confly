package com.demo.gridviewdemo;

import java.io.Serializable;

public class GridItem implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String imageId;
	
	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * @return the imageId
	 */
	public String getImagePath()
	{
		return imageId;
	}
	
	/**
	 * @param imageId the imageId to set
	 */
	public void setImagePath(String imageId)
	{
		this.imageId = imageId;
	}
}
