#ifndef CONFIG_H
#define CONFIG_H

/* dynamic segment id */
#define IMAGE_SEGMENT_ID (10)

/* N tile buffering */
#define N (8)

/* image size */
#define WIDTH (1920)
#define HEIGHT (1080)

/* sub image size */
#define TILE_WIDTH (WIDTH/(32))
#define TILE_HEIGHT (HEIGHT/(30))

/* iteration */
#define ITERATION (1)

/* verify tiling size multiplicity with size */
_Static_assert ( ((WIDTH%TILE_WIDTH) == 0), "Tile size must be a multiple of the global size");
_Static_assert ( ((HEIGHT%TILE_HEIGHT) == 0), "Tile size must be a multiple of the global size");

/* element of the matrix */
typedef uint32_t element_t;

//#define BENCH_MEMORY_BW

//#define REMOVE_WRITE /* DDR do not like write ! */

#endif

