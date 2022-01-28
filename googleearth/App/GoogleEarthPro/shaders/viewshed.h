#if defined(GL_ES) && defined(GE_PIXEL_SHADER)

// This is needed to use fwidth().
#extension GL_OES_standard_derivatives : enable

#endif

uniform highp mat4 viewshedMatrix;
varying highp vec3 viewshedDir;
varying highp float viewshedLen;

// Maximum world space radius of viewshed in fraction of Earth radius.
// Currently, kMaxWorldSpaceLength = 10000m / kWGS84Radius
// See earth/client/libs/math/units.cc
#define kMaxWorldSpaceLength 0.0015678559
// Offset in distance to reduce shadow acne.
#define kViewshedDepthOffset 0.00000031357119
// "Fringe" distance to fade nicely from good color to bad color.
#define kViewshedFringe 0.00000031357119
#define kGoodColor vec4(0.2, 1.0, 0.2, 1.0)  // visible color
#define kBadColor vec4(0.2, 0.2, 0.2, 1.0)  // hidden color
#define kOutsideColor vec4(0.3, 0.3, 0.3, 1.0)  // color outside viewshed range
#define kGoodColorMix 0.34
#define kBadColorMix 0.4
#define kViewshedBoundaryWidth 2.0  // width of the boundary of viewshed

// FloatToRgba & RgbaToFloat are inspired on various internet
// articles, e.g.:
//   - http://www.gamedev.net/topic/442138-packing-a-float-into-a-a8r8g8b8-texture-shader
//   - http://aras-p.info/blog/2009/07/30/encoding-floats-to-rgba-the-final/
//
// Doing a Google search for "float to rgba in shader" yields more
// background information.  Each channel basically represents some
// part of the floating-point number in increasing powers of 255.
//
// This math is also verified in libs/evll/tests/viewshed_shader_math_test.cc.
// If you modify these conversions, please also update that test.
//
const float k255_1 = 255.0;
const float k255_2 = 255.0 * 255.0;
const float k255_3 = 255.0 * 255.0 * 255.0;
const float kInv255 = (1.0 / 255.0);
vec4 FloatToRgba(float f) {
  vec4 rgba = fract(vec4(1.0, k255_1, k255_2, k255_3) * f);
  rgba -= rgba.yzww * vec4(kInv255, kInv255, kInv255, 0.0);
  return rgba;
}
float RgbaToFloat(vec4 rgba) {
  return dot(rgba, vec4(1.0, kInv255, 1.0 / k255_2, 1.0 / k255_3));
}

#if defined(GE_VERTEX_SHADER)
void InitViewshed(vec4 pos) {
  // See renderimpl.cc ApplyViewshed() for the math.
  viewshedDir = (viewshedMatrix * ig_ModelViewMatrix * pos).xyz;
  // TODO(johnan): Use Manhattan distance to avoid length() call?
  // With 32bpp float depth buffer the L2 norm seems to be working
  // well enough.
  viewshedLen = length(viewshedDir);
}
#endif  // defined(GE_VERTEX_SHADER)

#if defined(GE_PIXEL_SHADER) && defined(VIEWSHED_RENDER) && !defined(SKY)
uniform samplerCube viewshedTexture;

void ApplyViewshed(inout vec4 color) {
  if (viewshedLen <= kMaxWorldSpaceLength) {
    vec4 viewshedLookup = textureCube(viewshedTexture, viewshedDir);
    float viewshedDepth = RgbaToFloat(viewshedLookup);
    float viewshedDiff = viewshedLen - viewshedDepth;

    // kCubeMapPixelArcLen is the approximate arc length of 1 pixel in
    // the cube map over a unit circle, given by:
    //    2 PI / 4096 = 0.0015339807878856
    // Assuming 1024^2 cube map faces.
#define kCubeMapPixelArcLen 0.0015339807878856
    // Vary the depth offset by the distance from the viewshed
    // location.  The idea here is that the depth precision will
    // decrease with distance (floating point).  This seems to give
    // okay results.
    float depth_offset = 1.25 * kCubeMapPixelArcLen * viewshedLen;
    float depth_fringe = depth_offset;

    if (viewshedDiff < depth_offset) {
      // This point is considered visible because it is closer than
      // depth in cube map (+depth offset to reduce shadow acne).
      color = mix(color, kGoodColor, kGoodColorMix);
    } else if (viewshedDiff < depth_offset + depth_fringe) {
      // This point is marginally visible.  Fade from visible to
      // hidden color.  This makes the rendering look a bit better in
      // marginal areas.
      float viewshedMix = (viewshedDiff - depth_offset) / depth_fringe;
      color =
        mix(color, mix(kGoodColor, kBadColor, viewshedMix), kGoodColorMix);
    } else {
      // Point is not visible.
      color = mix(color, kBadColor, kBadColorMix);
    }
  } else {
    color *= kOutsideColor;
  }
  float viewshedLenFwidth = fwidth(viewshedLen);
  float viewshedBoundary =
    abs(viewshedLen - kMaxWorldSpaceLength) / viewshedLenFwidth;
  if (viewshedBoundary < kViewshedBoundaryWidth) {
    color = mix(vec4(0.0, 0.0, 0.0, 1.0), color,
                   viewshedBoundary / kViewshedBoundaryWidth);
  }
}
#endif  // defined(GE_PIXEL_SHADER) && defined(VIEWSHED_RENDER) && !defined(SKY)
