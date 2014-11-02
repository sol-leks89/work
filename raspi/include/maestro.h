/* 
	maestro.h
*/

#define REQUEST_GET_PARAMETER 0x81
#define REQUEST_SET_PARAMETER 0x82
#define REQUEST_GET_VARIABLES 0x83
#define REQUEST_SET_SERVO_VARIABLE 0x84
#define REQUEST_SET_TARGET 0x85
#define REQUEST_CLEAR_ERRORS 0x86
#define REQUEST_GET_SERVO_SETTINGS 0x87

// GET STACK and GET CALL STACK are only used on the Mini Maestro.
#define REQUEST_GET_STACK 0x88
#define REQUEST_GET_CALL_STACK 0x89
#define REQUEST_SET_PWM 0x8A

#define REQUEST_REINITIALIZE 0x90
#define REQUEST_ERASE_SCRIPT 0xA0
#define REQUEST_WRITE_SCRIPT 0xA1
#define REQUEST_SET_SCRIPT_DONE 0xA2
#define REQUEST_RESTART_SCRIPT_AT_SUBROUTINE 0xA3
#define REQUEST_RESTART_SCRIPT_AT_SUBROUTINE_WITH_PARAMETER 0xA4
#define REQUEST_RESTART_SCRIPT 0xA5
#define REQUEST_START_BOOTLOADER 0xFF

#define PARAMETER_CHANNEL_MODES_0_3 12
#define PARAMETER_CHANNEL_MODES_4_7 13

	#define PARAMETER_IO_MASK_C 16
        #define PARAMETER_OUTPUT_MASK_C 17




extern int maestroGetPosition(int fd, unsigned char channel);
extern int maestroSetTarget(int fd, unsigned char channel, unsigned short target);
extern int maestroInit(int *fd);
extern void maestroTerm(int fd);
extern int maestroGetParameter(int, unsigned char, unsigned char *);

