#include "Color.h"

/**
 * Constructs the Color
 * @param red The red intensity 0-255
 * @param green The green intensity 0-255
 * @param blue The blue intensity 0-255
 */
Color::Color(int red, int green, int blue)
{
	mRed = red;
	mGreen = green;
	mBlue = blue;
}

/**
 * Default Constructor
 */
Color::Color()
{

}

/**
 * Returns the red intensity
 * @return The red intensity
 */
int Color::getRed()
{
	return mRed;
}

/**
 * Returns the green intensity
 * @return The green intensity
 */
int Color::getGreen()
{
	return mGreen;
}

/**
 * Returns the blue intensity
 * @return The blue intensity
 */
int Color::getBlue()
{
	return mBlue;
}
