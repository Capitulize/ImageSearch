/***************************************************************************************
*    Title: ImageSearch.java
*    Author: William Burcham
*    Date: 3-12-2018
*    Code version: V1
*    Availability: https://github.com/Capitulize/ImageSearch
*
***************************************************************************************/
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.*;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class ImageSearch extends RecursiveAction {

	private static final long serialVersionUID = 1L; //Eclipse would give me a warning and it didn't 
													 //hurt to add, so I looked it up and apparently
													 //The serialVersionUID is a universal version identifier 
													 //for a Serializable class.
	/**
	 * We declare important values here.
	 * 
	 * @param mSignificant - Used as a character identifier, 0 stands for waldo, 1 for wilma and etc.
	 * 		  image 	   - Used to store an image so that we can avoid race conditions using the same
	 * 						 2D array. I think that's how it works.
	 */
	private int mSignificant;
	private static Color[][] image;

	/**
	 * This is a constructor to create ImageSearch objects with character identifiers. 
	 * Main use is when invokeAll is called, we can then separate each character with an int value,
	 * and avoid any resource sharing that could lead to deadlock, starvation, or race conditions.
	 * 
	 * 
	 * @param significant - Value in the constructor to separate characters.
	 */
	public ImageSearch(int significant) {
		mSignificant = significant;
	}
			/**
		 	* Main line of program, we grab file input and set ImageSearch objects here. We also print
		 	* information to user here.
		 	* 
		 	* @throws IOException - In case the file cannot be found
		 	*/
	public static void main(String args[]) throws IOException {

		/**
		 * We grab input from the user here, such as where the image is.
		 */
		System.out.println("Starting up...");
		Scanner scan = new Scanner(System.in);
		String imageLocation;
		System.out.println("Please designate file path to image.");
		imageLocation = scan.nextLine();
		scan.close();

		//We time how long it takes to store the image in the color object.
		long imageTime = System.currentTimeMillis();
		image = imageArray(imageLocation);
		long imageEndTime = System.currentTimeMillis();

		/**
		 * Creation of all imageSearch objects for each character, each character belongs to a number
		 * of 0-4.
		 */
		ImageSearch waldoSearch = new ImageSearch(0);
		ImageSearch wilmaSearch = new ImageSearch(1);
		ImageSearch odlawSearch = new ImageSearch(2);
		ImageSearch wizardSearch = new ImageSearch(3);
		ImageSearch woofSearch = new ImageSearch(4);

		/**
		 * We time how long it takes for parallelization time, as well as invoking all the 
		 * character ImageSearch objects into their own threads. 
		 */
		long startTime = System.currentTimeMillis();
		invokeAll(waldoSearch, wilmaSearch, odlawSearch, wizardSearch, woofSearch);
		long endTime = System.currentTimeMillis();

		/**
		 * Timing is printed out here.
		 */
		System.out.println("Parallel time: " + (endTime - startTime) + " milliseconds.");
		System.out.println(
				"Total Execution time: " + ((imageEndTime - imageTime) + endTime - startTime) + " milliseconds.");
	}
	
	/**
	 * This methods main purpose is to prepare a 2D color array using java's color API
	 * and then return it so that we can compare the color of the pixels in the image to find the characters.
	 * 
	 * @param imageLocation - The location of the image on the computer the program is being ran on.
	 * @return cols -	Returns a color 2D array for use in later parts of the program.
	 */
	private static Color[][] imageArray(String imageLocation) {

		/**
		 * We read in the image here.
		 */
		BufferedImage image = null;
		try 
		{
			image = ImageIO.read(new File(imageLocation));
			System.out.println("Understood, performing...");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//Color object that takes the color of each pixel and stores them into a 2D array
		Color[][] cols = new Color[image.getWidth()][image.getHeight()];
		
		//Double for loop to sequence through image and create a color 2D array
		for (int x = 0; x < image.getWidth(); x++) 
		{
			for (int y = 0; y < image.getHeight(); y++) 
			{
				/**
				 * Colors are split here, this is a common algorithm that cuts each
				 * pixel into red, blue, and green to get their separate RGB. We then store them 
				 * in an x,y format for each of use.
				 */
				int color = image.getRGB(x, y);
				int red = (color & 0x00ff0000) >> 16;
				int green = (color & 0x0000ff00) >> 8;
				int blue = color & 0x000000ff;
				Color col = new Color(red, green, blue);
				cols[x][y] = col;

			}
		}
		return cols; //Returned color 2D array for future use in program.
	}
	/**
	 * The compute method that is called upon by invokeAll, what happens here is each character has their
	 * own int value in their own threads. For example, waldo is 0, so inside compute, waldoSearch is called,
	 * and inside waldoSearch, waldo is found inside the image.
	 * 
	 * They all do this at the same time and they do not share any resources this way. This is the most 
	 * optimal thing I can think of, and it crushes the time limit.
	 */
	@Override
	protected synchronized void compute() {
		if (mSignificant == 0) {
			waldoSearch();
		}

		if (mSignificant == 1) {
			wilmaSearch();
		}

		if (mSignificant == 2) {
			odlawSearch();
		}

		if (mSignificant == 3) {
			wizardSearch();
		}

		if (mSignificant == 4) {
			woofSearch();
		}
	}//end compute

	/**
	 * waldoSearch is the method that searches for waldo inside the image.
	 */
	public void waldoSearch() 
	{
		/**
		 * We create a new color object from the main image called waldoImageSearch, so as to avoid
		 * any overlap.
		 */
		Color[][] waldoImageSearch = image;
		//We create a small waldo 2x2 object that represents his exact pixel rgb.
		Color[][] waldo = new Color[2][2];
		waldo[0][0] = new Color(238, 21, 32);
		waldo[1][0] = new Color(255, 255, 255);
		waldo[0][1] = new Color(255, 255, 255);
		waldo[1][1] = new Color(1, 136, 226);
		/**
		 * This is where the main comparison happens, we go through waldoImage pixel by pixel, comparing
		 * each one to the waldo object. If that pixel rgb is the same as waldo[0][0], we go down the 
		 * if sequence until we find all four conditions fulfilled, at which point we have succeeded. 
		 */
		for (int x = 0; x < waldoImageSearch.length; x++) 
		{
			for (int y = 0; y < waldoImageSearch[x].length; y++) 
			{
				if (waldoImageSearch[x][y].equals(waldo[0][0]) && waldoImageSearch[x + 1][y].equals(waldo[1][0])
						&& waldoImageSearch[x][y + 1].equals(waldo[0][1])
						&& waldoImageSearch[x + 1][y + 1].equals(waldo[1][1])) 
				{
					//Prints waldo's location and thread ID. We also exit as we no longer need to look.
					System.out.println("Waldo has been found at: " + "[" + x + "] [" + y + "] pixels by thread ID: "
							+ Thread.currentThread().getId());
					return;
				}
			}
		}
	}// end waldoSearch

	/**
	 * wilmaSearch is the method that searches for wilma inside the image.
	 */
	public void wilmaSearch() 
	{
		/**
		 * We create a new color object from the main image called wilmaImageSearch, so as to avoid
		 * any overlap.
		 */
		Color[][] wilmaImageSearch = image;
		//We create a small wilma 2x2 object that represents her exact pixel rgb.
		Color[][] wilma = new Color[2][2];
		wilma[0][0] = new Color(255, 255, 255);
		wilma[1][0] = new Color(238, 21, 32);
		wilma[0][1] = new Color(1, 136, 226);
		wilma[1][1] = new Color(255, 255, 255);
		/**
		 * This is where the main comparison happens, we go through waldoImage pixel by pixel, comparing
		 * each one to the wilma object. If that pixel rgb is the same as wilma[0][0], we go down the 
		 * if sequence until we find all four conditions fulfilled, at which point we have succeeded. 
		 */
		for (int x = 0; x < wilmaImageSearch.length; x++) 
		{
			for (int y = 0; y < wilmaImageSearch[x].length; y++) 
			{
				if (wilmaImageSearch[x][y].equals(wilma[0][0]) && wilmaImageSearch[x + 1][y].equals(wilma[1][0])
						&& wilmaImageSearch[x][y + 1].equals(wilma[0][1])
						&& wilmaImageSearch[x + 1][y + 1].equals(wilma[1][1])) 
				{
					//Prints wilma's location and thread ID. We also exit as we no longer need to look.
					System.out.println("Wilma has been found at: " + "[" + x + "] [" + y + "] pixels by thread ID: "
							+ Thread.currentThread().getId());
					return;
				}
			}
		}
	}// end wilmaSearch
	
	/**
	 * odlawSearch is the method that searches for odlaw inside the image.
	 */
	public void odlawSearch() 
	{
		/**
		 * We create a new color object from the main image called odlawImageSearch, so as to avoid
		 * any overlap.
		 */
		Color[][] odlawImageSearch = image;
		//We create a small odlaw 2x2 object that represents his exact pixel rgb.
		Color[][] odlaw = new Color[2][2];
		odlaw[0][0] = new Color(253, 252, 3);
		odlaw[1][0] = new Color(32, 32, 32);
		odlaw[0][1] = new Color(32, 32, 32);
		odlaw[1][1] = new Color(253, 252, 3);
		/**
		 * This is where the main comparison happens, we go through waldoImage pixel by pixel, comparing
		 * each one to the odlaw object. If that pixel rgb is the same as odlaw[0][0], we go down the 
		 * if sequence until we find all four conditions fulfilled, at which point we have succeeded. 
		 */
		for (int x = 0; x < odlawImageSearch.length; x++) 
		{
			for (int y = 0; y < odlawImageSearch[x].length; y++) 
			{
				if (odlawImageSearch[x][y].equals(odlaw[0][0]) && odlawImageSearch[x + 1][y].equals(odlaw[1][0])
						&& odlawImageSearch[x][y + 1].equals(odlaw[0][1])
						&& odlawImageSearch[x + 1][y + 1].equals(odlaw[1][1])) 
				{
					//Prints odlaw's location and thread ID. We also exit as we no longer need to look.
					System.out.println("Odlaw has been found at: " + "[" + x + "] [" + y + "] pixels by thread ID: "
							+ Thread.currentThread().getId());
					return;
				}
			}
		}
	}// end odlawSearch

	/**
	 * wizardSearch is the method that searches for wizard inside the image.
	 */
	public void wizardSearch() 
	{

		/**
		 * We create a new color object from the main image called wizardImageSearch, so as to avoid
		 * any overlap.
		 */
		Color[][] wizardImageSearch = image;
		//We create a small wizard 2x2 object that represents his exact pixel rgb.
		Color[][] wizard = new Color[2][2];
		wizard[0][0] = new Color(216, 216, 216);
		wizard[1][0] = new Color(238, 21, 32);
		wizard[0][1] = new Color(238, 21, 32);
		wizard[1][1] = new Color(216, 216, 216);
		/**
		 * This is where the main comparison happens, we go through waldoImage pixel by pixel, comparing
		 * each one to the wizard object. If that pixel rgb is the same as wizard[0][0], we go down the 
		 * if sequence until we find all four conditions fulfilled, at which point we have succeeded. 
		 */
		for (int x = 0; x < wizardImageSearch.length; x++) 
		{
			for (int y = 0; y < wizardImageSearch[x].length; y++) 
			{
				if (wizardImageSearch[x][y].equals(wizard[0][0]) && wizardImageSearch[x + 1][y].equals(wizard[1][0])
						&& wizardImageSearch[x][y + 1].equals(wizard[0][1])
						&& wizardImageSearch[x + 1][y + 1].equals(wizard[1][1])) 
				{
					//Prints wizard's location and thread ID. We also exit as we no longer need to look.
					System.out.println("Wizard whitebeard has been " + "found at: [" + x + "] [" + y
							+ "] pixels by thread ID: " + Thread.currentThread().getId());
					return;
				}
			}
		}
	}// end wizardSearch

	/**
	 * woofSearch is the method that searches for woof inside the image.
	 */
	public void woofSearch() 
	{
		/**
		 * We create a new color object from the main image called woofImageSearch, so as to avoid
		 * any overlap.
		 */
		Color[][] woofImageSearch = image;
		//We create a small wizard 2x2 object that represents his exact pixel rgb.
		Color[][] woof = new Color[2][2];
		woof[0][0] = new Color(255, 255, 255);
		woof[1][0] = new Color(238, 21, 32);
		woof[0][1] = new Color(238, 21, 32);
		woof[1][1] = new Color(255, 255, 255);
		/**
		 * This is where the main comparison happens, we go through waldoImage pixel by pixel, comparing
		 * each one to the woof object. If that pixel rgb is the same as woof[0][0], we go down the 
		 * if sequence until we find all four conditions fulfilled, at which point we have succeeded. 
		 */
		for (int x = 0; x < woofImageSearch.length; x++) 
		{
			for (int y = 0; y < woofImageSearch[x].length; y++) 
			{
				if (woofImageSearch[x][y].equals(woof[0][0]) && woofImageSearch[x + 1][y].equals(woof[1][0])
						&& woofImageSearch[x][y + 1].equals(woof[0][1])
						&& woofImageSearch[x + 1][y + 1].equals(woof[1][1])) 
				{
					//Prints woof's location and thread ID. We also exit as we no longer need to look.
					System.out.println("Woof has been found at: " + "[" + x + "] [" + y + "] pixels by thread ID: "
							+ Thread.currentThread().getId());
					return;
				}
			}
		}
	}// end woof

}// end ImageSearch
