#if defined(GL_ES)
precision GE_PRECISION float;
#else
#define lowp
#define mediump
#define highp
#endif

uniform mat4 ig_ModelViewMatrix;
uniform mat4 ig_ModelViewProjectionMatrix;
uniform mat4 ig_TextureMatrix;
uniform vec4 ig_LightDirectionInModelSpace;
uniform lowp float alpha_ref_value;

#if defined(MODEL) && !defined(USE_SIMPLIFIED_SHADER_VARIATION)
// Turn off atmosphere fog when rendering models.  This is done for a few
// reasons:
// 1) For models, there is no automatic mechanism to switch off the fog
//    when the eye is close to the ground.  Since most models are only visible
//    when the eye is close to them, the fog calculation wasn't adding any
//    value.
// 2) The atmosphere fog calculation only looks correct for objects close
//    to the surface of the planet.  Models in space look incorrect because
//    they have way too much fog applied to them.
#define USE_SIMPLIFIED_SHADER_VARIATION
#endif

#ifdef COLOR_DESATURATION
$input "color.h"
#endif

float saturate(float v) { return clamp(v, 0.0, 1.0); }
vec2 saturate(vec2 v) { return clamp(v, 0.0, 1.0); }
vec3 saturate(vec3 v) { return clamp(v, 0.0, 1.0); }
vec4 saturate(vec4 v) { return clamp(v, 0.0, 1.0); }

// Returns the quadrant portion of the given vertex flags.
float getQuadrant(vec4 vertexQuadrant) {
  return dot(vertexQuadrant, vec4(1.0, 2.0, 4.0, 8.0));
}

// Both getMasked methods assume that quadrantMask has a single 1 and the
// rest are 0.

vec4 getMaskedVertex(vec4 pos, vec4 quadrantMask, vec4 vertexQuadrant) {
  return pos * dot(quadrantMask, vertexQuadrant);
}

float getMaskedAlpha(vec4 quadrantAlphas, vec4 vertexQuadrant) {
  return dot(quadrantAlphas, vertexQuadrant);
}

// Computes a vertex color to show which quadrant a vertex is in.  Also marks
// vertices that would be rejected and error conditions (e.g. invalid
// quadrant).
vec4 getDebugVertexColor(vec4 quadrantMask, vec4 vertexQuadrant) {
  vec4 color;
  float quadrant = getQuadrant(vertexQuadrant);
  if (quadrant == 1.0) {
    // Red
    color = vec4(1.0, 0.0, 0.0, 1.0);
  } else if (quadrant == 2.0) {
    // Green
    color = vec4(0.0, 1.0, 0.0, 1.0);
  } else if (quadrant == 4.0) {
    // Blue
    color = vec4(0.0, 0.0, 1.0, 1.0);
  } else if (quadrant == 8.0) {
    // Cyan
    color = vec4(0.0, 1.0, 1.0, 1.0);
  } else if (quadrant == 0.0) {
    // Vertex didn't have a quadrant bit set.  Mark as saturated Yellow.
    color = vec4(10.0, 10.0, 0.0, 1.0);
  } else if (quadrant == 15.0) {
    // All quadrant bits were set.  Mark as White.
    color = vec4(10.0, 10.0, 10.0, 1.0);
  } else {
    // More than one quadrant bit was set or a quadrant bit was set to
    // something other than 1.0, so flag verts in saturated Magenta.
    color = vec4(10.0, 0.0, 10.0, 1.0);
  }
  // Make hidden quadrants dimmer.
  if (dot(quadrantMask, vertexQuadrant) == 0.)
    color *= .5;

  return color;
}

#ifdef DEBUG_QUADRANT_COLORS
// Used by the pixel shader to show quadrants, culled verts and error
// conditions when DEBUG_QUADRANT_COLORS is defined.
varying vec4 vout_debug_color;
#endif

// The "discard" instruction is not available for vertex shaders, so
// this function needs to be excluded when parsing a vertex shader.
#ifdef GE_PIXEL_SHADER

// When debugging is on, modulates the pixel color by the debug color output
// from the vertex shader.  Otherwise, the given pixel color is returned
// unchanged.
vec4 computeDebugPixelColor(vec4 pixelShaderColor) {
// OGL ES 2.0 does not support alpha test, so need to do it in the
// shader.
#if defined(GL_ES) && !defined(FORCE_NO_ALPHA_TEST)
// Terrain and sky don't use alpha test.
#if defined(OVERLAY) || defined(MODEL)
  // alpha_ref_value is set by igVisualContext, and will be -1 if alpha test
  // is off.
  if (pixelShaderColor.a < alpha_ref_value) {
    discard;
  }
#endif  // defined(OVERLAY) || defined(MODEL)
#endif  // defined(GL_ES) && !defined(FORCE_NO_ALPHA_TEST)

#ifdef DEBUG_QUADRANT_COLORS
  return pixelShaderColor * vout_debug_color;
#else
  return pixelShaderColor;
#endif
}

#endif  // GE_PIXEL_SHADER
