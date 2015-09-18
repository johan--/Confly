package com.bookdose.confly.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip extends AsyncTask<String, Integer, String> {
	
	private ProgressBar mProgress;
//	private Context context;
	
	private boolean isFirst = true;
	private String rootFolder;
	
//	private OnDownloadListener listener;
	
	private double fractionProgress = 1;

	public UnZip(Context context){

	}
	
	public UnZip(Context context, ProgressBar mProgress/*, OnDownloadListener listener*/) {
//		this.context = context;
//		this.mProgress = createLoadingDialog(context, false);
		this.mProgress = mProgress;
		this.mProgress.setMax(100);
//		this.listener = listener;
	}
	
	public UnZip(Context context, ProgressBar mProgress, double fractionProgress/*, OnDownloadListener listener*/) {
//		this.context = context;
//		this.mProgress = createLoadingDialog(context, false);
		this.mProgress = mProgress;
		this.mProgress.setMax(100);
		this.fractionProgress = fractionProgress;
//		this.listener = listener;
	}
	
	private void unzip(String zipFileName, String location, String savePath) {
		try  {
			
			final File file = new File(location, zipFileName);
			final FileInputStream fin = new FileInputStream(file);
			final ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			
			while ((ze = zin.getNextEntry()) != null) {
//				System.out.println("Decompress Unzipping " + ze.getName()); 
				
				final String fileName = ze.getName();
				if (isFirst) {
					rootFolder = savePath;
//					System.out.println("root folder "+rootFolder);
					isFirst = false;
				}

				if(ze.isDirectory()) {
					dirChecker(savePath, fileName);
				} else {
//					final long totalSize = ze.getSize();
					final long totalSize = file.length();
					int downloadedSize = 0;
					byte[] buffer = new byte[1024];
					int bufferLength = 0; //used to store a temporary size of the buffer
					
					final FileOutputStream fout = new FileOutputStream(savePath+"/"+fileName);
					
					int oldProg = 0;
					
					while ((bufferLength = zin.read(buffer)) > 0) {
						fout.write(buffer, 0, bufferLength);
						
						downloadedSize += bufferLength;
		                
		                final int prog = (int)((((double)downloadedSize/totalSize)*100) * fractionProgress);
		                
//		        		mProgress.setProgress(prog);
		                if (prog != oldProg) {
							mProgress.setProgress(mProgress.getProgress() + (prog - oldProg));
							oldProg = prog;
						}
					}
//					mProgress.setProgress(0);
					zin.closeEntry(); 
					fout.close(); 
				}
	      }
	      
		  zin.close();
//	      if (listener != null) {
//	    	  //listener.onUnzipComplete(rootFolder);
//	      }
	      
	    } catch(Exception e) {
//	    	if (listener != null) {
//	    		System.out.println("UnZip - unzip "+e.toString());
//	        	//listener.onUnzipError(e.toString());
//	        }
	    }
  } 
 
  private void dirChecker(String savePath, String dir) {
	  final File f = new File(savePath+"/"+dir);
	  if(!f.isDirectory()) {
		  f.mkdirs();
	  }
  }

  @Override
  protected String doInBackground(String... arg0) {
	  final String zipFile = arg0[0];
	  final String location = arg0[1];
	  final String savePath = arg0[2];
	  unzip(zipFile, location, savePath);
	  return null;
  }
  
}
