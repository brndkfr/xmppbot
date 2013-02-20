package de.raion.xmppbot.command.util;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.text.html.StyleSheet;

public class HtmlUtils {

	private HtmlUtils() {};
	
	public static String createAnchorTag(String text, String url) {
		StringBuilder builder= new StringBuilder();
		builder.append("<a href=\"").append(url).append("\">");
		builder.append(text).append("</a>");
		
		return builder.toString();
	}
	
	
	public static String createImageTag(String imageUrl, String alt) {
		StringBuilder builder = new StringBuilder();
		builder.append("<img src=\"").append(imageUrl).append("\" ");
		builder.append("alt=\"").append(alt).append("\">");
		return builder.toString();
	}
	
	public static String createImageTag(String imageUrl, String alt, String width, String height) {
		StringBuilder builder = new StringBuilder();
		builder.append("<img src=\"").append(imageUrl).append("\" ");
		builder.append("width=\"").append(width).append("\" height=\"").append(height).append("\" ");
		builder.append("alt=\"").append(alt).append("\">");
		return builder.toString();
	}
	
	public static String toDummyImageUrl(String text, int width, int height, Color foreground, Color background){
		StringBuilder builder = new StringBuilder("http://dummyimage.com/");
		builder.append(width).append("x").append(height).append("/");
		builder.append(Integer.toHexString(background.getRGB()).substring(2)).append("/");
		builder.append(Integer.toHexString(foreground.getRGB()).substring(2)).append(".png");
		builder.append("&text=").append(URLEncoder.encode(text));
		return builder.toString();
	}
	
	public static String toDummyImageUrl(int width, Color color){
		
		String hex = Integer.toHexString(color.getRGB()).substring(2);
		
		StringBuilder builder = new StringBuilder("http://dummyimage.com/");
		builder.append(width).append("/");
		builder.append(hex).append("/").append(hex).append(".png");
		return builder.toString();
	}
	
	
	public static Color convertHtmlColorNameToColor(String htmlColorName) {
		StyleSheet sheet = new StyleSheet();
		return sheet.stringToColor(htmlColorName.toLowerCase());
	
	}
	
	
	
	
}
