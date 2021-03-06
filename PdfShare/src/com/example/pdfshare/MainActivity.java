package com.example.pdfshare;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity implements Runnable {

	private Intent mShareIntent;
	
	private ByteArrayOutputStream os;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/** PDF Gen should run in own thread to not slow the GUI */
	public void makeAndSharePDF(View buttonSource) {
		new Thread(this).start();
	}	
		
	public void run() {
		
		// Create a shiny new (but blank) PDF document in memory
		PdfDocument document = new PdfDocument();

		// crate a page description
		PageInfo pageInfo = new PageInfo.Builder(300, 300, 1).create();

		// create a new page from the PageInfo
		Page page = document.startPage(pageInfo);

		// repaint the user's text into the page
		View content = findViewById(R.id.textArea);
		content.draw(page.getCanvas());

		// do final processing of the page
		document.finishPage(page);

		// Here you could add more pages in a longer doc app, but you'd have
		// to handle page-breaking yourself in e.g., a word processor

		// Now write the PDF document to a file
		os = new ByteArrayOutputStream();
		try {
			document.writeTo(os);
			document.close();
			os.close();
		} catch (IOException e) {
			throw new RuntimeException("Error generating file", e);
		}

		shareDocument(os);

	}

	private void shareDocument(ByteArrayOutputStream os) {
		mShareIntent = new Intent();
		mShareIntent.setAction(Intent.ACTION_SEND);
		mShareIntent.setType("application/pdf");
		mShareIntent.putExtra(getClass().getPackage().getName() + "." + "SendPDF", os.toByteArray());
		startActivity(mShareIntent);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
