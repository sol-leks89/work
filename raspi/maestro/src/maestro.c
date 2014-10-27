// NOTE: The Maestro's serial mode must be set to "USB Dual Port".
// NOTE: You must change the 'const char * device' line below.
 
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <termios.h>

#include "utiljww.h"


 
// Gets the position of a Maestro channel.
// See the "Serial Servo Commands" section of the user's guide.
int maestroGetPosition(int fd, unsigned char channel)
{
  unsigned char command[] = {0x90, channel};
  dumpBuf("GetPosition, command", command, sizeof(command));
  if(write(fd, command, sizeof(command)) == -1)
  {
    log_error("error writing");
    return -1;
  }
   
  unsigned char response[2];
  if(read(fd,response,2) != 2)
  {
    log_error("error reading");
    return -1;
  }
  dumpBuf("GetPosition, response", response, sizeof(response));
   
  return response[0] + 256*response[1];
}

// Gets the PARAMETER for the given channel
// See the "Serial Servo Commands" section of the user's guide.
int maestroGetParameter(int fd, unsigned char channel, unsigned char *retBuf)
{
  unsigned char command[] = {0x90, channel};
  dumpBuf("GetParameter, command", (unsigned char *) command, sizeof(command));

  if(write(fd, command, sizeof(command)) == -1)
  {
    log_error("error writing");
    return -1;
  }
   
  unsigned char response[2];
  if(read(fd,response,2) != 2)
  {
    log_error("error reading");
    return -1;
  }
   //response[0] + 256*response[1]
  return 0;
}
 
// Sets the target of a Maestro channel.
// See the "Serial Servo Commands" section of the user's guide.
// The units of 'target' are quarter-microseconds.
int maestroSetTarget(int fd, unsigned char channel, unsigned short target)
{
  unsigned char command[] = {0x84, channel, target & 0x7F, target >> 7 & 0x7F};
  dumpBuf("SerTarget, command", command, sizeof(command));
  if (write(fd, command, sizeof(command)) == -1)
  {
    log_error("error writing");
    return -1;
  }
  return 0;
}
 
int maestroInit(int *fd)
{
    // Open the Maestro's virtual COM port.
  //const char * device = "\\\\.\\USBSER000";  // Windows, "\\\\.\\COM6" also works
  //const char * device = "/dev/ttyACM0";  // Linux
  const char * device = "/dev/ttyACM0";  // Raspi
  //const char * device = "/dev/cu.usbmodem00034567"; // Mac OS X
  *fd = open(device, O_RDWR | O_NOCTTY);
  if (*fd == -1)
  {
    char errmsg[80];
    sprintf(errmsg, "Can;t open %s", device);
    log_error((char *) errmsg);
    return 1;
  }
  else
  {
    struct termios options;
    tcgetattr(*fd, &options);
    options.c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
    options.c_oflag &= ~(ONLCR | OCRNL);
    tcsetattr(*fd, TCSANOW, &options);

    return 0;
  }

}
void maestroTerm(int fd)
{
  close(fd);
}