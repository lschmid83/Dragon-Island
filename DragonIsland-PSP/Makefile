TARGET = DragonIsland
OBJS = $(wildcard *.cpp) $(wildcard *.c)

INCDIR =
CFLAGS = -O2 -G0 -Wall -g
CXXFLAGS = $(CFLAGS) -fno-exceptions -fno-rtti -g
ASFLAGS = $(CFLAGS)

LIBDIR =
LDFLAGS =
LIBS = -lstdc++ -lc -lpspgu -lpsprtc  -losl -lpng -lz -lmad \
		-lpspsdk -lpspctrl -lpspumd -lpsprtc \
		-lpsppower -lpspgu -lpspaudiolib -lpspaudio -lm
PSPSDK=$(shell psp-config --pspsdk-path)
include $(PSPSDK)/lib/build.mak

EXTRA_TARGETS   = EBOOT.PBP
PSP_EBOOT_TITLE = DragonIsland
