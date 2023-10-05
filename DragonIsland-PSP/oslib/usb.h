#include <pspusb.h>
#include <pspusbstor.h>
#include <pspsdk.h>

#define oslGetUsbState sceUsbGetState

enum {OSL_USB_ACTIVATED=PSP_USB_ACTIVATED, OSL_USB_CABLE_CONNECTED=PSP_USB_CABLE_CONNECTED, OSL_USB_CONNECTION_ESTABLISHED=PSP_USB_CONNECTION_ESTABLISHED};

extern int oslInitUsbStorage();
extern void oslStartUsbStorage();
extern void oslStopUsbStorage();
extern int oslDeinitUsbStorage();
extern __attribute__ ((constructor)) void oslLoaderInit();
