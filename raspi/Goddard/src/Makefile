CFLAGS=	-W -Wall -I../../include -pthread -g

all:
	OS=`uname`; \
	  test "$$OS" = Linux && LIBS="-ldl" ; \
	  $(CC) $(CFLAGS) goddard.c ../../mongoose/mongoose.c ../../maestro/src/maestro.c ../../utilities/src/log.c  $$LIBS $(ADD) -o goddard;


clean:
	rm -rf hello upload goddard websocket chat *.exe *.dSYM *.obj
