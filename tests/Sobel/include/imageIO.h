#ifndef IMAGEIO_H
#define IMAGEIO_H

void initCamera();
void camera(unsigned char* image, int width, int height);
void cleanCamera();

void initDisplay();
void display(unsigned char* image, int width, int height);
void cleanDisplay();

#endif//IMAGEIO_H

