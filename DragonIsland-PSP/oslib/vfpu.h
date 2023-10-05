#ifndef __OSL_VFPU_H__
#define __OSL_VFPU_H__

#ifdef __cplusplus
extern "C" {
#endif

/** @defgroup vfpu VFPU functions

	Functions using the Vector Floating Point Unit (VFPU). Please note that the PC does not support the VFPU, using it results in extremely slow performance, and some of the calls may not be emulated
	properly, if at all.
	@{
*/

//PC has no VFPU
#ifdef PSP

//	#include "vfpu_ops.h"
	#include <psptypes.h>

	extern float vfpu_vars[4] __attribute__((aligned(64)));

#endif

//Angle in radians, rayon = multiplication of the number.
/** Calculates the sine of an angle using the VFPU (very fast). Please note that #oslSin also uses the VFPU but first converts the angle from degrees to radians.
	\param angle
		Angle in radians
	\param rayon
		Radius
	\return
		sin(angle) * rayon
*/
extern float vfpu_sinf(float angle, float rayon);

/** Calculates the cosine of an angle using the VFPU (very fast). Please note that #oslCos also uses the VFPU but first converts the angle from degrees to radians.
	\param angle
		Angle in radians
	\param rayon
		Radius
	\return
		cos(angle) * rayon
*/
extern float vfpu_cosf(float angle, float rayon);


#ifdef __cplusplus
}
#endif

#endif
