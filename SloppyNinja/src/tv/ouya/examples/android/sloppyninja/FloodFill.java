package tv.ouya.examples.android.sloppyninja;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class FloodFill {
	
	public void floodFill(Bitmap  image, Point node, String targetColor, int replacementColor) {
		int width = image.getWidth();
		int height = image.getHeight();
		long target = Long.valueOf(targetColor, 16);//
		//long replacement = Long.valueOf(replacementColor, 16);
		//int counter = 0;
		int targetInt = (int)target;
		int replacementInt = replacementColor ;
		//Log.i("thread","here2  "+targetInt+"   "+replacementInt);
		if (targetInt != replacementInt) {
			Queue<Point> queue = new LinkedList<Point>();
			do {
					int x = node.x;
					int y = node.y;
					//Log.i("flood","   "+x+"   "+image.getPixel(x - 1, y)+"   "+targetInt);
					while (x > 0 && image.getPixel(x - 1, y) == targetInt) {
						x--;
						//Log.i("flood","xxx1 = "+ (x));
					}
					boolean spanUp = false;
					boolean spanDown = false;
					while (x < width && image.getPixel(x, y) == targetInt) {
						//int rep = (int) replacement;
						//Log.i("flood",""+ (++counter));
						//Log.i("flood","xxx2 = "+image.getPixel(x, y)+ "   "+targetInt);
						image.setPixel(x, y, replacementInt);
						if (!spanUp && y > 0 && image.getPixel(x, y - 1) == targetInt) {
							queue.add(new Point(x, y - 1));
							spanUp = true;
						} 
						else 
							if (spanUp && y > 0 && image.getPixel(x, y - 1) != targetInt) {
								spanUp = false;
							}
						
						if (!spanDown && y < height - 1 && image.getPixel(x, y + 1) == targetInt) {
							queue.add(new Point(x, y + 1));
							spanDown = true;
						} 
						else 
							if (spanDown && y < height - 1 && image.getPixel(x, y + 1) != targetInt) {
								spanDown = false;
							}
						x++;
					}
				} //do
			while ((node = queue.poll()) != null);
			Log.i("flood", "finidhed");
		}//if
	}//constructor 
	
}//class
