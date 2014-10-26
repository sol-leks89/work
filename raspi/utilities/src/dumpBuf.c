#include <stdio.h>
#include <malloc.h>

#include "utiljww.h"

int dumpBuf(char *intro, unsigned char *buf, size_t sz) 
{
	int retCd = 0;
	char *msg;
	char *cur;

	if (buf == 0)
	{
		printf("%s -- null pointer", intro);
		return -1;
	}

	// each byte will be 3 (2 for characters and 1 space)
	// after 16 bytes there will be a line feed and a dash after 8.
	msg = (char *)malloc((sz * 3) + ((sz / 16)*4) + 3);
	unsigned int i;
	if (msg == 0)
	{
		printf("%s -- malloc failed", intro);
		return -1;
	}
	cur = msg;
	for (i=0; i < sz; i++)
	{
		if (i && !(i%16))
		{
			sprintf(cur, "\n");
			cur += 1;
		}
		else if (i && !(i%8))		
	{
			sprintf(cur, "- ");
			cur += 2;
		}
		sprintf(cur, "%02x ",buf[i]);
		cur +=3;
		

	}
log_trace(intro);
log_trace(msg);
free(msg);




	return retCd;
}