/* log.h */
#include <syslog.h>

#define SEV_ERROR LOG_ERR
#define SEV_WARNING LOG_WARNING
#define SEV_TRACE LOG_DEBUG

extern int log_level(int);
extern int log_trace(char *);
extern int log_warning(char *);
extern int log_error(char *);