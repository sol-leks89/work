// goddard.h

#define TILT_UP_MAX 3500
#define TILT_CHANNEL 1
#define TILT_UP_MIN 8000
#define EYE_CHANNEL 5
#define STEP_UP -150
#define STEP_DOWN 150
#define FAST_SCAN 50000
#define SLOW_SCAN 350000
#define EYE_THRESHOLD 750

int init_tilt(int);
int find_min_edge(int fd, int channel, int min, int max);
