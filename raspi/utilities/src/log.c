#include <stdio.h>
#include <syslog.h>

#include "log.h"

int write_log(int severity, char *msg)
{
	int retCd = 0;
	log_level(severity+2);
	 openlog ("goddard.log", LOG_CONS | LOG_PID | LOG_NDELAY, LOG_LOCAL1);
	syslog (LOG_ALERT, msg);
	closelog ();

	return retCd;
}
int log_trace(char *msg)
{
	return write_log(SEV_TRACE, msg);
}
int log_warning(char *msg)
{
	return write_log(SEV_WARNING, msg);
}
int log_error(char *msg)
{
	return write_log(SEV_ERROR, msg);
}
int log_level(int level)
{
	return setlogmask (LOG_UPTO (level));
}