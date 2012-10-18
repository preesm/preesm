#ifndef ACTOR_H
#define ACTOR_H

void Camera_camera(i32 WIDTH, i32 HEIGHT, u8 *frame);
void Display_display(i32 WIDTH, i32 HEIGHT, u8 *frame);
void MergeImage_split(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *slices_in, u8 *frame);
void SeparateY_separate(i32 WIDTH, i32 HEIGHT, u8 *frame, u8 *Y);
void SobelFilter_filter(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *slice_in, u8 *slice_out);
void SplitImage_split(i32 NB_SLICES, i32 WIDTH, i32 HEIGHT, u8 *frame, u8 *slices_out);

#endif//ACTOR_H
