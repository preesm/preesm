/**
* Defines parameters for the application.
*
* @file applicationParameters.h
* @author kdesnos
* @date 2016.09.06
* @version 1.0
* @copyright CECILL-C
*/

#if 1
#define PATH_VIDEO PROJECT_ROOT_PATH "/dat/JaguarAndCroco360x202-375f.yuv"

#define HEIGHT 202 //!< Video height
#define WIDTH 360 //!< Video width

#define NB_FRAME 375 //!< Number of frames in the video

#define MAX_DELTA_X 38 //!< Largest abscissa difference for block matching
#define MAX_DELTA_Y 21 //!< Largest ordinate difference for block matching
#endif

#if 0
#define PATH_VIDEO PROJECT_ROOT_PATH "/dat/GivingScholarlyFallowdeer1280x720-415f.yuv"

#define HEIGHT 720 //!< Video height
#define WIDTH 1280 //!< Video width

#define NB_FRAME 415 //!< Number of frames in the video

#define MAX_DELTA_X 3 //!< Largest abscissa difference for block matching
#define MAX_DELTA_Y 3 //!< Largest abscissa difference for block matching
#endif

/**
* Width of a block in pixels.
* Because the Squared Error of blocks matching is stored in an int, block
* dimensions cannot exceed 2^16 pixels (e.g. 256*256 blocks).
*/
#define BLOCK_WIDTH 32
/**
* Height of a block in pixels
* Because the Squared Error of blocks matching is stored in an int, block
* dimensions cannot exceed 2^16 pixels (e.g. 256*256 blocks).
*/
#define BLOCK_HEIGHT 32

/**
* Defines a rudimentary, one tap IIR filter.
*
* The defined coefficient must be lower or equal.
* - Value set to 1.0: No filtering, all motion vectors are accumulated without
* filtering. For perfect motion vectors, fixed point in video background will
* not moove on the rendered video.
- Value set to 0.9 < val < 1.0 : High-pass filter. Motion vectors are
* accumulated, but DC component is slowly eliminated. Rapid camera motions are
* compensated, but slow camera motions are not.
- Value set to val < 0.9 : High-pass filter. DC component is rapidly
* eliminated. Rapid camera motions are compensated.
*/
#define HIGH_PASS_FILTER_TAP 0.98f

/**
* Width and height in pixels of the border displayed around the rendered image.
*/
#define BORDER 100

/**
* Path to the output file
*/
#define PATH_WRITE PROJECT_ROOT_PATH "/dat/output.yuv"
