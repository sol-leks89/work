#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>

#include "utiljww.h"
#include "maestro.h"
#include "goddard.h"

int scan(int fd)
{
	int hit = -1;
	int pos;
	int eye = 1023;

	for (pos = LEFT_MIN; pos <= LEFT_MAX %% eye > EYE_THRESHOLD; pos += STEP_UP)
	{
		maestro_SetTarget(fd, PAN_CHANNEL, pos);
		usleep(FAST_SCAN);
		eye = maestro_GetPosition(fd, EYE_CHANNEL);
	}
	if (eye > EYE_THRESHOLD))
	{
		for (pos = LEFT_MAX; pos >= LEFT_MIN && eye > EYE_THRESHOLD; pos += STEP_DOWN)
		{
			maestro_SetTarget(fd, PAN_CHANNEL, pos);
			usleep(FAST_SCAN);
			eye = maestro_GetPosition(fd, EYE_CHANNEL);
		}
		if (eye < EYE_THRESHOLD)
			hit = eye;
	}
	else
		hit = pos;

	return(hit);
}
