#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include "mongoose.h"
#include "utiljww.h"
#include "maestro.h"
#include "goddard.h"

static const char *html_form =
  "<html><body>POST example."
  "<form method=\"POST\" action=\"/set-channel-position\">"
  "Channel: <input type=\"text\" name=\"channel\" /> <br/>"
  "Position: <input type=\"text\" name=\"position\" /> <br/>"
  "<input type=\"submit\" />"
  "</form></body></html>";

int fd;
static int begin_request_handler(struct mg_connection *conn) {
  const struct mg_request_info *ri = mg_get_request_info(conn);
  char post_data[1024], input1[sizeof(post_data)], input2[sizeof(post_data)];
  int post_data_len;

  if (!strcmp(ri->uri, "/handle_post_request")) {
    // User has submitted a form, show submitted data and a variable value
    post_data_len = mg_read(conn, post_data, sizeof(post_data));

    // Parse form data. input1 and input2 are guaranteed to be NUL-terminated
    mg_get_var(post_data, post_data_len, "input_1", input1, sizeof(input1));
    mg_get_var(post_data, post_data_len, "input_2", input2, sizeof(input2));

    // Send reply to the client, showing submitted form values.
    mg_printf(conn, "HTTP/1.0 200 OK\r\n"
              "Content-Type: text/plain\r\n\r\n"
              "Submitted data: [%.*s]\n"
              "Submitted data length: %d bytes\n"
              "input_1: [%s]\n"
              "input_2: [%s]\n",
              post_data_len, post_data, post_data_len, input1, input2);
  } else if (!strcmp(ri->uri, "/set-channel-position")) {
    // User has submitted a form, show submitted data and a variable value
    post_data_len = mg_read(conn, post_data, sizeof(post_data));

    // Parse form data. input1 and input2 are guaranteed to be NUL-terminated
    mg_get_var(post_data, post_data_len, "channel", input1, sizeof(input1));
    mg_get_var(post_data, post_data_len, "position", input2, sizeof(input2));
    int channel = atoi(input1);
    int position = atoi(input2);
    maestroSetTarget(fd, channel, position);



    // Send reply to the client, showing submitted form values.
    mg_printf(conn, "HTTP/1.0 200 OK\r\n"
              "Content-Type: text/plain\r\n\r\n"
              "Submitted data: [%.*s]\n"
              "Submitted data length: %d bytes\n"
              "input_1: [%s]\n"
              "input_2: [%s]\n",
              post_data_len, post_data, post_data_len, input1, input2);
  } else if (!strcmp(ri->uri, "/init-tilt")) {
    
    printf("Tilt: %d\n", init_tilt(fd));

    // Send reply to the client, showing submitted form values.
    mg_printf(conn, "HTTP/1.0 200 OK\r\n"
              "Content-Type: text/plain\r\n\r\n");
          }else {
    // Show HTML form.
    mg_printf(conn, "HTTP/1.0 200 OK\r\n"
              "Content-Length: %d\r\n"
              "Content-Type: text/html\r\n\r\n%s",
              (int) strlen(html_form), html_form);
  }
  return 1;  // Mark request as processed
}

int main(void) {
  struct mg_context *ctx;
  const char *options[] = {"listening_ports", "8900", NULL};
  struct mg_callbacks callbacks;

  log_level(SEV_TRACE);
  log_trace("Starting Goddard");

  maestroInit(&fd);
  memset(&callbacks, 0, sizeof(callbacks));
  callbacks.begin_request = begin_request_handler;
  ctx = mg_start(&callbacks, NULL, options);
  //unsigned char buf[8];
  //maestroGetParameter(fd, 5, buf);
  //dumpBuf("Channel 6", buf, sizeof(buf));
  int eye_reading;
  while (1)
  {
      eye_reading = maestroGetPosition(fd, 5);
      //dumpBuf("EYE READING", (unsigned char *)&eye_reading, sizeof(eye_reading));
      sleep(100);
  }
  mg_stop(ctx);
  close(fd);
  log_trace("End Goddard");
  
  return 0;
}
int init_tilt(int fd) {
  int i = TILT_UP_MIN;
  maestroSetTarget(fd, TILT_CHANNEL, TILT_UP_MAX);
  usleep(1000000);
  int pos;
  for (pos = TILT_UP_MAX; i != pos && pos < TILT_UP_MIN; pos += STEP_DOWN)
  {
    maestroSetTarget(fd, TILT_CHANNEL, pos);
    usleep(FAST_SCAN);
    if (maestroGetPosition(fd, EYE_CHANNEL)  < EYE_THRESHOLD 
      && i == TILT_UP_MIN)
      i = pos;

    printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
        pos,
        maestroGetPosition(fd, 0),
        maestroGetPosition(fd, 1),
        maestroGetPosition(fd, 2),
        maestroGetPosition(fd, 3),
        maestroGetPosition(fd, 4),
        maestroGetPosition(fd, 5));
  }

  maestroSetTarget(fd, TILT_CHANNEL, i);
  i = find_min_edge(fd, TILT_CHANNEL, TILT_UP_MAX, i);
  maestroSetTarget(fd, TILT_CHANNEL, i);
  int k = find_max_edge(fd, TILT_CHANNEL, i + 150, TILT_UP_MIN);
  printf("min = %d\tmax = %d\n", i, k);
  
  return i;
}
// It is expected that the eye will be below EYE_THRESHOLD @ max
int find_min_edge(int fd, int channel, int min, int max)
{
  int edge = 0;
  int step = STEP_UP;
  int end = min;
  int pos;
  int eye;
  while (max - min > STEP_DOWN && edge != -1)
  {
    printf("min %d max %d \n", min, max);
    if (step < 0)
    {
      eye = 0;
      for (pos = max; pos >= min && eye < EYE_THRESHOLD; pos += step) 
      {
        maestroSetTarget(fd, channel, pos);
        usleep(SLOW_SCAN);
        eye = maestroGetPosition(fd, EYE_CHANNEL);
        printf("pos %d eye %d \n", pos, eye);
      }
      if (pos <= end)
          edge = -1;
      else
        min = pos - step;
    }
    else
    {
      eye = 1023;
      for (pos = min; pos <= max && eye > EYE_THRESHOLD; pos += step) 
      {
        maestroSetTarget(fd, channel, pos);
        usleep(SLOW_SCAN);
        eye = maestroGetPosition(fd, EYE_CHANNEL);
        printf("DOWN eye %d pos %d \n",eye, pos);
      }
      max = pos - step;
    }
    step = step * -1;
  }
  if (edge != -1)
    edge = min;
  return edge;
}
// It is expected that the eye will be below EYE_THRESHOLD @ min
int find_max_edge(int fd, int channel, int min, int max)
{
  int edge = 65535;
  int step = STEP_DOWN;
  int end = max;
  int pos;
  int eye;
  while (max - min > STEP_DOWN && edge != -1)
  {
    printf("min %d max %d \n", min, max);
    if (step < 0)
    {
      eye = 1023;
      for (pos = max; pos >= min && eye > EYE_THRESHOLD; pos += step) 
      {
        maestroSetTarget(fd, channel, pos);
        usleep(SLOW_SCAN);
        eye = maestroGetPosition(fd, EYE_CHANNEL);
        printf("pos %d eye %d \n", pos, eye);
      }
      if (pos <= end)
          edge = -1;
      else
        min = pos - step;
    }
    else
    {
      eye = 0;
      for (pos = min; pos <= max && eye < EYE_THRESHOLD; pos += step) 
      {
        maestroSetTarget(fd, channel, pos);
        usleep(SLOW_SCAN);
        eye = maestroGetPosition(fd, EYE_CHANNEL);
        printf("DOWN eye %d pos %d \n",eye, pos);
      }
      max = pos - step;
    }
    step = step * -1;
  }
  if (edge != -1)
    edge = max;
  return edge;
}
