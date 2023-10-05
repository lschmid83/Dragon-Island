#include "Image.h"

/**
 * Constructs the Image 
 * @param path The file input path
 * @param pixelFormat Format of pixels on the image
 */
Image::Image(char* filename, short pixelFormat)
{
	mImage = oslLoadImageFilePNG(filename, OSL_IN_RAM | OSL_SWIZZLED, pixelFormat);
  	if (mImage == NULL) {
    		fprintf(stderr, "Error: '%s' could not be opened\n", filename);
  	}
}

/** 
 * Default Constructor
 */
Image::Image()
{
};

/**
 * Returns the image data
 * @return OSL_IMAGE containing the image data
 */
OSL_IMAGE* Image::getImage()
{
	return mImage;
}

/**
 * Returns a subimage defined by a specified rectangular region. 
 * The returned BufferedImage shares the same data array as the original image. 
 * @param x The X coordinate of the upper-left corner of the specified rectangular region
 * @param y The Y coordinate of the upper-left corner of the specified rectangular region
 * @param w The width of the specified rectangular region
 * @param h The height of the specified rectangular region 
 * @return A BufferedImage that is the subimage of this BufferedImage. 
 */
OSL_IMAGE* Image::getSubimage(int x, int y, int w, int h)
{
	//create clipped image
	return oslCreateImageTileSize(mImage, x, y, w, h);
}

/**
 * Returns the width of the image
 * @return The width of the image
 */
int Image::getWidth()
{
	return mImage->sizeX;
}

/**
 * Returns the height of the image
 * @return The height of the image
 */
int Image::getHeight()
{
	return mImage->sizeY;
}

/**
 * Draws the image
 * @param g The drawing surface
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Image::draw(int x, int y)
{
	oslDrawImageXY(mImage, x, y);
}


/**
 * Returns an integer pixel in the default RGB color model (TYPE_INT_ARGB) and default sRGB colorspace
 *
 */
Color* Image::get_pixel32(int x, int y)
{    
	//Get a 32 bit (OSL_PF_8888) color for the pixel at x, y, whatever its pixelformat is.
	OSL_COLOR pixel = oslConvertColorEx(mImage->palette, OSL_PF_8888, mImage->pixelFormat, oslGetImagePixel(mImage, x, y));
	u8 red, green, blue, alpha;
	oslRgbaGet8888(pixel, red, green, blue, alpha);
	return new Color ((int)red, (int)green, (int)blue);
}

/**
 * Deallocates memory by destroying the Image
 */
Image::~Image()
{
	if(mImage)	
		oslDeleteImage(mImage);
}
