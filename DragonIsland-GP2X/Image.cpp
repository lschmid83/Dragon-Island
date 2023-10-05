#include "Image.h"

/**
 * Constructs the Image 
 * @param path The file input path
 */
Image::Image(char* filename)
{
	SDL_Surface *tmp = IMG_Load(filename);
	mImage = SDL_DisplayFormat(tmp); 	
	SDL_FreeSurface(tmp);

  	if (mImage == NULL) {
    	fprintf(stderr, "Error: '%s' could not be opened: %s\n", filename, IMG_GetError());
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
 * @return SDL_Surface containing the image data
 */
SDL_Surface* Image::getImage()
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
SDL_Surface* Image::getSubimage(int x, int y, int w, int h)
{
	//create clipped image and set transparent color (http://gpwiki.org/index.php/SDL:Tutorials:2D_Graphics)
	SDL_Surface *frame;
	frame = SDL_CreateRGBSurface(SDL_HWSURFACE, w, h, mImage->format->BitsPerPixel, 
		                         mImage->format->Rmask, mImage->format->Gmask, mImage->format->Bmask, 0 );
	SDL_SetColorKey(frame, SDL_SRCCOLORKEY, SDL_MapRGB(mImage->format, 168, 230, 29));
	SDL_Surface *mSubimage = SDL_DisplayFormat(frame); 				
	SDL_Rect srcR;
   	srcR.x = x;
	srcR.y = y;
	srcR.w = w;
	srcR.h = h;
	SDL_BlitSurface(mImage, &srcR, mSubimage, NULL);
	SDL_FreeSurface(frame);
	return mSubimage;
}

/**
 * Returns the width of the image
 * @return The width of the image
 */
int Image::getWidth()
{
	return mImage->w;
}

/**
 * Returns the height of the image
 * @return The height of the image
 */
int Image::getHeight()
{
	return mImage->h;
}

/**
 * Draws the image
 * @param g The drawing surface
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Image::draw(SDL_Surface *g, int x, int y)
{
	SDL_SetColorKey(mImage, SDL_SRCCOLORKEY, SDL_MapRGB(mImage->format, 168, 230, 29));	
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = mImage->w;  src.h = mImage->h;
  	dst.x = x;  dst.y = y;  dst.w =  mImage->w;  dst.h = mImage->h;
  	SDL_BlitSurface(mImage, &src, g, &dst);
}


/**
 * Returns an integer pixel in the default RGB color model (TYPE_INT_ARGB) and default sRGB colorspace
 *
 */
Color* Image::get_pixel32(int x, int y)
{    
	//http://www.gamedev.net/topic/296600-sdl---help-for-draw-on-and-get-pixelcolor/
 	Uint8 r;    Uint8 g;    Uint8 b; 
 	Uint32 *pixels = (Uint32 *)mImage->pixels;  
	Uint32 pixel = pixels[ ( y * mImage->w ) + x ];
	SDL_GetRGB(pixel, mImage->format, &r, &g, &b);    
	return new Color (r,g,b);
}

/**
 * Deallocates memory by destroying the Image
 */
Image::~Image()
{
	if(mImage)
	{
		SDL_FreeSurface(mImage);
		delete mImage;
	}
}
