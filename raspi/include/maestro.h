/* 
	maestro.h
*/

extern int maestroGetPosition(int fd, unsigned char channel);
extern int maestroSetTarget(int fd, unsigned char channel, unsigned short target);
extern int maestroInit(int *fd);
extern void maestroTerm(int fd);


