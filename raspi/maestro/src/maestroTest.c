#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
 
#include "maestro.h"

int main(int argc, char *argv[])
{
 
   int fd;

   if (maestroInit(&fd))
      return 1;
  printf("Completed init fd: %d\n", fd);



if (argc < 2) 
{
   int position = maestroGetPosition(fd, 0);
  printf("Current position is %d.\n", position);
 
  int target = (position < 6000) ? 8000 : 4000;
  printf("Setting target to %d (%d us).\n", target, target/4);
  maestroSetTarget(fd, 0, target);
 
}
else
{
  int i;
  int position = -1;

  maestroSetTarget(fd, 0, 0);
  

  for (i=2200; i<8000; i+= 32)
  {
    if (i%100 == 0)
      position = maestroGetPosition(fd, 0);
    printf("Current position is %d.\n", position);
 
    maestroSetTarget(fd, 0, i);  
    if (i==2200)
      usleep(630*1000);
    else
      usleep(35*100);
  } 
}   
  close(fd);
  return 0;
}
