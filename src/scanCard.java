import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.github.sarxos.webcam.*;
import net.sourceforge.tess4j.*;

public class scanCard {

	public static void main(String[] args)  {
	
		//Webcam webcam = Webcam.getDefault();
		
		//Dimension x = new Dimension(640,480);
		//webcam.setViewSize(x);
		//webcam.open();
		//BufferedImage image = webcam.getImage();
		
		//try {
		//	ImageIO.write(image, "JPG", new File(".\\image\\test.jpg"));
		//} catch (IOException e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//}
		
		File rawPath = new File(".\\image\\raw");
		File [] rawFiles = rawPath.listFiles();
		for (int i = 0; i < rawFiles.length; i++){
			if (rawFiles[i].isFile()){
				
				BufferedImage rawImage = null;
				
				try {
					rawImage = ImageIO.read(rawFiles[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				BufferedImage cropOut = rawImage.getSubimage(28, 25, 322, 35);
				
				try {
					ImageIO.write(cropOut, "jpg", new File(".\\image\\crop\\" + Integer.toString(i) + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		File cropPath = new File(".\\image\\crop");
		File [] cropFiles = cropPath.listFiles();
		for (int j = 0; j < cropFiles.length; j++){
			if (cropFiles[j].isFile()){
				
				BufferedImage cropImage = null;
				
				try {
					cropImage = ImageIO.read(cropFiles[j]);
				} catch (IOException e) {
					e.printStackTrace();
				}
												
				BufferedImage ocrOut = new BufferedImage(322, 35, BufferedImage.TYPE_BYTE_GRAY);  
				Graphics g = ocrOut.getGraphics();  
				g.drawImage(cropImage, 0, 0, null);  
				g.dispose();
				
				RescaleOp rescaleOp = new RescaleOp(2.0f, 15, null);
				rescaleOp.filter(ocrOut, ocrOut);  // Source and destination are the same.
				
				try {
					ImageIO.write(ocrOut, "jpg", new File(".\\image\\ocr\\" + Integer.toString(j) + ".jpg"));
				} catch (IOException e) {
					System.err.println("there");
					e.printStackTrace();
				}
				
				cropFiles[j].delete();
					
			}
		}
		
		File ocrPath = new File(".\\image\\ocr");
	    File [] ocrFiles = ocrPath.listFiles();
	    for (int k = 0; k < ocrFiles.length; k++){
	        if (ocrFiles[k].isFile()){ //this line weeds out other directories/folders
	            
		        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
		        instance.setDatapath(".\\Tess4J\\tessdata");
		        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
		        
		        try {
		            String result = instance.doOCR(ocrFiles[k]);
		            char startChar = result.charAt(0);
		            // cards starting with I look just like lower case L
		            if (startChar == 'l'){
		            	result = "I" + result.substring(1);
		            }
		            result = result.replace("\n", "");
		            // hyphens are sometimes misinterpreted as tilde
		            result = result.replace("~", "-");
		            System.out.print(result + "\n");
		        } catch (TesseractException e) {
		            System.err.println(e.getMessage());
		        }
		        
		        ocrFiles[k].delete();
		        
	        }
	    }
	}
}